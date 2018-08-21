package com.jweb.busi.entity.project;

import com.jweb.common.persistent.annotation.Bean;
import com.jweb.common.persistent.annotation.Field;

@Bean(table="jweb_busi_quota",name="配额")
public class Quota {
    @Field(name="配额ID")
    private String id;
    @Field(name="项目ID",required = true)
    private String projectId;
    @Field(name="核心数")
    private int cores;
    @Field(name="虚拟机数量")
    private int instances;
    @Field(name="内存大小(GB)")
    private int ram;
    @Field(name="卷数量")
    private int volumes;
    @Field(name="快照数量")
    private int snapshots;
    @Field(name="总存储大小(GB)")
    private int gigabytes;

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

    public int getCores() {
        return cores;
    }

    public void setCores(int cores) {
        this.cores = cores;
    }

    public int getInstances() {
        return instances;
    }

    public void setInstances(int instances) {
        this.instances = instances;
    }

    public int getRam() {
        return ram;
    }

    public void setRam(int ram) {
        this.ram = ram;
    }

    public int getVolumes() {
        return volumes;
    }

    public void setVolumes(int volumes) {
        this.volumes = volumes;
    }

    public int getSnapshots() {
        return snapshots;
    }

    public void setSnapshots(int snapshots) {
        this.snapshots = snapshots;
    }

    public int getGigabytes() {
        return gigabytes;
    }

    public void setGigabytes(int gigabytes) {
        this.gigabytes = gigabytes;
    }
}
