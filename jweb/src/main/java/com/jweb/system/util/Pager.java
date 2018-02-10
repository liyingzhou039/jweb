package com.jweb.system.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

 /** 
 * @ClassName: Pager 
 * @Description: TODO
 * @author: liyz
 * @date: 2018年1月31日 下午4:20:43 
 * @param <T> 
 */
public class Pager<T> {
	private long total;
	private List<T> rows=new ArrayList<T>();
	public Pager() {}
	public Pager(int total, List<T> rows) {
        this.total = total;
        this.rows = rows;
	}
	public Pager(List<? extends T> allRows,int offset,int limit,Map<String,String> filters) {
		
		List<T> rows = new ArrayList<T>();
		if(null!=allRows) {
			for(T t:allRows) {
				boolean ok=true;
				if(null!=filters) {
					for(String filed:filters.keySet()){
						String filter=filters.get(filed);
						if(filter==null||filter.trim().equals("")) {
							continue;
						}
						
						try {
							Method getter=t.getClass().getMethod("get"+filed.substring(0, 1).toUpperCase()+filed.substring(1));
							Object value =getter.invoke(t);
							
							if(value==null) {
								ok=false;
								break;
							}else {
								if(filter.startsWith("=")) {
									if(!value.toString().equals(filter.substring(1))) {
										ok=false;
										break;
									}
								}else if(value.toString().indexOf(filter)==-1) {
									ok=false;
									break;
								}
							}
						}catch(Exception e) {
							e.printStackTrace();
						}
					}
				}
				if(ok) {
					rows.add(t);
				}
			}
		}
        this.total = rows.size();
        List<T> pagerRows = new ArrayList<T>();
        for(int i=offset;i<offset+limit&&i<rows.size();i++) {
        	pagerRows.add(rows.get(i));
        }
        this.rows = pagerRows;
	}
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	public List<T> getRows() {
		return rows;
	}
	public void setRows(List<T> rows) {
		this.rows = rows;
	}
	
}
