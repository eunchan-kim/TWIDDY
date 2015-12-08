/*
 * Main developers: ·ù¿¬Èñ
 * Debuggers: ·ù¿¬Èñ
 */
package com.example.twiddy_ui;

import android.util.Log;
import net.daum.mf.speech.api.TextToSpeechListener;

public class TTSListener implements TextToSpeechListener {
	private DisplayEmotion parent;
	
	public TTSListener(DisplayEmotion _parent) {
		this.parent = _parent;
	}
	/* TextToSpeechListener Methods */
	@Override
	public void onError(int code, String msg) {
		Log.e("Play error " + code, msg);
	}

	@Override
	public void onFinished() {
		this.parent.handleTTSResult();
	}

}
