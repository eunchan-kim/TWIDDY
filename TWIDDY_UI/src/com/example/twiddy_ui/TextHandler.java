package com.example.twiddy_ui;

import java.util.ArrayList;
import java.util.Arrays;

import android.util.Log;

public class TextHandler {
	private static final ArrayList<String> calling_twiddy = new ArrayList<String>( 
			Arrays.asList("트위디","트위지", "트위티", "treaty", "트위터", "트리디비", "테디") );
	private static final ArrayList<String> yes = new ArrayList<String>( 
			Arrays.asList("응", "으", "알았어", "오케이", "OK", "ok", "Ok", "yes") );
	private static final ArrayList<String> no = new ArrayList<String>( 
			Arrays.asList("아니", "아미", "노", "no", "No", "NO") );

	public static EnumCommand checkCommand(String msg) {
		String cmd = msg.trim();
		Log.e("checkCommand", cmd);
		if (calling_twiddy.contains(cmd) ) {
			return EnumCommand.startRecording;
		}
		if (yes.contains(cmd) ) {
			return EnumCommand.yes;
		}
		if (no.contains(cmd) ) {
			return EnumCommand.no;
		}
				
		return EnumCommand.none;
	}
	
	public static String sentenceToFeed(String sentence) {
		//TODO
		return sentence;
	}
	
	public static String feedToSentence(String feed) {
		int idx = feed.indexOf(" ", 1);
		return feed.substring(idx);
	}
	
	public static String getSender(String feed) {
		int idx = feed.indexOf(" ", 1);
		return feed.substring(0, idx);
	}
	
	public static String askToUpload(String msg) {
		return "당신은 " + msg + " 라고 말했습니다. 트위터에 업로드 할까요?";
	}
	
	public static String askToRead(String sender) {
		return sender + " 님이 당신에게 멘션을 보냈습니다. 읽을까요?";
	}
	
}