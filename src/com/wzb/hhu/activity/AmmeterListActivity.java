package com.wzb.hhu.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.RedirectException;

import com.wzb.hhu.R;
import com.wzb.hhu.bean.AmmeterBean;
import com.wzb.hhu.util.ResTools;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
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

/**
 * @author wzb<wangzhibin_x@qq.com>
 * @date May 4, 2017 2:46:37 PM
 */

public class AmmeterListActivity extends BaseActivity implements OnScrollListener {

	private ImageView backView;
	private TextView titleView;
	private ListView listView;
	private int visibleLastIndex = 0;// 最后的可视项索引
	private int visibleItemCount;// 当前窗口可见项总数
	private int datasize = 38;// 模拟数据

	private AmmeterAdapter adapter;
	private View loadMoreView;
	private Button loadMoreBtn;
	private Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_ammeterlist);

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
				Intent intent = new Intent(AmmeterListActivity.this, ReadDataActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				AmmeterListActivity.this.startActivity(intent);
				finish();
			}
		});
	}

	private void initTitleView() {
		backView = (ImageView) findViewById(R.id.title_back);
		titleView = (TextView) findViewById(R.id.title_text);
		titleView.setText(ResTools.getResString(AmmeterListActivity.this, R.string.ammeter_list));
		backView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	private void initAdapter() {
		List<AmmeterBean> ammeters = new ArrayList<AmmeterBean>();
		for (int i = 1; i <= 10; i++) {
			AmmeterBean items = new AmmeterBean();
			items.setSn("sn" + i);
			items.setLocation("location" + i);
			items.setModel("model" + i);
			ammeters.add(items);

		}
		adapter = new AmmeterAdapter(ammeters);
	}

	private void loadMoreData() {
		int count = adapter.getCount();

		if (count + 10 <= datasize) {
			for (int i = count + 1; i <= count + 10; i++) {
				AmmeterBean items = new AmmeterBean();
				items.setSn("sn" + i);
				items.setLocation("location" + i);
				items.setModel("model" + i);
				adapter.addAmmeterItem(items);
			}
		} else {
			for (int i = count + 1; i <= datasize; i++) {
				AmmeterBean items = new AmmeterBean();
				items.setSn("sn" + i);
				items.setLocation("location" + i);
				items.setModel("model" + i);
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

			TextView meter_location = (TextView) convertView.findViewById(R.id.ammeter_location);
			meter_location.setText(ammeterItems.get(position).getLocation());

			TextView meter_model = (TextView) convertView.findViewById(R.id.ammeter_model);
			meter_model.setText(ammeterItems.get(position).getModel());
			int[] colors = { Color.WHITE, Color.rgb(219, 238, 244) };// RGB颜色

			convertView.setBackgroundColor(colors[position % 2]);// 每隔item之间颜色不同
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
