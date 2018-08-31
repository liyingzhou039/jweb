package com.jweb.common.persistent.dialect;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jweb.common.persistent.BeanPool;
import com.jweb.common.persistent.model.Column;
import com.jweb.common.persistent.model.PrepareSql;
import com.jweb.common.persistent.model.Table;
import com.jweb.common.persistent.model.Where;
import com.jweb.common.util.ObjectUtil;
import com.jweb.common.util.StringUtil;

 /** 
 * @ClassName: AbstractLchDialect 
 * @Description: TODO
 * @author: liyz
 * @date: 2018年1月31日 下午4:18:09  
 */
public abstract class  AbstractLchDialect {
	public abstract String getDBField(Column colum) ;
	public  String getCreateTableSql(Table t) {
		StringBuffer sb=new StringBuffer();
		if(null==t) { return "";}
		
		String tableName=t.getTableName();
		
		sb.append("CREATE TABLE ");
		sb.append(tableName);
		sb.append("(\n");
		
		List<Column> columns =  t.getColumns();
		boolean flag=false;
		if(null!=columns){
			for(int i=0;i<columns.size();i++){
				Column col=columns.get(i);
				String dbType=getDBField(col);
				if(null!=dbType){
					if(flag){
						sb.append(",\n");
					}
					flag=true;
					sb.append(dbType);
				}
				
			}
		}
		sb.append("\n)");
		return sb.toString();
	}
	public  <T> PrepareSql createBean(T bean) {
		PrepareSql pre = new PrepareSql();
		Table table = BeanPool.getBeanTable(bean.getClass());
		StringBuffer sql=new StringBuffer();
		sql.append("INSERT INTO ");
		sql.append(table.getTableName());
		sql.append("(");
		String colSql ="";
		for(Column col:table.getColumns()) {
			if(colSql.length()>0) {
				colSql+=",";
			}
			colSql+=col.getColumnName();
		}
		sql.append(colSql);
		sql.append(") VALUES(");
		
		Map<String,Object> map=ObjectUtil.toMap(bean);
		String valSql="";
		for(Column col:table.getColumns()) {
			if(valSql.length()>0){
				valSql+=',';
			}
			valSql+="?";
			pre.getParams().add(map.get(StringUtil.toCamel(col.getColumnName())));
		}
		sql.append(valSql);
		sql.append(")");
		pre.setSql(sql.toString());
		return pre;
	}
	public  <T> PrepareSql updateBean(T bean) {
		PrepareSql pre = new PrepareSql();
		Table table = BeanPool.getBeanTable(bean.getClass());
		StringBuffer sql=new StringBuffer();
		sql.append("UPDATE ");
		sql.append(table.getTableName());
		sql.append(" SET ");
		
		Map<String,Object> map=ObjectUtil.toMap(bean);
		String colSql ="";
		for(Column col:table.getColumns()) {
			String colName=col.getColumnName();
			if(colSql.length()>0) {
				colSql+=",";
			}
			colSql+=colName+"= ?";
			pre.getParams().add(map.get(StringUtil.toCamel(colName)));
		}
		sql.append(colSql);
		sql.append(" WHERE ID= ? ");
		pre.getParams().add(map.get("id"));
		pre.setSql(sql.toString());
		return pre;
	}
	public  <T> PrepareSql getBean(Class<T> beanClass,String id) {
		PrepareSql pre = new PrepareSql();
		Table table = BeanPool.getBeanTable(beanClass);
		StringBuffer sql=new StringBuffer();
		sql.append("SELECT ");
		
		String colSql ="";
		for(Column col:table.getColumns()) {
			if(colSql.length()>0) {
				colSql+=",";
			}
			colSql+=col.getColumnName();
		}
		sql.append(colSql);
		sql.append(" FROM ");
		sql.append(table.getTableName());
		sql.append(" WHERE ID= ?");
		pre.getParams().add(id);
		pre.setSql(sql.toString());
		return pre;
	}
	public  <T> PrepareSql removeBean(Class<T> beanClass,String id) {
		PrepareSql pre = new PrepareSql();
		Table table = BeanPool.getBeanTable(beanClass);
		StringBuffer sql=new StringBuffer();
		sql.append("DELETE FROM ");
		sql.append(table.getTableName());
		sql.append(" WHERE ID= ?");
		pre.getParams().add(id);
		pre.setSql(sql.toString());
		return pre;
	}
	public  <T> PrepareSql removeBeans(Class<T> beanClass,Where where) {
		PrepareSql pre = new PrepareSql();
		Table table = BeanPool.getBeanTable(beanClass);
		StringBuffer sql=new StringBuffer();
		sql.append("DELETE FROM ");
		sql.append(table.getTableName());
		
		if(where!=null) { 
			PrepareSql whereSql = Where.toSqlAndParams(where, new PrepareSql());
			sql.append(" WHERE "+whereSql.getSql());
			pre.getParams().addAll(whereSql.getParams());
		}
		
		pre.setSql(sql.toString());
		return pre;
	}
	public PrepareSql listBeans(Class<?> beanClass,Where where,String orderBy) {
		PrepareSql pre = new PrepareSql();
		Table table = BeanPool.getBeanTable(beanClass);
		StringBuffer sql=new StringBuffer();
		sql.append("select  * from ");
		sql.append(table.getTableName());
		if(null!=where) {
			PrepareSql whereSql = Where.toSqlAndParams(where, new PrepareSql());
			sql.append(" where  "+whereSql.getSql());
			pre.getParams().addAll(whereSql.getParams());
		}
		if(null!=orderBy && !"".equals(orderBy.trim())) { sql.append(" order by "+orderBy);}
		pre.setSql(sql.toString());
		return pre;
	}
	public abstract PrepareSql listBeans(Class<?> beanClass,int offset,int limit,Where where,String orderBy);
	public  PrepareSql listBeans(Class<?> beanClass,int offset,int limit,Where where) {
		return this.listBeans(beanClass, offset, limit,where,null);
	}
	public  PrepareSql listBeans(Class<?> beanClass,int offset,int limit) {
		return this.listBeans(beanClass, offset, limit,null,null);
	}
	
	public  PrepareSql toCountSql(Class<?> beanClass,Where where,String orderBy) {
		PrepareSql pre = new PrepareSql();
		Table table = BeanPool.getBeanTable(beanClass);
		StringBuffer sql=new StringBuffer();
		sql.append("select count(*) from (select * from "+table.getTableName());
		if(null!=where) {
			PrepareSql whereSql = Where.toSqlAndParams(where, new PrepareSql());
			sql.append(" where  "+whereSql.getSql());
			pre.getParams().addAll(whereSql.getParams());
		}
		if(null!=orderBy && !"".equals(orderBy.trim())) {sql.append(" order by "+orderBy);}
		sql.append(") t");
		pre.setSql(sql.toString());
		return pre;
	}
	public PrepareSql toCountSql(Class<?> beanClass,Where where) {
		return this.toCountSql(beanClass,where,null);
	}
	public PrepareSql toCountSql(Class<?> beanClass) {
		return this.toCountSql(beanClass,null,null);
	}
	
	
	
	
	public <T> List<T> toBeans(ResultSet rs,Class<T> beanClass) throws SQLException{
		List<T> ls = new ArrayList<T>(1);
		if(null!=rs) {
			ResultSetMetaData rsm =rs.getMetaData();
			while(rs.next()){
				T o = null;
				try {
					o = beanClass.newInstance();
				} catch (Exception e) {
					e.printStackTrace();
				}
				for(int i=0;i<rsm.getColumnCount();i++) {
					try {
						String colName = rsm.getColumnName(i+1).toLowerCase();
						String fieldName = StringUtil.toCamel(colName);
						Object value =rs.getObject(i+1);
						Field field = beanClass.getDeclaredField(fieldName);
						Class<?> fieldType = field.getType();
						Method setter = beanClass.getDeclaredMethod("set"+StringUtil.firstUpperCase(fieldName), fieldType);
						setter.invoke(o, ObjectUtil.valueOfClass(value, fieldType));
					}catch(Exception e) {e.printStackTrace();}
				}
				ls.add(o);
			}
		}
		return ls;
	}
	public <T> T toBean(ResultSet rs,Class<T> beanClass) throws SQLException{
		T o = null;
		if(null!=rs) {
			ResultSetMetaData rsm =rs.getMetaData();
			if(rs.next()){
				
				try {
					o = beanClass.newInstance();
				} catch (Exception e) {
					e.printStackTrace();
				}
				for(int i=0;i<rsm.getColumnCount();i++) {
					try {
						String colName = rsm.getColumnName(i+1).toLowerCase();
						String fieldName = StringUtil.toCamel(colName);
						Object value =rs.getObject(i+1);
						Field field = beanClass.getDeclaredField(fieldName);
						Class<?> fieldType = field.getType();
						Method setter = beanClass.getDeclaredMethod("set"+StringUtil.firstUpperCase(fieldName), fieldType);
						setter.invoke(o, ObjectUtil.valueOfClass(value, fieldType));
					}catch(Exception e) {e.printStackTrace();}
				}
			}
		}
		return o;
	}
	public static AbstractLchDialect getDialect(){
		return new SqliteDialect();
	}
}
