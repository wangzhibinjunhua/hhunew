package com.wzb.hhu.activity;

import org.apache.http.FormattedHeader;

import com.wzb.hhu.R;
import com.wzb.hhu.util.ResTools;

import android.app.ActivityManager.RunningAppProcessInfo;
import android.os.Bundle;
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

public class UserAddActivity extends BaseActivity implements OnClickListener{
	
	private ImageView backView;
	private TextView titleView;
	
	private EditText accountEt,passwordEt,nameEt;
	private CheckBox adminCb,rwCb,roCb;
	
	
	private Button okBtn,cancleBtn;
	
	
	private String account,password,name,permission;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_useradd);
		initTitleView();
		initView();
	}
	
	private void initView(){
		okBtn=(Button)findViewById(R.id.user_add_ok_btn);
		okBtn.setOnClickListener(this);
		cancleBtn=(Button)findViewById(R.id.user_add_cancle_btn);
		cancleBtn.setOnClickListener(this);
		
		accountEt=(EditText)findViewById(R.id.account_et);
		passwordEt=(EditText)findViewById(R.id.password_et);
		nameEt=(EditText)findViewById(R.id.name_et);
		
		adminCb=(CheckBox)findViewById(R.id.permission_admin);
		rwCb=(CheckBox)findViewById(R.id.permission_rw);
		roCb=(CheckBox)findViewById(R.id.permission_r);
		adminCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(adminCb.isChecked()){
					rwCb.setChecked(false);
					roCb.setChecked(false);
				}
			}
		});
		
		rwCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(rwCb.isChecked()){
					adminCb.setChecked(false);
					roCb.setChecked(false);
				}
			}
		});
		
		roCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(roCb.isChecked()){
					adminCb.setChecked(false);
					rwCb.setChecked(false);
				}
			}
		});
		
	}
	
	
	private void initTitleView() {
		backView = (ImageView) findViewById(R.id.title_back);
		titleView = (TextView) findViewById(R.id.title_text);
		titleView.setText(ResTools.getResString(UserAddActivity.this, R.string.user_manager));
		backView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
	
	private void addNewUser(){
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.user_add_cancle_btn:
			finish();
			break;
		case R.id.user_add_ok_btn:
			addNewUser();
			break;
		default:
			break;
		}
	}

}
