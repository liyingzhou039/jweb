package com.jweb.busi.entity.compute;

import com.jweb.common.persistent.annotation.Bean;
import com.jweb.common.persistent.annotation.Field;

@Bean(table="jweb_busi_compute_volume",name="磁盘")
public class Volume {
    @Field(name="ID")
    private String id;
    @Field(name="名称",required = true)
    private String name;
    @Field(name="虚机ID",required = true)
    private String serverId;
    @Field(name="磁盘ID",required = true)
    private String volumeId;
    @Field(name="大小",required = true)
    private Integer size;
    private String device;

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

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getVolumeId() {
        return volumeId;
    }

    public void setVolumeId(String volumeId) {
        this.volumeId = volumeId;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }
}
