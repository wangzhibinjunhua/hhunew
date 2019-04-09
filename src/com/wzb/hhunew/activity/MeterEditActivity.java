package com.wzb.hhunew.activity;

import com.wzb.hhunew.R;
import com.wzb.hhunew.bean.AmmeterBean;
import com.wzb.hhunew.util.CustomDialog;
import com.wzb.hhunew.util.DbUtil;
import com.wzb.hhunew.util.ResTools;
import com.wzb.hhunew.util.ToastUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MeterEditActivity extends BaseActivity implements OnClickListener{
	
	private ImageView backView;
	private TextView titleView;
	private ImageView btView;
	private ImageView titleMeterList;
	private Context mContext;
	
	private EditText snEt, passwordEt, locationEt;
	private CheckBox phrases1 ,phrases3;
	private Button okBtn, cancleBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_meteredit);
		mContext = MeterEditActivity.this;
		initTitleView();
		initView();
		showOldInfo();
	}
	
	private void showOldInfo(){
		Intent intent=getIntent();
		String oldSn=intent.getStringExtra("sn");
		AmmeterBean meter=DbUtil.getMeter(oldSn);
		if(meter==null){
			finish();
		}else{
			snEt.setText(meter.getSn());
			passwordEt.setText(meter.getPassword());
			locationEt.setText(meter.getLocation());
			if(meter.getModel().equals("1 Phrases")){
				phrases1.setChecked(true);
				phrases3.setChecked(false);
			}else{
				phrases1.setChecked(false);
				phrases3.setChecked(true);
			}
		}
		snEt.setClickable(false);
		snEt.setFocusable(false);
	}
	
	private void initView(){
		okBtn = (Button) findViewById(R.id.add_ok_btn);
		okBtn.setOnClickListener(this);
		cancleBtn = (Button) findViewById(R.id.add_cancle_btn);
		cancleBtn.setOnClickListener(this);
		
		snEt=(EditText)findViewById(R.id.sn_et);
		passwordEt=(EditText)findViewById(R.id.password_et);
		passwordEt.setVisibility(View.GONE);
		locationEt=(EditText)findViewById(R.id.location_et);
		
		phrases1=(CheckBox)findViewById(R.id.phrases_1);
		phrases3=(CheckBox)findViewById(R.id.phrases_3);
		
		phrases1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(phrases1.isChecked()){
					phrases3.setChecked(false);
				}
			}
		});
		
		phrases3.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(phrases3.isChecked()){
					phrases1.setChecked(false);
				}
			}
		});
		
	}
	
	private void initTitleView() {
		backView = (ImageView) findViewById(R.id.title_back);
		titleView = (TextView) findViewById(R.id.title_text);
		titleView.setText(ResTools.getResString(mContext, R.string.edit_meter));
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
	
	
	private void updateMeter(){
		String sn=snEt.getText().toString();
		String password=passwordEt.getText().toString();
		String location=locationEt.getText().toString();
		String model="";
		if(phrases1.isChecked()){
			model="1 Phrases";
		}else if(phrases3.isChecked()){
			model="3 Phrases";
		}
		
		if(!TextUtils.isEmpty(password) && !TextUtils.isEmpty(model)){
			
			
				CustomDialog.showWaitDialog(mContext);
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						CustomDialog.dismissDialog();
						gotoMeterListActivity();
						finish();
					}
				}, 2000);
				DbUtil.updateMeter(sn, password, location, model);
			
		}else{
			
			ToastUtil.showShortToast(mContext, mContext.getResources().getString(R.string.info_not_completed));
		}
		
	}
	private void gotoMeterListActivity(){
		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setClass(mContext, AmmeterListActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.add_ok_btn:
			updateMeter();
			break;
		case R.id.add_cancle_btn:
			finish();
			break;
		default:
			break;
		}
	}
	

}
