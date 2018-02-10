package com.jweb.system.entity;

import com.jweb.system.persistent.annotation.Bean;
import com.jweb.system.persistent.annotation.Field;
 /** 
 * @ClassName: Menu 
 * @Description: TODO
 * @author: liyz
 * @date: 2018年1月31日 下午4:15:46  
 */
@Bean(table="jweb_sys_menu",name="菜单")
public class Menu {
	
	@Field(name="ID")
	private String id;
	
	@Field(name="名称",required=true,validType={"length(0,10)"})
	private String name;
	
	@Field(name="URL")
	private String url;
	
	@Field(name="图标")
	private String icon;
	
	@Field(name="排序")
	private int orderNum;
	
	@Field(name="描述",size=1000)
	private String description;
	
	@Field(name="父级ID",required=true)
	private String parentId;

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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public int getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(int orderNum) {
		this.orderNum = orderNum;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
}