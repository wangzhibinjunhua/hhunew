package com.wzb.hhu.activity;
import com.wzb.hhu.R;
import com.wzb.hhu.util.ResTools;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import cn.qqtheme.framework.picker.DatePicker;

public class SettingTimeActivity extends BaseActivity{
	
	private ImageView backView;
	private TextView titleView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_settingtime);
		initTitleView();
	}
	
	private void initTitleView() {
		backView = (ImageView) findViewById(R.id.title_back);
		titleView = (TextView) findViewById(R.id.title_text);
		titleView.setText(ResTools.getResString(SettingTimeActivity.this, R.string.setting_time));
		backView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

}
