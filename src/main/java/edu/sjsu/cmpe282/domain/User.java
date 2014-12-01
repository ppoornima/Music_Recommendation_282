package edu.sjsu.cmpe282.domain;

import java.io.Serializable;

public class User implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String userid;
	private String passwd;
	private String lastlogin;

	public String getUserid() {
		return userid;
	}


	public void setUserid(String userid) {
		this.userid = userid;
	}


	public String getLastlogin() {
		return lastlogin;
	}


	public void setLastlogin(String lastlogin) {
		this.lastlogin = lastlogin;
	}


	public User() {
		super();
	}


	public User(String userid, String passwd) {
		super();
		this.userid = userid;

		this.passwd = passwd;
	}




	public String getPasswd() {
		return passwd;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

}
