package com.wzb.hhu.interf;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.util.ArrayList;
import java.util.List;

import com.wzb.hhu.util.DatabaseHelper;
import com.wzb.hhu.util.LogUtil;
import com.wzb.hhu.util.SharedPreferencesUtil;

/**
 * @author wzb<wangzhibin_x@qq.com>
 * @date May 8, 2017 2:54:00 PM	
 */
public class WApplication extends Application {

    /**
     * 全局上下文环境.
     */
    public static Context CONTEXT;
    /**
     * SP读写工具.
     */
    public static SharedPreferencesUtil sp;
    public static SharedPreferencesUtil sp_user;
    /**
     * 用户信息.
     */
  //  public static UserBean user;
    /**
     * 文件根目录
     */
    public static final String BASE_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Health/";
    /**
     * 图片文件目录
     */
    public static final String IMAGE_FILE_PATH = BASE_FILE_PATH + "image/";

    /**
     * 保存全部activity,便于管理
     */
    public static List<Activity> activityList = new ArrayList<Activity>();


    public static boolean isNetWork=true;
    
    /**
     * SP文件名.
     */
    private final String SP_NAME = "hhu";
    
    
    public static SQLiteDatabase db;
    
    public static int USER_LEVEL=0;
    
    @Override
    public void onCreate() {
        super.onCreate();
        CONTEXT = getApplicationContext();
        sp = new SharedPreferencesUtil(SP_NAME, SharedPreferencesUtil.PRIVATE, CONTEXT);
        sp_user = new SharedPreferencesUtil("hhu_user", SharedPreferencesUtil.PRIVATE, CONTEXT);
        db=new DatabaseHelper(CONTEXT, "hhu").getWritableDatabase();
        LogUtil.openLog(); // 正式发布请注释次程序语句.

    }

   



}
