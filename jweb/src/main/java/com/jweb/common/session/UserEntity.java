package com.jweb.common.session;

import java.util.UUID;

/** 
 * @ClassName: UserEntity 
 * @Description: TODO
 * @author: liyz
 * @date: 2018年1月31日 下午4:20:21 
 * @param <U> 
 */
public  class UserEntity<U> {
	private String token;
	private U user;
	private long endTime;
	public UserEntity() {}
	public UserEntity(U user, long endTime) {
		this.token = UUID.randomUUID().toString().replace("-", "");
		this.user = user;
		this.endTime = System.currentTimeMillis()+endTime;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public U getUser() {
		return user;
	}
	public void setUser(U user) {
		this.user = user;
	}
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	public boolean isExpired() {
		long now = System.currentTimeMillis();
		if(now>endTime) {
			return true;
		}else {
			return false;
		}
	}
}
