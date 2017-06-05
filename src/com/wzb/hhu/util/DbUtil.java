package com.wzb.hhu.util;

import com.wzb.hhu.bean.UserBean;
import com.wzb.hhu.interf.WApplication;

import android.content.ContentValues;

public class DbUtil {
	
	
	public static void addUser(String account,String password,String name,String permission ){
		ContentValues values=new ContentValues();
		values.put("account", account);
		values.put("password", password);
		values.put("name", name);
		values.put("permission", permission);
		WApplication.db.insert("user", null, values);
		WApplication.db.close();
		
	}
	
	public static void updateUser(String account,String password,String name,String permission){
		ContentValues values=new ContentValues();
		values.put("account", account);
		values.put("password", password);
		values.put("name", name);
		values.put("permission", permission);
		WApplication.db.update("user", values, "account=?", new String[]{account});
	}
	
	public static void deleteUser(String account){
		WApplication.db.delete("user", "account=?", new String[]{account});
		
	}
	
	public static UserBean searchUser(String account){
		
		return null;
	}

}
