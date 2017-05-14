package com.wzb.hhu.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.impl.conn.tsccm.WaitingThread;

import com.wzb.hhu.R;
import com.wzb.hhu.interf.WApplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

/**
 * @author wzb<wangzhibin_x@qq.com>
 * @date May 14, 2017 4:49:16 PM
 */
public class LoginNewActivity extends BaseActivity implements OnClickListener {

	private ImageView accSelect;
	private EditText userName, password;
	private Button btn_login, btn_exit;
	private String userNameValue, passwordValue;
	private PopupWindow pw;
	private ListView lv;
	private LinearLayout parent, option;
	private Map<String, String> map;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login_new);

		init();
	}

	private void init() {
		userName = (EditText) findViewById(R.id.et_acc);
		password = (EditText) findViewById(R.id.et_ps);
		btn_login = (Button) findViewById(R.id.btn_login);
		;
		btn_exit = (Button) findViewById(R.id.btn_exit);
		btn_login.setOnClickListener(this);
		btn_exit.setOnClickListener(this);

		accSelect = (ImageView) findViewById(R.id.acc_select);
		accSelect.setOnClickListener(this);
		
		map = (Map<String, String>) WApplication.sp_user.getAll();
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < (map.size()/2); i++) {
			String name = WApplication.sp_user.get("name" + i, "");
			list.add(name);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_exit:
			exit();
			break;
		case R.id.btn_login:
			Intent intent = new Intent(LoginNewActivity.this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			LoginNewActivity.this.startActivity(intent);
			finish();
			break;

		default:
			break;
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

	private void exit() {
		for (Activity activity : WApplication.activityList) {
			activity.finish();
		}
		finish();
		System.exit(0);
		System.gc();
	}
}
