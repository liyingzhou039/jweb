package com.jweb.busi.entity.compute;

import com.jweb.common.persistent.annotation.Bean;
import com.jweb.common.persistent.annotation.Field;

@Bean(table = "jweb_busi_compute_server",name="虚拟机")
public class Server {
    @Field(name="ID")
    private String id;
    @Field(name="名称",required = true)
    private String name;
    @Field(name="创建者ID",required = true)
    private String userId;
    @Field(name="项目ID",required = true)
    private String projectId;
    @Field(name="中心编码",required = true)
    private String centerCode;
    @Field(name="镜像ID",required = true)
    private String imageId;
    @Field(name="规格ID",required = true)
    private String flavorId;
    @Field(name="安全组ID",required = true)
    private String securityGroupId;
    @Field(name="管理员用户")
    private String admin;
    @Field(name="管理员密码")
    private String adminPass;
    @Field(name="IP")
    private String ip;
    @Field(name="状态")
    private String status;
    @Field(name="中心ID")
    private String centerResourceId;
    @Field(name="密钥ID")
    private String keypairId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getCenterCode() {
        return centerCode;
    }

    public void setCenterCode(String centerCode) {
        this.centerCode = centerCode;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getFlavorId() {
        return flavorId;
    }

    public void setFlavorId(String flavorId) {
        this.flavorId = flavorId;
    }

    public String getSecurityGroupId() {
        return securityGroupId;
    }

    public void setSecurityGroupId(String securityGroupId) {
        this.securityGroupId = securityGroupId;
    }

    public String getKeypairId() {
        return keypairId;
    }

    public void setKeypairId(String keypairId) {
        this.keypairId = keypairId;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getAdminPass() {
        return adminPass;
    }

    public void setAdminPass(String adminPass) {
        this.adminPass = adminPass;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCenterResourceId() {
        return centerResourceId;
    }

    public void setCenterResourceId(String centerResourceId) {
        this.centerResourceId = centerResourceId;
    }
}
