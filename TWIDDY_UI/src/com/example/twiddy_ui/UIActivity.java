/*
 * Main developers: 한주형
 * Debuggers: 한주형
 */
package com.example.twiddy_ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class UIActivity extends Activity implements OnClickListener{
		
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ui);
		ImageButton btn_start = (ImageButton) findViewById(R.id.start_btn); 
		ImageButton btn_login = (ImageButton) findViewById(R.id.login_btn);
		
		btn_start.setOnClickListener(this);
		btn_login.setOnClickListener(this);
	}
	
   

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.start_btn:
			Intent it_login_twitter = new Intent(this, TwitLogin.class);
			it_login_twitter.putExtra("login", false);
			startActivity(it_login_twitter);
			break;
		case R.id.login_btn:
			Intent it_start_twitter = new Intent(this, TwitLogin.class);
			it_start_twitter.putExtra("login", true);
			startActivity(it_start_twitter);
			break;
		}
	}
}
