package com.wzb.hhunew.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.wzb.hhunew.R;
import com.wzb.hhunew.interf.WApplication;
import com.wzb.hhunew.util.CustomDialog;
import com.wzb.hhunew.util.DbUtil;
import com.wzb.hhunew.util.EncryptionUtil;
import com.wzb.hhunew.util.LogUtil;
import com.wzb.hhunew.util.ToastUtil;
import com.wzb.sppnew.BluetoothState;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.TextView;
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
	
	private TextView languageValue;
	private ImageView languageBtn;
	
	
	private Bundle s;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.s=savedInstanceState;
		setContentView(R.layout.login_new);
		//testdata();
		init();
	}

	void testdata() {
		WApplication.sp_user.set("admin", EncryptionUtil.md5Encrypt("123456"));
	}

	private void init() {
		userName = (EditText) findViewById(R.id.et_acc);
		passWord = (EditText) findViewById(R.id.et_ps);
		btn_login = (Button) findViewById(R.id.btn_login);
		btn_exit = (Button) findViewById(R.id.btn_exit);
		btn_login.setOnClickListener(this);
		btn_exit.setOnClickListener(this);

		parent = (LinearLayout) findViewById(R.id.llayout);

		accSelect = (ImageView) findViewById(R.id.acc_select);
		accSelect.setOnClickListener(this);

		map = (Map<String, String>) WApplication.sp_user.getAll();
		List<String> list = new ArrayList<String>();
		LogUtil.logMessage("wzb", "" + map.size());
		for (Object obj : map.keySet()) {
			Object value = map.get(obj);
			LogUtil.logMessage("wzb", "key=" + obj.toString() + "value=" + value.toString());
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
		
		languageValue=(TextView)findViewById(R.id.language_value);
		String appLanguage=WApplication.sp.get("app_language", "en");
		if(appLanguage.equals("es")){
			languageValue.setText("Español");
		}else{
			languageValue.setText("English");
		}
		languageBtn=(ImageView)findViewById(R.id.language_btn);
		languageBtn.setOnClickListener(this);
		
	
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_exit:
			//test();
			exit();
			break;
		case R.id.btn_login:
			login();
			break;
		case R.id.acc_select:
			pw.showAsDropDown(parent, 15, -4);
			break;
		case R.id.language_btn:
			showSelectLanguage();
			break;
		default:
			break;
		}

	}
	private void saveAccount(String account){
		WApplication.sp_user.set(account, "1");
		String level=DbUtil.getUserLevel(account);
		if(level==null){
			WApplication.sp.set("current_level", "ReadUser");
		}else{
			WApplication.sp.set("current_level", level);
		}
	}

	private void login() {
		userNameValue = userName.getText().toString();
		passwordValue = passWord.getText().toString();

		String passw = DbUtil.getUserPw(userNameValue);
		 if(passw!=null && passw.equals(EncryptionUtil.md5Encrypt(passwordValue))){
		//if (true) {
			saveAccount(userNameValue); 
			Intent intent = new Intent(LoginNewActivity.this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			LoginNewActivity.this.startActivity(intent);
			
			// finish();
		} else {
			ToastUtil.showLongToast(LoginNewActivity.this, LoginNewActivity.this.getResources().getString(R.string.err_pw_or_acc));
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
	
	private void showSelectLanguage(){
		final String[] items = { "English","Español"};
	    AlertDialog.Builder listDialog = 
	        new AlertDialog.Builder(LoginNewActivity.this);
	   // listDialog.setTitle("我是一个列表Dialog");
	    listDialog.setItems(items, new DialogInterface.OnClickListener() {
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	            if(which==0){
	            	languageValue.setText("English");
	    			WApplication.sp.set("app_language", "en");
	            }else if(which==1){
	            	languageValue.setText("Español");
	    			WApplication.sp.set("app_language", "es");
	            }
	            //reStartApp();
	            updateUILanguage();
	        }
	    });
	    listDialog.show();
	}
	
	private void reStartApp(){
		Intent intent = new Intent(LoginNewActivity.this, LoginNewActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
		 // 杀掉进程
		 android.os.Process.killProcess(android.os.Process.myPid());
		 System.exit(0);
	}
	
	private void updateUILanguage(){
		onCreate(s);
		setContentView(R.layout.login_new);
		init();
	}
	
	void test(){
		 final String[] items = { "我是1","我是2","我是3","我是4" };
		    AlertDialog.Builder listDialog = 
		        new AlertDialog.Builder(LoginNewActivity.this);
		   // listDialog.setTitle("我是一个列表Dialog");
		    listDialog.setItems(items, new DialogInterface.OnClickListener() {
		        @Override
		        public void onClick(DialogInterface dialog, int which) {
		            // which 下标从0开始
		            // ...To-do
		            Toast.makeText(LoginNewActivity.this, 
		                "你点击了" + items[which], 
		                Toast.LENGTH_SHORT).show();
		        }
		    });
		    listDialog.show();
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
