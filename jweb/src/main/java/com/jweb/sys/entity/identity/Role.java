package com.jweb.sys.entity.identity;

import com.jweb.common.persistent.annotation.Bean;
import com.jweb.common.persistent.annotation.Field;

/**
 * @ClassName: Role
 * @Description: TODO
 * @author: liyz
 * @date: 2018年1月31日 下午4:15:46
 */
@Bean(table="jweb_sys_role",name="角色")
public class Role {

    @Field(name="ID")
    private String id;

    @Field(name="角色名称",required=true,validType={"length(0,75)"})
    private String name;

    @Field(name="角色描述",type = "TEXT")
    private String description;

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
}