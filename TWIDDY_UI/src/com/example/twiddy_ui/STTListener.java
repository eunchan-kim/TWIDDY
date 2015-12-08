/*
 * Use Newtone Library (GNU LGPL License) from
 * https://developers.daum.net/services/apis/newtone
 * For voice recognition
 * 
 * Main developers: 류연희
 * Debuggers: 류연희
 */
package com.example.twiddy_ui;

import java.util.ArrayList;

import android.os.Bundle;
import android.util.Log;
import net.daum.mf.speech.api.SpeechRecognizeListener;
import net.daum.mf.speech.api.SpeechRecognizerClient;

public class STTListener implements SpeechRecognizeListener{
	private DisplayEmotion parent;
	private String result = "";
	
	public STTListener(DisplayEmotion _parent) {
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
		Log.d("speech", "onResult: " + res);
	}

	@Override
	public void onError(int code, String msg) {
		Log.e("STT Error: " + code, msg);
		this.parent.showSTTReuslt(RunningTwiddy.ERROR_STT);
	}

	@Override
	public void onFinished() {
		Log.d("STT Finished", "onFinished");
		this.parent.showSTTReuslt(this.result);
	}
}
