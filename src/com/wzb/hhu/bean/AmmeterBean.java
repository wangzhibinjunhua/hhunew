package com.wzb.hhu.bean;



/**
 * @author wzb<wangzhibin_x@qq.com>
 * @date May 4, 2017 10:28:22 AM	
 */
public class AmmeterBean {
	
	private String sn;
	private String location;
	private String model;
	
	public AmmeterBean(){
		
	}
	public AmmeterBean(String sn,String location,String model){
		this.sn=sn;
		this.location=location;
		this.model=model;
	}
	
	public String getSn(){
		return sn;
	}
	
	public String getLocation(){
		return location;
	}
	
	public String getModel(){
		return model;
	} 
	
	public void setSn(String sn){
		this.sn=sn;
	}
	
	public void setLocation(String location){
		this.location=location;
	}
	
	public void setModel(String model){
		this.model=model;
	}
}
