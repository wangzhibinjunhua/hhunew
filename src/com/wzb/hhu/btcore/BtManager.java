package com.wzb.hhu.btcore;

import com.wzb.hhu.interf.WApplication;
import com.wzb.hhu.util.Common;
import com.wzb.hhu.util.LogUtil;
import com.wzb.spp.BluetoothState;
import com.wzb.spp.BluetoothSPP.BluetoothConnectionListener;
import com.wzb.spp.BluetoothSPP.OnDataReceivedListener;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * @author wzb<wangzhibin_x@qq.com>
 * @date Jun 11, 2017 10:54:42 PM
 */
public class BtManager extends BroadcastReceiver {

	private void openBt() {

		if (!WApplication.bt.isBluetoothEnabled()) {
			// Intent intent = new
			// Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			// startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
			WApplication.bt.enable();
		}
	}

	private void initSPPService() {
		if (!WApplication.bt.isServiceAvailable()) {
			WApplication.bt.setupService();
			WApplication.bt.startService(BluetoothState.DEVICE_OTHER);
		}
	}

	private void setBtListener() {
		WApplication.bt.setOnDataReceivedListener(new OnDataReceivedListener() {
			public void onDataReceived(byte[] data, String message) {
				String dataString = Common.bytesToHexString(data);
				LogUtil.logMessage("wzb", "datarec:" + dataString + " msg:" + message);
			}
		});

		WApplication.bt.setBluetoothConnectionListener(new BluetoothConnectionListener() {
			public void onDeviceConnected(String name, String address) {
				LogUtil.logMessage("wzb", "onDeviceConnected");

			}

			public void onDeviceDisconnected() {
				LogUtil.logMessage("wzb", "onDeviceDisconnected");
			}

			public void onDeviceConnectionFailed() {
				LogUtil.logMessage("wzb", "onDeviceConnectionFailed");
			}
		});

	}

	private void connect(Intent data) {
		if (WApplication.bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
			WApplication.bt.disconnect();
		}
		String address = data.getExtras().getString(BluetoothState.EXTRA_DEVICE_ADDRESS);
		LogUtil.logMessage("wzb", "connect:" + address);
		WApplication.bt.connect(data);

	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.d("wzb", "btmanager recevie:" + intent.getAction());
		String action = intent.getAction();
		if (action.equals(BroadcastAction.ACTION_CONNETION_ADDRESS)) {
			openBt();
			// setBtListener();
			initSPPService();
			connect(intent);
		} else if (action.equals(BroadcastAction.ACTION_OPEN_BT)) {
			openBt();
			initSPPService();
		}
	}

}
