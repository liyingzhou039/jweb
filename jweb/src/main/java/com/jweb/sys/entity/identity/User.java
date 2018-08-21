package com.jweb.sys.entity.identity;

import com.jweb.common.persistent.annotation.Bean;
import com.jweb.common.persistent.annotation.Field;

/**
* @ClassName: User
* @Description: TODO
* @author: liyz
* @date: 2018年1月31日 下午4:15:46
*/
@Bean(table="jweb_sys_user",name="用户")
public class User {

   @Field(name="ID")
   private String id;

   @Field(name="用户名",required=true,validType={"length(0,75)"})
   private String username;

   @Field(name="邮箱")
   private String email;

   @Field(name="是否激活")
   private boolean enabled;


   @Field(name="密码",validType={"length(0,75)"})
   private String password;

   @Field(name="角色描述",type = "TEXT")
   private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}