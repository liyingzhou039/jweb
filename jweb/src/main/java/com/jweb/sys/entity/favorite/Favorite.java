package com.jweb.sys.entity.favorite;

import com.jweb.common.persistent.annotation.Bean;
import com.jweb.common.persistent.annotation.Field;

@Bean(table="jweb_sys_favorite",name="收藏夹")
public class Favorite {
    @Field(name="ID")
    private String id;
    @Field(name="用户名",required = true)
    private String username;
    @Field(name="窗口ID")
    private String winId;
    @Field(name="标题")
    private String name;
    @Field(name="路径",size = 1000)
    private String url;

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

    public String getWinId() {
        return winId;
    }

    public void setWinId(String winId) {
        this.winId = winId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
