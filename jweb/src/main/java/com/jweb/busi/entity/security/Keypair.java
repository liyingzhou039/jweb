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
import com.jweb.common.session.Session;
import com.jweb.sys.dto.identity.LoginUser;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.common.ActionResponse;

import java.util.List;

@Bean(table = "jweb_busi_security_keypair",name="密钥对")
public class Keypair extends SyncBean {
    @Field(name="ID")
    private String id;
    @Field(name="创建者ID",required = true)
    private String userId;
    @Field(name="项目ID",required = true)
    private String projectId;
    @Field(name="名称",required = true)
    private String name;
    @Field(name="公钥",type="TEXT")
    private String publicKey;
    @Field(name="私钥",type="TEXT")
    private String privateKey;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    protected String createCenterResource(Center center, BeanService beanService, CenterService centerService) throws BusiException {
        String centerResourceId = null;
        try {
            LoginUser user = Session.getCurrentUser();
            OSClient.OSClientV3 os = centerService.os(center);
            //先从本地获取公钥，如果获取到了，则用本地的公钥创建，否则自动生成
            List<Keypair> keypairs = beanService.list(Keypair.class,
                        Where.create("name",Expression.eq,this.getName())
                        .and("projectId",Expression.eq,user.getProjectId())
                    );
            String publicKey = null;
            if(keypairs!=null && keypairs.size()>0){
                publicKey = keypairs.get(0).getPublicKey();
            }
            org.openstack4j.model.compute.Keypair kp = os.compute().keypairs().create(CenterService.PREFFIX
                    +center.getCode()+this.getId(),publicKey);

            if(publicKey==null){
                this.setPublicKey(kp.getPublicKey());
                this.setPrivateKey(kp.getPrivateKey());
                beanService.update(this);
            }
            centerResourceId = kp.getName();
        }catch (Exception e){
            e.printStackTrace();
            throw new BusiException("中心["+center.getName()+"]同步新增["+this.getName()+"]失败:"+e.getMessage());
        }
        return centerResourceId;
    }

    @Override
    protected void updateCenterResource(Center center, BeanService beanService, CenterService centerService) throws BusiException {
        return ;
    }

    @Override
    protected void removeCenterResource(Center center, BeanService beanService, CenterService centerService) throws BusiException {
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
                            ActionResponse r = os.compute().keypairs().delete(removeLc.getCenterResourceId());
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

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
}
