package com.wzb.hhu.util;

import java.util.ArrayList;

import com.wzb.hhu.bean.UserBean;
import com.wzb.hhu.interf.WApplication;

import android.content.ContentValues;
import android.database.Cursor;

public class DbUtil {

	public static void addUser(String account, String password, String name, String permission) {
		ContentValues values = new ContentValues();
		values.put("account", account);
		values.put("password", EncryptionUtil.md5Encrypt(password));
		values.put("name", name);
		values.put("permission", permission);
		WApplication.db.insert("user", null, values);

	}

	public static void updateUser(String account, String password, String name, String permission) {
		ContentValues values = new ContentValues();
		values.put("account", account);
		values.put("password", EncryptionUtil.md5Encrypt(password));
		values.put("name", name);
		values.put("permission", permission);
		WApplication.db.update("user", values, "account=?", new String[] { account });
	}

	public static void deleteUser(String account) {
		WApplication.db.delete("user", "account=?", new String[] { account });

	}

	public static long getAllUserCount() {
		String sql = "select count(*) from user";
		Cursor cursor = WApplication.db.rawQuery(sql, null);
		cursor.moveToFirst();
		long count = cursor.getLong(0);
		cursor.close();
		return count;
	}

	public static ArrayList<UserBean> getAllUser() {
		ArrayList<UserBean> list = new ArrayList<UserBean>();
		Cursor cursor = WApplication.db.rawQuery("select * from user", null);
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToNext();
			UserBean info = new UserBean();
			info.setAccount(cursor.getString(1));
			info.setName(cursor.getString(3));
			info.setLevel(cursor.getString(4));

			list.add(info);
		}
		cursor.close();
		return list;
	}

	public static UserBean getUser(String account) {

		Cursor cursor = WApplication.db.rawQuery("select * from user where account=?", new String[] { account });
		if (cursor.moveToNext()) {
			UserBean user = new UserBean();
			user.setAccount(cursor.getString(1));
			user.setPassword(cursor.getString(2));
			user.setName(cursor.getString(3));
			user.setLevel(cursor.getString(4));
			return user;
		}
		cursor.close();
		return null;
	}

}
