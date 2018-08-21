package com.jweb.busi.entity.sync;

import com.jweb.common.persistent.annotation.Bean;
import com.jweb.common.persistent.annotation.Field;

@Bean(table = "jweb_busi_sync_local_center", name = "本地资源与中心资源关系")
public class LocalCenterRelation {
    @Field(name = "ID")
    private String id;
    @Field(name = "中心编码",required = true)
    private String centerCode;
    @Field(name="本地实体名称",required = true)
    private String beanName;
    @Field(name="本地资源ID")
    private String localResourceId;
    @Field(name="中心资源ID")
    private String centerResourceId;
    @Field(name="是否需要更新")
    private boolean synced = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCenterCode() {
        return centerCode;
    }

    public void setCenterCode(String centerCode) {
        this.centerCode = centerCode;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getLocalResourceId() {
        return localResourceId;
    }

    public void setLocalResourceId(String localResourceId) {
        this.localResourceId = localResourceId;
    }

    public String getCenterResourceId() {
        return centerResourceId;
    }

    public void setCenterResourceId(String centerResourceId) {
        this.centerResourceId = centerResourceId;
    }

    public boolean isSynced() {
        return synced;
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
    }
}
