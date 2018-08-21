package com.jweb.busi.entity.center;

import com.jweb.common.persistent.annotation.Bean;
import com.jweb.common.persistent.annotation.Field;

@Bean(table="jweb_busi_center",name="数据中心")
public class Center {
    @Field(name="ID")
    private String id;
    @Field(name="中心编码",required = true)
    private String code;
    @Field(name="中心名称",required = true)
    private String name;
    @Field(name="中心类型",required = true)
    private String type;

    @Field(name="控制节点URL",required = true,size = 1000)
    private String endpointUrl;
    @Field(name="控制节点用户名",required = true)
    private String endpointUsername;
    @Field(name="控制节点密码",required = true)
    private String endpointPassword;
    @Field(name="监控节点URL",required = true,size = 1000)
    private String gangliaUrl;
    @Field(name="监控源",required = true)
    private String gangliaSource;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEndpointUrl() {
        return endpointUrl;
    }

    public void setEndpointUrl(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }

    public String getEndpointUsername() {
        return endpointUsername;
    }

    public void setEndpointUsername(String endpointUsername) {
        this.endpointUsername = endpointUsername;
    }

    public String getEndpointPassword() {
        return endpointPassword;
    }

    public void setEndpointPassword(String endpointPassword) {
        this.endpointPassword = endpointPassword;
    }

    public String getGangliaUrl() {
        return gangliaUrl;
    }

    public void setGangliaUrl(String gangliaUrl) {
        this.gangliaUrl = gangliaUrl;
    }

    public String getGangliaSource() {
        return gangliaSource;
    }

    public void setGangliaSource(String gangliaSource) {
        this.gangliaSource = gangliaSource;
    }
}
