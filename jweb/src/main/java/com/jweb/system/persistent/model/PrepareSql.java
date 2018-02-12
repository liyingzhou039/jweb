package com.jweb.system.persistent.model;

import java.util.ArrayList;
import java.util.List;

public class SqlAndParams {
	private String sql="";
	List<Object> params = new ArrayList<>(1);
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public List<Object> getParams() {
		return params;
	}
	public void setParams(List<Object> params) {
		this.params = params;
	}
	@Override
	public String toString() {
		String r = this.sql;
		String ov="";
		for(Object o:params) {
			if(ov.length()>0) {
				ov+=",";
			}
			if(o==null) {
				ov+="NULL";
			}else {
				ov+=o.getClass().getName()+"("+o+")";
			}
		}
		r+="\n"+ov;
		return r;
	}
}
