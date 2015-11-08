package com.example.twiddy_ui;

import java.util.ArrayList;

import android.os.Bundle;
import android.util.Log;
import net.daum.mf.speech.api.SpeechRecognizeListener;
import net.daum.mf.speech.api.SpeechRecognizerClient;

public class STTListener implements SpeechRecognizeListener{
	private VoiceActivity parent;
	
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
	}

	@Override
	public void onResults(Bundle arg0) {
		String res = "NONE";
		ArrayList<String> texts = arg0.getStringArrayList(SpeechRecognizerClient.KEY_RECOGNITION_RESULTS);

		if (texts.size() > 0) {
			res = texts.get(0);
		}
		this.parent.showReuslt(res);
	}

	@Override
	public void onError(int code, String msg) {
		Log.e("STT Error: " + code, msg);
	}

	@Override
	public void onFinished() {
	
	}
}
