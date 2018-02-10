package com.jweb.system.entity;

import com.jweb.system.persistent.annotation.Bean;
import com.jweb.system.persistent.annotation.Field;
 /** 
 * @ClassName: UserRole 
 * @Description: TODO
 * @author: liyz
 * @date: 2018年1月31日 下午4:16:21  
 */
@Bean(table="jweb_sys_user_role",name="用户角色关系")
public class UserRole {
	
	@Field(name="ID")
	private String id;
	
	@Field(name="用户ID",required=true)
	private String userId;
	
	@Field(name="角色ID",required=true)
	private String roleId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
}