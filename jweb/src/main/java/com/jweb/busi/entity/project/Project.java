package com.jweb.busi.entity.project;

import com.jweb.common.persistent.annotation.Bean;
import com.jweb.common.persistent.annotation.Field;

import java.util.List;

@Bean(table="jweb_busi_project",name="项目")
public class Project {
    @Field(name="项目ID")
    private String id;
    @Field(name="项目名称",required = true)
    private String name;
    @Field(name="项目描述",type="TEXT")
    private String description;
    @Field(name="是否激活",required = true)
    private boolean enabled;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    private Quota quota;
    private List<String> userIds;

    public Quota getQuota() {
        return quota;
    }

    public void setQuota(Quota quota) {
        this.quota = quota;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUsers(List<String> userIds) {
        this.userIds = userIds;
    }
}
