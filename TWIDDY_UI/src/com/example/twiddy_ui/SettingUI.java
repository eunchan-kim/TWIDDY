package com.example.twiddy_ui;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

public class SettingUI extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		AsyncTask a = new AsyncTask() {
			@Override
			protected Object doInBackground(Object... params) {
				// TODO Auto-generated method stub
				EmotionExtractor.getEmotion("나는 행복합니다.");
				return null;
			}
		};
		
		a.execute();

	}
}
