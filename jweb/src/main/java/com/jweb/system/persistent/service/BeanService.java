package com.jweb.system.persistent.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jweb.system.exception.BusiException;
import com.jweb.system.persistent.BeanPool;
import com.jweb.system.persistent.dialect.AbstractLchDialect;
import com.jweb.system.util.Pager;
import com.jweb.system.persistent.model.PrepareSql;
import com.jweb.system.persistent.model.Table;
import com.jweb.system.persistent.model.Where;
 /** 
 * @ClassName: BeanService 
 * @Description: TODO
 * @author: liyz
 * @date: 2018年1月31日 下午4:19:24  
 */
@Service
public class BeanService {
	@Autowired
	DataSource dataSource;
	private static AbstractLchDialect d= AbstractLchDialect.getDialect();
	public void createBeanTables() {
		List<Table> tables = BeanPool.getBeanTables();
		for(Table table:tables) {
			createBeanTable(table);
		}
	}
	private void setParams(PreparedStatement st,List<Object> params) {
		 try {
             for(int i=1;i<=params.size();i++) {
            	 st.setObject(i, params.get(i-1));
             }
         } catch (Exception e) {
        	 e.printStackTrace();
             System.out.println("填充参数出错，原因："+e.getMessage());
         }
	}
	public void createBeanTable(Table table) {
		String tbSql = d.getCreateTableSql(table);
		Connection conn = null;
		PreparedStatement st = null;  
		System.out.println("创建表["+table.getTableName()+"]");
		System.out.println(tbSql);
		try {
			conn = dataSource.getConnection(); 
			st=conn.prepareStatement(tbSql);  
            st.executeUpdate();
            System.out.println("成功");
		} catch (Exception e) {
			System.out.println("失败["+e.getMessage()+"]");
		}finally{
			try {
				st.close();
				conn.close();
			}catch(Exception e) {}
		}
	}
	public <T> void createBean(T t) throws BusiException{
		PrepareSql preSql=d.createBean(t);
		Connection conn = null;
		PreparedStatement st = null;  
		System.out.println("创建["+t.getClass().getSimpleName()+"]");
		System.out.println(preSql);
		try {
			conn = dataSource.getConnection(); 
			st=conn.prepareStatement(preSql.getSql()); 
			setParams(st,preSql.getParams());
            st.executeUpdate();
            System.out.println("成功");
		} catch (Exception e) {
			System.out.println("失败["+e.getMessage()+"]");
			throw new BusiException("创建["+t.getClass().getSimpleName()+"]失败");
		}finally{
			try {
				st.close();
				conn.close();
			}catch(Exception e) {}
		}
	}
	public <T> void createBeans(List<T> ts) throws BusiException{
		Connection conn = null;
		PreparedStatement st = null;  
		System.out.println("创建["+ts.getClass().getSimpleName()+"]");
		
		try {
			conn = dataSource.getConnection(); 
			for(T t:ts) {
				PrepareSql preSql=d.createBean(t);
				st=conn.prepareStatement(preSql.getSql());
				setParams(st,preSql.getParams());
				System.out.println(preSql);
				st.executeUpdate();
			}
            System.out.println("成功");
		} catch (Exception e) {
			System.out.println("失败["+e.getMessage()+"]");
			throw new BusiException("创建["+ts.getClass().getSimpleName()+"]失败");
		}finally{
			try {
				st.close();
				conn.close();
			}catch(Exception e) {}
		}
	}
	public <T> void removeBean(Class<T> beanClass,String id) throws BusiException{
		PrepareSql preSql = d.removeBean(beanClass,id);
		Connection conn = null;
		PreparedStatement st = null;  
		System.out.println("删除["+beanClass.getSimpleName()+"]");
		System.out.println(preSql);
		try {
			conn = dataSource.getConnection(); 
			st=conn.prepareStatement(preSql.getSql()); 
			setParams(st,preSql.getParams());
            st.executeUpdate();
            System.out.println("成功");
		} catch (Exception e) {
			System.out.println("失败["+e.getMessage()+"]");
			throw new BusiException("删除["+beanClass.getSimpleName()+"]失败");
		}finally{
			try {
				st.close();
				conn.close();
			}catch(Exception e) {}
		}
	}
	public <T> void removeBeans(Class<T> beanClass,Where where) throws BusiException{
		PrepareSql preSql =d.removeBeans(beanClass,where);
		Connection conn = null;
		PreparedStatement st = null;  
		System.out.println("删除["+beanClass.getSimpleName()+"]");
		System.out.println(preSql);
		try {
			conn = dataSource.getConnection(); 
			st=conn.prepareStatement(preSql.getSql()); 
			setParams(st,preSql.getParams());
            st.executeUpdate();
            System.out.println("成功");
		} catch (Exception e) {
			System.out.println("失败["+e.getMessage()+"]");
			throw new BusiException("删除["+beanClass.getSimpleName()+"]失败");
		}finally{
			try {
				st.close();
				conn.close();
			}catch(Exception e) {}
		}
	}
	public <T> void removeBeans(Class<T> beanClass,String[] ids) throws BusiException{
		Connection conn = null;
		PreparedStatement st = null;  
		System.out.println("删除["+beanClass.getSimpleName()+"]");
		
		try {
			conn = dataSource.getConnection(); 
			PrepareSql preSql=d.removeBean(beanClass,null);
			st=conn.prepareStatement(preSql.getSql());  
			System.out.println(preSql);
			for(String id:ids) {
				st.setString(1, id);
				st.addBatch();
			}
            st.executeBatch();
            System.out.println("成功");
		} catch (Exception e) {
			System.out.println("失败["+e.getMessage()+"]");
			throw new BusiException("删除["+beanClass.getSimpleName()+"]失败");
		}finally{
			try {
				st.close();
				conn.close();
			}catch(Exception e) {}
		}
	}
	public <T> void updateBean(T t) throws BusiException{
		PrepareSql preSql = d.updateBean(t);
		Connection conn = null;
		PreparedStatement st = null;  
		System.out.println("更新["+t.getClass().getSimpleName()+"]");
		System.out.println(preSql);
		try {
			conn = dataSource.getConnection(); 
			st=conn.prepareStatement(preSql.getSql());  
			setParams(st,preSql.getParams());
            st.executeUpdate();
            System.out.println("成功");
		} catch (Exception e) {
			System.out.println("失败["+e.getMessage()+"]");
			throw new BusiException("更新["+t.getClass().getSimpleName()+"]失败");
		}finally{
			try {
				st.close();
				conn.close();
			}catch(Exception e) {}
		}
	}
	public <T> Pager<T> getPager(Class<T> beanClass,int offset,int limit,Where where,String orderBy) throws BusiException{
		Pager<T> pager=new Pager<T>();
		PrepareSql preSql= d.listBeans(beanClass, offset, limit, where, orderBy);
		PrepareSql preCountSql= d.toCountSql(beanClass, where, orderBy);
		Connection conn = null;
		PreparedStatement st = null; 
		PreparedStatement countSt = null; 
		ResultSet beanRs = null;
		ResultSet countRs = null;
		System.out.println("查询["+beanClass.getSimpleName()+"]");
		System.out.println(preSql);
		System.out.println(preCountSql);
		try {
			conn = dataSource.getConnection(); 
			st=conn.prepareStatement(preSql.getSql());
			setParams(st,preSql.getParams());
			beanRs = st.executeQuery();
			pager.setRows(d.toBeans(beanRs, beanClass));
			long total = 0;
			countSt=conn.prepareStatement(preCountSql.getSql());
			setParams(countSt,preCountSql.getParams());
			countRs = countSt.executeQuery();
			while(null!=countRs&&countRs.next()){
				total = countRs.getLong(1);
				break;
			}
			pager.setTotal(total);
			System.out.println("成功");
		} catch (Exception e) {
			System.out.println("失败["+e.getMessage()+"]");
			throw new BusiException("获取["+beanClass.getSimpleName()+"]失败");
		}finally{
			try {
				beanRs.close();
				countRs.close();
				st.close();
				conn.close();
			}catch(Exception e) {}
		}
		return pager;
	}
	public <T> T getById(Class<T> beanClass,String id) throws BusiException{
		PrepareSql preSql =d.getBean(beanClass,id);
		Connection conn = null;
		PreparedStatement st = null;  
		ResultSet rs = null;
		T o = null;
		System.out.println("查询["+beanClass.getSimpleName()+"]");
		System.out.println(preSql);
		try {
			conn = dataSource.getConnection(); 
			st=conn.prepareStatement(preSql.getSql());
			setParams(st,preSql.getParams());
			rs = st.executeQuery();
			o = d.toBean(rs, beanClass);
			System.out.println("成功");
		} catch (Exception e) {
			System.out.println("失败["+e.getMessage()+"]");
			throw new BusiException("获取["+beanClass.getSimpleName()+"]失败");
		}finally{
			try {
				rs.close();
				st.close();
				conn.close();
			}catch(Exception e) {}
		}
		return o;
	}
	public <T> List<T> list(Class<T> beanClass,Where where,String orderBy) throws BusiException{
		List<T> ls = new ArrayList<>();
		PrepareSql preSql =d.listBeans(beanClass,where, orderBy);
		Connection conn = null;
		PreparedStatement st = null;  
		ResultSet rs = null;
		System.out.println("查询["+beanClass.getSimpleName()+"]");
		System.out.println(preSql);
		try {
			conn = dataSource.getConnection(); 
			st=conn.prepareStatement(preSql.getSql());
			setParams(st,preSql.getParams());
			rs = st.executeQuery();
			ls = d.toBeans(rs, beanClass);
			System.out.println("成功");
		} catch (Exception e) {
			System.out.println("失败["+e.getMessage()+"]");
			throw new BusiException("获取["+beanClass.getSimpleName()+"]失败");
		}finally{
			try {
				rs.close();
				st.close();
				conn.close();
			}catch(Exception e) {}
		}
		return ls;
	}
}
