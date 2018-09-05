package com.jweb.busi.entity.security;

import com.jweb.busi.entity.center.Center;
import com.jweb.busi.entity.sync.LocalCenterRelation;
import com.jweb.busi.service.center.CenterService;
import com.jweb.busi.service.sync.SyncBean;
import com.jweb.common.exception.BusiException;
import com.jweb.common.persistent.annotation.Bean;
import com.jweb.common.persistent.annotation.Field;
import com.jweb.common.persistent.model.Expression;
import com.jweb.common.persistent.model.Where;
import com.jweb.common.service.BeanService;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.compute.SecGroupExtension;

import java.util.List;

@Bean(table="jweb_busi_security_group",name="安全组")
public class SecurityGroup extends SyncBean {
    @Field(name="ID")
    private String id;
    @Field(name="创建者ID",required = true)
    private String userId;
    @Field(name="项目ID",required = true)
    private String projectId;
    @Field(name="名称",required = true)
    private String name;
    @Field(name="描述",size=1000)
    private String description;


    public String getId() {
        return id;
    }

    @Override
    protected String createCenterResource(Center center, BeanService beanService,CenterService centerService) throws BusiException {
        String centerResourceId = null;
        try {
            OSClient.OSClientV3 os = centerService.os(center);
            SecGroupExtension centerGroup = os.compute().securityGroups().create(CenterService.PREFFIX+center.getCode()+"-"+this.getId(),this.getName());
            if(centerGroup==null){
                throw new BusiException("");
            }
            centerResourceId = centerGroup.getId();
            //创建安全组默认会创建两条规则，尝试删除
            List<? extends org.openstack4j.model.network.SecurityGroupRule> rules = os.networking().securityrule().list();
            for(org.openstack4j.model.network.SecurityGroupRule rule : rules){
                if(rule.getSecurityGroupId().equals(centerResourceId)){
                    os.networking().securityrule().delete(rule.getId());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new BusiException("中心["+center.getName()+"]同步新增["+this.getName()+"]失败:"+e.getMessage());
        }
        return centerResourceId;
    }

    @Override
    protected void updateCenterResource(Center center, BeanService beanService,CenterService centerService) throws BusiException {
        //不需要更新
    }

    @Override
    protected void removeCenterResource(Center center, BeanService beanService,CenterService centerService) throws BusiException {
        try {
            List<LocalCenterRelation> removeLcs = beanService.list(
                    LocalCenterRelation.class
                    , Where.create("centerCode",Expression.eq,center.getCode())
                            .and("beanName",Expression.eq,this.getClass().getSimpleName())
                            .and("localResourceId",Expression.eq,null)
                            .and("synced",Expression.eq,false)
                    , null);
            if(null!=removeLcs&&removeLcs.size()>0){
                OSClient.OSClientV3 os = centerService.os(center);
                for(LocalCenterRelation removeLc : removeLcs){
                    try{
                        if(removeLc.getCenterResourceId()!=null) {
                            ActionResponse r = os.compute().securityGroups().delete(removeLc.getCenterResourceId());
                            if (r == null || !r.isSuccess()) {
                                throw new BusiException("");
                            }
                        }
                        beanService.remove(removeLc.getClass(),removeLc.getId());
                    }catch(Exception e){e.printStackTrace();}
                }
            }
        }catch (Exception e){
            throw new BusiException("中心["+center.getName()+"]同步删除["+this.getClass().getSimpleName()+"]失败:"+e.getMessage());
        }
    }
    @Override
    protected void syncDependency(Center center, BeanService beanService, CenterService centerService)
            throws BusiException{
        List<SecurityGroupRule> localRules = beanService.list(SecurityGroupRule.class,
                Where.create("securityGroupId",Expression.eq,this.getId()),null);
        if(null!=localRules){
            for(SecurityGroupRule localRule : localRules){
                localRule.syncResource(center,beanService,centerService);
            }
        }

    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
