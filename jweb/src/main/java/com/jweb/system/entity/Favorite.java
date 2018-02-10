package com.jweb.system.entity;

import com.jweb.system.persistent.annotation.Bean;
import com.jweb.system.persistent.annotation.Field;
 /** 
 * @ClassName: Favorite 
 * @Description: TODO
 * @author: liyz
 * @date: 2018年1月31日 下午4:15:35  
 */
@Bean(table="jweb_sys_favorite",name="收藏")
public class Favorite {
	
	@Field(name="ID")
	private String id;
	
	@Field(name="账号",required=true)
	private String username;
	
	@Field(name="窗口ID",required=true)
	private String winId;
	
	@Field(name="窗口名称",required=true)
	private String name;
	
	@Field(name="URL")
	private String url;

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

	public String getWinId() {
		return winId;
	}

	public void setWinId(String winId) {
		this.winId = winId;
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
}