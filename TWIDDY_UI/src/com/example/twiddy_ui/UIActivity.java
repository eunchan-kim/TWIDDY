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
		
		btn_start.setOnClickListener(this);
	}
	
   

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.start_btn:
			Intent it_twitter = new Intent(this, TwitLogin.class);
			startActivity(it_twitter);
			break;
		}
	}
}
