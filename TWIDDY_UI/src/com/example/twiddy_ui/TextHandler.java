package com.example.twiddy_ui;

import java.util.ArrayList;
import java.util.Arrays;

import android.util.Log;

public class TextHandler {
	private static final ArrayList<String> calling_twiddy = new ArrayList<String>( 
			Arrays.asList("Ʈ����","Ʈ����", "Ʈ��Ƽ", "treaty", "Ʈ����", "Ʈ�����", "�׵�") );
	private static final ArrayList<String> yes = new ArrayList<String>( 
			Arrays.asList("��", "��", "�˾Ҿ�", "������", "OK", "ok", "Ok", "yes") );
	private static final ArrayList<String> no = new ArrayList<String>( 
			Arrays.asList("�ƴ�", "�ƹ�", "��", "no", "No", "NO") );

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
		//TODO
		return feed;
	}
	
	public static String askToUpload(String msg) {
		return "����� " + msg + " ��� ���߽��ϴ�. Ʈ���Ϳ� ���ε� �ұ��?";
	}
	
	public static String askToRead(String sender) {
		return sender + " ���� ��ſ��� ����� ���½��ϴ�. �������?";
	}
	
}