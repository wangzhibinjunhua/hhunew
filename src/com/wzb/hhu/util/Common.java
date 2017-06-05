package com.wzb.hhu.util;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

import android.util.Log;

/**
 * @author wzb<wangzhibin_x@qq.com>
 * @date Jun 4, 2017 11:24:40 PM	
 */
public class Common {
	
	public static String bytesToHexString(byte[] src){  
        StringBuilder stringBuilder = new StringBuilder("");  
        if (src == null || src.length <= 0) {  
            return null;  
        }  
        for (int i = 0; i < src.length; i++) {  
            int v = src[i] & 0xFF;  
            String hv = Integer.toHexString(v);  
            if (hv.length() < 2) {  
                stringBuilder.append(0);  
            }  
            stringBuilder.append(hv);  
        }  
        return stringBuilder.toString();  
    }  
	
	private final static char[] mChars = "0123456789ABCDEF".toCharArray(); 
	 public static String str2HexStr(String str){    
	        StringBuilder sb = new StringBuilder();  
	        byte[] bs = str.getBytes();    
	          
	        for (int i = 0; i < bs.length; i++){    
	            sb.append(mChars[(bs[i] & 0xFF) >> 4]);    
	            sb.append(mChars[bs[i] & 0x0F]);  
	            //sb.append(' ');  
	        }    
	        return sb.toString().trim();    
	    }  
	
	public static byte[] parseHexStringToBytes(final String hex) {
		//String tmp = hex.substring(2).replaceAll("[^[0-9][a-f]]", "");
		String newValue =  hex.toLowerCase(Locale.getDefault());
		String tmp ="";
		if(newValue.substring(0,2).equals("0x")){
			tmp=newValue.substring(2);
		}else{
			tmp=newValue;
		}
		Log.d("wzb","tmp="+tmp);
		byte[] bytes = new byte[tmp.length() / 2]; // every two letters in the string are one byte finally
		
		String part = "";
		
		for(int i = 0; i < bytes.length; ++i) {
			part = "0x" + tmp.substring(i*2, i*2+2);
			bytes[i] = Long.decode(part).byteValue();
			//Log.d("wzb","byte["+i+"]="+bytes[i]);
		}
		//Log.d("wzb","bytes lenght="+bytes.length);
		return bytes;
	}
	
	public static String getMeterPw(String randPw,String meterPw){
		//String passWord="";
		//string s = "FF20012B";
		//string psw = "12345678";
		byte[] px = new byte[8];
		for(int i=0;i<8;i++)
		{
		    //第一轮则是整个s拼接上密码的第一个字符即"FF20012B1"
		    //第二轮则是s的后7个字符拼接上密码的前2个字符即"F20012B12"
		    //第三轮则是s的后6个字符拼接上密码的前3个字符即"20012B123"
		    //依次到8轮
		    //第八轮则是s的最后一个字符拼接上密码的8个字符即"B12345678"
		    String temp = randPw.substring(i) + meterPw.substring(0, i+1);
		    px[i] = crc((byte)0x5A, temp.getBytes());
		}
		//这个data就是上位机通过P1帧下发给电表的数据
		String data = "2"+MakeBCDstr(px)+"00000000";
		
		return data;
	}
	
	/********CRC算法************/
	//CRC表
	public static final byte[] CRC8Tbl = { 0x00, 0x31, 0x62, 0x53, (byte)0xC4, (byte)0xF5, (byte)0xA6, (byte)0x97, (byte)0xB9, (byte)0x88, (byte)0xDB, (byte)0xEA, 0x7D, 0x4C, 0x1F, 0x2E };
	/*算法实现*/
	//crc-CRC初值
	//buf-要计算CRC的数组
	//return-计算出来的CRC值
	private static byte crc(byte crc, byte[] buf)
	{
	    byte tmp;
	    int i;
	   // foreach (byte b in buf)
	    for(i=0;i<buf.length;i++)
	    {
	        tmp = (byte)((crc&0xff) >> 4);
	        crc =(byte)((crc&0xff) << 4);
	        crc ^= CRC8Tbl[tmp ^ ((buf[i]&0xff) >> 4)];
	        tmp = (byte)((crc&0xff) >> 4);
	        crc =(byte)((crc&0xff)<< 4);
	        byte test=(byte)(tmp ^ (buf[i] & 0x0F));
	        System.out.println(test);
	        crc ^= CRC8Tbl[tmp ^ (buf[i] & 0x0F)];
	    }
	    return crc;
	}
	/**********CRC算法**********/

	/**********byte数组转换为BCD码字符串***********/
	//buf-需要转换的byte数组
	//return-转换后的BCD码字符串
	private static String MakeBCDstr(byte[] buf)
	{
	    if (buf.length < 1) return "";   //数组有数据
	    StringBuilder sb = new StringBuilder();
	    int i;
	    //foreach (byte b in buf)  //遍历整个数组
	    for(i=0;i<buf.length;i++)
	    {
	        //字节的高4位+0x30转换为ASCII码
	        sb.append((char)(((buf[i]&0xff) >> 4) + 0x0030));
	        //字节的低4位+0x30转换为ASCII码
	        sb.append((char)((buf[i] & 0x0f) + 0x0030));
	    }
	    //返回转换的BCD码
	    return sb.toString();
	}
	/**********byte数组转换为BCD码字符串***********/

}
