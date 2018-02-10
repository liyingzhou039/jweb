package com.jweb.system.persistent.dialect;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.jweb.system.persistent.model.Column;
import com.jweb.system.persistent.model.Table;
import com.jweb.system.util.ObjectUtil;
import com.jweb.system.util.StringUtil;

 /** 
 * @ClassName: AbstractLchDialect 
 * @Description: TODO
 * @author: liyz
 * @date: 2018年1月31日 下午4:18:09  
 */
public abstract class  AbstractLchDialect {
	public abstract String getDBField(Column colum) ;
	public abstract String getCreateTableSql(Table t);
	public abstract <T> String createBean(T bean);
	public abstract <T> String updateBean(T bean);
	public abstract <T> String getBean(Class<T> beanClass,String id);
	public abstract <T> String removeBean(Class<T> beanClass,String id);
	public abstract <T> String removeBeans(Class<T> beanClass,String whereBy);
	public abstract String listBeans(Class<?> beanClass,String whereBy,String orderBy);
	public abstract String listBeans(Class<?> beanClass,int offset,int limit,String whereBy,String orderBy);
	public  String listBeans(Class<?> beanClass,int offset,int limit,String whereBy) {
		return this.listBeans(beanClass, offset, limit,whereBy,null);
	}
	public  String listBeans(Class<?> beanClass,int offset,int limit) {
		return this.listBeans(beanClass, offset, limit,null,null);
	}
	
	public abstract  String toCountSql(Class<?> beanClass,String whereBy,String orderBy);
	public String toCountSql(Class<?> beanClass,String whereBy) {
		return this.toCountSql(beanClass,whereBy,null);
	}
	public String toCountSql(Class<?> beanClass) {
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
		return new MysqlDialect();
	}
}
