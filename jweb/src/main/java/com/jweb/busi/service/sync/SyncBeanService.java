package com.jweb.busi.service.sync;

import com.jweb.busi.entity.center.Center;
import com.jweb.busi.entity.sync.LocalCenterRelation;
import com.jweb.busi.service.center.CenterService;
import com.jweb.common.dto.Execute;
import com.jweb.common.exception.BusiException;
import com.jweb.common.persistent.model.Expression;
import com.jweb.common.persistent.model.Where;
import com.jweb.common.service.BeanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Service
public class SyncBeanService{
    @Autowired
    BeanService beanService;
    @Autowired
    CenterService centerService;
    public <T> T create(T t) throws BusiException {
        T bean = this.createLocal(t);
        List<Center> centers = centerService.all();
        for(Center center:centers) {
            try {
                ((SyncBean) bean).syncResource(center, beanService, centerService);
            }catch (Exception e){e.printStackTrace();}
        }
        return bean;
    }
    public <T> T update(T t) throws BusiException {
        T bean = this.updateLocal(t);
        List<Center> centers = centerService.all();
        for(Center center:centers) {
            try {
                ((SyncBean) bean).syncResource(center, beanService, centerService);
            }catch (Exception e){e.printStackTrace();}
        }
        return bean;
    }
    public <T> void remove(Class<T> beanClass, String id) throws BusiException {
        this.removeLocal(beanClass,id);
        T bean = null;
        try {
            bean = beanClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Center> centers = centerService.all();
        for(Center center:centers) {
            try {
                ((SyncBean) bean).syncResource(center, beanService, centerService);
            }catch (Exception e){e.printStackTrace();}
        }
    }
    public <T> void remove(Class<T> beanClass, Where where) throws BusiException {
        this.removeLocal(beanClass,where);
        T bean = null;
        try {
            bean = beanClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Center> centers = centerService.all();
        for(Center center:centers) {
            try {
                ((SyncBean) bean).syncResource(center, beanService, centerService);
            }catch (Exception e){e.printStackTrace();}
        }
    }
    @Transactional
    public <T> T createLocal(T t) throws BusiException {
        T bean = beanService.create(t);
        return bean;
    }
    @Transactional
    public <T> T updateLocal(T t) throws BusiException {
        T bean = beanService.update(t);
        String beanId = null;
        try{
            Method idGetter=t.getClass().getDeclaredMethod("getId");
            beanId = (String)idGetter.invoke(t);
        }catch(Exception e){}
        //如果存在资源关系，将同步设置为false
        List<String> centerCodes = centerService.allCodes();
        List<LocalCenterRelation> lcs = beanService.list(
                LocalCenterRelation.class,
                Where.create("centerCode",Expression.in,centerCodes)
                        .and("beanName",Expression.eq,t.getClass().getSimpleName())
                        .and("localResourceId",Expression.eq,beanId)
                ,
                null
        );
        List<Execute> executes = new ArrayList<>();
        if(null!=lcs){
            for(LocalCenterRelation lc : lcs){
                lc.setSynced(false);
                Execute execute = new Execute(Execute.UPDATE,lc);
                executes.add(execute);
            }
        }
        beanService.executeBatch(executes);

        //如果不存在资源关系可以忽略
        return bean;
    }
    @Transactional
    public <T> void removeLocal(Class<T> beanClass, String id) throws BusiException {
        T bean = beanService.getById(beanClass,id);
        beanService.remove(beanClass,id);
        if(null==bean) {
            return ;
        }
        //将资源关系本地资源ID设置为null，同步设置为false
        List<String> centerCodes = centerService.allCodes();
        List<LocalCenterRelation> lcs = beanService.list(
                LocalCenterRelation.class,
                Where.create("centerCode",Expression.in,centerCodes)
                        .and("beanName",Expression.eq,beanClass.getSimpleName())
                        .and("localResourceId",Expression.eq,id)
                ,
                null
        );
        List<Execute> executes = new ArrayList<>();
        if(null!=lcs){
            for(LocalCenterRelation lc : lcs){
                lc.setLocalResourceId(null);
                lc.setSynced(false);
                Execute execute = new Execute(Execute.UPDATE,lc);
                executes.add(execute);
            }
        }
        beanService.executeBatch(executes);
    }
    @Transactional
    public <T> void removeLocal(Class<T> beanClass, Where where) throws BusiException {
        List<T> beans = beanService.list(beanClass,where,null);
        beanService.remove(beanClass,where);
        if(null==beans || beans.size()<=0){
            return ;
        }

        //将资源关系本地资源ID设置为null，同步设置为false
        List<String> centerCodes = centerService.allCodes();
        List<String> beanIds = new ArrayList<>();
        for(T bean : beans) {
            try{
                Method idGetter=bean.getClass().getDeclaredMethod("getId");
                String beanId = (String)idGetter.invoke(bean);
                if(null!=beanId) {
                    beanIds.add(beanId);
                }
            }catch(Exception e){}
        }

        List<Execute> executes = new ArrayList<>();

        List<LocalCenterRelation> lcs = beanService.list(
                LocalCenterRelation.class,
                Where.create("centerCode",Expression.in,centerCodes)
                        .and("beanName",Expression.eq,beanClass.getSimpleName())
                        .and("localResourceId",Expression.in,beanIds)
                ,
                null
        );
        if(null!=lcs){
            for(LocalCenterRelation lc : lcs){
                lc.setLocalResourceId(null);
                lc.setSynced(false);
                Execute execute = new Execute(Execute.UPDATE,lc);
                executes.add(execute);
            }
        }
        beanService.executeBatch(executes);
    }
}
