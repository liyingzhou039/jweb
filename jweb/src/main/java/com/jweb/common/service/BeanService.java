package com.jweb.common.service;

import com.jweb.common.exception.BusiException;
import com.jweb.common.persistent.BeanPool;
import com.jweb.common.persistent.dialect.AbstractLchDialect;
import com.jweb.common.persistent.model.PrepareSql;
import com.jweb.common.persistent.model.Table;
import com.jweb.common.persistent.model.Where;
import com.jweb.common.util.Pager;
import com.jweb.common.util.RegexUtil;
import com.jweb.common.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

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
    @Autowired
    private AbstractLchDialect d;
    @Autowired
    protected ValidatorService validatorService;

    public AbstractLchDialect getDialect() {
        return d;
    }
    private String parseOrderBy(String orderBy){
        if(null==orderBy) return "";
        String[] cols = orderBy.split(",");
        StringBuffer orderBySql = new StringBuffer();
        for(String col:cols){
            if(col!=null&& !col.trim().equals("")){
                col = col.trim();
                String[] words = col.split("\\s+");
                orderBySql.append(StringUtil.toSlide(words[0].trim()));
                if(words.length>1){
                    orderBySql.append(" ");
                    orderBySql.append(words[1]);
                }
                orderBySql.append(",");
            }
        }
        orderBy = orderBySql.toString();
        if(orderBy.endsWith(","))
            orderBy =orderBy.substring(0,orderBy.length()-1);
        return orderBy.toLowerCase();
    }
    public void createTables() {
        List<Table> tables = BeanPool.getBeanTables();
        for (Table table : tables) {
            createTable(table);
        }
    }

    private void setParams(PreparedStatement st, List<Object> params) {
        try {
            for (int i = 1; i <= params.size(); i++) {
                st.setObject(i, params.get(i - 1));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("填充参数出错，原因：" + e.getMessage());
        }
    }

    public void createTable(Table table) {
        String tbSql = d.getCreateTableSql(table);
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = DataSourceUtils.doGetConnection(dataSource);
            st = conn.prepareStatement(tbSql);
            st.executeUpdate();
        } catch (Exception e) {
            System.out.println("创建表[" + table.getTableName() + "]");
            System.out.println(tbSql);
            System.out.println("失败[" + e.getMessage() + "]");
        } finally {
            try {
                st.close();
                DataSourceUtils.releaseConnection(conn, dataSource);
            } catch (Exception e) {
            }
        }
    }

    public <T> T create(T t) throws BusiException {
        try{
            Method idGetter=t.getClass().getDeclaredMethod("getId");
            if(StringUtil.isNull((String)idGetter.invoke(t))) {
                Method idSetter = t.getClass().getDeclaredMethod("setId", String.class);
                idSetter.invoke(t, UUID.randomUUID().toString().replace("-", ""));
            }
        }catch(Exception e){}
        validatorService.check(t);
        PrepareSql preSql = d.createBean(t);
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = DataSourceUtils.doGetConnection(dataSource);
            st = conn.prepareStatement(preSql.getSql());
            setParams(st, preSql.getParams());
            st.executeUpdate();
        } catch (Exception e) {
            System.out.println("创建[" + t.getClass().getSimpleName() + "]");
            System.out.println(preSql);
            System.out.println("失败[" + e.getMessage() + "]");
            throw new BusiException("创建[" + t.getClass().getSimpleName() + "]失败:"+e.getMessage());
        } finally {
            try {
                st.close();
                DataSourceUtils.releaseConnection(conn, dataSource);
            } catch (Exception e) {
            }
        }
        return t;
    }


    public <T> void remove(Class<T> beanClass, String id) throws BusiException {
        PrepareSql preSql = d.removeBean(beanClass, id);
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = DataSourceUtils.doGetConnection(dataSource);
            st = conn.prepareStatement(preSql.getSql());
            setParams(st, preSql.getParams());
            st.executeUpdate();
        } catch (Exception e) {
            System.out.println("删除[" + beanClass.getSimpleName() + "]");
            System.out.println(preSql);
            System.out.println("失败[" + e.getMessage() + "]");
            throw new BusiException("删除[" + beanClass.getSimpleName() + "]失败");
        } finally {
            try {
                st.close();
                DataSourceUtils.releaseConnection(conn, dataSource);
            } catch (Exception e) {
            }
        }
    }

    public <T> void remove(Class<T> beanClass, Where where) throws BusiException {
        PrepareSql preSql = d.removeBeans(beanClass, where);
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = DataSourceUtils.doGetConnection(dataSource);
            st = conn.prepareStatement(preSql.getSql());
            setParams(st, preSql.getParams());
            st.executeUpdate();
        } catch (Exception e) {
            System.out.println("删除[" + beanClass.getSimpleName() + "]");
            System.out.println(preSql);
            System.out.println("失败[" + e.getMessage() + "]");
            throw new BusiException("删除[" + beanClass.getSimpleName() + "]失败");
        } finally {
            try {
                st.close();
                DataSourceUtils.releaseConnection(conn, dataSource);
            } catch (Exception e) {
            }
        }
    }

    @Transactional
    public <T> void remove(Class<T> beanClass, String[] ids) throws BusiException {
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = DataSourceUtils.doGetConnection(dataSource);
            PrepareSql preSql = d.removeBean(beanClass, null);
            st = conn.prepareStatement(preSql.getSql());
            System.out.println(preSql);
            for (String id : ids) {
                st.setString(1, id);
                st.addBatch();
            }
            st.executeBatch();
        } catch (Exception e) {
            System.out.println("失败[" + e.getMessage() + "]");
            throw new BusiException("删除[" + beanClass.getSimpleName() + "]失败");
        } finally {
            try {
                st.close();
                DataSourceUtils.releaseConnection(conn, dataSource);
            } catch (Exception e) {
            }
        }
    }

    public <T> T update(T t) throws BusiException {
        validatorService.check(t);
        PrepareSql preSql = d.updateBean(t);
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = DataSourceUtils.doGetConnection(dataSource);
            st = conn.prepareStatement(preSql.getSql());
            setParams(st, preSql.getParams());
            st.executeUpdate();
        } catch (Exception e) {
            System.out.println("更新[" + t.getClass().getSimpleName() + "]");
            System.out.println(preSql);
            System.out.println("失败[" + e.getMessage() + "]");
            throw new BusiException("更新[" + t.getClass().getSimpleName() + "]失败");
        } finally {
            try {
                st.close();
                DataSourceUtils.releaseConnection(conn, dataSource);
            } catch (Exception e) {
            }
        }
        return t;
    }

    public <T> Pager<T> getPager(Class<T> beanClass, int pageNumber, int pageSize, Where where, String orderBy) throws BusiException {

        orderBy = parseOrderBy(orderBy);

        Pager<T> pager = new Pager<T>();
        if(pageNumber<=1) pageNumber = 1;
        pager.setPageNumber(pageNumber);
        pager.setPageSize(pageSize);
        int offset = (pageNumber-1)*pageSize;
        int limit = pageSize;
        PrepareSql preSql = d.listBeans(beanClass, offset, limit, where, orderBy);
        PrepareSql preCountSql = d.toCountSql(beanClass, where, orderBy);
        Connection conn = null;
        PreparedStatement st = null;
        PreparedStatement countSt = null;
        ResultSet beanRs = null;
        ResultSet countRs = null;
        try {
            conn = DataSourceUtils.doGetConnection(dataSource);
            st = conn.prepareStatement(preSql.getSql());
            setParams(st, preSql.getParams());
            beanRs = st.executeQuery();
            pager.setRows(d.toBeans(beanRs, beanClass));
            long total = 0;
            countSt = conn.prepareStatement(preCountSql.getSql());
            setParams(countSt, preCountSql.getParams());
            countRs = countSt.executeQuery();
            while (null != countRs && countRs.next()) {
                total = countRs.getLong(1);
                break;
            }
            pager.setTotal(total);
        } catch (Exception e) {
            System.out.println("查询[" + beanClass.getSimpleName() + "]");
            System.out.println(preSql);
            System.out.println(preCountSql);
            System.out.println("失败[" + e.getMessage() + "]");
            throw new BusiException("获取[" + beanClass.getSimpleName() + "]失败");
        } finally {
            try {
                beanRs.close();
                countRs.close();
                st.close();
                DataSourceUtils.releaseConnection(conn, dataSource);
            } catch (Exception e) {
            }
        }
        return pager;
    }

    public <T> T getById(Class<T> beanClass, String id) throws BusiException {
        PrepareSql preSql = d.getBean(beanClass, id);
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        T o = null;
        try {
            conn = DataSourceUtils.doGetConnection(dataSource);
            st = conn.prepareStatement(preSql.getSql());
            setParams(st, preSql.getParams());
            rs = st.executeQuery();
            o = d.toBean(rs, beanClass);
        } catch (Exception e) {
            System.out.println("查询[" + beanClass.getSimpleName() + "]");
            System.out.println(preSql);
            System.out.println("失败[" + e.getMessage() + "]");
            throw new BusiException("获取[" + beanClass.getSimpleName() + "]失败");
        } finally {
            try {
                rs.close();
                st.close();
                DataSourceUtils.releaseConnection(conn, dataSource);
            } catch (Exception e) {
            }
        }
        return o;
    }
    public <T> List<T> list(Class<T> beanClass) throws BusiException {
        return this.list(beanClass,null,null);
    }
    public <T> List<T> list(Class<T> beanClass, Where where) throws BusiException {
        return this.list(beanClass,where,null);
    }
    public <T> List<T> list(Class<T> beanClass, Where where, String orderBy) throws BusiException {
        orderBy = parseOrderBy(orderBy);
        List<T> ls = new ArrayList<>();
        PrepareSql preSql = d.listBeans(beanClass, where, orderBy);
        System.out.println(preSql);
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            conn = DataSourceUtils.doGetConnection(dataSource);
            st = conn.prepareStatement(preSql.getSql());
            setParams(st, preSql.getParams());
            rs = st.executeQuery();
            ls = d.toBeans(rs, beanClass);
        } catch (Exception e) {
            System.out.println("查询[" + beanClass.getSimpleName() + "]");
            System.out.println(preSql);
            System.out.println("失败[" + e.getMessage() + "]");
            throw new BusiException("获取[" + beanClass.getSimpleName() + "]失败");
        } finally {
            try {
                rs.close();
                st.close();
                DataSourceUtils.releaseConnection(conn, dataSource);
            } catch (Exception e) {
            }
        }
        return ls;
    }

    public <T> T queryOne(Class<T> clazz,String hql,Object... params) throws  BusiException{
        List<T> ls = this.query(clazz,hql,params);
        if(ls==null || ls.size()<1) return  null;
        return ls.get(0);
    }
    public <T> List<T>  query(Class<T> clazz,String hql,Object... params) throws BusiException{
        List<T> ls = new ArrayList<>();
        if(hql==null) throw new BusiException("查询语句不能为空");
        List<Class<?>> beanClasses = BeanPool.getBeans();
        Map<String,String> beanNameTableNames = new HashMap<>();
        if(beanClasses!=null){
            for(Class beanClass:beanClasses){
                beanNameTableNames.put(beanClass.getSimpleName(),BeanPool.getBeanTable(beanClass).getTableName());
            }
        }
        for(String beanName:beanNameTableNames.keySet()){
            hql = hql.replace(beanName,beanNameTableNames.get(beanName).toLowerCase());
        }
        List<String> words= RegexUtil.getRegText("([0-9a-zA-Z_]+)",hql);
        //去掉关键字
        List<String> keys = new ArrayList<>(Arrays.asList(
                "select","from","limit","order","by","asc","desc","join",
                "on","in","left","right","inner","group","and","or",
                "where","update","delete","into"
        ));
        for(String word:words){
            if(!keys.contains(word.toLowerCase())){
                hql = hql.replace(word,StringUtil.toSlide(word));
            }
        }

        PrepareSql preSql = new PrepareSql();
        preSql.setSql(hql);
        preSql.setParams(Arrays.asList(params));

        System.out.println(preSql);
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            conn = DataSourceUtils.doGetConnection(dataSource);
            st = conn.prepareStatement(preSql.getSql());
            setParams(st, preSql.getParams());
            rs = st.executeQuery();
            ls = d.toBeans(rs, clazz);
        } catch (Exception e) {
            System.out.println("查询[" + clazz.getSimpleName() + "]");
            System.out.println(preSql);
            System.out.println("失败[" + e.getMessage() + "]");
            throw new BusiException("获取[" + clazz.getSimpleName() + "]失败");
        } finally {
            try {
                rs.close();
                st.close();
                DataSourceUtils.releaseConnection(conn, dataSource);
            } catch (Exception e) {
            }
        }
        return ls;
    }
    public void  update(String hql,Object... params) throws BusiException{
        if(hql==null) throw new BusiException("HQL语句不能为空");
        List<Class<?>> beanClasses = BeanPool.getBeans();
        Map<String,String> beanNameTableNames = new HashMap<>();
        if(beanClasses!=null){
            for(Class beanClass:beanClasses){
                beanNameTableNames.put(beanClass.getSimpleName(),BeanPool.getBeanTable(beanClass).getTableName());
            }
        }
        for(String beanName:beanNameTableNames.keySet()){
            hql = hql.replace(beanName,beanNameTableNames.get(beanName).toLowerCase());
        }
        List<String> words= RegexUtil.getRegText("([0-9a-zA-Z_]+)",hql);
        //去掉关键字
        List<String> keys = new ArrayList<>(Arrays.asList(
                "select","from","limit","order","by","asc","desc","join",
                "on","in","left","right","inner","group","and","or",
                "where","update","delete","into"
        ));
        for(String word:words){
            if(!keys.contains(word.toLowerCase())){
                hql = hql.replace(word,StringUtil.toSlide(word));
            }
        }

        PrepareSql preSql = new PrepareSql();
        preSql.setSql(hql);
        preSql.setParams(Arrays.asList(params));

        System.out.println(preSql);
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            conn = DataSourceUtils.doGetConnection(dataSource);
            st = conn.prepareStatement(preSql.getSql());
            setParams(st, preSql.getParams());
            st.executeUpdate();
        } catch (Exception e) {
            System.out.println("执行");
            System.out.println(preSql);
            System.out.println("失败[" + e.getMessage() + "]");
            throw new BusiException("执行失败");
        } finally {
            try {
                rs.close();
                st.close();
                DataSourceUtils.releaseConnection(conn, dataSource);
            } catch (Exception e) {
            }
        }
    }
}
