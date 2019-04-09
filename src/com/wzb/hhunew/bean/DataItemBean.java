package com.wzb.hhunew.bean;

/**
 * @author wzb<wangzhibin_x@qq.com>
 * @date May 17, 2017 9:59:03 AM
 */

public class DataItemBean {
	private String itemName;
	private String itemValue;
	private String itemState;
	private Boolean itemSelect;
	
	private Boolean isUp;
	private Boolean isHide;

	public DataItemBean() {

	}

	public DataItemBean(String itemName, String itemValue, String itemState, Boolean itemSelect) {
		this.itemName = itemName;
		this.itemValue = itemValue;
		this.itemState = itemState;
		this.itemSelect = itemSelect;
	}

	public String getItemName() {
		return itemName;
	}

	public String getItemValue() {
		return itemValue;
	}

	public String getItemState() {
		return itemState;
	}

	public Boolean getItemSelect() {
		return itemSelect;
	}

	public void setItemName(String name) {
		this.itemName = name;
	}

	public void setItemValue(String value) {
		this.itemValue = value;
	}

	public void setItemState(String state) {
		this.itemState = state;
	}

	public void setItemSelect(Boolean select) {
		this.itemSelect = select;
	}
	
	public void setisUp(Boolean b){
		this.isUp=b;
	}
	public Boolean getisUp(){
		return this.isUp;
	}
	
	public void setisHide(Boolean b){
		this.isHide=b;
	}
	public Boolean getisHide(){
		return this.isHide;
	}
	
	public void isUpToggle(){
		setisUp(!this.isUp);
	}

	public void cbToggle() {

		setItemSelect(!this.itemSelect);

	}
}
