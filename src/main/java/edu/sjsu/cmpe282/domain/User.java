package edu.sjsu.cmpe282.domain;

import java.io.Serializable;

public class User implements Serializable  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String firstName;
	private String lastName;
	private String email;
	private String passwd;
	private String lastLoginTime;
	
		
	public String getLastLoginTime() {
		return lastLoginTime;
	}


	public void setLastLoginTime(String lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}


	public User() {
		super();
	}
	
		
	public User(String firstName, String lastName, String email, String passwd) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.passwd = passwd;
	}



	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
		//new com.sun.jersey.spi.container.servlet.ServletContainer();
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPasswd() {
		return passwd;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

}
