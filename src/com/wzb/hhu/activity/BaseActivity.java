package com.wzb.hhu.activity;

import java.lang.ref.WeakReference;
import java.util.Locale;

import com.wzb.hhu.R;
import com.wzb.hhu.interf.WApplication;

import android.app.Activity;
import android.app.Dialog;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Window;
import android.widget.TextView;

public class BaseActivity extends Activity {

	public static BaseActivity instance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		WApplication.activityList.add(this);
		instance = this;
		updateAppLanguage();
	}

	public static class StaticHandler<T> extends Handler {
		public WeakReference<T> mWeakReference;

		public StaticHandler(T t) {
			mWeakReference = new WeakReference<T>(t);
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

	private void updateAppLanguage() {
		String sta = WApplication.sp.get("app_language", "en");
		// 本地语言设置
		Locale myLocale = new Locale(sta);
		Resources res = getResources();
		DisplayMetrics dm = res.getDisplayMetrics();
		Configuration conf = res.getConfiguration();
		conf.locale = myLocale;
		res.updateConfiguration(conf, dm);
	}

}
