package com.jweb.system.persistent.model;

import java.util.List;

import com.jweb.system.util.StringUtil;

public class Where {
	
	private String name;
	private Expression ex;
	private Object value;
	
	private String relation;
	private Where next;
	private Where sub;
	private Where() {}
	private Where(String name,Expression ex,Object value) {
		this.name=name;
		this.ex=ex;
		this.value=value;
	}
	public static Where create() {
		return new Where();
	}
	
	public Where and(String name,Expression ex,Object value) {
		Where where = new Where(name,ex,value);
		Where next=this;
		while(next.next!=null) {
			next=next.next;
		}
		next.next = where;
		next.relation="and";
		return this;
	}
	public Where or(String name,Expression ex,Object value) {
		Where where = new Where(name,ex,value);
		Where next=this;
		while(next.next!=null) {
			next=next.next;
		}
		next.next = where;
		next.relation="or";
		return this;
	}
	public Where andSub(Where where) {
		Where next=this;
		while(next.next!=null) {
			next=next.next;
		}
		next.sub = where;
		where.relation="and";
		return this;
	}
	public Where orSub(Where where) {
		Where next=this;
		while(next.next!=null) {
			next=next.next;
		}
		next.sub = where;
		where.relation="or";
		return this;
	}
	@Override
	public String toString() {
		PrepareSql prepareSql = toSqlAndParams(this,new PrepareSql());
		
		return prepareSql.toString();
	}
	public static PrepareSql toSqlAndParams(Where where,PrepareSql prepareSql) {
		if(where.name==null) {
			return toSqlAndParams(where.next,prepareSql);
		}
		String sql = where.ex.value();
		sql = sql.replace("${name}", StringUtil.toSlide(where.name));
		if(where.value instanceof List) {
			List<?> values = (List<?>) where.value;
			String vs = "";
			for(int i=0;i<values.size();i++) {
				vs+=",?";
			}
			sql = sql.replace("${value}", vs);
			prepareSql.getParams().addAll(values);
		}else if(where.value.getClass().isArray()){
			Object[] os = (Object[]) where.value;
			String vs = "";
			for(int i=0;i<os.length;i++) {
				vs+=",?";
				prepareSql.getParams().add(os[i]);
			}
			sql = sql.replace("${value}", vs);
		}else {
			sql = sql.replace("${value}", "?");
			prepareSql.getParams().add(where.value);
		}
		
		
		prepareSql.setSql(prepareSql.getSql()+sql);
		
		if(null!=where.sub) {
			prepareSql.setSql(prepareSql.getSql()+" "+where.sub.relation+" (");
			toSqlAndParams(where.sub,prepareSql);
			prepareSql.setSql(prepareSql.getSql()+" ) ");
		}
		
		if(null!=where.next) {
			prepareSql.setSql(prepareSql.getSql()+" "+where.relation+" ");
			toSqlAndParams(where.next,prepareSql);
		}
		return prepareSql;
	}
}
