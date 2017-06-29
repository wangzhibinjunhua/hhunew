package com.wzb.hhu.activity;


import com.wzb.hhu.R;
import com.wzb.hhu.interf.WApplication;
import com.wzb.hhu.view.MyGridAdapter;
import com.wzb.hhu.view.MyGridView;
import com.wzb.spp.test.SimpleActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author wzb<wangzhibin_x@qq.com>
 * @date May 3, 2017 10:55:01 AM
 */
public class MainActivity extends BaseActivity implements OnClickListener {

	private MyGridView mGridView;

	private ImageView backView;
	private ImageView btView;
	private TextView titleView;
	private ImageView titleMeterList;

	private String[] img_text;
	private int[] img_icon = { R.drawable.form, R.drawable.set, R.drawable.clock, R.drawable.warning,
			R.drawable.account, R.drawable.logout, R.drawable.set_con };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		initView();
	}

	private void initView() {
		Resources res = getResources();
		img_text = res.getStringArray(R.array.img_text);

		mGridView = (MyGridView) findViewById(R.id.gridview);
		// mGridView.setAdapter(new MyGridAdapter(this));
		mGridView.setAdapter(new MyGridAdapter(this, img_text, img_icon));
		mGridView.setOnItemClickListener(new MyItemClickListener());

		backView = (ImageView) findViewById(R.id.title_back);
		backView.setOnClickListener(this);
		backView.setVisibility(View.GONE);
		btView = (ImageView) findViewById(R.id.title_bt);
		btView.setVisibility(View.GONE);
		titleView = (TextView) findViewById(R.id.title_text);
		titleView.setText(getString(R.string.home_page));
		titleMeterList=(ImageView)findViewById(R.id.title_meterlist);
		titleMeterList.setVisibility(View.GONE);
	}

	private class MyItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Boolean isFirstUseMeter=WApplication.sp.get("current_sn", "__000___").equals("__000___");
			switch (arg2) {
			case 3:
				
				if(isFirstUseMeter){
					intent.setClass(MainActivity.this, AmmeterListActivity.class);
				}else{
					intent.setClass(MainActivity.this, EventLogActivity.class);
				}
				WApplication.sp.set("current_activity", "3");
				startActivity(intent);
				break;
			case 4:
				intent.setClass(MainActivity.this, UserManagerActivity.class);
				startActivity(intent);
				break;
			case 5:
				//intent.setClass(MainActivity.this, LoginNewActivity.class);
				//startActivity(intent);
				for (Activity activity : WApplication.activityList) {
					activity.finish();
				}
				finish();
				System.exit(0);
				System.gc();
				break;
			case 2:
				
				if(isFirstUseMeter){
					intent.setClass(MainActivity.this, AmmeterListActivity.class);
				}else{
					intent.setClass(MainActivity.this, SettingTimeActivity.class);
				}
				WApplication.sp.set("current_activity", "2");
				startActivity(intent);
				break;
			case 0:// read data
				
				if(isFirstUseMeter){
					intent.setClass(MainActivity.this, AmmeterListActivity.class);
				}else{
					intent.setClass(MainActivity.this, ReadDataActivity.class);
				}
				WApplication.sp.set("current_activity", "0");
				startActivity(intent);
				break;
			case 1:
				 //intent.setClass(MainActivity.this, SimpleActivity.class);
				
				if(isFirstUseMeter){
					intent.setClass(MainActivity.this, AmmeterListActivity.class);
				}else{
					intent.setClass(MainActivity.this, SettingActivity.class);
				}
				WApplication.sp.set("current_activity", "1");
				startActivity(intent);
				break;
			default:
				break;
			}

		}

	}

	private long firstTime = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			long secondTime = System.currentTimeMillis();
			if (secondTime - firstTime > 2000) {// 如果两次按键时间间隔大于2秒，则不退出
				Toast.makeText(this, getString(R.string.exit_dialog), 666).show();
				firstTime = secondTime;// 更新firstTime
				return true;
			} else {// 两次按键小于2秒时，退出应用
				exit();
				return false;
			}
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	private void exit() {
		for (Activity activity : WApplication.activityList) {
			activity.finish();
		}
		finish();
		System.exit(0);
		System.gc();
	}

}
