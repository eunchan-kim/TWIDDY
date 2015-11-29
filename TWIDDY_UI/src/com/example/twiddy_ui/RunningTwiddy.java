package com.example.twiddy_ui;

import android.util.Log;
import twitter4j.Twitter;
import twitter4j.TwitterException;

enum RunningState {
	stop,
	waiting,
	/* upload related */
	startRecording,
	recording,
	askToUpload,
	answeringUpload,
	upload,
	askAgain,
	answeringAgain,
	/* mention related */
	askToRead,
	answeringRead,
	readFeed,
}

public class RunningTwiddy {
	public static String ERROR_STT = "@@ERROR-STT@@";

	private RunningState state = RunningState.waiting;
	private DisplayEmotion parent;

	private String uploadMsg = "";
	private String alarmedMsg = "";

	private int errCount = 0;

	public RunningTwiddy(DisplayEmotion _parent) {
		this.parent = _parent;
	}

	public String getStatus() {
		return runningState(this.state);
	}

	private String runningState(RunningState state) {
		switch (this.state) {
		case stop:
			return "stop";
		case waiting:
			return "waiting";
		case startRecording:
			return "startRecording";
		case recording:
			return "recording";
		case askToUpload:
			return "askToUpload";
		case answeringUpload:
			return "answeringUpload";
		case upload:
			return "upload";
		case askAgain:
			return "askAgain";
		case answeringAgain:
			return "answeringAgain";
		case askToRead:
			return "askToRead";
		case answeringRead:
			return "answeringRead";
		case readFeed:
			return "readFeed";
		}
		return "None";
	}

	private void reset() {
		this.uploadMsg = "";
		this.alarmedMsg = "";
		this.state = RunningState.waiting;
		this.errCount = 0;
	}

	public void stop() {
		this.state = RunningState.stop;
	}

	public boolean isStop() {
		return this.state == RunningState.stop;
	}

	public void getNewMention(String msg) {
		if (this.state == RunningState.waiting) {
			this.state = RunningState.askToRead;
			this.alarmedMsg = TextHandler.feedToSentence(msg);
			this.parent.showEmotion(this.alarmedMsg);
			this.parent.performTTS(TextHandler.getSender(msg) + "님으로 부터 멘션이 도착했습니다. 읽을까요?");
		}
	}

	public void handleTTSResult() {
		switch (this.state) {
		case stop:
			Log.e("state", "stop");
			break;
		case waiting:
			Log.e("state", "waiting");
			TTStransitionFromWaiting();
			break;
		case startRecording:
			Log.e("state", "startRecording");
			TTStransitionFromStartRecording();
			break;
		case askToUpload:
			Log.e("state", "askToUpload");
			TTStransitionFromAskToUpload();
			break;
		case askAgain:
			Log.e("state",  "askAgain");
			TTStransitionFromAskAgain();
			break;
		case askToRead:
			Log.e("state", "askToRead");
			TTStransitionFromAskToRead();
			break;
		case readFeed:
			Log.e("state", "readFeed");
			TTStransitionFromReadFeed();
			break;
		default:
			Log.e("ERROR", runningState(this.state) + " State does not handle the TTS");
		}
	}

	public void handleSTTResult(String msg) {
		if (!isStop() && msg.equals(ERROR_STT)) {
			if (this.errCount > 3) {
				reset();
			} else {
				this.errCount++;
			}
			this.parent.performSTT();
			return;
		}
		switch (this.state) {
		case stop:
			Log.e("state", "stop");
			break;
		case waiting:
			Log.e("state", "waiting");
			STTtransitionFromWating(msg);
			break;
		case recording:
			Log.e("state", "recording");
			STTtransitionFromRecording(msg);
			break;
		case answeringUpload:
			Log.e("state",  "answeringUpload");
			STTtransitionFromAnsweringUpload(msg);
			break;
		case answeringAgain:
			Log.e("state",  "answeringAgain");
			STTtransitionFromAnsweringAgain(msg);
			break;
		case answeringRead:
			Log.e("state",  "answeringRead");
			STTtransitionFromAnsweringRead(msg);
			break;
		default:
			Log.e("ERROR", runningState(this.state) + " State does not handle the STT msg: " + msg);
		}

	}

	public void endedUpload() {
		switch (this.state) {
		case upload:
			this.reset();
			this.parent.performTTS("업로드가 완료되었습니다.");
			break;
		default:
			Log.e("ERROR", runningState(this.state) + " state does not handles endedUpload()");
		}
	}

	private void STTtransitionFromWating(String msg) {
		EnumCommand cmdCode = TextHandler.checkCommand(msg);
		switch (cmdCode) {
		case startRecording:
			this.state = RunningState.startRecording;
			this.parent.performTTS("네!");
			break;
		case yes:
		case no:
		case none:
			this.parent.performTTS("뭐라고하셨죠?.");
			break;
		}
	}

	private void STTtransitionFromRecording(String msg) {
		this.state = RunningState.askToUpload;
		this.uploadMsg = TextHandler.sentenceToFeed(msg);
		this.parent.performTTS(TextHandler.askToUpload(msg));
	}

	private void TTStransitionFromWaiting() {
		this.reset();
		this.parent.performSTT();
	}

	private void TTStransitionFromStartRecording() {
		this.state = RunningState.recording;
		this.parent.performSTT();
	}

	private void TTStransitionFromAskToUpload() {
		this.state = RunningState.answeringUpload;
		this.parent.performSTT();
	}

	private void TTStransitionFromAskAgain() {
		this.state = RunningState.answeringAgain;
		this.parent.performSTT();
	}

	private void TTStransitionFromAskToRead() {
		this.state = RunningState.answeringRead;
		this.parent.performSTT();
	}

	private void TTStransitionFromReadFeed() {
		this.reset();
	}

	private void STTtransitionFromAnsweringUpload(String msg) {
		EnumCommand cmdCode = TextHandler.checkCommand(msg);
		switch (cmdCode) {
		case yes:
			this.parent.uploadTweet(this.uploadMsg);
			this.state = RunningState.upload;
			break;
		case startRecording:
		case no:
		case none:
			this.reset();
			this.state = RunningState.askAgain;
			this.parent.performTTS("다른 메시지를 올릴까요?");
			break;
		}
	}

	private void STTtransitionFromAnsweringAgain(String msg) {
		EnumCommand cmdCode = TextHandler.checkCommand(msg);
		switch (cmdCode) {
		case yes:
			this.state = RunningState.recording;
			this.parent.performSTT();
			break;
		case startRecording:
		case no:
		case none:
			this.reset();
			this.parent.performTTS("업로드를 취소합니다.");
			break;
		}
	}

	private void STTtransitionFromAnsweringRead(String msg) {
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
			this.parent.performTTS("Bye~");
			break;
		}

	}
}
