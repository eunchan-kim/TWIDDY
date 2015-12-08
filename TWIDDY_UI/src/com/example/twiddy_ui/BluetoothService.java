/*
 * Modify open source project from 
 * https://android.googlesource.com/platform/development/+/eclair-passion-release/samples/BluetoothChat/src/com/example/android/BluetoothChat
 * For bluetooth connection
 * 
 * Main developers: 한주형
 * Debuggers: 한주형
 * 
 * Original Licence is below. (Apache License Version 2.0)
 */

/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.twiddy_ui;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;

public class BluetoothService {
	private static final int REQUEST_CONNECT_DEVICE = 1;
	private static final int REQUEST_ENABLE_BT = 2;
	
	public static final int STATE_NONE = 0;
	public static final int STATE_LISTEN = 1;
	public static final int STATE_CONNECTING = 2;
	public static final int STATE_CONNECTED = 3;
	
	private static final UUID MY_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805f9b34fb");
	private BluetoothAdapter btAdapter;
	private Activity mActivity;
	private ConnectThread mConnectThread;
	private int mState;
	private OutputStream mmOutStream;

	public BluetoothService(Activity ac) {
		mActivity = ac;
		mState = STATE_NONE;
		btAdapter = BluetoothAdapter.getDefaultAdapter();
	}

	public boolean getDeviceState() {
		if (btAdapter == null) {
			return false;
		} else {
			return true;
		}
	}

	public void enableBluetooth() {
		if (btAdapter.isEnabled()) {
			scanDevice();
		} else {
			Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			mActivity.startActivityForResult(i, REQUEST_ENABLE_BT);
		}
	}

	public void scanDevice() {
		Intent serverIntent = new Intent(mActivity, DeviceListActivity.class);
		mActivity.startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
	}

	public void getDeviceInfo(Intent data) {
		String address = data.getExtras().getString(
				DeviceListActivity.EXTRA_DEVICE_ADDRESS);
		BluetoothDevice device = btAdapter.getRemoteDevice(address);
		connect(device);
	}

	private synchronized void setState(int state) {
		mState = state;
	}

	public synchronized int getState() {
		return mState;
	}

	public synchronized void start() {
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}
	}

	public synchronized void connect(BluetoothDevice device) {
		if (mState == STATE_CONNECTING) {
			if (mConnectThread == null) {

			} else {
				mConnectThread.cancel();
				mConnectThread = null;
			}
		}
		mConnectThread = new ConnectThread(device);
		mConnectThread.start();
		setState(STATE_CONNECTING);
	}

	public synchronized void stop() {
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}
		setState(STATE_NONE);
	}

	public void write(byte[] out) {
		synchronized (this) {
			if (mState != STATE_CONNECTED)
				return;
		}
		try {
			mmOutStream.write(out);

		} catch (IOException e) {
		}
	}

	// ���� ����������
	private void connectionFailed() {
		setState(STATE_LISTEN);
	}

	private class ConnectThread extends Thread {
		private final BluetoothSocket mmSocket;
		
		public ConnectThread(BluetoothDevice device) {
			BluetoothSocket tmp = null;
			try {
				tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
			} catch (IOException e) {
			}
			mmSocket = tmp;			
		}

		public void run() {
			btAdapter.cancelDiscovery();

			try {
				mmSocket.connect();
				mmOutStream = mmSocket.getOutputStream();

			} catch (IOException e) {
				connectionFailed();
				try {
					mmSocket.close();
				} catch (IOException e2) {
				}
				BluetoothService.this.start();
				return;
			}

			synchronized (BluetoothService.this) {
				mConnectThread = null;
			}
			
			setState(STATE_CONNECTED);
		}

		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
			}
		}
	}

}