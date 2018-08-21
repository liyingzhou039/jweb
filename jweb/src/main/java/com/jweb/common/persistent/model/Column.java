package com.jweb.common.persistent.model;

 /** 
 * @ClassName: Column 
 * @Description: TODO
 * @author: liyz
 * @date: 2018年1月31日 下午4:18:55  
 */
public class Column {
	private String columnName;
	private String columnDesc;
	private Class<?> javaType;
	private String dbType;
	private int size;
	private int digits;
	private boolean required;
	private String[] validType;
	@Override
	public boolean equals(Object obj) {
		if(obj!=null&&obj instanceof Column){
			Column c2=(Column) obj;
			if(this.columnName.equals(c2.getColumnName())){
				return true;
			}
		}
		return false;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public String getColumnDesc() {
		return columnDesc;
	}
	public void setColumnDesc(String columnDesc) {
		this.columnDesc = columnDesc;
	}
	public Class<?> getJavaType() {
		return javaType;
	}
	public void setJavaType(Class<?> javaType) {
		this.javaType = javaType;
	}
	public String getDbType() {
		return dbType;
	}
	public void setDbType(String dbType) {
		this.dbType = dbType;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public int getDigits() {
		return digits;
	}
	public void setDigits(int digits) {
		this.digits = digits;
	}
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	public String[] getValidType() {
		return validType;
	}
	public void setValidType(String[] validType) {
		this.validType = validType;
	}
}
