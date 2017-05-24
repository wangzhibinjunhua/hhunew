package com.wzb.hhu.activity;
import java.util.Calendar;

import com.wzb.hhu.R;
import com.wzb.hhu.util.LogUtil;
import com.wzb.hhu.util.ResTools;

import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.picker.TimePicker;
import cn.qqtheme.framework.util.ConvertUtils;

public class SettingTimeActivity extends BaseActivity implements OnClickListener{
	
	private ImageView backView;
	private TextView titleView;
	
	private Button readBtn,writeBtn,returnBtn;
	private CheckBox sysClockCb;
	private EditText sysDate,sysTime;
	private ImageView dateSet,timeSet;
	private TextView meterDate,meterTime;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_settingtime);
		initTitleView();
		
		initView();
	}
	
	private void initView(){
		sysClockCb=(CheckBox)findViewById(R.id.sys_clock_cb);
		sysClockCb.setChecked(false);
		
		sysDate=(EditText)findViewById(R.id.sys_clock_date_value);
		sysTime=(EditText)findViewById(R.id.sys_clock_time_value);
		
		dateSet=(ImageView)findViewById(R.id.sys_clock_date_value_set);
		dateSet.setOnClickListener(this);
		
		timeSet=(ImageView)findViewById(R.id.sys_clock_time_value_set);
		timeSet.setOnClickListener(this);
		
		meterDate=(TextView)findViewById(R.id.meter_clock_date_value);
		meterTime=(TextView)findViewById(R.id.meter_clock_time_value);
		
		readBtn=(Button)findViewById(R.id.time_read_btn);
		readBtn.setOnClickListener(this);
		writeBtn=(Button)findViewById(R.id.time_write_btn);
		writeBtn.setOnClickListener(this);
		returnBtn=(Button)findViewById(R.id.time_back_btn);
		returnBtn.setOnClickListener(this);
		
		String[] dateTime=getSysDateTime();
		sysDate.setText(dateTime[0]+"-"+dateTime[1]+"-"+dateTime[2]);
		sysTime.setText(dateTime[3]+":"+dateTime[4]+":"+dateTime[5]);
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.time_back_btn:
			finish();
			break;
		case R.id.sys_clock_date_value_set:
			onYearMonthDayPicker();
			break;
		case R.id.sys_clock_time_value_set:
			onTimePicker();
			break;
		default:
			break;
		}
	}
	
	private void updateSysDateValue(String year, String month, String day){
		sysDate.setText(year+"-"+month+"-"+day);
	}

	private void updateSysTimeValue(String hour,String minute){
		sysTime.setText(hour+":"+minute+":"+"30");
	}
	
	private String[] getSysDateTime(){
		String[] dateTime=new String[6];
		Time t=new Time();
		t.setToNow();
		dateTime[0]=""+t.year;
		dateTime[1]=""+(t.month+1);
		dateTime[2]=""+t.monthDay;
		dateTime[3]=""+t.hour;
		dateTime[4]=""+t.minute;
		dateTime[5]=""+t.second;
		LogUtil.logMessage("wzb", "month="+t.month);
		return dateTime;
	}
	
	public void onYearMonthDayPicker() {
        final DatePicker picker = new DatePicker(this);
        picker.setCanceledOnTouchOutside(true);
        picker.setUseWeight(true);
        picker.setTopPadding(ConvertUtils.toPx(this, 20));
        picker.setRangeEnd(2111, 1, 11);
        picker.setRangeStart(2016, 1, 1);
        picker.setSelectedItem(2017, 5, 24);
        picker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
            @Override
            public void onDatePicked(String year, String month, String day) {
            	updateSysDateValue(year, month, day);
            }
        });
        picker.setOnWheelListener(new DatePicker.OnWheelListener() {
            @Override
            public void onYearWheeled(int index, String year) {
                picker.setTitleText(year + "-" + picker.getSelectedMonth() + "-" + picker.getSelectedDay());
            }

            @Override
            public void onMonthWheeled(int index, String month) {
                picker.setTitleText(picker.getSelectedYear() + "-" + month + "-" + picker.getSelectedDay());
            }

            @Override
            public void onDayWheeled(int index, String day) {
                picker.setTitleText(picker.getSelectedYear() + "-" + picker.getSelectedMonth() + "-" + day);
            }
        });
        picker.show();
    }
	
	 public void onTimePicker() {
	        TimePicker picker = new TimePicker(this, TimePicker.HOUR_24);
	        picker.setRangeStart(0, 0);//00:00
	        picker.setRangeEnd(23, 59);//23:59
	        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
	        int currentMinute = Calendar.getInstance().get(Calendar.MINUTE);
	        picker.setSelectedItem(currentHour, currentMinute);
	        picker.setTopLineVisible(false);
	        picker.setOnTimePickListener(new TimePicker.OnTimePickListener() {
	            @Override
	            public void onTimePicked(String hour, String minute) {
	            	updateSysTimeValue(hour,minute);
	            }
	        });
	        picker.show();
	    }

}
