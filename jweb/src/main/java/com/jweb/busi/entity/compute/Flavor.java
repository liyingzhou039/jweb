package com.jweb.busi.entity.compute;

import com.jweb.busi.entity.center.Center;
import com.jweb.busi.entity.sync.LocalCenterRelation;
import com.jweb.busi.service.center.CenterService;
import com.jweb.busi.service.sync.SyncBean;
import com.jweb.common.exception.BusiException;
import com.jweb.common.persistent.annotation.Bean;
import com.jweb.common.persistent.annotation.Field;
import com.jweb.common.persistent.model.Expression;
import com.jweb.common.persistent.model.Where;
import com.jweb.common.service.BeanService;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.common.ActionResponse;

import java.util.List;
@Bean(table = "jweb_busi_compute_flavor",name="虚拟机规格")
public class Flavor extends SyncBean {
    @Field(name="ID")
    private String id;
    @Field(name="名称",required = true)
    private String name;
    @Field(name="内存(GB)",required = true)
    private int ram;
    @Field(name="VCPU数量",required = true)
    private int vcpus;
    @Field(name="根磁盘(GB)",required = true)
    private int disk;
    @Field(name="Swap磁盘(GB)",required = true)
    private int swap;
    @Field(name="RX/TX 因子",required = true)
    private float rxtxFactor;
    @Field(name="临时磁盘(GB)",required = true)
    private int ephemeral;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    protected String createCenterResource(Center center, BeanService beanService, CenterService centerService) throws BusiException {
        String centerResourceId = null;
        try {
            OSClient.OSClientV3 os = centerService.os(center);
            org.openstack4j.model.compute.Flavor flavor = os.compute().flavors().create(
                    Builders.flavor()
                            .name(CenterService.PREFFIX+center.getCode()+"-"+this.getName())
                            .ram(1024*this.getRam())
                            .vcpus(this.getVcpus())
                            .disk(this.getDisk())
                            .swap(1024*this.getSwap())
                            .rxtxFactor(this.getRxtxFactor())
                            .ephemeral(this.getEphemeral())
                            .isPublic(true)
                            .build());
            if(flavor==null){
                throw new BusiException("");
            }
            centerResourceId = flavor.getId();
        }catch (Exception e){
            e.printStackTrace();
            throw new BusiException("中心["+center.getName()+"]同步新增["+this.getName()+"]失败:"+e.getMessage());
        }
        return centerResourceId;
    }

    @Override
    protected void updateCenterResource(Center center, BeanService beanService, CenterService centerService) throws BusiException {
        //不需要更新
    }

    @Override
    protected void removeCenterResource(Center center, BeanService beanService, CenterService centerService) throws BusiException {
        try {
            OSClient.OSClientV3 os = centerService.os(center);
            List<LocalCenterRelation> removeLcs = beanService.list(
                    LocalCenterRelation.class
                    , Where.create("centerCode",Expression.eq,center.getCode())
                            .and("beanName",Expression.eq,this.getClass().getSimpleName())
                            .and("localResourceId",Expression.eq,null)
                            .and("synced",Expression.eq,false)
                    , null);
            if(null!=removeLcs&&removeLcs.size()>0){
                for(LocalCenterRelation removeLc : removeLcs){
                    try{
                        if(removeLc.getCenterResourceId()!=null) {
                            ActionResponse r = os.compute().flavors().delete(removeLc.getCenterResourceId());
                            if (r == null || !r.isSuccess()) {
                                throw new BusiException("");
                            }
                        }
                        beanService.remove(removeLc.getClass(),removeLc.getId());
                    }catch(Exception e){e.printStackTrace();}
                }
            }
        }catch (Exception e){
            throw new BusiException("中心["+center.getName()+"]同步删除["+this.getClass().getSimpleName()+"]失败:"+e.getMessage());
        }
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

    public int getRam() {
        return ram;
    }

    public void setRam(int ram) {
        this.ram = ram;
    }

    public int getVcpus() {
        return vcpus;
    }

    public void setVcpus(int vcpus) {
        this.vcpus = vcpus;
    }

    public int getDisk() {
        return disk;
    }

    public void setDisk(int disk) {
        this.disk = disk;
    }

    public int getSwap() {
        return swap;
    }

    public void setSwap(int swap) {
        this.swap = swap;
    }

    public float getRxtxFactor() {
        return rxtxFactor;
    }

    public void setRxtxFactor(float rxtxFactor) {
        this.rxtxFactor = rxtxFactor;
    }

    public int getEphemeral() {
        return ephemeral;
    }

    public void setEphemeral(int ephemeral) {
        this.ephemeral = ephemeral;
    }
}
