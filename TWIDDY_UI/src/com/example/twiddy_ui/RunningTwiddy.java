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

	public RunningState state = RunningState.waiting;
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

	public void getNewMention(String sender, String msg) {
		this.state = RunningState.askToRead;
		this.alarmedMsg = TextHandler.feedToSentence(msg);			
		this.parent.performTTS(sender + "한테서 트윗이 왔어. 읽을까?");
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
			Log.e("HANDLESTT", "ERROR_STT");
			if (this.errCount > 3) {
				Log.e("HANDLESTT", "reset");
				reset();
			} else {
				Log.e("HANDLESTT", "errCount++");
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
			this.parent.performTTS("업로드 했어.");
			this.parent.showEnumEmotion(EnumEmotion.Happy);
			break;
		default:
			Log.e("ERROR", runningState(this.state) + " state does not handles endedUpload()");
		}
	}

	private void STTtransitionFromWating(String msg) {
		EnumCommand cmdCode = TextHandler.checkCommandExtend(msg);
		int rand;
		switch (cmdCode) {
		case startRecording:
			this.state = RunningState.startRecording;
			this.parent.performTTS("불렀어?");
			this.parent.showEnumEmotion(EnumEmotion.Happy);
			break;
		case hi:
			rand = (int)(Math.random()*3);
			switch(rand) {
			case 0:
				this.parent.performTTS("반가워!");
				break;
			case 1:
				this.parent.performTTS("안녕!");
				break;
			case 2:
				this.parent.performTTS("방가 방가!");
				break;
			}			
			this.parent.showEnumEmotion(EnumEmotion.Start);
			break;
		case compliment:
			rand = (int)(Math.random()*3);
			switch(rand) {
			case 0:
				this.parent.performTTS("고마워!");
				break;
			case 1:
				this.parent.performTTS("이 정도 쯤이야!");
				break;
			case 2:
				this.parent.performTTS("흥, 딱히 너를 위해서 한건 아니라구.");
				break;
			}
			this.parent.showEnumEmotion(EnumEmotion.Happy);
			break;
		case detention:
			rand = (int)(Math.random()*3);
			switch(rand) {
			case 0:
				this.parent.performTTS("히잉 미안해");
				break;
			case 1:
				this.parent.performTTS("내가 잘못했어");
				break;
			case 2:
				this.parent.performTTS("훌쩍 훌쩍");
				break;
			}
			this.parent.showEnumEmotion(EnumEmotion.Angry);
			break;
		case who:
			rand = (int)(Math.random()*3);
			switch(rand) {
			case 0:
				this.parent.performTTS("나는 너의 친구 테디베어야");
				break;
			case 1:
				this.parent.performTTS("나는 자바 코드로 만들어진 트위디야. 가비지 컬렉터가 일품이지 후후");
				break;
			case 2:
				this.parent.performTTS("나는 구십구 퍼센트의 코드와 일 퍼센트의 버그로 이루어져있어.");
				break;
			}
			this.parent.showEnumEmotion(EnumEmotion.Explain);
			break;
		case where:
			rand = (int)(Math.random()*3);
			switch(rand) {
			case 0:
				this.parent.performTTS("나는 카이스트에서 태어났어.");
				break;
			case 1:
				this.parent.performTTS("나는 전산학프로젝트에서 태어났어.");
				break;
			case 2:
				this.parent.performTTS("나는 엔원에서 태어났어.");
				break;
			}			
			this.parent.showEnumEmotion(EnumEmotion.Explain);
			break;
		case what:
			rand = (int)(Math.random()*3);
			switch(rand) {
			case 0:
				this.parent.performTTS("너의 이야기를 듣고있어.");
				break;
			case 1:
				this.parent.performTTS("보면 모르니?");
				break;
			case 2:
				this.parent.performTTS("여기서 빠져나갈 방법을 찾고있어");
				break;
			}
			this.parent.showEnumEmotion(EnumEmotion.Explain);
			break;
		case yes:
		case no:
		case none:
			this.parent.performTTS("뭐라고 했어?");
			this.parent.showEnumEmotion(EnumEmotion.Normal);
			break;
		
		}
	}

	private void STTtransitionFromRecording(String msg) {
		this.state = RunningState.askToUpload;
		this.uploadMsg = TextHandler.sentenceToFeed(msg);
		this.parent.performTTS(msg + " 라고 들었는데 트위터에 올릴까?");
		this.parent.showEnumEmotion(EnumEmotion.Normal);
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
		this.parent.performSTT();
	}

	private void STTtransitionFromAnsweringUpload(String msg) {
		EnumCommand cmdCode = TextHandler.checkCommand(msg);
		switch (cmdCode) {
		case yes:
			this.parent.uploadTweet(this.uploadMsg);
			this.state = RunningState.upload;				
			break;		
		case no:
			this.reset();
			this.state = RunningState.askAgain;
			this.parent.performTTS("다른 할말 있어?");
			break;
		case startRecording:
		case none:
			this.state = RunningState.askToUpload;
			this.parent.performTTS("트위터에 올려?");
			break;
		}
	}

	private void STTtransitionFromAnsweringAgain(String msg) {
		EnumCommand cmdCode = TextHandler.checkCommand(msg);
		switch (cmdCode) {
		case yes:
			this.state = RunningState.startRecording;
			this.parent.performTTS("그럼 뭐라고 올릴까?");
			break;		
		case no:		
			this.reset();
			this.parent.performTTS("올리지 않을게");
			this.parent.showEnumEmotion(EnumEmotion.Normal);
			break;
		case startRecording:
		case none:
			this.state = RunningState.askAgain;
			this.parent.performTTS("다른 할말 있냐구");
			break;			
		}
	}

	private void STTtransitionFromAnsweringRead(String msg) {
		EnumCommand cmdCode = TextHandler.checkCommand(msg);
		switch (cmdCode) {
		case yes:
			this.state = RunningState.readFeed;
			this.parent.showEmotion(this.alarmedMsg);
			this.parent.performTTS(this.alarmedMsg);
			break;		
		case no:
			this.reset();
			this.parent.performTTS("안읽을게");
			break;
		case startRecording:
		case none:
			this.state = RunningState.askToRead;
			this.parent.performTTS("새로 온 트위터를 읽을까?");
			break;
		}

	}
}
