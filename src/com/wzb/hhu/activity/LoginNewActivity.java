package com.wzb.hhu.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.wzb.hhu.R;
import com.wzb.hhu.interf.WApplication;
import com.wzb.hhu.util.EncryptionUtil;
import com.wzb.hhu.util.LogUtil;
import com.wzb.hhu.util.ToastUtil;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
	private EditText userName, passWord;
	private Button btn_login, btn_exit;
	private String userNameValue, passwordValue;
	private PopupWindow pw;
	private ListView listView;
	private LinearLayout parent, option;
	private Map<String, String> map;
	private int width, i;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login_new);
		testdata();
		init();
	}

	void testdata() {
		WApplication.sp_user.set("admin", EncryptionUtil.md5Encrypt("123456"));
		WApplication.sp_user.set("test", EncryptionUtil.md5Encrypt("12345"));
	}

	private void init() {
		userName = (EditText) findViewById(R.id.et_acc);
		passWord = (EditText) findViewById(R.id.et_ps);
		btn_login = (Button) findViewById(R.id.btn_login);
		btn_exit = (Button) findViewById(R.id.btn_exit);
		btn_login.setOnClickListener(this);
		btn_exit.setOnClickListener(this);
		
		parent=(LinearLayout) findViewById(R.id.llayout);

		accSelect = (ImageView) findViewById(R.id.acc_select);
		accSelect.setOnClickListener(this);

		map = (Map<String, String>) WApplication.sp_user.getAll();
		List<String> list = new ArrayList<String>();
		LogUtil.logMessage("wzb", "" + map.size());
		for (Object obj : map.keySet()) {
			 Object value = map.get(obj );
			 LogUtil.logMessage("wzb",
			 "key="+obj.toString()+"value="+value.toString());
			String name = obj.toString();
			list.add(name);
		}
		/*
		 * for (int i = 0; i < (map.size()/2); i++) {
		 * 
		 * String name = WApplication.sp_user.get("name" + i, "");
		 * list.add(name); }
		 */

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.login_items, R.id.item, list);
		option = (LinearLayout) getLayoutInflater().inflate(R.layout.login_option, null);

		listView = (ListView) option.findViewById(R.id.op);
		listView.setAdapter(adapter);

		WindowManager wManager = (WindowManager) getSystemService(this.WINDOW_SERVICE);
		width = wManager.getDefaultDisplay().getWidth() * 4 / 5;

		pw = new PopupWindow(option, width, LayoutParams.WRAP_CONTENT, true);
		ColorDrawable dw = new ColorDrawable(00000);
		pw.setBackgroundDrawable(dw);
		pw.setOutsideTouchable(true);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

				String username = adapterView.getItemAtPosition(position).toString();

				userName.setText(username);

				// 选择后，popupwindow自动消失
				pw.dismiss();
			}

		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_exit:
			exit();
			break;
		case R.id.btn_login:
			login();
			break;
		case R.id.acc_select:
			pw.showAsDropDown(parent, 15, -4);
			break;

		default:
			break;
		}

	}
	
	private void login(){
		userNameValue=userName.getText().toString();
		passwordValue=passWord.getText().toString();
		
		String passw=WApplication.sp_user.get(userNameValue, "123");
		//if(passw.equals(EncryptionUtil.md5Encrypt(passwordValue))){
		if(true){
			Intent intent = new Intent(LoginNewActivity.this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			LoginNewActivity.this.startActivity(intent);
			//finish();
		}else{
			ToastUtil.showLongToast(LoginNewActivity.this,"帐号或密码不正确");
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
