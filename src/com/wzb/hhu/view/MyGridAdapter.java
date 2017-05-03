package com.wzb.hhu.view;


import com.wzb.hhu.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * AUTHER wzb<wangzhibin_x@foxmail.com>
 * 2016-2-23����02:43:13	
 */
public class MyGridAdapter extends BaseAdapter{
	
	private Context mContext;
	
	public String[] img_text;
	public int[] img_icon;
	

	
	public MyGridAdapter(Context context){
		super();
		this.mContext=context;
	}
	
	public MyGridAdapter(Context context,String[] img_text,int[] img_icon){
		super();
		this.mContext=context;
		this.img_icon=img_icon;
		this.img_text=img_text;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return img_text.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		if(convertView == null){
			
			convertView=LayoutInflater.from(mContext).inflate(R.layout.home_grid_item, parent,false);
		}
		
		TextView tv=BaseViewHolder.get(convertView,R.id.tv_item);
		ImageView iv=BaseViewHolder.get(convertView,R.id.iv_item);
		
		iv.setBackgroundResource(img_icon[position]);
		tv.setText(img_text[position]);
		return convertView;
	}
	
	

}
