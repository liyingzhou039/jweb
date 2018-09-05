package com.jweb.busi.entity.compute;

import com.jweb.common.persistent.annotation.Bean;
import com.jweb.common.persistent.annotation.Field;

import java.util.Date;

@Bean(table="jweb_busi_compute_snapshot",name="虚机快照")
public class Snapshot {
    @Field(name="ID")
    private String id;
    @Field(name="名称",required = true)
    private String name;
    @Field(name="虚机ID",required = true)
    private String serverId;
    @Field(name="快照ID",required = true)
    private String snapshotId;

    private Long size;
    private Date createAt;

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

    public String getSnapshotId() {
        return snapshotId;
    }

    public void setSnapshotId(String snapshotId) {
        this.snapshotId = snapshotId;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }
}
