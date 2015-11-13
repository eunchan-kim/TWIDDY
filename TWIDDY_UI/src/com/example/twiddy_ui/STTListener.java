package com.example.twiddy_ui;

import java.util.ArrayList;

import android.os.Bundle;
import android.util.Log;
import net.daum.mf.speech.api.SpeechRecognizeListener;
import net.daum.mf.speech.api.SpeechRecognizerClient;

public class STTListener implements SpeechRecognizeListener{
	private VoiceActivity parent;
	private String result = "";
	
	public STTListener(VoiceActivity _parent) {
		this.parent = _parent;
	}

	/* SpeechRecognizeListener Methods */
	@Override
	public void onAudioLevel(float arg0) {
//		Log.d("speech", "onAudioLevel: " + arg0);
	}

	@Override
	public void onBeginningOfSpeech() {
	}

	@Override
	public void onEndOfSpeech() {
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
	public void onResults(Bundle result) {
		String res = "NONE";
		ArrayList<String> texts = result.getStringArrayList(SpeechRecognizerClient.KEY_RECOGNITION_RESULTS);

		if (texts.size() > 0) {
			res = texts.get(0);
		}
		this.result = res;
	}

	@Override
	public void onError(int code, String msg) {
		Log.e("STT Error: " + code, msg);
	}

	@Override
	public void onFinished() {
		Log.d("STT Finished", "onFinished");
		this.parent.showSTTReuslt(this.result);
	}
}
