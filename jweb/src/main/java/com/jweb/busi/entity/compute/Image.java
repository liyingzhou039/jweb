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
import org.openstack4j.model.common.Payload;
import org.openstack4j.model.common.Payloads;
import org.openstack4j.model.image.v2.ContainerFormat;
import org.openstack4j.model.image.v2.DiskFormat;
import org.openstack4j.model.image.v2.builder.ImageBuilder;
import org.springframework.boot.ApplicationHome;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

@Bean(table = "jweb_busi_compute_image",name = "镜像")
public class Image extends SyncBean {
    @Field(name="ID")
    private String id;
    @Field(name="名称",required = true)
    private String name;
    @Field(name="描述")
    private String description;
    @Field(name="文件名",required = true)
    private String fileName;
    @Field(name="磁盘格式",required = true)
    private String diskFormat;
    @Field(name="最小磁盘")
    private long minDisk;
    @Field(name="最小内存")
    private long minRam;
    @Field(name="镜像大小")
    private long size;
    @Field(name="镜像状态")
    private String status;
    @Field(name="元数据",type = "TEXT")
    private String metadataJson;
    Map<String,String> metadata;

    public String getId() {
        return id;
    }

    @Override
    protected String createCenterResource(Center center, BeanService beanService, CenterService centerService)
            throws BusiException {
        String centerResourceId = null;
        try {
            OSClient.OSClientV3 os = centerService.os(center);

            ImageBuilder ib= Builders.imageV2()
                    .name(CenterService.PREFFIX+center.getCode()+"-"+this.getName())
                    .containerFormat(ContainerFormat.DOCKER)
                    .visibility(org.openstack4j.model.image.v2.Image.ImageVisibility.forValue("public"))
                    .diskFormat(DiskFormat.value(this.getDiskFormat()))
                    .minDisk(this.getMinDisk())
                    .minRam(this.getMinRam()*1024)
                    .isProtected(false);

            if(null!=this.getMetadata()) {
                for(String key : this.getMetadata().keySet()) {
                    ib.additionalProperty(key, this.getMetadata().get(key));
                }
            }

            org.openstack4j.model.image.v2.Image image = os.imagesV2().create(ib.build());
            centerResourceId = image.getId();

            ApplicationHome home = new ApplicationHome(this.getClass());
            File jarDir = home.getSource().getParentFile();
            File imageFile = new File(jarDir.getAbsolutePath()+File.separator+"images"+
                    File.separator+this.getFileName());
            Payload<File> payload = Payloads.create(imageFile);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    centerService.os(center).imagesV2().upload(image.getId(), payload, image);
                }
            }).start();

        }catch (Exception e){
            e.printStackTrace();
            throw new BusiException("中心["+center.getName()+"]同步新增["+this.getName()+"]失败:"+e.getMessage());
        }
        return centerResourceId;
    }

    @Override
    protected void updateCenterResource(Center center, BeanService beanService, CenterService centerService)
            throws BusiException {
        try {
            OSClient.OSClientV3 os = centerService.os(center);
            List<LocalCenterRelation> localCenterRelations = beanService.list(LocalCenterRelation.class,
                    Where.create("centerCode",Expression.eq,center.getCode())
                            .and("beanName",Expression.eq,this.getClass().getSimpleName())
                            .and("localResourceId",Expression.eq,this.getId()));
            if(localCenterRelations ==null && localCenterRelations.size() <= 0 )
                return ;
            org.openstack4j.model.image.v2.Image image = os.imagesV2()
                    .get(localCenterRelations.get(0).getCenterResourceId());

            Map<String, String> additionalProperties = null;
            try {
                Method method = image.getClass().getMethod("getAdditionalProperties");
                additionalProperties =(Map<String, String>) method.invoke(image);
                additionalProperties.clear();
            }catch(Exception ee) {ee.printStackTrace();}


            ImageBuilder ib= image.toBuilder()
                    .name(CenterService.PREFFIX+center.getCode()+"-"+this.getName())
                    .visibility(org.openstack4j.model.image.v2.Image.ImageVisibility.forValue("public"))
                    .diskFormat(DiskFormat.value(this.getDiskFormat()))
                    .minDisk(this.getMinDisk())
                    .minRam(this.getMinRam()*1024)
                    .isProtected(false);

            if(null!=this.getMetadata()) {
                for(String key : this.getMetadata().keySet()) {
                    ib.additionalProperty(key, this.getMetadata().get(key));
                }
            }

            os.imagesV2().update(ib.build());
        }catch (Exception e){
            e.printStackTrace();
            throw new BusiException("中心["+center.getName()+"]同步更新["+this.getName()+"]失败:"+e.getMessage());
        }
    }

    @Override
    protected void removeCenterResource(Center center, BeanService beanService, CenterService centerService)
            throws BusiException {
        try {
            List<LocalCenterRelation> removeLcs = beanService.list(
                    LocalCenterRelation.class
                    , Where.create("centerCode",Expression.eq,center.getCode())
                            .and("beanName",Expression.eq,this.getClass().getSimpleName())
                            .and("localResourceId",Expression.eq,null)
                    , null);
            if(null!=removeLcs&&removeLcs.size()>0){
                OSClient.OSClientV3 os = centerService.os(center);
                for(LocalCenterRelation removeLc : removeLcs){
                    try{
                        if(removeLc.getCenterResourceId()!=null) {
                            ActionResponse r = os.imagesV2().delete(removeLc.getCenterResourceId());
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDiskFormat() {
        return diskFormat;
    }

    public void setDiskFormat(String diskFormat) {
        this.diskFormat = diskFormat;
    }

    public long getMinDisk() {
        return minDisk;
    }

    public void setMinDisk(long minDisk) {
        this.minDisk = minDisk;
    }

    public long getMinRam() {
        return minRam;
    }

    public void setMinRam(long minRam) {
        this.minRam = minRam;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMetadataJson() {
        return metadataJson;
    }

    public void setMetadataJson(String metadataJson) {
        this.metadataJson = metadataJson;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
}
