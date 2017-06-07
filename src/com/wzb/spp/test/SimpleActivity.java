/*
 * Copyright 2014 Akexorcist
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wzb.spp.test;

import android.os.Bundle;
import android.util.Log;

import com.wzb.hhu.R;
import com.wzb.hhu.util.Common;
import com.wzb.hhu.util.LogUtil;
import com.wzb.spp.BluetoothSPP;
import com.wzb.spp.BluetoothSPP.BluetoothConnectionListener;
import com.wzb.spp.BluetoothSPP.OnDataReceivedListener;
import com.wzb.spp.BluetoothState;
import com.wzb.spp.DeviceList;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


public class SimpleActivity extends Activity {
	BluetoothSPP bt;
	String password="";
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_simple);
		
		bt = new BluetoothSPP(this);

		if(!bt.isBluetoothAvailable()) {
			Toast.makeText(getApplicationContext()
					, "Bluetooth is not available"
					, Toast.LENGTH_SHORT).show();
            finish();
		}
		
		bt.setOnDataReceivedListener(new OnDataReceivedListener() {
			public void onDataReceived(byte[] data, String message) {
				String dataString=Common.bytesToHexString(data);
				LogUtil.logMessage("wzb", "datarec:"+dataString+" msg:"+message);
				if(dataString.startsWith("0150300228")){
					password=dataString.substring(dataString.indexOf("28")+2,dataString.indexOf("29"));
					LogUtil.logMessage("wzb", "password="+password);
					password=Common.asciiToString(password);
					LogUtil.logMessage("wzb", "ascii password="+password);
				}
				//Toast.makeText(SimpleActivity.this, message, Toast.LENGTH_SHORT).show();
			}
		});
		
		bt.setBluetoothConnectionListener(new BluetoothConnectionListener() {
			public void onDeviceConnected(String name, String address) {
				LogUtil.logMessage("wzb", "onDeviceConnected");
				Toast.makeText(getApplicationContext()
						, "Connected to " + name + "\n" + address
						, Toast.LENGTH_SHORT).show();
			}

			public void onDeviceDisconnected() {
				LogUtil.logMessage("wzb", "onDeviceDisconnected");
				Toast.makeText(getApplicationContext()
						, "Connection lost", Toast.LENGTH_SHORT).show();
			}

			public void onDeviceConnectionFailed() {
				LogUtil.logMessage("wzb", "onDeviceConnectionFailed");
				Toast.makeText(getApplicationContext()
						, "Unable to connect", Toast.LENGTH_SHORT).show();
			}
		});
		
		Button btnConnect = (Button)findViewById(R.id.btnConnect);
		btnConnect.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		if(bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
        			bt.disconnect();
        		} else {
                    Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE); 
        		}
        	}
        }); 
		
		initView();
	}
	
	private void sppSend(String s){
		bt.send(Common.parseHexStringToBytes(s), false);
	}
	
	private void initView(){
		Button btnBaud=(Button)findViewById(R.id.baud);
		btnBaud.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sppSend("0x063036310d0a");
			}
		});
		
		Button btnPw=(Button)findViewById(R.id.password);
		btnPw.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String crcPw=Common.getMeterPw(password, "87153668");
				LogUtil.logMessage("wzb","crcpw="+crcPw);
				String pwHeadHex="0150320228";
				String pwEndHex="2903";
				String pwV=pwHeadHex+Common.str2HexStr(crcPw)+pwEndHex;
				String pwVxor=Common.xorHex(pwV.substring(2));
				sppSend(pwV+pwVxor);
			}
		});
		
		Button btnReadData=(Button)findViewById(R.id.readdata);
		btnReadData.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String r96e1e0="0152310239362e312e30282903";
				String r96e1e0Xor=Common.xorHex(r96e1e0.substring(2));
				sppSend(r96e1e0+r96e1e0Xor);
			}
		});
		
		
	}
	
	public void onDestroy() {
        super.onDestroy();
        bt.stopService();
    }
	
	public void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) {
        	Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if(!bt.isServiceAvailable()) { 
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
                setup();
            }
        }
    }
	
	
	
	public void setup() {
		Button btnSend = (Button)findViewById(R.id.btnSend);
		btnSend.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		//bt.send("Text", true);
        		LogUtil.logMessage("wzb", "send");
        		String s="0x2f3f31323334353638210d0a";
        		bt.send(Common.parseHexStringToBytes(s), false);
        	}
        });
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
			if(resultCode == Activity.RESULT_OK)
                bt.connect(data);
		} else if(requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if(resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
                setup();
            } else {
                Toast.makeText(getApplicationContext()
                		, "Bluetooth was not enabled."
                		, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
