package com.jweb.common.persistent.dialect;

import java.util.Date;

import com.jweb.common.persistent.BeanPool;
import com.jweb.common.persistent.model.Column;
import com.jweb.common.persistent.model.PrepareSql;
import com.jweb.common.persistent.model.Table;
import com.jweb.common.persistent.model.Where;
import com.jweb.common.util.StringUtil;

 /** 
 * @ClassName: MysqlDialect 
 * @Description: TODO
 * @author: liyz
 * @date: 2018年1月31日 下午4:18:22  
 */
public class SqliteDialect extends AbstractLchDialect {
	@Override
	public String getDBField(Column column) {
		
		if (column.getDbType() != null && !column.getDbType().equals("")) {
			return "\t" + StringUtil.toSlide(column.getColumnName()) + " " + column.getDbType();
		}
		String dbType = "";
		Class<?> type = column.getJavaType();
		if (type.equals(Integer.TYPE)||type.equals(Integer.class)) {
			dbType = " INTEGER DEFAULT 0";

		} else if (type.equals(Long.TYPE)||type.equals(Long.class)) {
			dbType = " BIGINT DEFAULT 0";

		} else if (type.equals(Double.TYPE)||type.equals(Double.class)) {
			dbType = " DOUBLE DEFAULT 0.0";

		} else if (type.equals(Float.TYPE)||type.equals(Float.class)) {
			dbType = " FLOAT DEFAULT 0.0";

		} else if (type.equals(Short.TYPE)||type.equals(Short.class)) {
			dbType = " INTEGER DEFAULT 0";

		} else if (type.equals(Byte.TYPE)||type.equals(Byte.class)) {
			dbType = " INTEGER DEFAULT 0";

		} else if (type.equals(Boolean.TYPE)||type.equals(Boolean.class)) {
			dbType = " BIT DEFAULT 0";

		} else if (type.equals(String.class)) {
			dbType = " VARCHAR(" + column.getSize() + ") DEFAULT NULL";

		}else if (type.equals(Date.class)) {
			dbType = " DATETIME DEFAULT NULL";

		}else if (type.equals(Character.TYPE)||type.equals(Character.class)) {
			dbType = " VARCHAR(2) DEFAULT NULL";
		} else {
			return null;
		}
		/*
		 * 如果字段为id则加上主键
		 */
		if(column.getColumnName().equalsIgnoreCase("id")){
			dbType="VARCHAR(75) PRIMARY KEY";
		}
		return "\t" + column.getColumnName() + " " + dbType;
	}
	@Override
	public  PrepareSql listBeans(Class<?> beanClass, int offset, int limit,Where where,String orderBy) {
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
		if(null!=orderBy) {sql.append(" order by "+orderBy);}
		sql.append(" limit "+offset+","+limit+" ");
		pre.setSql(sql.toString());
		return pre;
	}
}
