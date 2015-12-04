package com.example.twiddy_ui;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import net.daum.mf.speech.api.SpeechRecognizeListener;
import net.daum.mf.speech.api.SpeechRecognizerClient;
import net.daum.mf.speech.api.SpeechRecognizerManager;
import net.daum.mf.speech.api.TextToSpeechClient;
import net.daum.mf.speech.api.TextToSpeechListener;
import net.daum.mf.speech.api.TextToSpeechManager;
import twitter4j.Twitter;
import twitter4j.TwitterException;

class MentionThread extends TimerTask {
	private DisplayEmotion parent;
	private String last_sender;
	private String last_mention;
	
	MentionThread(DisplayEmotion _parent) {
		this.parent = _parent;
		this.last_mention = "";
	}

	@Override
	public void run() {
		try {
			List<twitter4j.Status> statuses = this.parent.twitter.getMentionsTimeline();			
			Log.e("MENTION","Get new mention");
			if (statuses.isEmpty() == false) {				
				String current_mention = statuses.get(0).getText();
				if (this.last_mention.equals("")) {
					this.last_mention = current_mention;
				} else if (!this.last_mention.equals(current_mention)) {
					this.last_sender = statuses.get(0).getUser().getName();						
					if(this.parent.getNewMention(this.last_sender, current_mention)) {
						// Mention Read Success
						this.last_mention = current_mention;						
					}
					else {
						this.parent.isMentionStored = true;
					}
					Log.e("MENTION", "last sender : " + this.last_sender);
					Log.e("MENTION", "last mention : " + current_mention);
				}
			}
		}
		catch (TwitterException te) {
			if (401 == te.getStatusCode()) {
				Log.d("update","Unable to get the access token.");
			} else {
				te.printStackTrace();
			}
		}
	}
}

public class DisplayEmotion  extends Activity implements OnClickListener{
	public static String NEWTONE_API_KEY = "dcd2a896fab93d17a09e2d752ef0e145";
	private TextToSpeechClient tts_client;
	private SpeechRecognizerClient stt_client;
	private RunningTwiddy twiddy;
	public Twitter twitter;
	private Timer mention_thread;
	private Timer debug_thread;
	Emotion emotion;
	private static final int REQUEST_CONNECT_DEVICE = 1;
	private static final int REQUEST_ENABLE_BT = 2;
	private BluetoothService btService = null;
	private TextView recording;
	private int waitCnt = 0;
	public boolean isMentionStored = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display);

		Button btn_normal = (Button)findViewById(R.id.btn_normal);
		Button btn_happy = (Button)findViewById(R.id.btn_happy);
		Button btn_angry = (Button)findViewById(R.id.btn_angry);
		btn_normal.setOnClickListener(this);
		btn_happy.setOnClickListener(this);
		btn_angry.setOnClickListener(this);
		
		recording = (TextView)findViewById(R.id.txt_recording);
		recording.setVisibility(View.GONE);

		emotion = new Emotion(this);
		emotion.changeEmotion(EnumEmotion.Normal);
		FrameLayout frame = (FrameLayout)findViewById(R.id.layout_display);
		frame.addView(emotion, 0);

		/* Login to Twitter */
		Intent intent = getIntent();
		this.twitter = (Twitter) intent.getExtras().get("twitter");

		/* keep display on */
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		/* Bluetooth Service ON */
		if(btService == null) {
			btService = new BluetoothService(this);
		}		
		
		if(btService.getState() != BluetoothService.STATE_CONNECTED 
				&& btService.getDeviceState()){
			btService.enableBluetooth();
		}
		else {
			finish();
		}

		/* getting mentions thread */
		this.mention_thread = new Timer();
		this.mention_thread.scheduleAtFixedRate(new MentionThread(this), 0, 30000);

		/* Voice Related */
		this.twiddy = new RunningTwiddy(this);

		/* Set TTS Module */
		TextToSpeechManager.getInstance().initializeLibrary(getApplicationContext());
		TextToSpeechListener tts_listener = new TTSListener(this);
		this.tts_client = new TextToSpeechClient.Builder()
				.setApiKey(NEWTONE_API_KEY)
				.setSpeechSpeed(1.2)
				.setSpeechVoice(TextToSpeechClient.VOICE_WOMAN_DIALOG_BRIGHT)
				.setListener(tts_listener)
				.build();

		/* Set STT Module */
		SpeechRecognizerManager.getInstance().initializeLibrary(this);
		SpeechRecognizeListener stt_listener = new STTListener(this);
		this.stt_client = new SpeechRecognizerClient.Builder()
				.setApiKey(NEWTONE_API_KEY)
				.setServiceType(SpeechRecognizerClient.SERVICE_TYPE_WEB)
				.setGlobalTimeOut(60)
				.build();
		this.stt_client.setSpeechRecognizeListener(stt_listener);

		Button btn_hear = (Button) findViewById(R.id.btn_hear);
		btn_hear.setBackgroundColor(Color.TRANSPARENT);
		btn_hear.setOnClickListener(this);

		/* for DEBUG and STT Error Handling */
		debug_thread = new Timer();
		debug_thread.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				waitCnt++;				
				if(waitCnt > 3)	{
					Log.e("STT ERROR", "restart!");
					if (!twiddy.isStop()) {
						stt_client.stopRecording();
						stt_client.startRecording(false);
					}
				}
				if(isMentionStored) {
					// TODO: Think later
					isMentionStored = false;
					mention_thread.notify();					
				}
				Log.e("Timer", twiddy.getStatus());
			}
		}, 0, 10000);
	}
	
	 private void sendMessage(String message) {
	        if (btService.getState() != BluetoothService.STATE_CONNECTED) {
	            Toast.makeText(this, "NOT CONNECTED YET!", Toast.LENGTH_SHORT).show();
	            return;
	        }

	        if (message.length() > 0) {
	            byte[] send = message.getBytes();
	            btService.write(send);
	        }
	    }

		public void onActivityResult(int requestCode, int resultCode, Intent data) { 
	        switch (requestCode) {        
	        case REQUEST_CONNECT_DEVICE:
	            if (resultCode == Activity.RESULT_OK) {
	            	btService.getDeviceInfo(data);
	            }
	            break;
	        case REQUEST_ENABLE_BT:
	            if (resultCode == Activity.RESULT_OK) {
	            	btService.scanDevice();
	            } 
	            break;
	        }
		}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.btn_normal:
			emotion.changeEmotion(EnumEmotion.Normal);
			sendMessage("n");
			break;
		case R.id.btn_happy:
			emotion.changeEmotion(EnumEmotion.Happy);
			sendMessage("h");
			break;
		case R.id.btn_angry:
			emotion.changeEmotion(EnumEmotion.Angry);
			sendMessage("a");
			break;
		case R.id.btn_hear:
			this.performSTT();
			break;
		}
	}

	/* Voice Activity Methods */
	public void performTTS(final String msg) {
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Log.e("TTS", msg);
				if (!twiddy.isStop()) {
					tts_client.play(msg);
					recording.setVisibility(View.GONE);
				}
			}
		});
	}

	public void performSTT() {
		Log.e("STT", "start");
		if (!this.twiddy.isStop()) {
			this.waitCnt = 0;
			this.stt_client.startRecording(false);
			Log.e("STT", "started");
			recording.setVisibility(View.VISIBLE);
		}
	}

	
	public void showSTTReuslt(final String result_text) {
		this.runOnUiThread(new Runnable() {
			public void run() {				
				twiddy.handleSTTResult(result_text);
			}
		});
	}

	public void handleTTSResult() {
		this.runOnUiThread(new Runnable() {
			public void run() {
				twiddy.handleTTSResult();
			}
		});
	}

	public boolean getNewMention(final String sender, final String msg) {
		stt_client.stopRecording();
		return twiddy.getNewMention(sender, msg);
	}

	public void uploadTweet(final String msg) {
		Thread thread = new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					twitter.updateStatus(msg); 						    
				} catch (TwitterException te) {
					if (401 == te.getStatusCode()) {
						Log.d("update","Unable to get the access token.");
					} else {
						te.printStackTrace();
					}
				}
				finally {
					twiddy.endedUpload();
				}
				Log.e("uploading", msg);
			}
		});
		thread.start();
	}
	
	public void showEnumEmotion(final EnumEmotion e) {
		runOnUiThread(new Runnable() {
			public void run() {
				switch(e) {
				case Normal:
					emotion.changeEmotion(EnumEmotion.Normal);
					sendMessage("n");
					break;
				case Happy:
					emotion.changeEmotion(EnumEmotion.Happy);
					sendMessage("h");
					break;
				case Angry:
					emotion.changeEmotion(EnumEmotion.Angry);
					sendMessage("a");
					break;
				case Start:
					emotion.changeEmotion(EnumEmotion.Happy);
					sendMessage("s");
					break;
				case Explain:
					emotion.changeEmotion(EnumEmotion.Normal);
					sendMessage("e");
					break;
				}
			}
		});
	}

	public void showEmotion(final String msg) {

		AsyncTask askServer = new AsyncTask() {
			@Override
			protected Object doInBackground(Object... params) {
				// TODO Auto-generated method stub
				
				final int score = EmotionExtractor.getEmotion(msg);
				Log.e("EMOTION MSG", msg);
				Log.e("EMOTION SCORE", "SCORE: "+score);
				runOnUiThread(new Runnable() {
					public void run() {
						if (score == -1234) {
							//Do nothing (error state)
						}
						else if (score == 0) {
							emotion.changeEmotion(EnumEmotion.Normal);
							sendMessage("n");
						} else if (score > 0) {
							emotion.changeEmotion(EnumEmotion.Happy);
							sendMessage("h");
						} 
						else if (score < 0) {
							emotion.changeEmotion(EnumEmotion.Angry);
							sendMessage("a");
						}
					}
				});
				return null;
			}
		};

		askServer.execute();

	}
	@Override
	public void onBackPressed() {
		this.stt_client.stopRecording();
		this.tts_client.stop();
		this.twiddy.stop();
		this.mention_thread.cancel();
		this.mention_thread.purge();
		this.debug_thread.cancel();
		this.debug_thread.purge();
		super.onBackPressed();
	}
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		this.tts_client.play();
		super.onRestart();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		TextToSpeechManager.getInstance().finalizeLibrary();
		SpeechRecognizerManager.getInstance().finalizeLibrary();
	}

	protected class Emotion extends View{
		Thread animator = null;
		Paint pnt;
		DisplayMetrics displayMetrics;
		int deviceWidth, deviceHeight;
		float startAngle;
		float mouseTop, mouseBottom;
		int radius, eyeRadius;
		RectF rect;
		EnumEmotion currentEmotion;
		EmotionAnimation eAnim;

		public Emotion(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
			pnt = new Paint();
			displayMetrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);			 
			deviceWidth = displayMetrics.widthPixels;
			deviceHeight = displayMetrics.heightPixels;
			radius = deviceWidth/4;
			eyeRadius = radius/6;
			rect = new RectF();
			currentEmotion = EnumEmotion.Normal; //Default: Normal
			startAngle = 30f;
			mouseTop = deviceHeight/5*2+eyeRadius*3;
			mouseBottom = deviceHeight/5*2+eyeRadius*3;
			eAnim = new EmotionAnimation(currentEmotion);
			postInvalidate();
		}

		public void changeEmotion(EnumEmotion e)
		{
			currentEmotion = e;
			eAnim.changeEmotion(e);
			this.startAnimation(eAnim);
			postInvalidate();
		}

		@Override
		protected void onDraw(Canvas canvas) {
			// TODO Auto-generated method stub
			super.onDraw(canvas);

			switch(currentEmotion)
			{
			case Normal:
				canvas.drawColor(Color.WHITE);
				break;
			case Happy:
				canvas.drawColor(Color.GREEN);
				break;
			case Angry:
				canvas.drawColor(Color.RED);
				break;
			}     

			pnt.setStyle(Style.FILL);
			pnt.setColor(Color.YELLOW);
			canvas.drawCircle(deviceWidth/2, deviceHeight/5*2, radius, pnt);
			pnt.setColor(Color.BLACK);
			canvas.drawCircle(deviceWidth/2-eyeRadius*2, deviceHeight/5*2-eyeRadius, eyeRadius, pnt);
			canvas.drawCircle(deviceWidth/2+eyeRadius*2, deviceHeight/5*2-eyeRadius, eyeRadius, pnt);
			pnt.setStrokeWidth(10);
			pnt.setStyle(Style.STROKE);

			rect.set(deviceWidth/2-eyeRadius*3,mouseTop,deviceWidth/2+eyeRadius*3,mouseBottom);                            
			canvas.drawArc(rect, startAngle, 120, false, pnt);

		}

		protected class EmotionAnimation extends Animation {
			EnumEmotion mEmotion;
			EnumEmotion prevEmotion;

			float normalStart = 30f;
			float happyStart = 30f;
			float angryStart = 210f;

			float normalTop = deviceHeight/5*2+eyeRadius*3;
			float happyTop = deviceHeight/5*2;
			float angryTop = deviceHeight/5*2+eyeRadius*3;

			float normalBottom = deviceHeight/5*2+eyeRadius*3;
			float happyBottom = deviceHeight/5*2+eyeRadius*3;
			float angryBottom = deviceHeight/5*2+eyeRadius*6;

			float curTop;
			float curBottom;

			public EmotionAnimation(EnumEmotion etype) {
				mEmotion = etype;

				setDuration(1000);
				setInterpolator(new LinearInterpolator());
			}

			public void changeEmotion(EnumEmotion etype) {
				prevEmotion = mEmotion;
				curTop = emotion.mouseTop;
				curBottom = emotion.mouseBottom;
				mEmotion = etype;
			}

			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {
				// TODO Auto-generated method stub
				float fullTime = interpolatedTime * 2;

				switch(mEmotion)
				{
				case Normal:
					if(fullTime < 1.f) {
						emotion.mouseTop = curTop * (1-fullTime) + normalTop * fullTime;
						emotion.mouseBottom = curBottom * (1-fullTime) + normalBottom * fullTime;
					}	            	        	
					break;
				case Happy:
					if(prevEmotion == EnumEmotion.Normal) {
						if(fullTime < 1.f) {
							emotion.mouseTop = curTop * (1-fullTime) + happyTop * fullTime;
							emotion.startAngle = happyStart;		
						}
					}
					else {
						if(fullTime < 1.f) {
							emotion.mouseBottom = curBottom * (1-fullTime) + happyBottom * fullTime;
						}
						else {
							emotion.mouseTop = curTop * (2-fullTime) + happyTop * (fullTime-1);
							emotion.startAngle = happyStart;
						}
					}	            	
					break;
				case Angry:
					if(prevEmotion == EnumEmotion.Normal) {
						if(fullTime < 1.f) {
							emotion.mouseBottom = curBottom * (1-fullTime) + angryBottom * fullTime;
							emotion.startAngle = angryStart;
						}
					}
					else {
						if(fullTime < 1.f) {
							emotion.mouseTop = curTop * (1-fullTime) + angryTop * fullTime;
						}
						else {
							emotion.mouseBottom = curBottom * (2-fullTime) + angryBottom * (fullTime-1);
							emotion.startAngle = angryStart;
						}
					}	
					break;
				}   
				invalidate();
			}

		}
	}

}
