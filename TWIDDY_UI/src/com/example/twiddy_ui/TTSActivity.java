package com.example.twiddy_ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import net.daum.mf.speech.api.TextToSpeechClient;
import net.daum.mf.speech.api.TextToSpeechListener;
import net.daum.mf.speech.api.TextToSpeechManager;


public class TTSActivity extends Activity implements OnClickListener, TextToSpeechListener{
	private TextToSpeechClient tts_client;
	public static String NEWTONE_API_KEY = "dcd2a896fab93d17a09e2d752ef0e145";
	
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
	}

	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.btn_read:
			EditText content_view = (EditText) findViewById(R.id.tts_content);
			String content_text = content_view.getText().toString();
			Log.d("msg", content_text);
			this.tts_client.play(content_text);
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		TextToSpeechManager.getInstance().finalizeLibrary();
	}

	@Override
	public void onError(int arg0, String arg1) {
		Log.e(String.format("Play error %d", arg0), arg1);
	}

	@Override
	public void onFinished() {
		// TODO Auto-generated method stub	
	}

}
