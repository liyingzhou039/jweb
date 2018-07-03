package com.jweb.busi.entiry;

import java.util.Date;

import com.jweb.system.persistent.annotation.Bean;
import com.jweb.system.persistent.annotation.Field;

@Bean(table="jweb_sys_access",name="访问统计")
public class AccessBean {
	@Field(name="ID")
	private String id;
	
	@Field(name="节点")
	private String ip;
	
	@Field(name="uri",required=true)
	private String uri;
	
	@Field(name="访问时间",required=true)
	private Date time;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	
	
}
