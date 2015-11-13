package com.example.twiddy_ui;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import net.daum.mf.speech.api.SpeechRecognizeListener;
import net.daum.mf.speech.api.SpeechRecognizerClient;
import net.daum.mf.speech.api.SpeechRecognizerManager;
import net.daum.mf.speech.api.TextToSpeechClient;
import net.daum.mf.speech.api.TextToSpeechListener;
import net.daum.mf.speech.api.TextToSpeechManager;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class DisplayEmotion  extends Activity implements OnClickListener{
	public static String NEWTONE_API_KEY = "dcd2a896fab93d17a09e2d752ef0e145";
	private TextToSpeechClient tts_client;
	private SpeechRecognizerClient stt_client;
	private RunningTwiddy twiddy;
	
	Emotion emotion;
	
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
		
		emotion = new Emotion(this);
		emotion.changeEmotion(EnumEmotion.Normal);
		FrameLayout frame = (FrameLayout)findViewById(R.id.layout_display);
		frame.addView(emotion, 0);
		/* Login to Twitter */
		Intent intent = getIntent();
		final Twitter twitter = (Twitter) intent.getExtras().get("twitter");
		
		final Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg){
			    emotion.changeEmotion(EnumEmotion.values()[msg.what]);
			}
		};
		
		Thread thread = new Thread(new Runnable(){
		    @Override
		    public void run() {
		    	try {
					List<twitter4j.Status> statuses = twitter.getHomeTimeline();
					Log.d("TIMELINE","Showing home timeline.");
				    for (twitter4j.Status status : statuses) {
				        Log.d("TIMELINE",status.getUser().getName() + ":" +
				                           status.getText());
				    }	 
				    try{
				    	while(true){
						    Thread.sleep(2000);
							handler.sendEmptyMessage(EnumEmotion.Happy.ordinal());
							Thread.sleep(2000);
							handler.sendEmptyMessage(EnumEmotion.Normal.ordinal());
							Thread.sleep(2000);
							handler.sendEmptyMessage(EnumEmotion.Angry.ordinal());
				    	}
				    }
				    catch(InterruptedException e) {}
				    
				} catch (TwitterException te) {
		        	if (401 == te.getStatusCode()) {
		        		System.out.println("Unable to get the access token.");
		        	} else {
		        		te.printStackTrace();
		        	}
		        }
		    }
		});
		thread.start();
		
		
		/* Voice Related */

		this.twiddy = new RunningTwiddy(this);
		
		/* Set TTS Module */
		TextToSpeechManager.getInstance().initializeLibrary(getApplicationContext());
		TextToSpeechListener tts_listener = new TTSListener(this);
		this.tts_client = new TextToSpeechClient.Builder()
				.setApiKey(NEWTONE_API_KEY)
				.setSpeechSpeed(1.0)
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
		btn_hear.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.btn_normal:
			emotion.changeEmotion(EnumEmotion.Normal);
			break;
		case R.id.btn_happy:
			emotion.changeEmotion(EnumEmotion.Happy);
			break;
		case R.id.btn_angry:
			emotion.changeEmotion(EnumEmotion.Angry);
			break;
		case R.id.btn_hear:
			this.performSTT();
			break;
		}
	}
	
	/* Voice Activity Methods */
	public void performTTS(String msg) {
		Log.e("TTS", msg);
		this.tts_client.play(msg);
	}
	
	public void performSTT() {
		Log.e("STT", "start");
		this.stt_client.startRecording(false);
	}

	public void showSTTReuslt(final String result_text) {
		this.runOnUiThread(new Runnable() {
			public void run() {				
				twiddy.handleResult(result_text);
			}
		});
	}
	
	public void handleTTSResult() {
		twiddy.handleResult(RunningTwiddy.ENDED_TTS);
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
