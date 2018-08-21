package com.jweb.sys.entity.page;

import com.jweb.common.persistent.annotation.Bean;
import com.jweb.common.persistent.annotation.Field;

@Bean(table="jweb_sys_page",name="页面")
public class PageBean {
	@Field(name="ID")
	private String id;
	
	@Field(name="名称")
	private String name;
	
	@Field(name="uri",required=true)
	private String uri;
	
	@Field(name="页面内容",required=true,type="TEXT")
	private String page;

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

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}
}
