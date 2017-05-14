package com.wzb.hhu.activity;

import com.wzb.hhu.R;
import com.wzb.hhu.interf.WApplication;
import com.wzb.hhu.view.MyGridAdapter;
import com.wzb.hhu.view.MyGridView;

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
public class MainActivity extends BaseActivity implements OnClickListener{
	
	private MyGridView mGridView;

	private ImageView backView;
	private TextView titleView;
	
	
	private String[] img_text;
	private int[] img_icon={R.drawable.form,R.drawable.set,R.drawable.clock,
			R.drawable.warning,R.drawable.account,R.drawable.set_con,R.drawable.logout};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		initView();
	}
	
	private void initView(){
		Resources res=getResources();
		img_text=res.getStringArray(R.array.img_text);
		
		mGridView = (MyGridView) findViewById(R.id.gridview);
		// mGridView.setAdapter(new MyGridAdapter(this));
		mGridView.setAdapter(new MyGridAdapter(this,img_text,img_icon));
		mGridView.setOnItemClickListener(new MyItemClickListener());

		backView = (ImageView) findViewById(R.id.title_back);
		backView.setOnClickListener(this);
		backView.setVisibility(View.GONE);
		titleView = (TextView) findViewById(R.id.title_text);
		titleView.setText(getString(R.string.home_page));
	}
	
	private class MyItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
//			Toast.makeText(HomeActivity.this, "" + arg2, Toast.LENGTH_SHORT)
//					.show();
			Intent intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			switch (arg2) {
			case 2:
				//intent.setClass(MainActivity.this, DeviceManagerActivity.class);
				//startActivity(intent);
				break;
			case 6:
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(MainActivity.this, LoginNewActivity.class);
				startActivity(intent);
				break;
			case 1:
				//intent.setClass(MainActivity.this, MainActivity.class);
				//startActivity(intent);
				break;
			case 0:
				
				intent.setClass(MainActivity.this, AmmeterListActivity.class);
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
				Toast.makeText(this, getString(R.string.exit_dialog), 666)
						.show();
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
