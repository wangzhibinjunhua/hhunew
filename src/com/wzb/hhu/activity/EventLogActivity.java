package com.wzb.hhu.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.wzb.hhu.R;
import com.wzb.hhu.bean.DataItemBean;
import com.wzb.hhu.util.LogUtil;
import com.wzb.hhu.util.ResTools;

import android.graphics.Color;
import android.os.Bundle;
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

	private ImageView backView;
	private TextView titleView;
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
				eventAdapter.getDataItem(arg2).cbToggle();
				if (eventAdapter.getDataItem(arg2).getItemSelect()) {
					eventListStr.add("" + arg2);
				} else {
					eventListStr.remove("" + arg2);
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
