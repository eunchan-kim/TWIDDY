package com.example.twiddy_ui;

import java.util.List;

import android.util.Log;
import twitter4j.Twitter;
import twitter4j.TwitterException;

enum RunningState {
	stop,
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
	public static String ERROR_STT = "@@ERROR-STT@@";

	private RunningState state = RunningState.waiting;
	private DisplayEmotion parent;
	private Twitter twitter;
	private String lastmentold;
	private String lastmentnew;

	private String uploadMsg = "";
	private String alarmedMsg = "";

	private boolean valid_transition = false;

	public RunningTwiddy(DisplayEmotion _parent, Twitter _twitter, String _lastment) {
		this.parent = _parent;
		this.twitter = _twitter;
		this.lastmentold = _lastment;
	}

	private void reset() {
		this.uploadMsg = "";
		this.alarmedMsg = "";
		this.valid_transition = false;
		this.state = RunningState.waiting;
	}

	public void stop() {
		Log.e("stop", "stop()");
		this.state = RunningState.stop;
	}

	public boolean isRunning() {
		return this.state != RunningState.stop;
	}

	public void handleResult(String msg) {
		if (isRunning() && msg.equals(ERROR_STT)) {
			reset();
			this.parent.performSTT();
			return;
		}
		switch (this.state) {
		case stop:
			Log.e("state", "stop");
			break;
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
			if (valid_transition) {
				state = RunningState.recording;
				parent.performSTT();
			} else {
				parent.performSTT();
			}			
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
				this.parent.performTTS("뭐라고하셨죠?.");
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
				Thread thread = new Thread(new Runnable(){
					@Override
					public void run() {
						try {
							twitter.updateStatus(uploadMsg); 						    
						} catch (TwitterException te) {
							if (401 == te.getStatusCode()) {
								Log.d("update","Unable to get the access token.");
							} else {
								te.printStackTrace();
							}
						}
						finally {
							handleResult("");
						}
					
						Log.e("uploading", uploadMsg);
					}
				});
				thread.start();
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
		this.parent.performTTS("업로드가 완료되었습니다.");
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
				this.parent.performTTS("�뾽濡쒕뱶瑜� 痍⑥냼�빀�땲�떎.");
				break;
			}
		}
	}

	private void transitionFromReadFeed(String msg) {
		this.reset();
		this.parent.performTTS("Bye~");
	}
}
