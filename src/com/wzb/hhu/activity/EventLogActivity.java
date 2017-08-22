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

import android.content.Context;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class EventLogActivity extends BaseActivity implements OnClickListener, OnScrollListener {
	public static final int UPDATE_BT_STATE = 0xff0001;
	private ImageView backView;
	private TextView titleView;
	private ImageView btView;
	private ImageView titleMeterList;

	private Context mContext;

	private Button readBtn, exportBtn, returnBtn;

	ArrayList<Integer> selectedItem = null;

	private EventAdapter eventAdapter;

	private ListView eventListView = null;

	private String meterSn, meterPw;

	private static int curComCmd = 0xff;
	private static int curItemId = 0;
	Drawable drawableUp, drawableDown;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_eventlog);
		mContext = EventLogActivity.this;
		meterSn = getIntent().getStringExtra("meter_sn");
		meterPw = getIntent().getStringExtra("meter_pw");
		drawableUp = mContext.getResources().getDrawable(R.drawable.up_icon);
		drawableDown = mContext.getResources().getDrawable(R.drawable.down_icon);
		initTitleView();
		initView();
	}

	private void initView() {
		readBtn = (Button) findViewById(R.id.event_read_btn);
		readBtn.setOnClickListener(this);
		exportBtn = (Button) findViewById(R.id.event_export_btn);
		exportBtn.setOnClickListener(this);
		returnBtn = (Button) findViewById(R.id.event_back_btn);
		returnBtn.setOnClickListener(this);

		selectedItem = new ArrayList<Integer>();

		eventListView = (ListView) findViewById(R.id.lv_eventlog);

		initAdapter();

		eventListView.setAdapter(eventAdapter);
		eventListView.setOnScrollListener(this);
		eventListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub

				eventAdapter.getDataItem(arg2).cbToggle();
				if (arg2 == 0) {
					if (eventAdapter.getDataItem(arg2).getItemSelect()) {
						eventAdapter.selectAll();
					} else {
						eventAdapter.unSelectAll();
					}
				}
				
				if(arg2==2){
					eventAdapter.set2ItemsSelect(eventAdapter.getDataItem(arg2).getItemSelect());
				}
				
				if(arg2==4+10){
					eventAdapter.set4ItemsSelect(eventAdapter.getDataItem(arg2).getItemSelect());
				}
				if(arg2==6+10*2){
					eventAdapter.set6ItemsSelect(eventAdapter.getDataItem(arg2).getItemSelect());
				}
				if(arg2==8+10*3){
					eventAdapter.set8ItemsSelect(eventAdapter.getDataItem(arg2).getItemSelect());
				}
				if(arg2==10+10*4){
					eventAdapter.set10ItemsSelect(eventAdapter.getDataItem(arg2).getItemSelect());
				}

				LogUtil.logMessage("wzb", "cb:" + arg2 + " " + eventAdapter.getDataItem(arg2).getItemSelect());
				eventAdapter.notifyDataSetChanged();
			}

		});

	}

	private void initAdapter() {
		List<DataItemBean> dataItems = new ArrayList<DataItemBean>();

		String x[] = ResTools.getResStringArray(EventLogActivity.this, R.array.event);
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
			dataItems.add(item);
		}

		eventAdapter = new EventAdapter(dataItems);
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
		titleMeterList = (ImageView) findViewById(R.id.title_meterlist);
		titleMeterList.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				WApplication.sp.set("current_activity", "3");
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
		titleView.setText(ResTools.getResString(EventLogActivity.this, R.string.event_log) + ":\n" + meterSn);

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
				mHandler.removeCallbacks(timeout);
				updateUI(rString, selectedItem.get(curItemId));
				curItemId++;
				if (curItemId > selectedItem.size() - 1) {
					LogUtil.logMessage("wzb", "read completed");
					CustomDialog.dismissDialog();
					closeCon();
					ToastUtil.showLongToast(mContext, mContext.getResources().getString(R.string.read_ok));
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
		if(TextUtils.isEmpty(ainfo)){
			ainfo="-/-";
		}
		eventAdapter.updateDataItem(id, ainfo, "Y");
		eventAdapter.notifyDataSetChanged();
	}

	private void sendDataItemCmd() {
		int curComItem = selectedItem.get(curItemId);
		String cmd = ResTools.getResStringArray(mContext, R.array.event_cmd)[curComItem];
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
				LogUtil.logMessage("wzb", "EventLogActivity datarec:" + dataString + " msg:" + message);

				Message msg = mHandler.obtainMessage();
				msg.what = curComCmd;
				msg.obj = dataString;
				mHandler.sendMessage(msg);

			}
		});

		WApplication.bt.setBluetoothConnectionListener(new BluetoothConnectionListener() {
			public void onDeviceConnected(String name, String address) {
				LogUtil.logMessage("wzb", "EventLogActivity onDeviceConnected");
				mHandler.sendEmptyMessage(UPDATE_BT_STATE);
			}

			public void onDeviceDisconnected() {
				LogUtil.logMessage("wzb", "EventLogActivity onDeviceDisconnected");
				mHandler.sendEmptyMessage(UPDATE_BT_STATE);
			}

			public void onDeviceConnectionFailed() {
				LogUtil.logMessage("wzb", "EventLogActivity onDeviceConnectionFailed");
				mHandler.sendEmptyMessage(UPDATE_BT_STATE);
			}
		});

	}
	String savePath="";
	private void exportData() {
		calSelectedItem();
		if(selectedItem ==null || selectedItem.size()==0){
			ToastUtil.showShortToast(mContext, mContext.getResources().getString(R.string.select_item));
			return;
		}
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
			ToastUtil.showShortToast(mContext, mContext.getResources().getString(R.string.storage_unavailable));
			return;
		}
		String tempDate = new SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis());
		String currentSn=WApplication.sp.get("current_sn", "null");
		savePath = path + "/" +currentSn+"_"+ tempDate + ".txt";
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(savePath, true);
			for (int i = 0; i < eventAdapter.getCount(); i++) {
				DataItemBean item = eventAdapter.getDataItem(i);
				if (!TextUtils.isEmpty(item.getItemValue())) {

					outputStream.write(item.getItemName().getBytes());
					outputStream.write(" ".getBytes());
					outputStream.write(item.getItemValue().getBytes());
					outputStream.write("\r\n".getBytes());

				}
			}
			outputStream.close();
			new Handler().postDelayed(new Runnable() {
				public void run() {
					CustomDialog.dismissDialog();
					ToastUtil.showLongToast(mContext, mContext.getResources().getString(R.string.file_saved_in)+savePath);
				}
			}, 2000);
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
		case R.id.event_read_btn:
			LogUtil.logMessage("wzb", "" + selectedItem);
			LogUtil.logMessage("wzb", "sn:" + meterSn + " pw:" + meterPw);
			test_read();
			break;
		case R.id.event_back_btn:
			finish();
			break;
		case R.id.event_export_btn:
			exportData();
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
		for (int i = 0; i < eventAdapter.getCount(); i++) {
			if (eventAdapter.getDataItem(i).getItemSelect()) {
				if (i != 0) {
					selectedItem.add(i);
				}
			}
		}
	}

	private void test_read() {
		// get selectedItem
		calSelectedItem();
		LogUtil.logMessage("wzb", "selecteditem:" + selectedItem);
		if (!WApplication.bt.isConnected()) {
			ToastUtil.showShortToast(mContext, mContext.getResources().getString(R.string.bt_disconnect_show));
		} else {

			if (selectedItem == null || selectedItem.size() == 0) {
				ToastUtil.showShortToast(mContext, mContext.getResources().getString(R.string.select_item));
			} else {
				// CustomDialog.showWaitDialog(mContext, "读取中...");
				CustomDialog.showWaitAndCancelDialog(mContext, mContext.getResources().getString(R.string.reading), new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						closeCon();
						CustomDialog.dismissDialog();
					}
				});
				initCom();
				// mHandler.sendEmptyMessageDelayed(0xffff, 5000);
				mHandler.postDelayed(timeout, 5000);
			}
		}
	}
	
	Runnable timeout = new Runnable() {
        @Override
        public void run() {
        	if(curComCmd!=0xff0002){
				CustomDialog.dismissDialog();
				ToastUtil.showLongToast(mContext, mContext.getResources().getString(R.string.read_fail));
			}
        }
    };

	class EventAdapter extends BaseAdapter {

		List<DataItemBean> dataItems;

		public EventAdapter(List<DataItemBean> dataItems) {
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
			//itemList.setVisibility(View.GONE);
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
			
			
			if(position==2){
				itemList.setVisibility(View.VISIBLE);
				itemList.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						LogUtil.logMessage("wzb", "11 Energy");
						dataItems.get(2).isUpToggle();
						set2ItemsHide(dataItems.get(2).getisUp());
						eventAdapter.notifyDataSetChanged();
					}
				});
			}
			
			if(position==4+10){
				itemList.setVisibility(View.VISIBLE);
				itemList.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						LogUtil.logMessage("wzb", "11 Energy");
						dataItems.get(4+10).isUpToggle();
						set4ItemsHide(dataItems.get(4+10).getisUp());
						eventAdapter.notifyDataSetChanged();
					}
				});
			}
			
			if(position==6+10*2){
				itemList.setVisibility(View.VISIBLE);
				itemList.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						LogUtil.logMessage("wzb", "11 Energy");
						dataItems.get(6+10*2).isUpToggle();
						set6ItemsHide(dataItems.get(6+10*2).getisUp());
						eventAdapter.notifyDataSetChanged();
					}
				});
			}
			
			if(position==8+10*3){
				itemList.setVisibility(View.VISIBLE);
				itemList.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						LogUtil.logMessage("wzb", "11 Energy");
						dataItems.get(8+10*3).isUpToggle();
						set8ItemsHide(dataItems.get(8+10*3).getisUp());
						eventAdapter.notifyDataSetChanged();
					}
				});
			}
			
			if(position==10+10*4){
				itemList.setVisibility(View.VISIBLE);
				itemList.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						LogUtil.logMessage("wzb", "11 Energy");
						dataItems.get(10+10*4).isUpToggle();
						set10ItemsHide(dataItems.get(10+10*4).getisUp());
						eventAdapter.notifyDataSetChanged();
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
				if (position == 2 || position == 4+10 || position == 6+10*2 
						|| position == 8+10*3 || position == 10+10*4
					) {
					itemList.setVisibility(View.VISIBLE);
				}
			}
			

			return convertView;
		}
		
		
		public void set2ItemsSelect(Boolean select){
			for(int i=2;i<3+10;i++){
				dataItems.get(i).setItemSelect(select);
			}
		}
		public void set4ItemsSelect(Boolean select){
			for(int i=4+10;i<5+10*2;i++){
				dataItems.get(i).setItemSelect(select);
			}
		}
		public void set6ItemsSelect(Boolean select){
			for(int i=6+10*2;i<7+10*3;i++){
				dataItems.get(i).setItemSelect(select);
			}
		}
		public void set8ItemsSelect(Boolean select){
			for(int i=8+10*3;i<9+10*4;i++){
				dataItems.get(i).setItemSelect(select);
			}
		}
		public void set10ItemsSelect(Boolean select){
			for(int i=10+10*4;i<11+10*5;i++){
				dataItems.get(i).setItemSelect(select);
			}
		}
		
		
		public void set2ItemsHide(Boolean select){
			for(int i=2+1;i<3+10;i++){
				dataItems.get(i).setisHide(select);
			}
		}
		public void set4ItemsHide(Boolean select){
			for(int i=4+1+10;i<5+10*2;i++){
				dataItems.get(i).setisHide(select);
			}
		}
		public void set6ItemsHide(Boolean select){
			for(int i=6+1+10*2;i<7+10*3;i++){
				dataItems.get(i).setisHide(select);
			}
		}
		public void set8ItemsHide(Boolean select){
			for(int i=8+1+10*3;i<9+10*4;i++){
				dataItems.get(i).setisHide(select);
			}
		}
		public void set10ItemsHide(Boolean select){
			for(int i=10+1+10*4;i<11+10*5;i++){
				dataItems.get(i).setisHide(select);
			}
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
