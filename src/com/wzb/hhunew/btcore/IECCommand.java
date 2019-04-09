package com.wzb.hhunew.btcore;

import com.wzb.hhunew.interf.WApplication;
import com.wzb.hhunew.util.Common;

/**
 * @author wzb<wangzhibin_x@qq.com>
 * @date Jun 11, 2017 11:28:51 PM
 */
public class IECCommand {
	
	public static final String MONTH_DEFAULT_NUM="12";
	public static final String RATE_DEFAULT_NUM="4";
	
	private static final String READ_CMD_HEAD="01523102";
	private static final String READ_CMD_END="282903";
	
	public static void sppSend(String s) {
		WApplication.bt.send(Common.parseHexStringToBytes(s), false);
	}
	//合相总有功电量
	public static void cmd2(){
		
	} 

}
