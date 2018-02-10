package com.jweb.system.entity;

import com.jweb.system.persistent.annotation.Bean;
import com.jweb.system.persistent.annotation.Field;
 /** 
 * @ClassName: RoleElement 
 * @Description: TODO
 * @author: liyz
 * @date: 2018年1月31日 下午4:16:06  
 */
@Bean(table="jweb_sys_role_element",name="角色资源关系")
public class RoleElement {
	
	@Field(name="ID")
	private String id;
	
	@Field(name="角色ID",required=true)
	private String roleId;
	
	@Field(name="资源ID",required=true)
	private String elementId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}
}