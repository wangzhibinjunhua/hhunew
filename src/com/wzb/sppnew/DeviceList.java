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

package com.wzb.sppnew;

import java.util.Set;

import com.wzb.hhunew.R;
import com.wzb.hhunew.activity.BaseActivity;
import com.wzb.hhunew.btcore.BroadcastAction;
import com.wzb.hhunew.interf.WApplication;
import com.wzb.hhunew.util.Common;
import com.wzb.hhunew.util.CustomDialog;
import com.wzb.hhunew.util.LogUtil;
import com.wzb.hhunew.util.ResTools;
import com.wzb.hhunew.util.ToastUtil;
import com.wzb.sppnew.BluetoothSPP.BluetoothConnectionListener;
import com.wzb.sppnew.BluetoothSPP.OnDataReceivedListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.style.UpdateAppearance;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

@SuppressLint("NewApi")
public class DeviceList extends BaseActivity {

	private ImageView backView;
	private ImageView btView;
	private ImageView titleMeterList;
	private TextView titleView;
	// Debugging
	private static final String TAG = "BluetoothSPP";
	private static final boolean D = true;

	// Member fields
	private BluetoothAdapter mBtAdapter;
	private ArrayAdapter<String> mPairedDevicesArrayAdapter;
	private Set<BluetoothDevice> pairedDevices;
	private Button scanButton;
	public static final int DEVICE_CONNECTION_FAILED = 0xff01;
	public static final int DEVICE_CONNECTED = 0xff02;
	public static final int DEVICE_DISCONNECTED = 0xff03;
	public static final int START_DISCOVER_BT = 0xff04;
	public static final int UPDATE_CONNECTED_BT_STATE = 0xff05;
	private String connectAddress = "";

	private TextView btState;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Setup the window
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		int listId = getIntent().getIntExtra("layout_list", R.layout.device_list);
		setContentView(listId);

		initTitleView();
		String strBluetoothDevices = getIntent().getStringExtra("bluetooth_devices");
		if (strBluetoothDevices == null)
			strBluetoothDevices = "Bluetooth Devices";
		setTitle(strBluetoothDevices);

		// Set result CANCELED in case the user backs out
		setResult(Activity.RESULT_CANCELED);

		btState = (TextView) findViewById(R.id.tv);

		// Initialize the button to perform device discovery
		scanButton = (Button) findViewById(R.id.button_scan);
		String strScanDevice = getIntent().getStringExtra("scan_for_devices");
		if (strScanDevice == null)
			strScanDevice = "SCAN FOR DEVICES";
		scanButton.setText(strScanDevice);
		scanButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				doDiscovery();

			}
		});

		// Initialize array adapters. One for already paired devices
		// and one for newly discovered devices
		int layout_text = getIntent().getIntExtra("layout_text", R.layout.device_name);
		mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, layout_text);

		// Find and set up the ListView for paired devices
		ListView pairedListView = (ListView) findViewById(R.id.list_devices);
		pairedListView.setAdapter(mPairedDevicesArrayAdapter);
		pairedListView.setOnItemClickListener(mDeviceClickListener);

		// Register for broadcasts when a device is discovered
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.registerReceiver(mReceiver, filter);

		// Register for broadcasts when discovery has finished
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		this.registerReceiver(mReceiver, filter);

		// Get the local Bluetooth adapter
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		// Get a set of currently paired devices
		pairedDevices = mBtAdapter.getBondedDevices();

		// If there are paired devices, add each one to the ArrayAdapter
		// if (pairedDevices.size() > 0) {
		if (false) {
			for (BluetoothDevice device : pairedDevices) {
				mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
			}
		} else {
			String noDevices = "No devices found";
			mPairedDevicesArrayAdapter.add(noDevices);
		}
	}

	private void initTitleView() {
		backView = (ImageView) findViewById(R.id.title_back);
		btView = (ImageView) findViewById(R.id.title_bt);
		btView.setVisibility(View.GONE);
		titleView = (TextView) findViewById(R.id.title_text);
		titleView.setText(ResTools.getResString(DeviceList.this, R.string.bt_device));
		backView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		titleMeterList = (ImageView) findViewById(R.id.title_meterlist);
		titleMeterList.setVisibility(View.GONE);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (!WApplication.bt.isBluetoothEnabled()) {
			Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
		} else {
			if (!WApplication.bt.isServiceAvailable()) {
				WApplication.bt.setupService();
				WApplication.bt.startService(BluetoothState.DEVICE_OTHER);
			}
			mHandler.sendEmptyMessage(START_DISCOVER_BT);
		}
		setBtListener();
		updateBtDevice();
	}

	private void updateBtDevice() {
		String connectedDeviceName = WApplication.bt.getConnectedDeviceName();
		String connectedDeviceAddr = WApplication.bt.getConnectedDeviceAddress();

		if (connectedDeviceAddr != null && !TextUtils.isEmpty(connectedDeviceAddr)) {
			btState.setText("connected" + "\n" + connectedDeviceName + "\n" + connectedDeviceAddr);
			btState.setBackgroundColor(Color.GREEN);
		} else {
			btState.setText("No device connected");
			btState.setBackgroundColor(Color.RED);
		}
	}

	private  Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case DEVICE_CONNECTED:
				ToastUtil.showShortToast(DeviceList.this, "connected to" + connectAddress);
				CustomDialog.dismissDialog();
				finish();
				break;
			case DEVICE_CONNECTION_FAILED:
				ToastUtil.showShortToast(DeviceList.this, "connection failed");
				CustomDialog.dismissDialog();
				break;
			case DEVICE_DISCONNECTED:
				ToastUtil.showShortToast(DeviceList.this, "disconnected");
				CustomDialog.dismissDialog();
			case START_DISCOVER_BT:
				doDiscovery();
				break;
			case UPDATE_CONNECTED_BT_STATE:
				updateBtDevice();
				break;
			default:
				break;
			}
		};
	};

	private void setBtListener() {
		WApplication.bt.setOnDataReceivedListener(new OnDataReceivedListener() {
			public void onDataReceived(byte[] data, String message) {
				String dataString = Common.bytesToHexString(data);
				LogUtil.logMessage("wzb", " DeviceList datarec:" + dataString + " msg:" + message);
			}
		});

		WApplication.bt.setBluetoothConnectionListener(new BluetoothConnectionListener() {
			public void onDeviceConnected(String name, String address) {
				LogUtil.logMessage("wzb", " DeviceList onDeviceConnected");
				mHandler.sendEmptyMessage(DEVICE_CONNECTED);
			}

			public void onDeviceDisconnected() {
				LogUtil.logMessage("wzb", "DeviceList onDeviceDisconnected");
				mHandler.sendEmptyMessage(DEVICE_DISCONNECTED);
			}

			public void onDeviceConnectionFailed() {
				LogUtil.logMessage("wzb", "DeviceList onDeviceConnectionFailed");
				mHandler.sendEmptyMessage(DEVICE_CONNECTION_FAILED);
			}
		});

	}

	protected void onDestroy() {
		super.onDestroy();
		// Make sure we're not doing discovery anymore
		if (mBtAdapter != null) {
			mBtAdapter.cancelDiscovery();
		}

		// Unregister broadcast listeners
		this.unregisterReceiver(mReceiver);
		this.finish();
	}
	
	Runnable timeout = new Runnable() {
        @Override
        public void run() {
        	if (mBtAdapter.isDiscovering()) {
				mBtAdapter.cancelDiscovery();
			}
        }
    };

	// Start device discover with the BluetoothAdapter
	private void doDiscovery() {
		CustomDialog.showWaitAndCancelDialog(DeviceList.this, "discovery...", new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//CustomDialog.dismissDialog();
				mHandler.removeCallbacks(timeout);
				if (mBtAdapter != null) {
					mBtAdapter.cancelDiscovery();
				}
			}
		});
		if (D)
			Log.d(TAG, "doDiscovery()");

		// Remove all element from the list
		mPairedDevicesArrayAdapter.clear();

		// If there are paired devices, add each one to the ArrayAdapter
		// if (pairedDevices.size() > 0) {
		if (false) {
			for (BluetoothDevice device : pairedDevices) {
				mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
			}
		} else {
			String strNoFound = getIntent().getStringExtra("no_devices_found");
			if (strNoFound == null)
				strNoFound = "No devices found";
			mPairedDevicesArrayAdapter.add(strNoFound);
		}

		// Indicate scanning in the title
		String strScanning = getIntent().getStringExtra("scanning");
		if (strScanning == null)
			strScanning = "Scanning for devices...";
		setProgressBarIndeterminateVisibility(true);
		setTitle(strScanning);

		// Turn on sub-title for new devices
		// findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);
		// If we're already discovering, stop it
		if (mBtAdapter.isDiscovering()) {
			mBtAdapter.cancelDiscovery();
		}

		// Request discover from BluetoothAdapter
		mBtAdapter.startDiscovery();
		
		
		mHandler.removeCallbacks(timeout);
		mHandler.postDelayed(timeout, 7*1000);
		
	}

	// The on-click listener for all devices in the ListViews
	private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
			// Cancel discovery because it's costly and we're about to connect
			if (mBtAdapter.isDiscovering())
				mBtAdapter.cancelDiscovery();

			String strNoFound = getIntent().getStringExtra("no_devices_found");
			if (strNoFound == null)
				strNoFound = "No devices found";
			if (!((TextView) v).getText().toString().equals(strNoFound)) {
				// Get the device MAC address, which is the last 17 chars in the
				// View
				String info = ((TextView) v).getText().toString();
				String address = info.substring(info.length() - 17);
				connectAddress = address;
				// Create the result Intent and include the MAC address
				Intent intent = new Intent();
				intent.putExtra(BluetoothState.EXTRA_DEVICE_ADDRESS, address);

				intent.setAction(BroadcastAction.ACTION_CONNETION_ADDRESS);
				sendBroadcast(intent);
				CustomDialog.showWaitDialog(DeviceList.this, "connecting...");
				btState.setText("No device connected");
				btState.setBackgroundColor(Color.RED);
				// Set result and finish this Activity
				// setResult(Activity.RESULT_OK, intent);
				// finish();
			}
		}
	};

	// The BroadcastReceiver that listens for discovered devices and
	// changes the title when discovery is finished
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			LogUtil.logMessage("wzb", "onReceive action:" + action);
			// When discovery finds a device
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Get the BluetoothDevice object from the Intent
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

				// If it's already paired, skip it, because it's been listed
				// already
				// if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
				String strNoFound = getIntent().getStringExtra("no_devices_found");
				if (strNoFound == null)
					strNoFound = "No devices found";

				if (mPairedDevicesArrayAdapter.getItem(0).equals(strNoFound)) {
					mPairedDevicesArrayAdapter.remove(strNoFound);
				}
				mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
				// }

				mPairedDevicesArrayAdapter.notifyDataSetChanged();

				// When discovery is finished, change the Activity title
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				CustomDialog.dismissDialog();
				setProgressBarIndeterminateVisibility(false);
				String strSelectDevice = getIntent().getStringExtra("select_device");
				if (strSelectDevice == null)
					strSelectDevice = "Select a device to connect";
				setTitle(strSelectDevice);
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
			if (resultCode == Activity.RESULT_OK) {
				//mHandler.sendEmptyMessage(START_DISCOVER_BT);
	
			}else{
				ToastUtil.showShortToast(DeviceList.this, getResources().getString(R.string.bt_not_enabled));
				finish();
			}
		}
	}

}
