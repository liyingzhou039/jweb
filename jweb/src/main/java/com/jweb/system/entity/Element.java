package com.jweb.system.entity;


import com.jweb.system.persistent.annotation.Bean;
import com.jweb.system.persistent.annotation.Field;
 /** 
 * @ClassName: Element 
 * @Description: TODO
 * @author: liyz
 * @date: 2018年1月31日 下午4:14:54  
 */
@Bean(table="jweb_sys_element",name="资源")
public class Element {
	
	@Field(name="ID")
	private String id;
	
	@Field(name="类型",required=true)
	private String type;
	
	@Field(name="名称",required=true,validType={"length(0,50)"})
	private String name;
	
	@Field(name="路经",required=true)
	private String url;
	
	@Field(name="所属菜单",required=true)
	private String menuId;
	
	@Field(name="访问方式")
	private String method;
	
	@Field(name="描述",size=1000)
	private String description;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public String getMenuId() {
		return menuId;
	}

	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}