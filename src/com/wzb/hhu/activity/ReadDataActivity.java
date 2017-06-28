package com.wzb.hhu.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.LogManager;

import com.wzb.hhu.R;
import com.wzb.hhu.bean.AmmeterBean;
import com.wzb.hhu.bean.DataItemBean;
import com.wzb.hhu.btcore.IECCommand;
import com.wzb.hhu.interf.WApplication;
import com.wzb.hhu.util.Common;
import com.wzb.hhu.util.CustomDialog;
import com.wzb.hhu.util.LogUtil;
import com.wzb.hhu.util.ResTools;
import com.wzb.hhu.util.ToastUtil;
import com.wzb.hhu.view.DataViewHolder;
import com.wzb.spp.BluetoothSPP.BluetoothConnectionListener;
import com.wzb.spp.BluetoothSPP.OnDataReceivedListener;
import com.wzb.spp.DeviceList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import junit.framework.Test;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author wzb<wangzhibin_x@qq.com>
 * @date May 9, 2017 9:51:19 AM
 */
public class ReadDataActivity extends BaseActivity implements OnScrollListener, OnClickListener {

	public static final int UPDATE_BT_STATE = 0xff0001;

	private ImageView backView;
	private TextView titleView;
	private ImageView titleMeterList;
	private ImageView btView;
	private Context mContext;
	private Button readBtn, stopBtn, exportBtn, returnBtn;

	ArrayList<Integer> selectedItem = null;
	private DataAdapter ElecAdapter;

	private ListView ElecListView = null;

	private String meterSn, meterPw;

	private static int curComCmd = 0xff;
	private static int curItemId = 0;
	List<DataItemBean> mdataItems;
	Drawable drawableUp, drawableDown;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_readdata);
		mContext = ReadDataActivity.this;
		// meterSn = getIntent().getStringExtra("meter_sn");
		// meterPw = getIntent().getStringExtra("meter_pw");
		drawableUp = mContext.getResources().getDrawable(R.drawable.up_icon);
		drawableDown = mContext.getResources().getDrawable(R.drawable.down_icon);
		initTitleView();
		initView();
	}

	private void initView() {
		readBtn = (Button) findViewById(R.id.data_read_btn);
		readBtn.setOnClickListener(this);
		stopBtn = (Button) findViewById(R.id.data_stop_btn);
		stopBtn.setOnClickListener(this);
		exportBtn = (Button) findViewById(R.id.data_export_btn);
		exportBtn.setOnClickListener(this);
		returnBtn = (Button) findViewById(R.id.data_back_btn);
		returnBtn.setOnClickListener(this);

		selectedItem = new ArrayList<Integer>();

		ElecListView = (ListView) findViewById(R.id.lv_data);

		initAdapter();

		ElecListView.setAdapter(ElecAdapter);
		ElecListView.setOnScrollListener(this);
		ElecListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				ElecAdapter.getDataItem(arg2).cbToggle();
				// if (ElecAdapter.getDataItem(arg2).getItemSelect()) {
				// selectedItem.add(arg2);
				// } else {
				// selectedItem.remove(arg2);
				// }
				if (arg2 == 0) {
					ElecAdapter.setAllItemsSelect(ElecAdapter.getDataItem(arg2).getItemSelect());
				}
				if (arg2 == 1) {
					ElecAdapter.setEnergyItemsSelect(ElecAdapter.getDataItem(arg2).getItemSelect());
				}

				if (arg2 == 14) {
					ElecAdapter.setRateItemsSelect(ElecAdapter.getDataItem(arg2).getItemSelect());
				}

				if (arg2 == 23) {
					ElecAdapter.setDemandItemsSelect(ElecAdapter.getDataItem(arg2).getItemSelect());
				}

				if (arg2 == 27) {
					ElecAdapter.setVoltageItemsSelect(ElecAdapter.getDataItem(arg2).getItemSelect());
				}

				if (arg2 == 31) {
					ElecAdapter.setCurrentItemsSelect(ElecAdapter.getDataItem(arg2).getItemSelect());
				}

				if (arg2 == 36) {
					ElecAdapter.setPowerItemsSelect(ElecAdapter.getDataItem(arg2).getItemSelect());
				}

				LogUtil.logMessage("wzb", "cb:" + arg2 + " " + ElecAdapter.getDataItem(arg2).getItemSelect());
				ElecAdapter.notifyDataSetChanged();
			}
		});
	}

	private void initAdapter() {
		mdataItems = new ArrayList<DataItemBean>();

		String x[] = ResTools.getResStringArray(ReadDataActivity.this, R.array.elec);
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
			item.setisHide(false);
			item.setisUp(false);
			mdataItems.add(item);
		}

		ElecAdapter = new DataAdapter(mdataItems);

	}

	private void initTitleView() {
		backView = (ImageView) findViewById(R.id.title_back);
		titleView = (TextView) findViewById(R.id.title_text);
		// titleView.setText(ResTools.getResString(ReadDataActivity.this,
		// R.string.read_data)+":"+meterSn);
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

		titleMeterList = (ImageView) findViewById(R.id.title_meterlist);
		titleMeterList.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				WApplication.sp.set("current_activity", "0");
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
		titleView.setText(ResTools.getResString(mContext, R.string.read_data) + ":\n" + meterSn);
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
					curComCmd = 0xff0002;
					curItemId = 0;
					sendDataItemCmd();

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
		ElecAdapter.updateDataItem(id, ainfo, "Y");
		ElecAdapter.notifyDataSetChanged();
	}

	private void sendDataItemCmd() {
		int curComItem = selectedItem.get(curItemId);
		String cmd = ResTools.getResStringArray(mContext, R.array.elec_cmd)[curComItem];
		if (curComItem == 3 || curComItem == 5 || curComItem == 7 || curComItem == 9 || curComItem == 11
				|| curComItem == 13) {
			cmd = cmd + IECCommand.MONTH_DEFAULT_NUM;
		}

		if (curComItem == 15 || curComItem == 17 || curComItem == 19 || curComItem == 21) {
			cmd = cmd + IECCommand.RATE_DEFAULT_NUM;
		}

		if (curComItem == 16 || curComItem == 18 || curComItem == 20 || curComItem == 22) {
			cmd = cmd + IECCommand.RATE_DEFAULT_NUM + "*" + IECCommand.MONTH_DEFAULT_NUM;
		}
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
				LogUtil.logMessage("wzb", "ReadDataActivity onDeviceConnected");
				mHandler.sendEmptyMessage(UPDATE_BT_STATE);
			}

			public void onDeviceDisconnected() {
				LogUtil.logMessage("wzb", "ReadDataActivity onDeviceDisconnected");
				mHandler.sendEmptyMessage(UPDATE_BT_STATE);
			}

			public void onDeviceConnectionFailed() {
				LogUtil.logMessage("wzb", "ReadDataActivity onDeviceConnectionFailed");
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
			for (int i = 0; i < ElecAdapter.getCount(); i++) {
				DataItemBean item = ElecAdapter.getDataItem(i);
				if (!TextUtils.isEmpty(item.getItemValue())) {

					outputStream.write(item.getItemName().getBytes());
					outputStream.write(" ".getBytes());
					outputStream.write(item.getItemValue().getBytes());
					outputStream.write("\r\n".getBytes());

				}
			}
			outputStream.close();
			CustomDialog.dismissDialog();
			ToastUtil.showLongToast(mContext, "file saved in "+savePath);
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
		case R.id.data_read_btn:
			LogUtil.logMessage("wzb", "" + selectedItem);
			LogUtil.logMessage("wzb", "sn:" + meterSn + " pw:" + meterPw);
			test_read();
			break;
		case R.id.data_back_btn:
			finish();
			break;
		case R.id.data_export_btn:
			exportData();
			break;
		case R.id.data_stop_btn:
			CustomDialog.dismissDialog();
			break;
		default:
			break;
		}
	}

	private void initCom() {
		curComCmd = 0xfe;
		String sendData = "2f3f" + Common.stringToAscii(meterSn) + "210d0a";
		LogUtil.logMessage("wzb", "senddata=" + sendData);
		IECCommand.sppSend(sendData);
	}

	private void calSelectedItem() {
		selectedItem.clear();
		for (int i = 0; i < ElecAdapter.getCount(); i++) {
			if (ElecAdapter.getDataItem(i).getItemSelect()) {
				if (i != 0 || i != 1 || i != 23 || i != 27 || i != 31 || i != 36) {
					selectedItem.add(i);
				}
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

	private void test_read() {
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
				CustomDialog.showWaitAndCancelDialog(mContext, "读取中...", waitcancleListener);
				initCom();
				// mHandler.sendEmptyMessageDelayed(0xffff, 5000);
			}
		}
	}

	class DataAdapter extends BaseAdapter {

		List<DataItemBean> dataItems;

		public DataAdapter(List<DataItemBean> dataItems) {
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
			itemList.setClickable(true);

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

			// test
			// if(tvName.getText().equals("Item Name")){
			if (position == 0) {
				itemList.setVisibility(View.VISIBLE);
				itemList.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						LogUtil.logMessage("wzb", "11 item name");
						dataItems.get(0).isUpToggle();
						dataItems.get(1).setisUp(false);
						dataItems.get(14).setisUp(false);
						dataItems.get(23).setisUp(false);
						dataItems.get(27).setisUp(false);
						dataItems.get(31).setisUp(false);
						dataItems.get(36).setisUp(false);
						setAllItemsHide(dataItems.get(0).getisUp());
						ElecAdapter.notifyDataSetChanged();
					}
				});
			}

			// if(tvName.getText().equals("Energy")){
			if (position == 1) {
				itemList.setVisibility(View.VISIBLE);
				itemList.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						LogUtil.logMessage("wzb", "11 Energy");
						dataItems.get(1).isUpToggle();
						setEnergyItemsHide(dataItems.get(1).getisUp());
						ElecAdapter.notifyDataSetChanged();
					}
				});
			}

			if (position == 14) {
				itemList.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						LogUtil.logMessage("wzb", "11 Energy");
						dataItems.get(14).isUpToggle();
						setRateItemsHide(dataItems.get(14).getisUp());
						ElecAdapter.notifyDataSetChanged();
					}
				});
			}

			if (position == 23) {
				itemList.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						LogUtil.logMessage("wzb", "11 Energy");
						dataItems.get(23).isUpToggle();
						setDemandItemsHide(dataItems.get(23).getisUp());
						ElecAdapter.notifyDataSetChanged();
					}
				});
			}

			if (position == 27) {
				itemList.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						LogUtil.logMessage("wzb", "11 Energy");
						dataItems.get(27).isUpToggle();
						setVoltageItemsHide(dataItems.get(27).getisUp());
						ElecAdapter.notifyDataSetChanged();
					}
				});
			}

			if (position == 31) {
				itemList.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						LogUtil.logMessage("wzb", "11 Energy");
						dataItems.get(31).isUpToggle();
						setCurrentItemsHide(dataItems.get(31).getisUp());
						ElecAdapter.notifyDataSetChanged();
					}
				});
			}

			if (position == 36) {
				itemList.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						LogUtil.logMessage("wzb", "11 Energy");
						dataItems.get(36).isUpToggle();
						setPowerItemsHide(dataItems.get(36).getisUp());
						ElecAdapter.notifyDataSetChanged();
					}
				});
			}

			if (dataItems.get(position).getisUp()) {
				itemList.setBackground(drawableDown);
			} else {
				itemList.setBackground(drawableUp);
			}

			if (dataItems.get(position).getisHide()) {
				tvName.setVisibility(View.GONE);
				tvValue.setVisibility(View.GONE);
				tvState.setVisibility(View.GONE);
				cb.setVisibility(View.GONE);
				itemList.setVisibility(View.GONE);
			} else {
				tvName.setVisibility(View.VISIBLE);
				tvValue.setVisibility(View.VISIBLE);
				tvState.setVisibility(View.VISIBLE);
				cb.setVisibility(View.VISIBLE);
				itemList.setVisibility(View.INVISIBLE);
				if (position == 0 || position == 1 || position == 14 || position == 23 || position == 27
						|| position == 31 || position == 36) {
					itemList.setVisibility(View.VISIBLE);
				}
			}

			return convertView;
		}

		public DataItemBean getDataItem(int id) {

			return dataItems.get(id);
		}

		public void updateDataItem(int id, String value, String state) {
			dataItems.get(id).setItemValue(value);
			dataItems.get(id).setItemState(state);
		}

		public void setAllItemsSelect(Boolean select) {
			for (int i = 0; i < dataItems.size(); i++) {
				dataItems.get(i).setItemSelect(select);
			}
		}

		public void setEnergyItemsSelect(Boolean select) {
			for (int i = 1; i < 14; i++) {
				dataItems.get(i).setItemSelect(select);
			}
		}

		public void setRateItemsSelect(Boolean select) {
			for (int i = 14; i < 23; i++) {
				dataItems.get(i).setItemSelect(select);
			}
		}

		public void setDemandItemsSelect(Boolean select) {
			for (int i = 23; i < 27; i++) {
				dataItems.get(i).setItemSelect(select);
			}
		}

		public void setVoltageItemsSelect(Boolean select) {
			for (int i = 27; i < 31; i++) {
				dataItems.get(i).setItemSelect(select);
			}
		}

		public void setCurrentItemsSelect(Boolean select) {
			for (int i = 31; i < 36; i++) {
				dataItems.get(i).setItemSelect(select);
			}
		}

		public void setPowerItemsSelect(Boolean select) {
			for (int i = 36; i < 53; i++) {
				dataItems.get(i).setItemSelect(select);
			}
		}

		public void setAllItemsHide(Boolean select) {
			for (int i = 1; i < dataItems.size(); i++) {
				dataItems.get(i).setisHide(select);
			}
		}

		public void setEnergyItemsHide(Boolean select) {
			for (int i = 2; i < 14; i++) {
				dataItems.get(i).setisHide(select);
			}
		}

		public void setRateItemsHide(Boolean select) {
			for (int i = 15; i < 23; i++) {
				dataItems.get(i).setisHide(select);
			}
		}

		public void setDemandItemsHide(Boolean select) {
			for (int i = 24; i < 27; i++) {
				dataItems.get(i).setisHide(select);
			}
		}

		public void setVoltageItemsHide(Boolean select) {
			for (int i = 28; i < 31; i++) {
				dataItems.get(i).setisHide(select);
			}
		}

		public void setCurrentItemsHide(Boolean select) {
			for (int i = 32; i < 36; i++) {
				dataItems.get(i).setisHide(select);
			}
		}

		public void setPowerItemsHide(Boolean select) {
			for (int i = 37; i < 53; i++) {
				dataItems.get(i).setisHide(select);
			}
		}

	}

	/*
	 * class DataAdapter extends BaseAdapter { public HashMap<Integer, Boolean>
	 * isSelected; private Context context = null; private LayoutInflater
	 * inflater = null; private List<HashMap<String, Object>> list = null;
	 * private String keyString[] = null; private String itemString = null; //
	 * 记录每个item中textview的值 private int idValue[] = null;// id值
	 *
	 * public DataAdapter(Context context, List<HashMap<String, Object>> list,
	 * int resource, String[] from, int[] to) { this.context = context;
	 * this.list = list; keyString = new String[from.length]; idValue = new
	 * int[to.length]; System.arraycopy(from, 0, keyString, 0, from.length);
	 * System.arraycopy(to, 0, idValue, 0, to.length); inflater =
	 * LayoutInflater.from(context); init(); }
	 *
	 * // 初始化 设置所有checkbox都为未选择 public void init() { isSelected = new
	 * HashMap<Integer, Boolean>(); for (int i = 0; i < list.size(); i++) {
	 * isSelected.put(i, false); } }
	 *
	 * @Override public int getCount() { return list.size(); }
	 *
	 * @Override public Object getItem(int arg0) { return list.get(arg0); }
	 *
	 * @Override public long getItemId(int arg0) { return 0; }
	 *
	 * @Override public View getView(int position, View view, ViewGroup arg2) {
	 * DataViewHolder holder = null; if (holder == null) { holder = new
	 * DataViewHolder(); if (view == null) { view =
	 * inflater.inflate(R.layout.data_list_item, null); } holder.tvName =
	 * (TextView) view.findViewById(R.id.data_item_name); holder.tvValue =
	 * (TextView) view.findViewById(R.id.data_item_value); holder.tvState =
	 * (TextView) view.findViewById(R.id.data_item_state); holder.cb =
	 * (CheckBox) view.findViewById(R.id.data_item_cb); view.setTag(holder); }
	 * else { holder = (DataViewHolder) view.getTag(); } HashMap<String, Object>
	 * map = list.get(position); if (map != null) { itemString = (String)
	 * map.get(keyString[0]); holder.tvName.setText(itemString); }
	 * holder.cb.setChecked(isSelected.get(position)); return view; } }
	 */

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

}
