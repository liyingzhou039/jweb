package com.jweb.system.persistent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jweb.system.persistent.annotation.Bean;
import com.jweb.system.persistent.annotation.Field;
import com.jweb.system.persistent.dialect.AbstractLchDialect;
import com.jweb.system.persistent.model.Column;
import com.jweb.system.persistent.model.Table;
import com.jweb.system.util.StringUtil;

 /** 
 * @ClassName: BeanPool 
 * @Description: TODO
 * @author: liyz
 * @date: 2018年1月31日 下午4:17:59  
 */
public class BeanPool {
	private static Map<Class<?>,Table> classTableMappers = new HashMap<Class<?>,Table>();
	private static Map<String,Class<?>> classNameClassMappers = new HashMap<String,Class<?>>();
	
	public static boolean contains(Class<?> bean) {
		return classTableMappers.containsKey(bean);
	}
	/**
	 * 获取指定包下的所有Bean
	 */
	public static void loadBeans(String packageName){
		List<Class<?>> classes=ClassUtil.getClasses(packageName);
		for(Class<?> clazz:classes){
			Bean beanAnnotation=clazz.getAnnotation(Bean.class);
			if(beanAnnotation!=null){
				addBean(clazz);
				classNameClassMappers.put(StringUtil.firstLowerCase(clazz.getSimpleName()), clazz);
			}
		}
	}
	
	public static void addBean(Class<?> bean){
		if(contains(bean)) {
			return ;
		}
		
		Bean annotation=bean.getAnnotation(Bean.class);
		Table table=new Table();
		table.setTableName(StringUtil.toSlide(getTableName(bean)));
		table.setTableDesc(annotation.name());
		if(!contains(bean)){
			java.lang.reflect.Field[] fieldsA=bean.getDeclaredFields();
			for(java.lang.reflect.Field field:fieldsA){
				Field fAnnotation=field.getAnnotation(Field.class);
				if(fAnnotation!=null){
					Column col=new Column();
					col.setColumnName(StringUtil.toSlide(field.getName()));
					col.setColumnDesc(fAnnotation.name());
					col.setJavaType(field.getType());
					col.setDbType(fAnnotation.type());
					col.setSize(fAnnotation.size());
					col.setDigits(fAnnotation.digits());
					col.setRequired(fAnnotation.required());
					col.setValidType(fAnnotation.validType());
					table.getColumns().add(col);
				}
			}
			classTableMappers.put(bean, table);
		}
	}
	public static Table getBeanTable(Class<?> bean){
		return classTableMappers.get(bean);
	}
	public static Class<?> getBeanClassBySimpleName(String simpleName){
		return classNameClassMappers.get(StringUtil.firstLowerCase(simpleName));
	}
	public static List<Table> getBeanTables(){
		List<Table> tables = new ArrayList<Table>();
		for(Class<?> key:classTableMappers.keySet()) {
			tables.add(classTableMappers.get(key));
		}
		return tables;
	}
	
	public static List<Class<?>> getBeans(){
		List<Class<?>> beans = new ArrayList<Class<?>>();
		for(Class<?> key:classTableMappers.keySet()) {
			beans.add(key);
		}
		return beans;
	}
	static {
		loadBeans("com.jweb");
	}
	
	
	
	private static String getTableName(Class<?> bean) {
		Bean ba=bean.getAnnotation(Bean.class);
		String tableName=ba.table();
		if(tableName==null||tableName.trim().equals("")){
			tableName=bean.getSimpleName();
		}
		return StringUtil.toSlide(tableName);
	}
	public static void main(String[] args){
		AbstractLchDialect d=AbstractLchDialect.getDialect();
		List<Table> tables = BeanPool.getBeanTables();
		for(Table table:tables) {
			System.out.println(d.getCreateTableSql(table));
		}
	}
}
