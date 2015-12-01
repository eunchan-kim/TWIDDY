package com.example.twiddy_ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class UIActivity extends Activity implements OnClickListener{
		
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ui);
		
		Button btn_start = (Button) findViewById(R.id.btn_start);
		Button btn_setting = (Button) findViewById(R.id.btn_setting);
		
		btn_start.setOnClickListener(this);
		btn_setting.setOnClickListener(this);
	}
	
   

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.btn_start:
			Intent it_start = new Intent(this, StartUI.class);
			startActivity(it_start);
			break;
		case R.id.btn_setting:
			
			/*
			Intent it_setting = new Intent(this, SettingUI.class);
			startActivity(it_setting);
			*/
			break;
		}
	}
}
