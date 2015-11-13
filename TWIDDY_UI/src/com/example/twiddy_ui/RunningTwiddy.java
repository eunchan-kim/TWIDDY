package com.example.twiddy_ui;

import android.util.Log;

enum RunningState {
	waiting,
	recording,
	askToUpload,
	uploading,
	gettingAlarm,
	askToRead,
	readFeed,
}

public class RunningTwiddy {
	public static String ENDED_TTS = "@@ENDED-TTS@@";

	private RunningState state = RunningState.waiting;
	private DisplayEmotion parent;

	private String uploadMsg = "";
	private String alarmedMsg = "";
	
	private boolean valid_transition = false;

	public RunningTwiddy(DisplayEmotion _parent) {
		this.parent = _parent;
	}

	private void reset() {
		this.uploadMsg = "";
		this.alarmedMsg = "";
		this.valid_transition = false;
		this.state = RunningState.waiting;
	}

	public void handleResult(String msg) {
		switch (this.state) {
		case waiting:
			Log.e("state", "waiting");
			transitionFromWating(msg);
			break;
		case recording:
			Log.e("state", "recording");
			transitionFromRecording(msg);
			break;
		case askToUpload:
			Log.e("state", "askToUpload");
			transitionFromAskToUpload(msg);
			break;
		case uploading:
			Log.e("state", "uploading");
			transitionFromUploading(msg);
			break;
		case gettingAlarm:
			Log.e("state", "gettingAlarm");
			transitionFromGettingAlarm(msg);
			break;
		case askToRead:
			Log.e("state", "askToRead");
			transitionFromAskToRead(msg);
			break;
		case readFeed:
			Log.e("state", "readFeed");
			transitionFromReadFeed(msg);
			break;
		}

	}

	private void transitionFromWating(String msg) {
		if (msg.equals(ENDED_TTS)) {
			if (this.valid_transition)
				this.state = RunningState.recording;
			this.parent.performSTT();
		} else {
			EnumCommand cmdCode = TextHandler.checkCommand(msg);
			switch (cmdCode) {
			case startRecording:
				this.valid_transition = true;
				this.parent.performTTS("네!");
				break;
			case yes:
			case no:
			case none:
				this.valid_transition = false;
				this.parent.performTTS("뭐라고 하셨죠?.");
				break;
			}
		}
	}

	private void transitionFromRecording(String msg) {
		this.state = RunningState.askToUpload;
		this.uploadMsg = TextHandler.sentenceToFeed(msg);
		this.parent.performTTS(TextHandler.askToUpload(msg));
	}

	private void transitionFromAskToUpload(String msg) {
		if (msg.equals(ENDED_TTS)) {
			this.parent.performSTT();
		} else {
			EnumCommand cmdCode = TextHandler.checkCommand(msg);
			switch (cmdCode) {
			case yes:
				this.state = RunningState.uploading;
				//TODO
				Log.e("uploading", this.uploadMsg);
				break;
			case startRecording:
			case no:
			case none:
				this.reset();
				this.parent.performTTS("업로드를 취소합니다.");
				break;
			}
		}
	}

	private void transitionFromUploading(String msg) {
		this.reset();
		this.parent.performTTS("업로드가 끝났습니다.");
	}

	private void transitionFromGettingAlarm(String msg) {
		this.state = RunningState.askToRead;
		this.alarmedMsg = TextHandler.feedToSentence(msg);
		this.parent.performTTS(TextHandler.askToRead(msg));
	}

	private void transitionFromAskToRead(String msg) {
		if (msg.equals(ENDED_TTS)) {
			this.parent.performSTT();
		} else {
			EnumCommand cmdCode = TextHandler.checkCommand(msg);
			switch (cmdCode) {
			case yes:
				this.state = RunningState.readFeed;
				this.parent.performTTS(this.alarmedMsg);
				break;
			case startRecording:
			case no:
			case none:
				this.reset();
				this.parent.performTTS("업로드를 취소합니다.");
				break;
			}
		}
	}

	private void transitionFromReadFeed(String msg) {
		this.reset();
		this.parent.performTTS("Bye~");
	}
}
