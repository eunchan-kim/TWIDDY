package com.example.twiddy_ui;

import java.util.ArrayList;
import java.util.Arrays;

import android.util.Log;

public class TextHandler {
	private static final ArrayList<String> calling_twiddy = new ArrayList<String>( 
			Arrays.asList("트위디", "jd", "jdc", "돼지", "트위기", "트위지", "트위티", "treaty", "트위터", "트리디비", "테디", "데디", "태디", "떄디") );
	private static final ArrayList<String> yes = new ArrayList<String>( 
			Arrays.asList("응", "있어", "있다고", "있다구" ,"어", "허", "러", "여", "으", "알았어", "오케이", "OK", "ok", "Ok", "yes", "그래", "그랩", "이레", "구래", "그레", "구레") );
	private static final ArrayList<String> no = new ArrayList<String>( 
			Arrays.asList("아니", "하니", "알리", "아내", "아미", "노", "no", "No", "NO", "싫어", "실어", "시러", "없어", "업어", "업서", "없다구", "없다고") );
	private static final ArrayList<String> hi = new ArrayList<String>( 
			Arrays.asList("안녕", "안영", "하이", "hi", "Hi", "안녕하세요", "헤이", "어이") );
	private static final ArrayList<String> compliment = new ArrayList<String>( 
			Arrays.asList("잘했어", "옳지", "Good", "굳") );
	private static final ArrayList<String> detention = new ArrayList<String>( 
			Arrays.asList("넌나빠", "넌못됐어", "싫어", "넌싫어", "나빠", "못됐어", "안돼") );
	private static final ArrayList<String> who = new ArrayList<String>( 
			Arrays.asList("누구야", "누구니", "넌누구니", "넌누구야", "너는누구니", "누구냐넌") );
	private static final ArrayList<String> where = new ArrayList<String>( 
			Arrays.asList("어디서왔어", "어디서왔니", "너는어디서왔니",  "너는어디서왔어", "넌어디서왔니", "넌어디왔니", "넌어디서태어났니", "난어디서태어났니", "너는어디서태어났니") );
	private static final ArrayList<String> what = new ArrayList<String>( 
			Arrays.asList("뭐해", "뭐하니", "뭐하는중이야", "머해") );
	
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

	public static EnumCommand checkCommandExtend(String msg) {
		String cmd = msg.trim().replaceAll("\\s","");
		Log.e("checkCommand", cmd);
		if (calling_twiddy.contains(cmd) ) {
			return EnumCommand.startRecording;
		}
		if (hi.contains(cmd) ) {
			return EnumCommand.hi;
		}
		if (compliment.contains(cmd) ) {
			return EnumCommand.compliment;
		}
		if (detention.contains(cmd) ) {
			return EnumCommand.detention;
		}
		if (who.contains(cmd) ) {
			return EnumCommand.who;
		}
		if (where.contains(cmd) ) {
			return EnumCommand.where;
		}
		if (what.contains(cmd) ) {
			return EnumCommand.what;
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
		return feed.substring(idx+1);
	}
	
	public static String getSender(String feed) {
		int idx = feed.indexOf(" ", 1);
		return feed.substring(0, idx);
	}
	
}