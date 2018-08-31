package com.jweb.busi.service.sync;

import com.jweb.busi.entity.center.Center;
import com.jweb.busi.entity.sync.LocalCenterRelation;
import com.jweb.busi.service.center.CenterService;
import com.jweb.common.exception.BusiException;
import com.jweb.common.persistent.model.Expression;
import com.jweb.common.persistent.model.Where;
import com.jweb.common.service.BeanService;

import java.util.List;

public abstract class SyncBean {
    public abstract  String getId();
    public String getCenterResourceId(Center center, BeanService beanService, CenterService centerService) throws BusiException{
        this.syncResource(center,beanService,centerService);
        List<LocalCenterRelation> lcs = beanService.list(
                LocalCenterRelation.class
                , Where.create("centerCode",Expression.eq,center.getCode())
                        .and("beanName",Expression.eq,this.getClass().getSimpleName())
                        .and("localResourceId",Expression.eq,this.getId()));
        if(lcs==null || lcs.size()<=0 || !lcs.get(0).isSynced()){
            throw new BusiException("中心["+center.getName()+"]未同步["+this.getClass().getSimpleName()+"]");
        }
        return lcs.get(0).getCenterResourceId();
    }
    public String syncResource(Center center, BeanService beanService, CenterService centerService) throws BusiException{
        String centerResourceId = null;

        //1）查询出本地资源关系
        List<LocalCenterRelation> lcs = beanService.list(
                LocalCenterRelation.class
                , Where.create("centerCode",Expression.eq,center.getCode())
                        .and("beanName",Expression.eq,this.getClass().getSimpleName())
                        .and("localResourceId",Expression.eq,this.getId())
                , null);
        //2-1)本地没有资源关系：a.新增中心资源 b.新增本地资源关系
        if(lcs==null || lcs.size()<=0){
            centerResourceId = this.createCenterResource(center,beanService,centerService);
            LocalCenterRelation lc = new LocalCenterRelation();
            lc.setCenterCode(center.getCode());
            lc.setBeanName(this.getClass().getSimpleName());
            lc.setLocalResourceId(this.getId());
            lc.setCenterResourceId(centerResourceId);
            lc.setSynced(true);
            beanService.create(lc);
        }
        //2-2)本地有资源关系，synced=false,中心资源ID为null,新增中心资源，更新资源关系状态
        else if(
                lcs.get(0).isSynced()==false &&  lcs.get(0).getLocalResourceId()!=null
                        && lcs.get(0).getCenterResourceId()==null){
            centerResourceId = this.createCenterResource(center,beanService,centerService);
            LocalCenterRelation lc = new LocalCenterRelation();
            lc.setCenterCode(center.getCode());
            lc.setBeanName(this.getClass().getSimpleName());
            lc.setLocalResourceId(this.getId());
            lc.setCenterResourceId(centerResourceId);
            lc.setSynced(true);
            lc.setId(lcs.get(0).getId());
            beanService.update(lc);
        }
        //2-3)本地有资源关系，synced=false,本地、中心资源ID存在,更新中心资源
        else if(lcs.get(0).isSynced()==false&& lcs.get(0).getLocalResourceId()!=null && lcs.get(0).getCenterResourceId()!=null){
            centerResourceId = lcs.get(0).getCenterResourceId();
            this.updateCenterResource(center,beanService,centerService);
        //2-4)本地有资源关系，synced=true,说明已经同步，无操作
        }else{
            centerResourceId = lcs.get(0).getCenterResourceId();
        }

        //3)清理本地已经删除的资源
        this.removeCenterResource(center,beanService,centerService);
        this.syncDependency(center,beanService,centerService);
        return centerResourceId;
    }
    protected void syncDependency(Center center, BeanService beanService, CenterService centerService) throws BusiException{

    }
    protected abstract String createCenterResource(Center center, BeanService beanService,CenterService centerService) throws BusiException;
    protected abstract void updateCenterResource(Center center, BeanService beanService,CenterService centerService) throws BusiException;
    protected abstract void removeCenterResource(Center center, BeanService beanService,CenterService centerService) throws BusiException;
}
