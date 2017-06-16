package com.wzb.hhu.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.RedirectException;

import com.wzb.hhu.R;
import com.wzb.hhu.bean.AmmeterBean;
import com.wzb.hhu.interf.WApplication;
import com.wzb.hhu.util.Common;
import com.wzb.hhu.util.CustomDialog;
import com.wzb.hhu.util.DbUtil;
import com.wzb.hhu.util.LogUtil;
import com.wzb.hhu.util.ResTools;
import com.wzb.hhu.util.ToastUtil;
import com.wzb.spp.BluetoothState;
import com.wzb.spp.DeviceList;
import com.wzb.spp.BluetoothSPP.BluetoothConnectionListener;
import com.wzb.spp.BluetoothSPP.OnDataReceivedListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
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

public class AmmeterListActivity extends BaseActivity implements OnScrollListener {

	public static final int UPDATE_BT_STATE=0xff0001;
	
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
	
	private Context mContext;

	private ImageView searchBtn, addBtn, delBtn;
	private EditText searchEt;
	
	private String selectedSn,selectedPw;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_ammeterlist);
		mContext = AmmeterListActivity.this;
		initTitleView();

		searchEt = (EditText) findViewById(R.id.ammeter_et);
		addBtn = (ImageView) findViewById(R.id.add_btn);
		addBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setClass(AmmeterListActivity.this, MeterAddActivity.class);
				startActivity(intent);
			}
		});

		searchBtn = (ImageView) findViewById(R.id.search_btn);
		searchBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				String searchSn = searchEt.getText().toString();
				if (TextUtils.isEmpty(searchSn)) {
						ToastUtil.showShortToast(mContext, "搜索内容不能为空!");
				} else {
					Intent intent = new Intent();
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setClass(AmmeterListActivity.this, MeterSearchActivity.class);
					intent.putExtra("search_sn", searchSn);
					startActivity(intent);
				}
			}
		});

		loadMoreView = getLayoutInflater().inflate(R.layout.loadmore, null);
		loadMoreBtn = (Button) loadMoreView.findViewById(R.id.load_more_btn);

		loadMoreBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				loadMoreBtn.setText("正在加载..");
				loadMoreBtn.setClickable(false);
				new Handler().postDelayed(new Runnable() {

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
				selectedSn=adapter.getAmmeterBean(arg2).getSn();
				selectedPw=adapter.getAmmeterBean(arg2).getPassword();
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
	
	private void gotoReadData(String sn,String pw){
		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setClass(mContext, ReadDataActivity.class);
		intent.putExtra("meter_sn", sn);
		intent.putExtra("meter_pw", pw);
		startActivity(intent);
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
			gotoReadData(selectedSn, selectedPw);
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
		titleView.setText(ResTools.getResString(AmmeterListActivity.this, R.string.ammeter_list));
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

		ammeters = DbUtil.getSomeMeter(10);
		adapter = new AmmeterAdapter(ammeters);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		updateShowMeters();
		setBtListener();
		updateBtState();
		long meterCount = DbUtil.getAllMeterCount();
		LogUtil.logMessage("wzb", "meterCount=" + meterCount);
		if (meterCount <= 10) {
			loadMoreBtn.setVisibility(View.GONE);
		} else {
			loadMoreBtn.setVisibility(View.VISIBLE);
		}
	}

	private void updateShowMeters() {
		List<AmmeterBean> meters = new ArrayList<AmmeterBean>();
		meters = DbUtil.getSomeMeter(10);
		adapter.clearAlldata();
		adapter.updateList(meters);
		adapter.notifyDataSetChanged();

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
				LogUtil.logMessage("wzb", "AmmeterListActivity onDeviceConnected");
				mHandler.sendEmptyMessage(UPDATE_BT_STATE);
			}

			public void onDeviceDisconnected() {
				LogUtil.logMessage("wzb", "AmmeterListActivity onDeviceDisconnected");
				mHandler.sendEmptyMessage(UPDATE_BT_STATE);
			}

			public void onDeviceConnectionFailed() {
				LogUtil.logMessage("wzb", "AmmeterListActivity onDeviceConnectionFailed");
				mHandler.sendEmptyMessage(UPDATE_BT_STATE);
			}
		});

	}

	private void loadMoreData() {

		int count = adapter.getCount();
		long meterCount = DbUtil.getAllMeterCount();
		LogUtil.logMessage("wzb", "all meterCount=" + meterCount);

		if (count < meterCount) {
			List<AmmeterBean> meters = new ArrayList<AmmeterBean>();
			meters = DbUtil.getSomeMeter(count + 10);
			adapter.clearAlldata();
			adapter.updateList(meters);
			adapter.notifyDataSetChanged();
		} else {
			ToastUtil.showLongToast(mContext, "没有更多数据了");
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

		public void clearAlldata() {
			ammeterItems.clear();
		}

		public void updateList(List<AmmeterBean> list) {
			this.ammeterItems = list;
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
