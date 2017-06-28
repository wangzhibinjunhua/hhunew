package com.wzb.hhu.activity;

import java.util.ArrayList;
import java.util.Calendar;

import com.wzb.hhu.R;
import com.wzb.hhu.btcore.IECCommand;
import com.wzb.hhu.interf.WApplication;
import com.wzb.hhu.util.Common;
import com.wzb.hhu.util.CustomDialog;
import com.wzb.hhu.util.LogUtil;
import com.wzb.hhu.util.ResTools;
import com.wzb.hhu.util.ToastUtil;
import com.wzb.spp.BluetoothSPP.BluetoothConnectionListener;
import com.wzb.spp.BluetoothSPP.OnDataReceivedListener;
import com.wzb.spp.DeviceList;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.picker.TimePicker;
import cn.qqtheme.framework.util.ConvertUtils;

public class SettingTimeActivity extends BaseActivity implements OnClickListener {
	public static final int UPDATE_BT_STATE=0xff0001;
	private ImageView backView;
	private TextView titleView;
	private ImageView btView;
	private ImageView titleMeterList;

	private Context mContext;

	private Button readBtn, writeBtn, returnBtn;
	private CheckBox sysClockCb;
	private EditText sysDate, sysTime;
	private ImageView dateSet, timeSet;
	private TextView meterDate, meterTime;
	
	private String meterSn, meterPw;
	
	private static int curComCmd = 0xff;
	private static int curItemId = 0;
	private static Boolean isRead=true;
	
	ArrayList<Integer> selectedItem = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_settingtime);
		mContext = SettingTimeActivity.this;
		selectedItem = new ArrayList<Integer>();
		initTitleView();
		setBtListener();
		initView();
	}

	private void initView() {
		sysClockCb = (CheckBox) findViewById(R.id.sys_clock_cb);
		sysClockCb.setChecked(false);

		sysDate = (EditText) findViewById(R.id.sys_clock_date_value);
		sysTime = (EditText) findViewById(R.id.sys_clock_time_value);

		dateSet = (ImageView) findViewById(R.id.sys_clock_date_value_set);
		dateSet.setOnClickListener(this);

		timeSet = (ImageView) findViewById(R.id.sys_clock_time_value_set);
		timeSet.setOnClickListener(this);

		meterDate = (TextView) findViewById(R.id.meter_clock_date_value);
		meterTime = (TextView) findViewById(R.id.meter_clock_time_value);

		readBtn = (Button) findViewById(R.id.time_read_btn);
		readBtn.setOnClickListener(this);
		writeBtn = (Button) findViewById(R.id.time_write_btn);
		writeBtn.setOnClickListener(this);
		returnBtn = (Button) findViewById(R.id.time_back_btn);
		returnBtn.setOnClickListener(this);

		String[] dateTime = getSysDateTime();
		sysDate.setText(dateTime[0] + "-" + dateTime[1] + "-" + dateTime[2]);
		sysTime.setText(dateTime[3] + ":" + dateTime[4] + ":" + dateTime[5]);
	}

	private void initTitleView() {
		backView = (ImageView) findViewById(R.id.title_back);
		titleView = (TextView) findViewById(R.id.title_text);
		
		backView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		btView = (ImageView) findViewById(R.id.title_bt);
		btView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(getApplicationContext(), DeviceList.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});
		titleMeterList=(ImageView)findViewById(R.id.title_meterlist);
		titleMeterList.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				WApplication.sp.set("current_activity", "2");
				Intent intent = new Intent(getApplicationContext(), AmmeterListActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				finish();
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setBtListener();
		updateBtState();
		meterSn=WApplication.sp.get("current_sn", "");
		meterPw=WApplication.sp.get("current_pw", "");
		titleView.setText(ResTools.getResString(SettingTimeActivity.this, R.string.setting_time)+":\n"+meterSn);
		
	}

	private void updateBtState() {
		Drawable drawableDisconnect = mContext.getResources().getDrawable(R.drawable.disconnect);
		Drawable drawableconnect = mContext.getResources().getDrawable(R.drawable.connected);
		if (WApplication.bt.isConnected()) {

			btView.setBackground(drawableconnect);
		} else {
			btView.setBackground(drawableDisconnect);
		}
	}
	
	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			String rString = (String) msg.obj;
			switch (msg.what) {
			case UPDATE_BT_STATE:
				updateBtState();
				break;
			case 0xfe:
				//if (rString.startsWith("2f")) {
					curComCmd = 0xfd;
					String s = "063033310d0a";
					IECCommand.sppSend(s);
				//}
				break;
			case 0xfd:
				if (rString.startsWith("0150300228")) {
					curComCmd = 0xfc;
					String password = rString.substring(rString.indexOf("28") + 2, rString.indexOf("29"));
					LogUtil.logMessage("wzb", "password=" + password);
					password = Common.asciiToString(password);
					LogUtil.logMessage("wzb", "ascii password=" + password);
					sendPassword(password);
				}
				break;
			case 0xfc:
				if (rString.equals("06") || rString.equals("6")) {
					// start read
					if(isRead){
						curComCmd = 0xff0002;
						curItemId = 0;
						sendDataItemCmd();
					}else{
						curComCmd = 0xff0003;
						curItemId = 0;
						sendWriteDataItemCmd();
					}

				}
				break;
			case 0xff0002:
				updateUI(rString, selectedItem.get(curItemId));
				curItemId++;
				if (curItemId > selectedItem.size()-1) {
					LogUtil.logMessage("wzb", "read completed");
					CustomDialog.dismissDialog();
					closeCon();
				} else {
					sendDataItemCmd();
				}
				break;
				
			case 0xff0003:
				updateWriteUI(rString, selectedItem.get(curItemId));
				curItemId++;
				if (curItemId > selectedItem.size()-1) {
					LogUtil.logMessage("wzb", "read completed");
					CustomDialog.dismissDialog();
					closeCon();
				} else {
					sendWriteDataItemCmd();
				}
				break;

			case 0xffff:
				CustomDialog.dismissDialog();
				break;
			default:
				break;
			}
		};
	};
	
	private void sendPassword(String pw) {
		String crcPw = Common.getMeterPw(pw, meterPw);
		LogUtil.logMessage("wzb", "crcpw=" + crcPw);
		String pwHeadHex = "0150320228";
		String pwEndHex = "2903";
		String pwV = pwHeadHex + Common.str2HexStr(crcPw) + pwEndHex;
		String pwVxor = Common.xorHex(pwV.substring(2));
		IECCommand.sppSend(pwV + pwVxor);
	}
	
	private void closeCon(){
		String string="01423003";
		String xor = Common.xorHex(string.substring(2));
		IECCommand.sppSend(string+xor);
	}
	
	private void updateUI(String s, int id) {
		String info = s.substring(s.indexOf("28") + 2, s.indexOf("29"));
		LogUtil.logMessage("wzb", "####: info:"+info);
		String ainfo=Common.asciiToString(info);
		LogUtil.logMessage("wzb", "***: ainfo:"+ainfo);
		if(id==0)meterTime.setText(ainfo);
		if(id==1)meterDate.setText(ainfo);
	}
	
	private void updateWriteUI(String s, int id) {
		LogUtil.logMessage("wzb", "####: info:"+s);
		String ainfo=Common.asciiToString(s);
		LogUtil.logMessage("wzb", "***: ainfo:"+ainfo);
		if(id==0){
			if(s.equals("06")){
				ToastUtil.showShortToast(mContext, "time:write ok");
			}else if(s.equals("15")){
				ToastUtil.showShortToast(mContext, "time:write failed");
			}
		}else if(id==1){
			if(s.equals("06")){
				ToastUtil.showShortToast(mContext, "date:write ok");
			}else if(s.equals("15")){
				ToastUtil.showShortToast(mContext, "date:write failed");
			}
		}
		
	}

	private void sendDataItemCmd() {
		int curComItem = selectedItem.get(curItemId);
		String cmd = ResTools.getResStringArray(mContext, R.array.time_cmd)[curComItem];
		String sendData = "01523102" + Common.stringToAscii(cmd) + "282903";
		String sendDataXor = Common.xorHex(sendData.substring(2));
		IECCommand.sppSend(sendData + sendDataXor);
	}
	
	private void sendWriteDataItemCmd(){
		int curComItem = selectedItem.get(curItemId);
		String cmd = ResTools.getResStringArray(mContext, R.array.time_cmd)[curComItem];
		String writeValue="";
		if(curComItem==0){
			writeValue=sysTime.getText().toString();
		}else if(curComItem==1){
			writeValue=sysDate.getText().toString();
		}
		String sendData = "01573102" + Common.stringToAscii(cmd) + "28"+Common.stringToAscii(writeValue)+"2903";
		String sendDataXor = Common.xorHex(sendData.substring(2));
		IECCommand.sppSend(sendData + sendDataXor);
	}

	private void initCom() {
		curComCmd = 0xfe;
		String sendData = "2f3f" + Common.stringToAscii(meterSn) + "210d0a";
		LogUtil.logMessage("wzb", "senddata=" + sendData);
		IECCommand.sppSend(sendData);
	}
	

	
	private void calSelectedItem(){
		selectedItem.clear();
		selectedItem.add(0);
		selectedItem.add(1);
	}
	
	OnClickListener waitcancleListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			closeCon();
			CustomDialog.dismissDialog();
		}
	};


	private void test_read() {
		isRead=true;
		//get selectedItem
		calSelectedItem();
		LogUtil.logMessage("wzb", "selecteditem:"+selectedItem);
		if (!WApplication.bt.isConnected()) {
			ToastUtil.showShortToast(mContext, "蓝牙处于断开状态，请连接");
		} else {

			if (selectedItem == null || selectedItem.size() == 0) {
				ToastUtil.showShortToast(mContext, "请选择需要读取的数据项");
			} else {
				//CustomDialog.showWaitDialog(mContext, "读取中...");
				CustomDialog.showWaitAndCancelDialog(mContext, "读取中...", waitcancleListener);
				initCom();
				//mHandler.sendEmptyMessageDelayed(0xffff, 5000);
			}
		}
	}
	
	private void test_write() {
		if(!WApplication.sp.get("current_level", "ReadUser").equals("AdminUser")
				|| !WApplication.sp.get("current_level", "ReadUser").equals("ProgramUser")){
			ToastUtil.showShortToast(mContext, "没有权限");
			return;
		}
		isRead=false;
		//get selectedItem
		calSelectedItem();
		LogUtil.logMessage("wzb", "selecteditem:"+selectedItem);
		if(!sysClockCb.isChecked()){
			ToastUtil.showShortToast(mContext, "请选择要写入的数据项");
			return;
		}
		if (!WApplication.bt.isConnected()) {
			ToastUtil.showShortToast(mContext, "蓝牙处于断开状态，请连接");
		} else {

			if (selectedItem == null || selectedItem.size() == 0) {
				ToastUtil.showShortToast(mContext, "请选择需要写入的数据项");
			} else {
				//CustomDialog.showWaitDialog(mContext, "读取中...");
				CustomDialog.showWaitAndCancelDialog(mContext, "写入中...", waitcancleListener);
				initCom();
				//mHandler.sendEmptyMessageDelayed(0xffff, 5000);
			}
		}
	}


	private void setBtListener() {
		WApplication.bt.setOnDataReceivedListener(new OnDataReceivedListener() {
			public void onDataReceived(byte[] data, String message) {
				String dataString = Common.bytesToHexString(data);
				LogUtil.logMessage("wzb", "SettingTimeActivity datarec:" + dataString + " msg:" + message);

				Message msg = mHandler.obtainMessage();
				msg.what = curComCmd;
				msg.obj = dataString;
				mHandler.sendMessage(msg);
			}
		});

		WApplication.bt.setBluetoothConnectionListener(new BluetoothConnectionListener() {
			public void onDeviceConnected(String name, String address) {
				LogUtil.logMessage("wzb", "SettingTimeActivity onDeviceConnected");
				mHandler.sendEmptyMessage(UPDATE_BT_STATE);

			}

			public void onDeviceDisconnected() {
				LogUtil.logMessage("wzb", "SettingTimeActivity onDeviceDisconnected");
				mHandler.sendEmptyMessage(UPDATE_BT_STATE);
			}

			public void onDeviceConnectionFailed() {
				LogUtil.logMessage("wzb", "SettingTimeActivity onDeviceConnectionFailed");
				mHandler.sendEmptyMessage(UPDATE_BT_STATE);
			}
		});

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.time_back_btn:
			finish();
			break;
		case R.id.sys_clock_date_value_set:
			onYearMonthDayPicker();
			break;
		case R.id.sys_clock_time_value_set:
			onTimePicker();
			break;
		case R.id.time_read_btn:
			test_read();
			break;
		case R.id.time_write_btn:
			test_write();
			break;
		default:
			break;
		}
	}

	private void updateSysDateValue(String year, String month, String day) {
		sysDate.setText(year + "-" + month + "-" + day);
	}

	private void updateSysTimeValue(String hour, String minute) {
		sysTime.setText(hour + ":" + minute + ":" + "30");
	}

	private String[] getSysDateTime() {
		String[] dateTime = new String[6];
		Time t = new Time();
		t.setToNow();
		dateTime[0] = "" + t.year;
		dateTime[1] = "" + (t.month + 1);
		dateTime[2] = "" + t.monthDay;
		dateTime[3] = "" + t.hour;
		dateTime[4] = "" + t.minute;
		dateTime[5] = "" + t.second;
		LogUtil.logMessage("wzb", "month=" + t.month);
		return dateTime;
	}

	public void onYearMonthDayPicker() {
		final DatePicker picker = new DatePicker(this);
		picker.setCanceledOnTouchOutside(true);
		picker.setUseWeight(true);
		picker.setTopPadding(ConvertUtils.toPx(this, 20));
		picker.setRangeEnd(2111, 1, 11);
		picker.setRangeStart(2016, 1, 1);
		picker.setSelectedItem(2017, 5, 24);
		picker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
			@Override
			public void onDatePicked(String year, String month, String day) {
				updateSysDateValue(year, month, day);
			}
		});
		picker.setOnWheelListener(new DatePicker.OnWheelListener() {
			@Override
			public void onYearWheeled(int index, String year) {
				picker.setTitleText(year + "-" + picker.getSelectedMonth() + "-" + picker.getSelectedDay());
			}

			@Override
			public void onMonthWheeled(int index, String month) {
				picker.setTitleText(picker.getSelectedYear() + "-" + month + "-" + picker.getSelectedDay());
			}

			@Override
			public void onDayWheeled(int index, String day) {
				picker.setTitleText(picker.getSelectedYear() + "-" + picker.getSelectedMonth() + "-" + day);
			}
		});
		picker.show();
	}

	public void onTimePicker() {
		TimePicker picker = new TimePicker(this, TimePicker.HOUR_24);
		picker.setRangeStart(0, 0);// 00:00
		picker.setRangeEnd(23, 59);// 23:59
		int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		int currentMinute = Calendar.getInstance().get(Calendar.MINUTE);
		picker.setSelectedItem(currentHour, currentMinute);
		picker.setTopLineVisible(false);
		picker.setOnTimePickListener(new TimePicker.OnTimePickListener() {
			@Override
			public void onTimePicked(String hour, String minute) {
				updateSysTimeValue(hour, minute);
			}
		});
		picker.show();
	}

}
