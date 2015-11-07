package com.example.twiddy_ui;

import java.util.ArrayList;

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


public class TTSActivity extends Activity implements OnClickListener, TextToSpeechListener, SpeechRecognizeListener{
	public static String NEWTONE_API_KEY = "dcd2a896fab93d17a09e2d752ef0e145";
	private TextToSpeechClient tts_client;
	private SpeechRecognizerClient stt_client;
	private String result_text = "Ready?";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tts);

		TextToSpeechManager.getInstance().initializeLibrary(getApplicationContext());

		tts_client = new TextToSpeechClient.Builder()
				.setApiKey(NEWTONE_API_KEY)
				.setSpeechSpeed(1.0)
				.setSpeechVoice(TextToSpeechClient.VOICE_WOMAN_READ_CALM)
				.setListener(this)
				.build();

		Button btn_read = (Button) findViewById(R.id.btn_read);
		btn_read.setOnClickListener(this);

		SpeechRecognizerManager.getInstance().initializeLibrary(this);
		stt_client = new SpeechRecognizerClient.Builder()
				.setApiKey(NEWTONE_API_KEY)
				.setServiceType(SpeechRecognizerClient.SERVICE_TYPE_WEB)
				.build();
		stt_client.setSpeechRecognizeListener(this);

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
			this.tts_client.play(content_text);
			break;

		case R.id.btn_hear:
			this.stt_client.startRecording(true);
			break;
		}
	}
	
	private void showReuslt() {
		TextView result_view = (TextView) findViewById(R.id.stt_result);
		result_view.setText(this.result_text);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		TextToSpeechManager.getInstance().finalizeLibrary();
		SpeechRecognizerManager.getInstance().finalizeLibrary();
	}

	/* TextToSpeechListener Methods */
	@Override
	public void onError(int arg0, String arg1) {
		Log.e("Play error " + arg0, arg1);
	}

	@Override
	public void onFinished() {
		// TODO Auto-generated method stub	
	}

	/* SpeechRecognizeListener Methods */
	@Override
	public void onAudioLevel(float arg0) {
//		Log.d("speech", "onAudioLevel: " + arg0);
	}

	@Override
	public void onBeginningOfSpeech() {
		Log.d("speech", "onBeginningOfSpeech");
	}

	@Override
	public void onEndOfSpeech() {
		Log.d("speech", "onEndOfSpeech");
	}

	@Override
	public void onPartialResult(String arg0) {
		Log.d("speech", "onPartialResult: " + arg0);
		
	}

	@Override
	public void onReady() {
		Log.d("speech", "onReady");
	}

	@Override
	public void onResults(Bundle arg0) {
		String res = "NONE";
		ArrayList<String> texts = arg0.getStringArrayList(SpeechRecognizerClient.KEY_RECOGNITION_RESULTS);

		if (texts.size() > 0) {
			this.result_text = texts.get(0);
			res = texts.get(0);
		}
		Log.d("speech", "onResults: " + res);
	}

}
