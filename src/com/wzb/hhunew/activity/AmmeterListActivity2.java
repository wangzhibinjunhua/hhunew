package com.wzb.hhunew.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.RedirectException;

import com.wzb.hhunew.R;
import com.wzb.hhunew.bean.AmmeterBean;
import com.wzb.hhunew.interf.WApplication;
import com.wzb.hhunew.util.Common;
import com.wzb.hhunew.util.CustomDialog;
import com.wzb.hhunew.util.DbUtil;
import com.wzb.hhunew.util.LogUtil;
import com.wzb.hhunew.util.ResTools;
import com.wzb.sppnew.BluetoothState;
import com.wzb.sppnew.DeviceList;
import com.wzb.sppnew.BluetoothSPP.BluetoothConnectionListener;
import com.wzb.sppnew.BluetoothSPP.OnDataReceivedListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

/**
 * @author wzb<wangzhibin_x@qq.com>
 * @date May 4, 2017 2:46:37 PM
 */

public class AmmeterListActivity2 extends BaseActivity implements OnScrollListener {

	private ImageView backView;
	private ImageView btView;
	private TextView titleView;
	private ListView listView;
	private int visibleLastIndex = 0;// 最后的可视项索引
	private int visibleItemCount;// 当前窗口可见项总数
	private int datasize = 38;// 模拟数据

	private int curPosition = -1;
	private AmmeterAdapter adapter;
	private View loadMoreView;
	private Button loadMoreBtn;
	private Handler mHandler = new Handler();
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_ammeterlist);
		mContext = AmmeterListActivity2.this;
		initTitleView();

		loadMoreView = getLayoutInflater().inflate(R.layout.loadmore, null);
		loadMoreBtn = (Button) loadMoreView.findViewById(R.id.load_more_btn);

		loadMoreBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				loadMoreBtn.setText("正在加载..");
				loadMoreBtn.setClickable(false);
				mHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						loadMoreData();
						adapter.notifyDataSetChanged();
						loadMoreBtn.setText("load more ...");
						loadMoreBtn.setClickable(true);
						// listView.setSelection(0);
					}
				}, 2000);
			}
		});

		listView = (ListView) findViewById(R.id.ammeter_lv);
		listView.addFooterView(loadMoreView);
		initAdapter();
		listView.setAdapter(adapter);
		listView.setOnScrollListener(this);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				Log.d("wzb", "arg2=" + arg2 + " " + adapter.getAmmeterBean(arg2).getSn());
				curPosition = arg2;
				adapter.notifyDataSetChanged();
				startReadData(adapter.getAmmeterBean(arg2).getSn());
				// Intent intent = new Intent(AmmeterListActivity.this,
				// ReadDataActivity.class);
				// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				// AmmeterListActivity.this.startActivity(intent);
				// finish();
			}
		});
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				Log.d("wzb", "long click arg2=" + arg2 + " " + adapter.getAmmeterBean(arg2).getSn());
				curPosition = arg2;
				adapter.notifyDataSetChanged();
				return true;
			}
		});

	}

	private void startReadData(String sn) {
		CustomDialog.showOkAndCalcelDialog(mContext, "读取数据", "你确定要操作这个电表吗?" + "\n SN:" + sn, okListener,
				cancleListener);
	}

	OnClickListener okListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			CustomDialog.dismissDialog();

		}
	};

	OnClickListener cancleListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			CustomDialog.dismissDialog();
		}
	};

	private void initTitleView() {
		backView = (ImageView) findViewById(R.id.title_back);
		titleView = (TextView) findViewById(R.id.title_text);
		btView = (ImageView) findViewById(R.id.title_bt);
		titleView.setText(ResTools.getResString(AmmeterListActivity2.this, R.string.ammeter_list));
		backView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

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

	private void initAdapter() {
		List<AmmeterBean> ammeters = new ArrayList<AmmeterBean>();
		for (int i = 1; i <= 10; i++) {
			AmmeterBean items = new AmmeterBean();
			items.setSn("sn" + i);
			items.setPassword("pw" + i);
			items.setLocation("location" + i);
			items.setModel("model" + i);
			ammeters.add(items);

		}

		adapter = new AmmeterAdapter(ammeters);
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

	String password = "";

	private void setBtListener() {
		WApplication.bt.setOnDataReceivedListener(new OnDataReceivedListener() {
			public void onDataReceived(byte[] data, String message) {
				String dataString = Common.bytesToHexString(data);
				LogUtil.logMessage("wzb", "11 datarec:" + dataString + " msg:" + message);
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
				LogUtil.logMessage("wzb", "11 onDeviceConnected");
				updateBtState();
			}

			public void onDeviceDisconnected() {
				LogUtil.logMessage("wzb", "11 onDeviceDisconnected");
				updateBtState();
			}

			public void onDeviceConnectionFailed() {
				LogUtil.logMessage("wzb", "11 onDeviceConnectionFailed");
				updateBtState();
			}
		});

	}

	private void loadMoreData() {
		// test
		String s = "0x2f3f303030303031323334353638210d0a";
		WApplication.bt.send(Common.parseHexStringToBytes(s), false);

		int count = adapter.getCount();

		if (count + 10 <= datasize) {
			for (int i = count + 1; i <= count + 10; i++) {
				AmmeterBean items = new AmmeterBean();
				items.setSn("sn" + i);
				items.setLocation("location" + i);
				items.setModel("model" + i);
				items.setPassword("pw" + i);
				adapter.addAmmeterItem(items);
			}
		} else {
			for (int i = count + 1; i <= datasize; i++) {
				AmmeterBean items = new AmmeterBean();
				items.setSn("sn" + i);
				items.setLocation("location" + i);
				items.setModel("model" + i);
				items.setPassword("pw" + i);
				adapter.addAmmeterItem(items);
			}
		}

	}

	class AmmeterAdapter extends BaseAdapter {

		List<AmmeterBean> ammeterItems;

		public AmmeterAdapter(List<AmmeterBean> ammeterItems) {
			// TODO Auto-generated constructor stub
			this.ammeterItems = ammeterItems;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return ammeterItems.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return ammeterItems.get(position);
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
				convertView = getLayoutInflater().inflate(R.layout.ammeter_list_item, null);
			}

			TextView meter_sn = (TextView) convertView.findViewById(R.id.ammeter_sn);
			meter_sn.setText(ammeterItems.get(position).getSn());

			TextView meter_pw = (TextView) convertView.findViewById(R.id.ammeter_password);
			meter_pw.setText(ammeterItems.get(position).getPassword());

			TextView meter_location = (TextView) convertView.findViewById(R.id.ammeter_location);
			meter_location.setText(ammeterItems.get(position).getLocation());

			TextView meter_model = (TextView) convertView.findViewById(R.id.ammeter_model);
			meter_model.setText(ammeterItems.get(position).getModel());
			int[] colors = { Color.WHITE, Color.rgb(219, 238, 244) };// RGB颜色

			convertView.setBackgroundColor(colors[position % 2]);// 每隔item之间颜色不同

			if (curPosition == position) {
				convertView.setBackgroundColor(Color.YELLOW);
			}
			return convertView;
		}

		public void addAmmeterItem(AmmeterBean items) {
			ammeterItems.add(items);
		}

		public AmmeterBean getAmmeterBean(int id) {

			return ammeterItems.get(id);
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
