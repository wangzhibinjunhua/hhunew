package com.wzb.hhu.activity;

import java.lang.ref.WeakReference;

import com.wzb.hhu.R;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.TextView;

public class BaseActivity extends Activity{

	public static BaseActivity instance;
	private Dialog progressDialog;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		instance = this;
	}
	
	public static class StaticHandler<T> extends Handler {
		public WeakReference<T> mWeakReference;

		public StaticHandler(T t) {
			mWeakReference = new WeakReference<T>(t);
		}
	}
	
	public Dialog getDialog() {
		if (progressDialog == null) {
			progressDialog = new Dialog(this, R.style.dialog);
			progressDialog.setContentView(R.layout.progress_dialog);
		}
		return progressDialog;
	}

	public void showDialog(String title, String message, boolean cancelable) {
		if (isFinishing()) {
			return;
		}

		try {
			progressDialog = getDialog();
			TextView dialogMsg = (TextView) progressDialog.findViewById(R.id.tv_dialog_msg);
			dialogMsg.setText(message);
			progressDialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void showDialog(String title, String message) {
		showDialog(title, message, true);
	}
	
	public void showDialog(){
		if (isFinishing()) {
			return;
		}
		
		try {
			progressDialog = getDialog();
			progressDialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public void dismisssDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
