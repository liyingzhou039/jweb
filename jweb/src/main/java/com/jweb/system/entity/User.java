package com.jweb.system.entity;


import java.util.List;

import com.jweb.system.persistent.annotation.Bean;
import com.jweb.system.persistent.annotation.Field;
 /** 
 * @ClassName: User 
 * @Description: TODO
 * @author: liyz
 * @date: 2018年1月31日 下午4:14:54  
 */
@Bean(table="jweb_sys_user",name="用户")
public class User {
	
	@Field(name="ID")
	private String id;
	
	@Field(name="帐号",required=true,validType={"length(0,50)"})
	private String username;
	
	@Field(name="密码",required=true,validType={"length(0,50)"})
	private String password;
	
	@Field(name="描述",size=1000)
	private String description;
	
	private String token;
	
	private List<Role> roles;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
	
	
}