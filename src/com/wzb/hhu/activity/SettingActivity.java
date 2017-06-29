package com.wzb.hhu.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.wzb.hhu.R;
import com.wzb.hhu.bean.DataItemBean;
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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SettingActivity extends BaseActivity implements OnScrollListener, OnClickListener {
	public static final int UPDATE_BT_STATE = 0xff0001;
	private ImageView backView;
	private TextView titleView;
	private ImageView btView;
	private ImageView titleMeterList;

	private Context mContext;
	private Button readBtn, stopBtn, exportBtn, returnBtn;

	ArrayList<Integer> selectedItem = null;
	private SettingAdapter settingAdapter;

	private ListView settingListView = null;

	private String meterSn, meterPw;

	private static int curComCmd = 0xff;
	private static int curItemId = 0;

	private static Boolean isRead = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_setting);
		mContext = SettingActivity.this;
		// meterSn = getIntent().getStringExtra("meter_sn");
		// meterPw = getIntent().getStringExtra("meter_pw");
		initTitleView();
		initView();
	}

	private void initView() {
		readBtn = (Button) findViewById(R.id.set_read_btn);
		readBtn.setOnClickListener(this);
		stopBtn = (Button) findViewById(R.id.set_write_btn);
		stopBtn.setOnClickListener(this);
		exportBtn = (Button) findViewById(R.id.set_export_btn);
		exportBtn.setOnClickListener(this);
		returnBtn = (Button) findViewById(R.id.set_back_btn);
		returnBtn.setOnClickListener(this);

		selectedItem = new ArrayList<Integer>();

		settingListView = (ListView) findViewById(R.id.lv_setting);

		initAdapter();

		settingListView.setAdapter(settingAdapter);
		settingListView.setOnScrollListener(this);
		settingListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				settingAdapter.getDataItem(arg2).cbToggle();
				if (arg2 == 0) {
					if (settingAdapter.getDataItem(arg2).getItemSelect()) {
						settingAdapter.selectAll();
					} else {
						settingAdapter.unSelectAll();
					}
				}
				LogUtil.logMessage("wzb", "cb:" + arg2 + " " + settingAdapter.getDataItem(arg2).getItemSelect());
				settingAdapter.notifyDataSetChanged();
			}

		});
		settingListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				LogUtil.logMessage("wzb", "setting long click arg2:" + arg2);
				editSettingValue(arg2);
				return true;
			}
		});

	}

	private void editSettingValue(final int id) {

		final EditText et = new EditText(this);
		et.setMaxLines(1);
		et.setSingleLine(true);
		et.setText(settingAdapter.getDataItem(id).getItemValue());
		new AlertDialog.Builder(this).setTitle(settingAdapter.getDataItem(id).getItemName())
				.setIcon(android.R.drawable.ic_dialog_info).setView(et)
				.setPositiveButton("ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						String input = et.getText().toString();
						if (!TextUtils.isEmpty(input)) {
							settingAdapter.getDataItem(id).setItemValue(input);
							settingAdapter.notifyDataSetChanged();
						} else {
							ToastUtil.showShortToast(mContext, "输入数据不能为空");
						}
					}
				}

				).setCancelable(false).setNegativeButton("cancle", null).show();
	}

	private void initAdapter() {
		List<DataItemBean> dataItems = new ArrayList<DataItemBean>();

		String x[] = ResTools.getResStringArray(SettingActivity.this, R.array.setting);
		for (int i = 0; i < x.length; i++) {
			DataItemBean item = new DataItemBean();
			LogUtil.logMessage("wzb", "x=" + x[i]);
			if (i == 0) {
				item.setItemName(x[i]);
				item.setItemValue("Value");
				item.setItemState("State");
				item.setItemSelect(false);
			} else {
				item.setItemName(x[i]);
				item.setItemValue("");
				item.setItemState("");
				item.setItemSelect(false);
			}
			dataItems.add(item);
		}

		settingAdapter = new SettingAdapter(dataItems);
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

				Intent intent = new Intent(mContext, DeviceList.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});
		titleMeterList = (ImageView) findViewById(R.id.title_meterlist);
		titleMeterList.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				WApplication.sp.set("current_activity", "1");
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
		meterSn = WApplication.sp.get("current_sn", "");
		meterPw = WApplication.sp.get("current_pw", "");
		titleView.setText(ResTools.getResString(SettingActivity.this, R.string.meter_setting) + ":\n" + meterSn);
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
				// if (rString.startsWith("2f")) {
				curComCmd = 0xfd;
				String s = "063033310d0a";
				IECCommand.sppSend(s);
				// }
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
					if (isRead) {
						curComCmd = 0xff0002;
						curItemId = 0;
						sendDataItemCmd();
					} else {
						curComCmd = 0xff0003;
						curItemId = 0;
						sendWriteDataItemCmd();
					}

				}
				break;
			case 0xff0002:
				updateUI(rString, selectedItem.get(curItemId));
				curItemId++;
				if (curItemId > selectedItem.size() - 1) {
					LogUtil.logMessage("wzb", "read completed");
					CustomDialog.dismissDialog();
					closeCon();
				} else {
					sendDataItemCmd();
				}
				break;
			case 0xff0003:
				// updateWriteUI(rString, selectedItem.get(curItemId));
				curItemId++;
				if (curItemId > selectedItem.size() - 1) {
					LogUtil.logMessage("wzb", "write completed");
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

	private void closeCon() {
		String string = "01423003";
		String xor = Common.xorHex(string.substring(2));
		IECCommand.sppSend(string + xor);
	}

	private void updateUI(String s, int id) {
		String info = s.substring(s.indexOf("28") + 2, s.indexOf("29"));
		LogUtil.logMessage("wzb", "####: info:" + info);
		String ainfo = Common.asciiToString(info);
		LogUtil.logMessage("wzb", "***: ainfo:" + ainfo);
		settingAdapter.updateDataItem(id, ainfo, "Y");
		settingAdapter.notifyDataSetChanged();
	}

	private void sendDataItemCmd() {
		int curComItem = selectedItem.get(curItemId);
		String cmd = ResTools.getResStringArray(mContext, R.array.setting_cmd)[curComItem];
		String sendData = "01523102" + Common.stringToAscii(cmd) + "282903";
		String sendDataXor = Common.xorHex(sendData.substring(2));
		IECCommand.sppSend(sendData + sendDataXor);
	}

	private void sendPassword(String pw) {
		String crcPw = Common.getMeterPw(pw, meterPw);
		LogUtil.logMessage("wzb", "crcpw=" + crcPw);
		String pwHeadHex = "0150320228";
		String pwEndHex = "2903";
		String pwV = pwHeadHex + Common.str2HexStr(crcPw) + pwEndHex;
		String pwVxor = Common.xorHex(pwV.substring(2));
		IECCommand.sppSend(pwV + pwVxor);
	}

	private void setBtListener() {
		WApplication.bt.setOnDataReceivedListener(new OnDataReceivedListener() {
			public void onDataReceived(byte[] data, String message) {
				String dataString = Common.bytesToHexString(data);
				LogUtil.logMessage("wzb", "ReadDataActivity datarec:" + dataString + " msg:" + message);

				Message msg = mHandler.obtainMessage();
				msg.what = curComCmd;
				msg.obj = dataString;
				mHandler.sendMessage(msg);

			}
		});

		WApplication.bt.setBluetoothConnectionListener(new BluetoothConnectionListener() {
			public void onDeviceConnected(String name, String address) {
				LogUtil.logMessage("wzb", "SettingActivity onDeviceConnected");
				mHandler.sendEmptyMessage(UPDATE_BT_STATE);
			}

			public void onDeviceDisconnected() {
				LogUtil.logMessage("wzb", "SettingActivity onDeviceDisconnected");
				mHandler.sendEmptyMessage(UPDATE_BT_STATE);
			}

			public void onDeviceConnectionFailed() {
				LogUtil.logMessage("wzb", "SettingActivity onDeviceConnectionFailed");
				mHandler.sendEmptyMessage(UPDATE_BT_STATE);
			}
		});

	}

	private void exportData() {
		CustomDialog.showWaitDialog(mContext);
		String path = "";
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			try {
				path = Environment.getExternalStorageDirectory().getCanonicalPath().toString() + "/HHU";
				File files = new File(path);
				if (!files.exists()) {
					files.mkdir();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			ToastUtil.showShortToast(mContext, "设备存储不可用");
			return;
		}
		String tempDate = new SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis());
		String savePath = path + "/" + tempDate + ".txt";
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(savePath, true);
			for (int i = 0; i < settingAdapter.getCount(); i++) {
				DataItemBean item = settingAdapter.getDataItem(i);
				if (!TextUtils.isEmpty(item.getItemValue())) {

					outputStream.write(item.getItemName().getBytes());
					outputStream.write(" ".getBytes());
					outputStream.write(item.getItemValue().getBytes());
					outputStream.write("\r\n".getBytes());

				}
			}
			outputStream.close();
			CustomDialog.dismissDialog();
			ToastUtil.showLongToast(mContext, "file saved in " + savePath);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.set_read_btn:
			LogUtil.logMessage("wzb", "" + selectedItem);
			LogUtil.logMessage("wzb", "sn:" + meterSn + " pw:" + meterPw);
			test_read();
			break;
		case R.id.set_back_btn:
			finish();
			break;
		case R.id.set_export_btn:
			exportData();
			break;
		case R.id.set_write_btn:
			break;
		default:
			break;
		}
	}

	private void sendWriteDataItemCmd() {
		int curComItem = selectedItem.get(curItemId);
		String cmd = ResTools.getResStringArray(mContext, R.array.setting)[curComItem];
		String writeValue = "";
		writeValue = settingAdapter.getDataItem(curComItem).getItemValue();
		String sendData = "01573102" + Common.stringToAscii(cmd) + "28" + Common.stringToAscii(writeValue) + "2903";
		String sendDataXor = Common.xorHex(sendData.substring(2));
		IECCommand.sppSend(sendData + sendDataXor);
	}

	private void initCom() {
		curComCmd = 0xfe;
		String sendData = "2f3f" + Common.stringToAscii(meterSn) + "210d0a";
		LogUtil.logMessage("wzb", "senddata=" + sendData);
		IECCommand.sppSend(sendData);
	}

	private void calSelectedItem() {
		selectedItem.clear();
		for (int i = 0; i < settingAdapter.getCount(); i++) {
			if (settingAdapter.getDataItem(i).getItemSelect()) {
				if (i != 0) {
					selectedItem.add(i);
				}
			}
		}
	}

	private void calWriteSelectedItem() {
		selectedItem.clear();
		for (int i = 0; i < settingAdapter.getCount(); i++) {
			if (settingAdapter.getDataItem(i).getItemSelect()
					&& !TextUtils.isEmpty(settingAdapter.getDataItem(i).getItemValue())) {
				if (i != 0) {
					selectedItem.add(i);
				}
			}
		}
	}

	private void test_read() {
		isRead = true;
		// get selectedItem
		calSelectedItem();
		LogUtil.logMessage("wzb", "selecteditem:" + selectedItem);
		if (!WApplication.bt.isConnected()) {
			ToastUtil.showShortToast(mContext, "蓝牙处于断开状态，请连接");
		} else {

			if (selectedItem == null || selectedItem.size() == 0) {
				ToastUtil.showShortToast(mContext, "请选择需要读取的数据项");
			} else {
				// CustomDialog.showWaitDialog(mContext, "读取中...");
				CustomDialog.showWaitAndCancelDialog(mContext, "读取中...", new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						closeCon();
						CustomDialog.dismissDialog();
					}
				});
				initCom();
				// mHandler.sendEmptyMessageDelayed(0xffff, 5000);
			}
		}
	}

	private void test_write() {

		if (!WApplication.sp.get("current_level", "ReadUser").equals("AdminUser")
				|| !WApplication.sp.get("current_level", "ReadUser").equals("ProgramUser")) {
			ToastUtil.showShortToast(mContext, "没有权限");
			return;
		}
		isRead = false;
		calWriteSelectedItem();
		if (!WApplication.bt.isConnected()) {
			ToastUtil.showShortToast(mContext, "蓝牙处于断开状态，请连接");
		} else {

			if (selectedItem == null || selectedItem.size() == 0) {
				ToastUtil.showShortToast(mContext, "请选择需要写入的数据项");
			} else {
				// CustomDialog.showWaitDialog(mContext, "读取中...");
				CustomDialog.showWaitAndCancelDialog(mContext, "写入中...", waitcancleListener);
				initCom();
				// mHandler.sendEmptyMessageDelayed(0xffff, 5000);
			}
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

	class SettingAdapter extends BaseAdapter {

		List<DataItemBean> dataItems;

		public SettingAdapter(List<DataItemBean> dataItems) {
			// TODO Auto-generated constructor stub
			this.dataItems = dataItems;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return dataItems.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return dataItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.data_list_item, null);
			}
			ImageView itemList = (ImageView) convertView.findViewById(R.id.item_list);
			itemList.setVisibility(View.GONE);
			TextView tvName = (TextView) convertView.findViewById(R.id.data_item_name);
			TextView tvValue = (TextView) convertView.findViewById(R.id.data_item_value);
			TextView tvState = (TextView) convertView.findViewById(R.id.data_item_state);
			CheckBox cb = (CheckBox) convertView.findViewById(R.id.data_item_cb);
			tvName.setText(dataItems.get(position).getItemName());
			tvValue.setText(dataItems.get(position).getItemValue());
			tvState.setText(dataItems.get(position).getItemState());
			cb.setChecked(dataItems.get(position).getItemSelect());

			int[] colors = { Color.WHITE, Color.rgb(219, 238, 244) };// RGB颜色

			convertView.setBackgroundColor(colors[position % 2]);// 每隔item之间颜色不同

			return convertView;
		}

		public DataItemBean getDataItem(int id) {

			return dataItems.get(id);
		}

		public void updateDataItem(int id, String value, String state) {
			dataItems.get(id).setItemValue(value);
			dataItems.get(id).setItemState(state);
		}

		public void selectAll() {
			for (int i = 0; i < dataItems.size(); i++) {
				dataItems.get(i).setItemSelect(true);
			}
		}

		public void unSelectAll() {
			for (int i = 0; i < dataItems.size(); i++) {
				dataItems.get(i).setItemSelect(false);
			}
		}

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

}
