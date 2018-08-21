package com.jweb.busi.entity.project;

import com.jweb.common.persistent.annotation.Bean;
import com.jweb.common.persistent.annotation.Field;

@Bean(table="jweb_busi_project_user",name="项目成员关系")
public class ProjectUser {
    @Field(name="ID")
    private String id;
    @Field(name="项目ID",required = true)
    private String projectId;
    @Field(name="成员ID",required = true)
    private String userId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
