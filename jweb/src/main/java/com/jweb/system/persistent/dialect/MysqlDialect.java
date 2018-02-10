package com.jweb.system.persistent.dialect;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.jweb.system.persistent.BeanPool;
import com.jweb.system.persistent.model.Column;
import com.jweb.system.persistent.model.Table;
import com.jweb.system.util.ObjectUtil;
import com.jweb.system.util.StringUtil;

 /** 
 * @ClassName: MysqlDialect 
 * @Description: TODO
 * @author: liyz
 * @date: 2018年1月31日 下午4:18:22  
 */
public class MysqlDialect extends AbstractLchDialect {
	@Override
	public String getDBField(Column column) {
		
		if (column.getDbType() != null && !column.getDbType().equals("")) {
			return "\t" + StringUtil.toSlide(column.getColumnName()) + " " + column.getDbType();
		}
		String dbType = "";
		Class type = column.getJavaType();
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
	public String getCreateTableSql(Table t) {
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
	@Override
	public <T> String createBean(T bean) {
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
			valSql+=ObjectUtil.toSqlValue(map.get(StringUtil.toCamel(col.getColumnName())));
		}
		sql.append(valSql);
		sql.append(")");
		
		return sql.toString();
	}
	@Override
	public <T> String updateBean(T bean) {
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
			colSql+=colName+"="+ObjectUtil.toSqlValue(map.get(StringUtil.toCamel(colName)));
		}
		sql.append(colSql);
		sql.append(" WHERE ID="+ObjectUtil.toSqlValue(map.get("id")));
		
		return sql.toString();
	}
	@Override
	public <T> String getBean(Class<T> beanClass,String id) {
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
		sql.append(" WHERE ID=");
		sql.append(ObjectUtil.toSqlValue(id));
		return sql.toString();
	}
	@Override
	public <T> String removeBean(Class<T> beanClass,String id) {
		Table table = BeanPool.getBeanTable(beanClass);
		StringBuffer sql=new StringBuffer();
		sql.append("DELETE FROM ");
		sql.append(table.getTableName());
		sql.append(" WHERE ID=");
		sql.append(ObjectUtil.toSqlValue(id));
		return sql.toString();
	}
	@Override
	public <T> String removeBeans(Class<T> beanClass,String whereBy) {
		Table table = BeanPool.getBeanTable(beanClass);
		StringBuffer sql=new StringBuffer();
		sql.append("DELETE FROM ");
		sql.append(table.getTableName());
		if(whereBy!=null) { sql.append(" WHERE "+whereBy);}
		return sql.toString();
	}
	@Override
	public  String listBeans(Class<?> beanClass,String whereBy,String orderBy) {
		Table table = BeanPool.getBeanTable(beanClass);
		StringBuffer sql=new StringBuffer();
		sql.append("select  * from ");
		sql.append(table.getTableName());
		if(null!=whereBy) { sql.append(" where  "+whereBy);}
		if(null!=orderBy) { sql.append(" order by "+orderBy);}
		return sql.toString();
	}
	@Override
	public  String listBeans(Class<?> beanClass, int offset, int limit,String whereBy,String orderBy) {
		Table table = BeanPool.getBeanTable(beanClass);
		StringBuffer sql=new StringBuffer();
		sql.append("select  * from ");
		sql.append(table.getTableName());
		if(null!=whereBy) { sql.append(" where  "+whereBy);}
		if(null!=orderBy) {sql.append(" order by "+orderBy);}
		sql.append(" limit "+offset+","+limit+" ");
		return sql.toString();
	}
	@Override
	public  String toCountSql(Class<?> beanClass,String whereBy,String orderBy) {
		Table table = BeanPool.getBeanTable(beanClass);
		StringBuffer sql=new StringBuffer();
		sql.append("select count(*) from (select * from "+table.getTableName());
		if(null!=whereBy) {sql.append(" where  "+whereBy);}
		if(null!=orderBy) {sql.append(" order by "+orderBy);}
		sql.append(") t");
		return sql.toString();
	}
}
