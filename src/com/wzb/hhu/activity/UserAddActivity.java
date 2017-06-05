package com.wzb.hhu.activity;

import com.wzb.hhu.R;

import android.os.Bundle;
import android.view.Window;

public class UserAddActivity extends BaseActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_useradd);
	}

}
