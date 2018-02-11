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
	public static Where build() {
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
		SqlAndParams sqlAndParams = toSqlAndParams(this,new SqlAndParams());
		
		return sqlAndParams.toString();
	}
	public static SqlAndParams toSqlAndParams(Where where,SqlAndParams sqlAndParams) {
		if(where.name==null) {
			return toSqlAndParams(where.next,sqlAndParams);
		}
		String sql = where.ex.value();
		sql = sql.replace("${name}", StringUtil.toSlide(where.name));
		if(where.value instanceof List) {
			List<?> values = (List<?>) where.value;
			String vs = "";
			for(Object o : values) {
				if(vs.length()>0) {
					vs+=",";
				}
				vs+="?";
			}
			sql = sql.replace("${value}", vs);
			sqlAndParams.getParams().addAll(values);
		}else {
			sql = sql.replace("${value}", "?");
			sqlAndParams.getParams().add(where.value);
		}
		
		
		sqlAndParams.setSql(sqlAndParams.getSql()+sql);
		
		if(null!=where.sub) {
			sqlAndParams.setSql(sqlAndParams.getSql()+" "+where.sub.relation+" (");
			toSqlAndParams(where.sub,sqlAndParams);
			sqlAndParams.setSql(sqlAndParams.getSql()+" ) ");
		}
		
		if(null!=where.next) {
			sqlAndParams.setSql(sqlAndParams.getSql()+" "+where.relation+" ");
			toSqlAndParams(where.next,sqlAndParams);
		}
		return sqlAndParams;
	}
}
