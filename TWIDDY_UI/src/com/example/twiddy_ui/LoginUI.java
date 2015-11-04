package com.example.twiddy_ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LoginUI extends Activity implements OnClickListener{
	
	EditText et_email;
	EditText et_password;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loginui);
		Button btn_login = (Button)findViewById(R.id.btn_login);
		et_email = (EditText)findViewById(R.id.et_email);
		et_password = (EditText)findViewById(R.id.et_password);
		Intent intent = getIntent();
		EnumSNS type = (EnumSNS) intent.getExtras().get("type");
		
		switch(type)
		{
		case facebook:
			btn_login.setText("페이스북에 로그인 합니다.");
			break;
		case twitter:
			btn_login.setText("트위터에 로그인 합니다.");
			break;
		case kakaotalk:
			btn_login.setText("카카오톡에 로그인 합니다.");
			break;
		}		
		btn_login.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.btn_login:
			// TODO Auto-generated method stub
			String userId = et_email.getText().toString();
			String userPw = et_password.getText().toString();
			// TODO: 유저 아이디와 패스워드를 가지고 로그인 하기
			
			
			///////////
			Intent it_display = new Intent(this, DisplayEmotion.class);
			startActivity(it_display);
			break;
		}
	}
}
