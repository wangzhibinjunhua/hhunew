package com.wzb.hhu.activity;

import org.apache.http.FormattedHeader;

import com.wzb.hhu.R;
import com.wzb.hhu.bean.UserBean;
import com.wzb.hhu.interf.WApplication;
import com.wzb.hhu.util.CustomDialog;
import com.wzb.hhu.util.DbUtil;
import com.wzb.hhu.util.ResTools;
import com.wzb.hhu.util.ToastUtil;

import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class UserEditActivity extends BaseActivity implements OnClickListener {

	private ImageView backView;
	private TextView titleView;
	
	private ImageView btView;
	private ImageView titleMeterList;

	private EditText accountEt, passwordEt, nameEt;
	private CheckBox adminCb, rwCb, roCb;

	private Button okBtn, cancleBtn;

	private String account, password, name, permission;

	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_useradd);
		mContext = UserEditActivity.this;
		initTitleView();
		initView();
		showOldInfo();
	}

	private void showOldInfo() {
		Intent intent = getIntent();
		String oldAccount = intent.getStringExtra("account");
		UserBean user = DbUtil.getUser(oldAccount);
		if (user != null) {
			accountEt.setText(user.getAccount());
			passwordEt.setText("******");
			nameEt.setText(user.getName());
			String permission = user.getLevel();
			if (permission.equals("AdminUser")) {
				setAdminCbChecked();
			} else if (permission.equals("ProgramUser")) {
				setRwCbChecked();
			} else if (permission.equals("ReadUser")) {
				setRoCbChecked();
			}
		} else {
			finish();
		}
	}

	private void setAdminCbChecked() {
		adminCb.setChecked(true);
		rwCb.setChecked(false);
		roCb.setChecked(false);
	}

	private void setRwCbChecked() {
		adminCb.setChecked(false);
		rwCb.setChecked(true);
		roCb.setChecked(false);
	}

	private void setRoCbChecked() {
		adminCb.setChecked(false);
		rwCb.setChecked(false);
		roCb.setChecked(true);
	}

	private void initView() {
		okBtn = (Button) findViewById(R.id.user_add_ok_btn);
		okBtn.setOnClickListener(this);
		cancleBtn = (Button) findViewById(R.id.user_add_cancle_btn);
		cancleBtn.setOnClickListener(this);

		accountEt = (EditText) findViewById(R.id.account_et);
		passwordEt = (EditText) findViewById(R.id.password_et);
		accountEt.setClickable(false);
		accountEt.setFocusable(false);
		nameEt = (EditText) findViewById(R.id.name_et);

		adminCb = (CheckBox) findViewById(R.id.permission_admin);
		rwCb = (CheckBox) findViewById(R.id.permission_rw);
		roCb = (CheckBox) findViewById(R.id.permission_r);
		adminCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (adminCb.isChecked()) {
					rwCb.setChecked(false);
					roCb.setChecked(false);
				}
			}
		});

		rwCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (rwCb.isChecked()) {
					adminCb.setChecked(false);
					roCb.setChecked(false);
				}
			}
		});

		roCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (roCb.isChecked()) {
					adminCb.setChecked(false);
					rwCb.setChecked(false);
				}
			}
		});

	}

	private void initTitleView() {
		backView = (ImageView) findViewById(R.id.title_back);
		titleView = (TextView) findViewById(R.id.title_text);
		titleView.setText(ResTools.getResString(mContext, R.string.edit_user));
		backView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		btView = (ImageView) findViewById(R.id.title_bt);
		btView.setVisibility(View.GONE);
		titleMeterList=(ImageView)findViewById(R.id.title_meterlist);
		titleMeterList.setVisibility(View.GONE);
	}

	private void updateUser() {
		String account = accountEt.getText().toString();

		String password = passwordEt.getText().toString();
		String name = nameEt.getText().toString();
		String permission = "";
		if (adminCb.isChecked()) {
			permission = "AdminUser";
		} else if (rwCb.isChecked()) {
			permission = "ProgramUser";
		} else if (roCb.isChecked()) {
			permission = "ReadUser";
		}

		if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(name)
				&& !TextUtils.isEmpty(permission)) {
			CustomDialog.showWaitDialog(mContext, mContext.getResources().getString(R.string.updating));
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					CustomDialog.dismissDialog();
					finish();
				}
			}, 2000);
			DbUtil.updateUser(account, password, name, permission);

		} else {
			ToastUtil.showShortToast(mContext, mContext.getResources().getString(R.string.info_not_completed));
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.user_add_cancle_btn:
			finish();
			break;
		case R.id.user_add_ok_btn:
			updateUser();
			break;
		default:
			break;
		}
	}

}
