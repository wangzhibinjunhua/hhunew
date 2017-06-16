package com.wzb.hhu.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.wzb.hhu.R;
import com.wzb.hhu.bean.DataItemBean;
import com.wzb.hhu.interf.WApplication;
import com.wzb.hhu.util.Common;
import com.wzb.hhu.util.LogUtil;
import com.wzb.hhu.util.ResTools;
import com.wzb.spp.BluetoothSPP.BluetoothConnectionListener;
import com.wzb.spp.BluetoothSPP.OnDataReceivedListener;
import com.wzb.spp.DeviceList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
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
	public static final int UPDATE_BT_STATE=0xff0001;
	private ImageView backView;
	private TextView titleView;
	private ImageView btView;

	private Context mContext;

	private Button readBtn, stopBtn, exportBtn, returnBtn;

	ArrayList<String> eventListStr = null;
	private List<HashMap<String, Object>> eventList = null;
	private EventAdapter eventAdapter;

	private ListView eventListView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_eventlog);
		mContext = EventLogActivity.this;
		initTitleView();
		initView();
	}

	private void initView() {
		readBtn = (Button) findViewById(R.id.event_read_btn);
		readBtn.setOnClickListener(this);
		stopBtn = (Button) findViewById(R.id.event_stop_btn);
		stopBtn.setOnClickListener(this);
		exportBtn = (Button) findViewById(R.id.event_export_btn);
		exportBtn.setOnClickListener(this);
		returnBtn = (Button) findViewById(R.id.event_back_btn);
		returnBtn.setOnClickListener(this);

		eventListStr = new ArrayList<String>();

		eventListView = (ListView) findViewById(R.id.lv_eventlog);

		initAdapter();

		eventListView.setAdapter(eventAdapter);
		eventListView.setOnScrollListener(this);
		eventListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				if (arg2 == 0) {
					if (eventAdapter.getDataItem(arg2).getItemSelect()) {
						eventAdapter.unSelectAll();
					} else {
						eventAdapter.selectAll();
					}
				} else {
					eventAdapter.getDataItem(arg2).cbToggle();
					if (eventAdapter.getDataItem(arg2).getItemSelect()) {
						eventListStr.add("" + arg2);
					} else {
						eventListStr.remove("" + arg2);
					}
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
			dataItems.add(item);
		}

		eventAdapter = new EventAdapter(dataItems);
	}

	private void initTitleView() {
		backView = (ImageView) findViewById(R.id.title_back);
		titleView = (TextView) findViewById(R.id.title_text);
		titleView.setText(ResTools.getResString(EventLogActivity.this, R.string.event_log));
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
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setBtListener();
		updateBtState();
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
	
	Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case UPDATE_BT_STATE:
				updateBtState();
				break;
			default:
				break;
			}
		};
	};

	String password = "";

	private void setBtListener() {
		WApplication.bt.setOnDataReceivedListener(new OnDataReceivedListener() {
			public void onDataReceived(byte[] data, String message) {
				String dataString = Common.bytesToHexString(data);
				LogUtil.logMessage("wzb", "EventLogActivity datarec:" + dataString + " msg:" + message);
				if (dataString.startsWith("0150300228")) {
					password = dataString.substring(dataString.indexOf("28") + 2, dataString.indexOf("29"));
					LogUtil.logMessage("wzb", "password=" + password);
					password = Common.asciiToString(password);
					LogUtil.logMessage("wzb", "ascii password=" + password);
				}
				// Toast.makeText(SimpleActivity.this, message,
				// Toast.LENGTH_SHORT).show();
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.event_read_btn:
			LogUtil.logMessage("wzb", "" + eventListStr);
			break;
		case R.id.event_back_btn:
			finish();
			break;
		case R.id.event_export_btn:
			break;
		case R.id.event_stop_btn:
			break;
		default:
			break;
		}
	}

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
