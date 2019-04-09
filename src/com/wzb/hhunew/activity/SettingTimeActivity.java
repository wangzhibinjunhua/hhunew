package com.wzb.hhunew.activity;

import java.util.ArrayList;
import java.util.Calendar;

import com.wzb.hhunew.R;
import com.wzb.hhunew.btcore.IECCommand;
import com.wzb.hhunew.interf.WApplication;
import com.wzb.hhunew.util.Common;
import com.wzb.hhunew.util.CustomDialog;
import com.wzb.hhunew.util.LogUtil;
import com.wzb.hhunew.util.ResTools;
import com.wzb.hhunew.util.ToastUtil;
import com.wzb.sppnew.DeviceList;
import com.wzb.sppnew.BluetoothSPP.BluetoothConnectionListener;
import com.wzb.sppnew.BluetoothSPP.OnDataReceivedListener;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;
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
	private CheckBox dateCb,timeCb;
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
		dateCb = (CheckBox) findViewById(R.id.date_cb);
		dateCb.setChecked(false);
		
		timeCb = (CheckBox) findViewById(R.id.time_cb);
		timeCb.setChecked(false);

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
				mHandler.removeCallbacks(timeout);
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
				mHandler.removeCallbacks(timeout);
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
		if(curComItem==1){
			writeValue=sysTime.getText().toString();
		}else if(curComItem==0){
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
	

	
	private void calSelectedItem(boolean read){
		selectedItem.clear();
		if(read){
			selectedItem.add(0);
			selectedItem.add(1);
		}else{
			if(dateCb.isChecked())selectedItem.add(0);
			if(timeCb.isChecked())selectedItem.add(1);
		}
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
		calSelectedItem(true);
		LogUtil.logMessage("wzb", "selecteditem:"+selectedItem);
		if (!WApplication.bt.isConnected()) {
			ToastUtil.showShortToast(mContext, mContext.getResources().getString(R.string.bt_disconnect_show));
		} else {

			if (selectedItem == null || selectedItem.size() == 0) {
				ToastUtil.showShortToast(mContext, mContext.getResources().getString(R.string.select_item));
			} else {
				//CustomDialog.showWaitDialog(mContext, "读取中...");
				CustomDialog.showWaitAndCancelDialog(mContext, mContext.getResources().getString(R.string.reading), waitcancleListener);
				initCom();
				//mHandler.sendEmptyMessageDelayed(0xffff, 5000);
				mHandler.postDelayed(timeout, 5000);
			}
		}
	}
	
	private void test_write() {
		if(!WApplication.sp.get("current_level", "ReadUser").equals("AdminUser")
				&& !WApplication.sp.get("current_level", "ReadUser").equals("ProgramUser")){
			ToastUtil.showShortToast(mContext, mContext.getResources().getString(R.string.no_permission));
			return;
		}
		isRead=false;
		//get selectedItem
		calSelectedItem(false);
		LogUtil.logMessage("wzb", "selecteditem:"+selectedItem);
		if (selectedItem == null || selectedItem.size() == 0) {
			ToastUtil.showShortToast(mContext, mContext.getResources().getString(R.string.select_item));
			return;
		}
		if (!WApplication.bt.isConnected()) {
			ToastUtil.showShortToast(mContext, mContext.getResources().getString(R.string.bt_disconnect_show));
		} else {

			if (selectedItem == null || selectedItem.size() == 0) {
				ToastUtil.showShortToast(mContext, mContext.getResources().getString(R.string.select_item));
			} else {
				//CustomDialog.showWaitDialog(mContext, "读取中...");
				CustomDialog.showWaitAndCancelDialog(mContext, mContext.getResources().getString(R.string.writing), waitcancleListener);
				initCom();
				//mHandler.sendEmptyMessageDelayed(0xffff, 5000);
				mHandler.postDelayed(timeout, 5000);
			}
		}
	}
	
	Runnable timeout = new Runnable() {
        @Override
        public void run() {
        	if(curComCmd!=0xff0002 && curComCmd!=0xff0003){
				CustomDialog.dismissDialog();
				ToastUtil.showLongToast(mContext, mContext.getResources().getString(R.string.read_fail));
			}
        }
    };


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
		dateTime[0] = String.format("%2d", t.year).replace(" ","0");//"" + t.year;
		dateTime[1] = String.format("%2d", t.month + 1).replace(" ","0");//"" + (t.month + 1);
		dateTime[2] = String.format("%2d", t.monthDay).replace(" ","0");//"" + t.monthDay;
		dateTime[3] = String.format("%2d", t.hour).replace(" ","0");//"" + t.hour;
		dateTime[4] = String.format("%2d", t.minute).replace(" ","0");//"" + t.minute;
		dateTime[5] = String.format("%2d", t.second).replace(" ","0");//"" + t.second;
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
