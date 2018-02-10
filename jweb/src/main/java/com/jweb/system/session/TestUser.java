package com.jweb.system.session;

 /** 
 * @ClassName: TestUser 
 * @Description: TODO
 * @author: liyz
 * @date: 2018年1月31日 下午4:20:15  
 */
public class TestUser{
	private String name;
	private String pass;
	public TestUser() {}
	public TestUser(String name, String pass) {
		super();
		this.name = name;
		this.pass = pass;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPass() {
		return pass;
	}
	public void setPass(String pass) {
		this.pass = pass;
	}
}
