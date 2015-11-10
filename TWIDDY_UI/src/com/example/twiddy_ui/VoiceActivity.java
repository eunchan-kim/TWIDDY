package com.example.twiddy_ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import net.daum.mf.speech.api.SpeechRecognizeListener;
import net.daum.mf.speech.api.SpeechRecognizerClient;
import net.daum.mf.speech.api.SpeechRecognizerManager;
import net.daum.mf.speech.api.TextToSpeechClient;
import net.daum.mf.speech.api.TextToSpeechListener;
import net.daum.mf.speech.api.TextToSpeechManager;


public class VoiceActivity extends Activity implements OnClickListener {
	public static String NEWTONE_API_KEY = "dcd2a896fab93d17a09e2d752ef0e145";
	private TextToSpeechClient tts_client;
	private SpeechRecognizerClient stt_client;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tts);

		/* Set TTS Module */
		TextToSpeechManager.getInstance().initializeLibrary(getApplicationContext());
		TextToSpeechListener tts_listener = new TTSListener(this);
		tts_client = new TextToSpeechClient.Builder()
				.setApiKey(NEWTONE_API_KEY)
				.setSpeechSpeed(1.0)
				.setSpeechVoice(TextToSpeechClient.VOICE_WOMAN_DIALOG_BRIGHT)
				.setListener(tts_listener)
				.build();

		Button btn_read = (Button) findViewById(R.id.btn_read);
		btn_read.setOnClickListener(this);

		/* Set STT Module */
		SpeechRecognizerManager.getInstance().initializeLibrary(this);
		SpeechRecognizeListener stt_listener = new STTListener(this);
		stt_client = new SpeechRecognizerClient.Builder()
				.setApiKey(NEWTONE_API_KEY)
				.setServiceType(SpeechRecognizerClient.SERVICE_TYPE_WEB)
				.setGlobalTimeOut(60)
				.build();
		stt_client.setSpeechRecognizeListener(stt_listener);

		Button btn_hear = (Button) findViewById(R.id.btn_hear);
		btn_hear.setOnClickListener(this);		
	}

	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.btn_read:
			EditText content_view = (EditText) findViewById(R.id.tts_content);
			String content_text = content_view.getText().toString();
			this.performTTS(content_text);
			break;

		case R.id.btn_hear:
			this.performSTT();
			break;
		}
	}
	
	private void performTTS(String msg) {
		Log.e("TTS", msg);
		this.tts_client.play(msg);
	}
	
	private void performSTT() {
		this.stt_client.startRecording(false);
	}

	public void showReuslt(final String result_text) {
		this.runOnUiThread(new Runnable() {
			public void run() {
				TextView result_view = (TextView) findViewById(R.id.stt_result);
				result_view.setText(result_text);
				
				performTTS(result_text);
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		TextToSpeechManager.getInstance().finalizeLibrary();
		SpeechRecognizerManager.getInstance().finalizeLibrary();
	}

}
