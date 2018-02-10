package com.jweb.system.entity;


import com.jweb.system.persistent.annotation.Bean;
import com.jweb.system.persistent.annotation.Field;
 /** 
 * @ClassName: Role 
 * @Description: TODO
 * @author: liyz
 * @date: 2018年1月31日 下午4:14:54  
 */
@Bean(table="jweb_sys_role",name="角色")
public class Role {
	
	@Field(name="ID")
	private String id;
	
	@Field(name="名称",required=true,validType={"length(0,50)"})
	private String name;
	
	@Field(name="描述",size=1000)
	private String description;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}