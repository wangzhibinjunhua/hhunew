package com.wzb.hhu.util;

import android.content.Context;
import android.content.res.Resources;
/**
 * @author wzb<wangzhibin_x@qq.com>
 * @date May 5, 2017 10:25:30 AM	
 */
public class ResTools {
	
	public static String getResString(Context context,int id){
		Resources res=context.getResources();
		return res.getString(id);
	}
}
