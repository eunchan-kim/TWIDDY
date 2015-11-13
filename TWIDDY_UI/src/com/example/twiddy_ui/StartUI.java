package com.example.twiddy_ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

public class StartUI extends Activity implements OnClickListener{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_startui);
		ImageButton imgbtn_facebook = (ImageButton)findViewById(R.id.imgbtn_facebook);
		ImageButton imgbtn_twitter = (ImageButton)findViewById(R.id.imgbtn_twitter);
		
		imgbtn_facebook.setOnClickListener(this);
		imgbtn_twitter.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.imgbtn_facebook:
			Intent it_facebook = new Intent(this, LoginUI.class);
			it_facebook.putExtra("type", EnumSNS.facebook);
			startActivity(it_facebook);
			break;
		case R.id.imgbtn_twitter:
			Intent it_twitter = new Intent(this, TwitLogin.class);
			it_twitter.putExtra("type", EnumSNS.twitter);
			startActivity(it_twitter);
			break;
		}
	}

}
