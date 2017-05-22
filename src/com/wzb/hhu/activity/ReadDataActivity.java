package com.wzb.hhu.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.wzb.hhu.R;
import com.wzb.hhu.bean.AmmeterBean;
import com.wzb.hhu.bean.DataItemBean;
import com.wzb.hhu.util.LogUtil;
import com.wzb.hhu.util.ResTools;
import com.wzb.hhu.view.DataViewHolder;

import android.content.Context;
import android.os.Bundle;
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
public class ReadDataActivity extends BaseActivity implements OnScrollListener,OnClickListener{

	private ImageView backView;
	private TextView titleView;
	private Button ReadBtn,StopBtn;
	
	String name[] = { "G1", "G2", "G3", "G4", "G5", "G6", "G7", "G8", "G9",  
            "G10", "G11", "G12", "G13", "G14" };
	
	
	ArrayList<String> ElecListStr = null;  
    private List<HashMap<String, Object>> ElecList = null;  
    private DataAdapter ElecAdapter; 
    
    private ListView ElecListView=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_readdata);
		initTitleView();
		initView();
	}
	
	private void initView() {
		ElecListStr=new ArrayList<String>();
		
		ElecListView=(ListView)findViewById(R.id.lv_data);
		
		initAdapter();
		
		ElecListView.setAdapter(ElecAdapter);
		ElecListView.setOnScrollListener(this);
		ElecListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				ElecAdapter.getDataItem(arg2).cbToggle();
				ElecListStr.add(""+arg2);
				LogUtil.logMessage("wzb", "cb:"+arg2+" "+ElecAdapter.getDataItem(arg2).getItemSelect());
				ElecAdapter.notifyDataSetChanged();
			}
		});
	}
	
	private void initAdapter(){
		List<DataItemBean> dataItems=new ArrayList<DataItemBean>();
		DataItemBean items=new DataItemBean();
		items.setItemName("Item name");
		items.setItemValue("Value");
		items.setItemState("State");
		items.setItemSelect(false);
		dataItems.add(items);
		String x[]=ResTools.getResStringArray(ReadDataActivity.this, R.array.elec);
		for(int i=0;i<x.length;i++){
			
			
			items.setItemName(x[i]);
			items.setItemValue("");
			items.setItemState("");
			items.setItemSelect(false);
			dataItems.add(items);
		}
		ElecAdapter=new DataAdapter(dataItems);
		
	}

	private void initTitleView() {
		backView = (ImageView) findViewById(R.id.title_back);
		titleView = (TextView) findViewById(R.id.title_text);
		titleView.setText(ResTools.getResString(ReadDataActivity.this, R.string.read_data));
		backView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		ReadBtn=(Button)findViewById(R.id.data_read_btn);
		ReadBtn.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.data_read_btn:
			LogUtil.logMessage("wzb", ""+ElecListStr);
			break;
		default:
			break;
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

			TextView tvName = (TextView) convertView.findViewById(R.id.data_item_name);
			TextView tvValue = (TextView) convertView.findViewById(R.id.data_item_value);
			TextView tvState = (TextView) convertView.findViewById(R.id.data_item_state);
			CheckBox cb = (CheckBox) convertView.findViewById(R.id.data_item_cb);
			tvName.setText(dataItems.get(position).getItemName());
			tvValue.setText(dataItems.get(position).getItemValue());
			tvState.setText(dataItems.get(position).getItemState());
			cb.setChecked(dataItems.get(position).getItemSelect());
			return convertView;
		}

		
		public DataItemBean getDataItem(int id){
			
			return dataItems.get(id);
		}

	}

	/*
	class DataAdapter extends BaseAdapter {
		public HashMap<Integer, Boolean> isSelected;
		private Context context = null;
		private LayoutInflater inflater = null;
		private List<HashMap<String, Object>> list = null;
		private String keyString[] = null;
		private String itemString = null; // 记录每个item中textview的值
		private int idValue[] = null;// id值

		public DataAdapter(Context context, List<HashMap<String, Object>> list, int resource, String[] from, int[] to) {
			this.context = context;
			this.list = list;
			keyString = new String[from.length];
			idValue = new int[to.length];
			System.arraycopy(from, 0, keyString, 0, from.length);
			System.arraycopy(to, 0, idValue, 0, to.length);
			inflater = LayoutInflater.from(context);
			init();
		}

		// 初始化 设置所有checkbox都为未选择
		public void init() {
			isSelected = new HashMap<Integer, Boolean>();
			for (int i = 0; i < list.size(); i++) {
				isSelected.put(i, false);
			}
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View view, ViewGroup arg2) {
			DataViewHolder holder = null;
			if (holder == null) {
				holder = new DataViewHolder();
				if (view == null) {
					view = inflater.inflate(R.layout.data_list_item, null);
				}
				holder.tvName = (TextView) view.findViewById(R.id.data_item_name);
				holder.tvValue = (TextView) view.findViewById(R.id.data_item_value);
				holder.tvState = (TextView) view.findViewById(R.id.data_item_state);
				holder.cb = (CheckBox) view.findViewById(R.id.data_item_cb);
				view.setTag(holder);
			} else {
				holder = (DataViewHolder) view.getTag();
			}
			HashMap<String, Object> map = list.get(position);
			if (map != null) {
				itemString = (String) map.get(keyString[0]);
				holder.tvName.setText(itemString);
			}
			holder.cb.setChecked(isSelected.get(position));
			return view;
		}
	}*/

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		
	}

	
}
