package com.jweb.busi.service.compute;

import com.jweb.busi.entity.compute.Server;
import com.jweb.busi.entity.compute.Volume;
import com.jweb.busi.entity.project.Quota;
import com.jweb.busi.service.center.CenterService;
import com.jweb.busi.service.project.ProjectService;
import com.jweb.common.exception.BusiException;
import com.jweb.common.service.BeanService;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.storage.block.VolumeAttachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class VolumeService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    BeanService beanService;
    @Autowired
    CenterService centerService;
    @Autowired
    ProjectService projectService;

    public void create(Volume volume) throws BusiException {
        try {
            Quota quota = new Quota();
            quota.setVolumes(1);
            quota.setGigabytes(volume.getSize());
            projectService.checkQuota(quota);
            Server server = beanService.getById(Server.class,volume.getServerId());
            OSClient.OSClientV3 os = centerService.os(server.getCenterCode());
            //创建卷
            org.openstack4j.model.storage.block.Volume rVolume = os.blockStorage().volumes()
                    .create(Builders.volume()
                            .name(CenterService.PREFFIX+volume.getName())
                            .description(server.getName()+"上的磁盘")
                            .size(volume.getSize())
                            .build()
                    );
            if(null!=rVolume){
                volume.setSize(rVolume.getSize());
                volume.setVolumeId(rVolume.getId());
                beanService.create(volume);
                //挂载卷
                long timeout = 3 * 60 * 1000;
                long time = 0;
                rVolume = os.blockStorage().volumes().get(volume.getVolumeId());
                while (rVolume == null || rVolume.getStatus() == null
                        || org.openstack4j.model.storage.block.Volume.Status.CREATING.value()
                        .equals(rVolume.getStatus().value())) {
                    try {
                        Thread.sleep(1000);
                        time += 1000;
                    } catch (Exception ee) {
                    }
                    rVolume = os.blockStorage().volumes().get(volume.getVolumeId());
                    if (time > timeout) throw new BusiException("超时");
                }
                org.openstack4j.model.compute.VolumeAttachment r= os.compute().servers()
                        .attachVolume(server.getCenterResourceId(),volume.getVolumeId(),null);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            throw new BusiException(e.getMessage());
        }
    }

    public void remove(String volumeId) throws BusiException {
        try {
            Volume volume = beanService.getById(Volume.class,volumeId);
            Server server = beanService.getById(Server.class,volume.getServerId());
            OSClient.OSClientV3 os = centerService.os(server.getCenterCode());
            //解除挂载
            org.openstack4j.model.storage.block.Volume rVolume = os.blockStorage()
                    .volumes().get(volume.getVolumeId());
            List<? extends VolumeAttachment>  attachments = rVolume.getAttachments();
            if(null!=attachments){
                for(VolumeAttachment volumeAttachment:attachments){
                    ActionResponse r=os.compute().servers().detachVolume(server.getCenterResourceId(),volumeAttachment.getId());
                    if(r==null || !r.isSuccess()){
                        throw new BusiException("解除挂载失败，请重试");
                    }
                }

            }

            rVolume = os.blockStorage().volumes().get(volume.getVolumeId());
            long timeout=3*60*1000;
            long time=0;
            while(rVolume==null || rVolume.getStatus()==null
                    || org.openstack4j.model.storage.block.Volume.Status.DETACHING.value()
                    .equals(rVolume.getStatus().value())){
                try{ Thread.sleep(1000);time+=1000;}catch (Exception ee){}
                rVolume = os.blockStorage().volumes().get(volume.getVolumeId());
                if(time>timeout) throw new BusiException("超时");
            }

            //删除磁盘
            ActionResponse r = os.blockStorage().volumes().delete(volume.getVolumeId());
            if(r==null || !r.isSuccess()){
                throw new BusiException("删除失败："+r.getFault());
            }
            beanService.remove(Volume.class,volume.getId());
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new BusiException(e.getMessage());
        }
    }

    public List<Volume> getSynced(List<Volume> volumes){
        if(null==volumes||volumes.size()<1) return new ArrayList<>();
        try{
            String serverId = volumes.get(0).getServerId();
            Server server = beanService.getById(Server.class,serverId);
            String centerCode = server.getCenterCode();
            OSClient.OSClientV3 os = centerService.os(centerCode);

            List<? extends org.openstack4j.model.storage.block.Volume>
                    rVolumes = os.blockStorage().volumes().list();
            if(null!=rVolumes){
                for(org.openstack4j.model.storage.block.Volume rVolume:rVolumes){
                    for(Volume volume:volumes) {
                        if (rVolume.getId().equals(volume.getVolumeId())){
                            volume.setSize(rVolume.getSize());
                            List<? extends VolumeAttachment>  attachments = rVolume.getAttachments();
                            if(null!=attachments && attachments.size()>0){
                                volume.setDevice(attachments.get(0).getDevice());
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            throw new BusiException(e.getMessage());
        }
        return volumes;
    }
}
