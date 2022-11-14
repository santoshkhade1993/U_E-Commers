package com.eshop.common;

public class User {

	private int id;
	private String name;
	private String mob;
	private String address;
	private String pass;
	private String email;
	
	public User() {
		
	}
	
	public User(String name, String mob, String address, String pass, String email) {
		this.name = name;
		this.mob = mob;
		this.address = address;
		this.pass = pass;
		this.email = email;
	}

	public void setId(int id) {
		this.id=id;
		
	}
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMob() {
		return mob;
	}

	public void setMob(String mob) {
		this.mob = mob;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	
	
}
