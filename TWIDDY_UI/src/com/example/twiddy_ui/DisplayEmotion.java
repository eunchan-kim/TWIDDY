/*
 * Main developers: 한주형, 류연희
 * Debuggers: 한주형, 류연희
 */
package com.example.twiddy_ui;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
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
	private String last_mention;
	private String last_sender;
	
	MentionThread(DisplayEmotion _parent) {
		this.parent = _parent;
		this.last_mention = "";
		this.last_sender = "";
	}

	@Override
	public void run() {
		try {
			List<twitter4j.Status> statuses = this.parent.twitter.getMentionsTimeline();			
			Log.e("MENTION","Get new mention");
			if (statuses.isEmpty() == false) {		
				
				String current_mention = statuses.get(0).getText();
				
				if (last_mention.equals("")) {
					last_mention = current_mention;	
				} else if (!last_mention.equals(current_mention)) {
					last_mention = current_mention;	
					last_sender = statuses.get(0).getUser().getName();
					MentionInfo minfo = new MentionInfo(last_mention, last_sender);
					parent.qLock.lock();
					Log.e("Mention Queue", "ENQUEUE 1");
					parent.mentionQ.offer(minfo);
					parent.qLock.unlock();
					Log.e("MENTION", "last sender : " + last_sender);
					Log.e("MENTION", "last mention : " + last_mention);
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

class MentionInfo {
	public String mention;
	public String sender;
	MentionInfo(String _m, String _s) {
		this.mention = _m;
		this.sender = _s;
	}
}

public class DisplayEmotion  extends Activity implements OnClickListener{
	public static String NEWTONE_API_KEY2 = "dcd2a896fab93d17a09e2d752ef0e145"; // 류연희
	public static String NEWTONE_API_KEY = "fe89703bb8fb3756c8c0d612785cbb0b"; // 한주형
	private TextToSpeechClient tts_client;
	private SpeechRecognizerClient stt_client;
	public RunningTwiddy twiddy;
	public Twitter twitter;
	private MentionThread mention_thread;
	private Timer mention_timer;
	private Timer debug_thread;
	private static final int REQUEST_CONNECT_DEVICE = 1;
	private static final int REQUEST_ENABLE_BT = 2;
	private BluetoothService btService = null;
	private TextView recording;
	private int waitCnt = 0;
	public boolean isMentionStored = false;
	public Queue<MentionInfo> mentionQ;
	public Lock qLock;
	private LinearLayout bg;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display);
		
		LinearLayout bg = (LinearLayout) findViewById(R.id.display_background);
		bg.setBackgroundResource(R.drawable.normal);
				
		mentionQ = new LinkedList<MentionInfo>();
		qLock = new ReentrantLock();
		
		recording = (TextView)findViewById(R.id.txt_recording);
		recording.setVisibility(View.GONE);


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
		this.mention_thread = new MentionThread(this);
		this.mention_timer = new Timer();
		this.mention_timer.scheduleAtFixedRate(this.mention_thread, 0, 120000); // 2 min

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
				if(waitCnt > 1)	{
					Log.e("STT ERROR", "restart!");
					if (!twiddy.isStop()) {
						stt_client.stopRecording();
						stt_client.startRecording(false);
					}
				}
				
				if(twiddy.state == RunningState.waiting) {
					qLock.lock();
					if(mentionQ.peek() != null) {
						Log.e("Mention Queue", "POP 1");
						MentionInfo minfo = (MentionInfo)mentionQ.poll();
						getNewMention(minfo.sender, minfo.mention);
					}
					qLock.unlock();					
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

	public void getNewMention(final String sender, final String msg) {
		this.runOnUiThread(new Runnable() {
			public void run() {
				twiddy.getNewMention(sender, msg);
			}
		});		
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
					bg = (LinearLayout) findViewById(R.id.display_background);
					bg.setBackgroundResource(R.drawable.normal);
					sendMessage("n");
					break;
				case Happy:
					bg = (LinearLayout) findViewById(R.id.display_background);
					bg.setBackgroundResource(R.drawable.happy);
					sendMessage("h");
					break;
				case Angry:
					bg = (LinearLayout) findViewById(R.id.display_background);
					bg.setBackgroundResource(R.drawable.angry);
					sendMessage("a");
					break;
				case Start:
					bg = (LinearLayout) findViewById(R.id.display_background);
					bg.setBackgroundResource(R.drawable.happy);
					sendMessage("s");
					break;
				case Explain:
					bg = (LinearLayout) findViewById(R.id.display_background);
					bg.setBackgroundResource(R.drawable.happy);
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
							showEnumEmotion(EnumEmotion.Normal);
						} else if (score > 0) {
							showEnumEmotion(EnumEmotion.Happy);
						} 
						else if (score < 0) {
							showEnumEmotion(EnumEmotion.Angry);
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
		this.mention_timer.cancel();
		this.mention_timer.purge();
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

}
