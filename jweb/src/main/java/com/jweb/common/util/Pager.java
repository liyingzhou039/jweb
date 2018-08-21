package com.jweb.common.util;

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
	private int pageSize;
	private int pageNumber;
	private List<T> rows=new ArrayList<T>();
	public Pager() {}
	public Pager(List<? extends T> allRows,int pageNumber,int pageSize,Map<String,String> filters) {

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
		if(pageNumber<=1) pageNumber =1;

        this.total = rows.size();
		this.pageSize = pageSize;
		this.pageNumber = pageNumber;
        List<T> pagerRows = new ArrayList<T>();
        long offset = (pageNumber-1)*pageSize;

        for(int i=(pageNumber-1)*pageSize;i<pageNumber*pageSize&&i<rows.size();i++) {
        	pagerRows.add(rows.get(i));
        }
        this.rows = pagerRows;
	}

     public long getTotal() {
         return total;
     }

     public int getPageSize() {
         return pageSize;
     }

     public int getPageNumber() {
         return pageNumber;
     }

     public List<T> getRows() {
         return rows;
     }

     public void setTotal(long total) {
         this.total = total;
     }

     public void setPageSize(int pageSize) {
         this.pageSize = pageSize;
     }

     public void setPageNumber(int pageNumber) {
         this.pageNumber = pageNumber;
     }

     public void setRows(List<T> rows) {
         this.rows = rows;
     }
 }
