package com.wzb.hhu.activity;

import com.wzb.hhu.R;
import com.wzb.hhu.util.ResTools;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MeterAddActivity extends BaseActivity implements OnClickListener{
	
	private ImageView backView;
	private TextView titleView;
	private Context mContext;
	
	private EditText snEt, passwordEt, locationEt,modelEt;
	private Button okBtn, cancleBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_meteradd);
		mContext = MeterAddActivity.this;
		initTitleView();
		initView();
	}
	
	private void initView(){
		okBtn = (Button) findViewById(R.id.add_ok_btn);
		okBtn.setOnClickListener(this);
		cancleBtn = (Button) findViewById(R.id.add_cancle_btn);
		cancleBtn.setOnClickListener(this);
		
		snEt=(EditText)findViewById(R.id.sn_et);
		passwordEt=(EditText)findViewById(R.id.password_et);
		locationEt=(EditText)findViewById(R.id.location_et);
		modelEt=(EditText)findViewById(R.id.model_et);
		
	}
	
	private void initTitleView() {
		backView = (ImageView) findViewById(R.id.title_back);
		titleView = (TextView) findViewById(R.id.title_text);
		titleView.setText(ResTools.getResString(mContext, R.string.new_meter));
		backView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
	
	
	private void addNewMeter(){
		String sn=snEt.getText().toString();
		String password=passwordEt.getText().toString();
		String location=locationEt.getText().toString();
		String model=modelEt.getText().toString();
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.add_btn:
			addNewMeter();
			break;
		case R.id.btn_cancle:
			finish();
			break;
		default:
			break;
		}
	}
	

}
