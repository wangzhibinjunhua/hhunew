package com.wzb.hhu.activity;

import com.wzb.hhu.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

/**
 * @author wzb<wangzhibin_x@qq.com>
 * @date May 14, 2017 4:49:16 PM	
 */ 
public class LoginNewActivity extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);  
		setContentView(R.layout.login_new);
	}

}
