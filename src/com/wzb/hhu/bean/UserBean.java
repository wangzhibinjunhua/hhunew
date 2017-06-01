package com.wzb.hhu.bean;

public class UserBean {
	private String account;
	private String name;
	private String level;
	private String password;

	public UserBean() {

	}

	public UserBean(String account, String name, String level) {
		this.account = account;
		this.name = name;
		this.level = level;
	}

	public String getAccount() {
		return account;
	}

	public String getName() {
		return name;
	}

	public String getLevel() {
		return level;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password){
		this.password=password;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setLevel(String level) {

		this.level = level;
	}

}
