package com.jweb.system.persistent.model;

import java.util.ArrayList;
import java.util.List;

 /** 
 * @ClassName: Table 
 * @Description: TODO
 * @author: liyz
 * @date: 2018年1月31日 下午4:19:02  
 */
public class Table {
	private String tableName;
	private String tableDesc;
	@Override 
	public boolean equals(Object obj) {
		if(obj!=null&&obj instanceof Table){
			Table t2=(Table) obj;
			if(this.tableName.equals(t2.tableName)){
				return true;
			}
		}
		return false;
	}
	List<Column> columns= new ArrayList<Column>();
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getTableDesc() {
		return tableDesc;
	}
	public void setTableDesc(String tableDesc) {
		this.tableDesc = tableDesc;
	}
	public List<Column> getColumns() {
		return columns;
	}
	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}
	
}
