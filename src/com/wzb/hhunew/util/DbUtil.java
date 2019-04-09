package com.wzb.hhunew.util;

import java.util.ArrayList;

import com.wzb.hhunew.bean.AmmeterBean;
import com.wzb.hhunew.bean.UserBean;
import com.wzb.hhunew.interf.WApplication;

import android.content.ContentValues;
import android.database.Cursor;

public class DbUtil {

	public static String getUserLevel(String account){
		Cursor cursor = WApplication.db.rawQuery("select * from user where account=?", new String[] { account });
		if (cursor.moveToNext()) {
			return cursor.getString(4);
		}
		cursor.close();
		return null;
	}

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
	
	public static String getUserPw(String account){
		Cursor cursor = WApplication.db.rawQuery("select * from user where account=?", new String[] { account });
		if (cursor.moveToNext()) {
			return cursor.getString(2);
		}
		cursor.close();
		return null;
	}
	
	//meter
	public static AmmeterBean getMeter(String sn){
		Cursor cursor = WApplication.db.rawQuery("select * from meter where sn=?", new String[] { sn });
		if (cursor.moveToNext()) {
			AmmeterBean meter = new AmmeterBean();
			meter.setSn(cursor.getString(1));
			meter.setPassword(cursor.getString(2));
			meter.setLocation(cursor.getString(3));
			meter.setModel(cursor.getString(4));
			return meter;
		}
		cursor.close();
		
		return null;
	}
	
	public static String getMeterModel(String sn){
		Cursor cursor = WApplication.db.rawQuery("select * from meter where sn=?", new String[] { sn });
		if (cursor.moveToNext()) {
			return cursor.getString(4);
		}
		cursor.close();
		return null;
	}
	
	
	public static ArrayList<AmmeterBean> getAllMeter(){
		ArrayList<AmmeterBean> list = new ArrayList<AmmeterBean>();
		Cursor cursor = WApplication.db.rawQuery("select * from meter order by id desc", null);
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToNext();
			AmmeterBean info = new AmmeterBean();
			info.setSn(cursor.getString(1));
			info.setPassword(cursor.getString(2));
			info.setLocation(cursor.getString(3));
			info.setModel(cursor.getString(4));

			list.add(info);
		}
		cursor.close();
		return list;
		
	}
	
	public static ArrayList<AmmeterBean> getSomeMeter(int num){
		if(num<1) num=10;
		ArrayList<AmmeterBean> list = new ArrayList<AmmeterBean>();
		Cursor cursor = WApplication.db.rawQuery("select * from meter order by id desc limit ?", new String[]{""+num});
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToNext();
			AmmeterBean info = new AmmeterBean();
			info.setSn(cursor.getString(1));
			info.setPassword(cursor.getString(2));
			info.setLocation(cursor.getString(3));
			info.setModel(cursor.getString(4));
			list.add(info);
		}
		cursor.close();
		return list;
		
	}
	
	
	public static void deleteMeter(String sn){
		WApplication.db.delete("meter", "sn=?", new String[] { sn });
		
	}
	
	public static void updateMeter(String sn,String password,String location,String model){
		ContentValues values = new ContentValues();
		values.put("sn", sn);
		values.put("password", password);
		values.put("location", location);
		values.put("model", model);
		WApplication.db.update("meter", values, "sn=?", new String[] { sn });
	}
	
	public static void addMeter(String sn,String password,String location,String model){
		LogUtil.logMessage("wzb", "add meter: sn="+sn+"pw="+password+"location="+location+"model="+model);;
		ContentValues values = new ContentValues();
		values.put("sn", sn);
		values.put("password", password);
		values.put("location", location);
		values.put("model", model);
		WApplication.db.insert("meter", null, values);
	}
	
	public static long getAllMeterCount() {
		String sql = "select count(*) from meter";
		Cursor cursor = WApplication.db.rawQuery(sql, null);
		cursor.moveToFirst();
		long count = cursor.getLong(0);
		cursor.close();
		return count;
	}
	
	public static ArrayList<AmmeterBean> searchSomeMeter(String sn){
		ArrayList<AmmeterBean> list = new ArrayList<AmmeterBean>();
		Cursor cursor = WApplication.db.rawQuery("select * from meter where sn like ? order by id desc", new String[]{"%"+sn+"%"});
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToNext();
			AmmeterBean info = new AmmeterBean();
			info.setSn(cursor.getString(1));
			info.setPassword(cursor.getString(2));
			info.setLocation(cursor.getString(3));
			info.setModel(cursor.getString(4));
			list.add(info);
		}
		cursor.close();
		return list;
		
	}
	


}
