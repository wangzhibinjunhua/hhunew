package com.wzb.hhunew.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author wzb<wangzhibin_x@qq.com>
 * @date May 7, 2017 12:35:15 AM
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	private static final int VERSION = 1;

	public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	public DatabaseHelper(Context context, String name) {
		this(context, name, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(
				"create table meter(id integer primary key autoincrement,sn varchar(45),password varchar(45),location varchar(45),model varchar(45))");
		db.execSQL(
				"create table user(id integer primary key autoincrement,account varchar(45),password varchar(45),name varchar(45),permission varchar(45))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
