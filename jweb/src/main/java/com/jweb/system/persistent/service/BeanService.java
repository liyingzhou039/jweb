package com.jweb.system.persistent.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jweb.system.exception.BusiException;
import com.jweb.system.persistent.BeanPool;
import com.jweb.system.persistent.dialect.AbstractLchDialect;
import com.jweb.system.util.Pager;
import com.jweb.system.persistent.model.Table;
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
		String sql=d.createBean(t);
		Connection conn = null;
		PreparedStatement st = null;  
		System.out.println("创建["+t.getClass().getSimpleName()+"]");
		System.out.println(sql);
		try {
			conn = dataSource.getConnection(); 
			st=conn.prepareStatement(sql);  
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
		Statement st = null;  
		System.out.println("创建["+ts.getClass().getSimpleName()+"]");
		
		try {
			conn = dataSource.getConnection(); 
			st=conn.createStatement();
			for(T t:ts) {
				String sql=d.createBean(t);
				System.out.println(sql);
				st.addBatch(sql);
			}
            st.executeBatch();
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
		String sql=d.removeBean(beanClass,id);
		Connection conn = null;
		PreparedStatement st = null;  
		System.out.println("删除["+beanClass.getSimpleName()+"]");
		System.out.println(sql);
		try {
			conn = dataSource.getConnection(); 
			st=conn.prepareStatement(sql);  
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
	public <T> void removeBeans(Class<T> beanClass,String whereBy) throws BusiException{
		String sql=d.removeBeans(beanClass,whereBy);
		Connection conn = null;
		PreparedStatement st = null;  
		System.out.println("删除["+beanClass.getSimpleName()+"]");
		System.out.println(sql);
		try {
			conn = dataSource.getConnection(); 
			st=conn.prepareStatement(sql);  
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
		Statement st = null;  
		System.out.println("删除["+beanClass.getSimpleName()+"]");
		
		try {
			conn = dataSource.getConnection(); 
			st=conn.createStatement();  
			for(String id:ids) {
				String sql=d.removeBean(beanClass,id);
				System.out.println(sql);
				st.addBatch(sql);
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
		String sql=d.updateBean(t);
		Connection conn = null;
		PreparedStatement st = null;  
		System.out.println("更新["+t.getClass().getSimpleName()+"]");
		System.out.println(sql);
		try {
			conn = dataSource.getConnection(); 
			st=conn.prepareStatement(sql);  
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
	public <T> Pager<T> getPager(Class<T> beanClass,int offset,int limit,String whereBy,String orderBy) throws BusiException{
		Pager pager=new Pager();
		String sql=d.listBeans(beanClass, offset, limit, whereBy, orderBy);
		String cSql=d.toCountSql(beanClass, whereBy, orderBy);
		Connection conn = null;
		Statement st = null;  
		ResultSet beanRs = null;
		ResultSet countRs = null;
		System.out.println("查询["+beanClass.getSimpleName()+"]");
		System.out.println(sql);
		System.out.println(cSql);
		try {
			conn = dataSource.getConnection(); 
			st=conn.createStatement();
			beanRs = st.executeQuery(sql);
			pager.setRows(d.toBeans(beanRs, beanClass));
			long total = 0;
			countRs = st.executeQuery(cSql);
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
		String sql=d.getBean(beanClass,id);
		Connection conn = null;
		Statement st = null;  
		ResultSet rs = null;
		T o = null;
		System.out.println("查询["+beanClass.getSimpleName()+"]");
		System.out.println(sql);
		try {
			conn = dataSource.getConnection(); 
			st=conn.createStatement();
			rs = st.executeQuery(sql);
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
	public <T> List<T> list(Class<T> beanClass,String whereBy,String orderBy) throws BusiException{
		List<T> ls = new ArrayList();
		String sql=d.listBeans(beanClass,whereBy, orderBy);
		Connection conn = null;
		Statement st = null;  
		ResultSet rs = null;
		System.out.println("查询["+beanClass.getSimpleName()+"]");
		System.out.println(sql);
		try {
			conn = dataSource.getConnection(); 
			st=conn.createStatement();
			rs = st.executeQuery(sql);
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
