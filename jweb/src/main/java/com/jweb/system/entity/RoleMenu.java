package com.jweb.system.entity;

import com.jweb.system.persistent.annotation.Bean;
import com.jweb.system.persistent.annotation.Field;
 /** 
 * @ClassName: RoleMenu 
 * @Description: TODO
 * @author: liyz
 * @date: 2018年1月31日 下午4:16:16  
 */
@Bean(table="jweb_sys_role_menu",name="角色菜单关系")
public class RoleMenu {
	
	@Field(name="ID")
	private String id;
	
	@Field(name="角色ID",required=true)
	private String roleId;
	
	@Field(name="菜单ID",required=true)
	private String menuId;

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

	public String getMenuId() {
		return menuId;
	}

	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}
}