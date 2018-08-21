package com.jweb.common.util;

 /** 
 * @ClassName: Result 
 * @Description: TODO
 * @author: liyz
 * @date: 2018年1月31日 下午4:20:55 
 * @param <T> 
 */
public class Result<T> {
	private boolean ok = false;
	public boolean isOk() {
		return ok;
	}
	public void setOk(boolean ok) {
		this.ok = ok;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public T getEntity() {
		return entity;
	}
	public void setEntity(T entity) {
		this.entity = entity;
	}
	private String msg;
	private T entity; 
}
