package com.wzb.hhu.activity;

import com.wzb.hhu.R;
import com.wzb.hhu.interf.WApplication;
import com.wzb.hhu.util.ResTools;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ImageView;
import android.widget.TextView;

public class ApplicationSetting extends BaseActivity{
	private ImageView backView;
	private TextView titleView;
	private ImageView btView;
	private ImageView titleMeterList;
	
	private Context mContext;
	
	private TextView languageTv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_applicationsetting);
		mContext=ApplicationSetting.this;
		initTitleView();
		initView();
	}
	
	private void initTitleView() {
		backView = (ImageView) findViewById(R.id.title_back);
		titleView = (TextView) findViewById(R.id.title_text);
		titleView.setText(ResTools.getResString(mContext, R.string.application_setting));
		backView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		btView = (ImageView) findViewById(R.id.title_bt);
		btView.setVisibility(View.GONE);
		titleMeterList=(ImageView)findViewById(R.id.title_meterlist);
		titleMeterList.setVisibility(View.GONE);
	}
	
	
	private void initView(){
		languageTv=(TextView)findViewById(R.id.sys_language_select);
		String appLanguage=WApplication.sp.get("app_language", "en");
		if(appLanguage.equals("es")){
			languageTv.setText("Español");
		}else{
			languageTv.setText("English");
		}
		languageTv.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
				// TODO Auto-generated method stub
				menu.add(0, 0, 0, "English");
				menu.add(0, 1, 0, "Español");
			}
		});
	}
	


	
	public boolean onContextItemSelected(MenuItem item) {

		
		switch (item.getItemId()) {
		case 0:// en
			languageTv.setText("English");
			WApplication.sp.set("app_language", "en");
			break;

		case 1:// es
			languageTv.setText("Español");
			WApplication.sp.set("app_language", "es");
			break;

		default:
			break;
		}
		reStartApp();
		return super.onContextItemSelected(item);

	}
	
	private void reStartApp(){
		Intent intent = new Intent(mContext, MainActivity.class);
		 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
		 // 杀掉进程
		 android.os.Process.killProcess(android.os.Process.myPid());
		 System.exit(0);
	}

}
