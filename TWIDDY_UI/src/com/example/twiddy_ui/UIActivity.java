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
	private static final int REQUEST_CONNECT_DEVICE = 1;
	private static final int REQUEST_ENABLE_BT = 2;
	private BluetoothService btService = null;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ui);
		
		Button btn_start = (Button) findViewById(R.id.btn_start);
		Button btn_setting = (Button) findViewById(R.id.btn_setting);
		
		btn_start.setOnClickListener(this);
		btn_setting.setOnClickListener(this);
		
		if(btService == null) {
			btService = new BluetoothService(this);
		}		
		
		if(btService.getState() != BluetoothService.STATE_CONNECTED 
				&& btService.getDeviceState()){
			btService.enableBluetooth();
		}
		else {
			finish();
		}
	}
	
    private void sendMessage(String message) {
        if (btService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, "NOT CONNECTED YET!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (message.length() > 0) {
            byte[] send = message.getBytes();
            btService.write(send);
        }
    }

	public void onActivityResult(int requestCode, int resultCode, Intent data) { 
        switch (requestCode) {        
        case REQUEST_CONNECT_DEVICE:
            if (resultCode == Activity.RESULT_OK) {
            	btService.getDeviceInfo(data);
            }
            break;
        case REQUEST_ENABLE_BT:
            if (resultCode == Activity.RESULT_OK) {
            	btService.scanDevice();
            } 
            break;
        }
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
			sendMessage("a");
			/*
			Intent it_setting = new Intent(this, SettingUI.class);
			startActivity(it_setting);
			*/
			break;
		}
	}
}
