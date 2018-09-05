package com.jweb.busi.service.compute;

import com.jweb.busi.entity.center.Center;
import com.jweb.busi.entity.compute.Flavor;
import com.jweb.busi.entity.compute.Server;
import com.jweb.busi.entity.compute.Snapshot;
import com.jweb.busi.entity.compute.Volume;
import com.jweb.busi.entity.project.Quota;
import com.jweb.busi.entity.security.Keypair;
import com.jweb.busi.entity.security.SecurityGroup;
import com.jweb.busi.service.center.CenterService;
import com.jweb.busi.service.project.ProjectService;
import com.jweb.common.exception.BusiException;
import com.jweb.common.persistent.model.Expression;
import com.jweb.common.persistent.model.Where;
import com.jweb.common.service.BeanService;
import com.jweb.common.util.DateUtil;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.compute.ServerCreate;
import org.openstack4j.model.compute.builder.ServerCreateBuilder;
import org.openstack4j.model.image.v2.Image;
import org.openstack4j.model.network.Network;
import org.openstack4j.model.network.Port;
import org.openstack4j.model.network.Subnet;
import org.openstack4j.model.storage.block.VolumeAttachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

@Service
public class SnapshotService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    BeanService beanService;
    @Autowired
    CenterService centerService;
    @Autowired
    ServerService serverService;
    @Autowired
    ImageService imageService;
    @Autowired
    ProjectService projectService;

    public void create(String serverId) throws BusiException {
        try {
            Quota quota = new Quota();
            quota.setSnapshots(1);
            projectService.checkQuota(quota);

            Server server = beanService.getById(Server.class,serverId);
            OSClient.OSClientV3 os = centerService.os(server.getCenterCode());
            String snapshotName = server.getName()+"-"+DateUtil.getFormatDate(new Date(),"yyyyMMddHHmmss");
            String snapshotId = os.compute().servers().createSnapshot(server.getCenterResourceId()
                    ,CenterService.PREFFIX+snapshotName);
            if(null!=snapshotId){
                Snapshot snapshot = new Snapshot();
                snapshot.setName(snapshotName);
                snapshot.setServerId(serverId);
                snapshot.setSnapshotId(snapshotId);
                beanService.create(snapshot);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            throw new BusiException(e.getMessage());
        }
    }

    public void createImage(String serverId,String imageName) throws BusiException {
        try {
            Server server = beanService.getById(Server.class,serverId);
            OSClient.OSClientV3 os = centerService.os(server.getCenterCode());
            if(null==imageName || imageName.trim().equals("")) {
                imageName = server.getName() + "-" + DateUtil.getFormatDate(new Date(), "yyyyMMddHHmmss");
            }
            String imageId = os.compute().servers().createSnapshot(server.getCenterResourceId(), imageName);
            if(null!=imageId){
                Image image = os.imagesV2().get(imageId);
                long timeout=10*60*1000;
                long time=0;
                while(image==null || image.getStatus()==null || !Image.ImageStatus.ACTIVE.value().equals(image.getStatus().value())){
                    try{ Thread.sleep(1000);time+=1000;}catch (Exception ee){}
                    image = os.imagesV2().get(imageId);
                    if(time>timeout) throw new BusiException("超时");
                }
                String imageFileName=imageName+".img";
                File imageFile = imageService.getImageFile(imageFileName);
                System.out.println("-----开始下载镜像数据-----");
                System.out.println(System.currentTimeMillis());

                ActionResponse r = os.imagesV2().download(imageId,imageFile);
                System.out.println(System.currentTimeMillis());
                System.out.println("-----------------------");
                if(r!=null && r.isSuccess()){
                    com.jweb.busi.entity.compute.Image localImage = new com.jweb.busi.entity.compute.Image();
                    localImage.setMetadataJson("{}");
                    localImage.setMetadata(new HashMap<String,String>());
                    localImage.setDescription("由虚拟机["+server.getName()+"]制作的镜像");
                    localImage.setDiskFormat(image.getDiskFormat().name());
                    localImage.setFileName(imageFileName);
                    localImage.setMinDisk(image.getMinDisk());
                    localImage.setMinRam(image.getMinRam());
                    localImage.setName(imageName);
                    localImage.setSize(image.getSize());
                    imageService.create(localImage);
                    //删除临时创建的快照
                    os.imagesV2().delete(imageId);
                }else{
                    if(imageFile.exists()){
                        imageFile.delete();
                    }
                    throw new BusiException("下载镜像数据失败");
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new BusiException(e.getMessage());
        }
    }

    public void restore(String snapshotId) throws BusiException {
        try {
            Snapshot snapshot = beanService.getById(Snapshot.class,snapshotId);
            Server server = beanService.getById(Server.class,snapshot.getServerId());
            server = serverService.getSynced(server);
            Center center = centerService.getByCode(server.getCenterCode());
            OSClient.OSClientV3 os = centerService.os(center);

            Network network = centerService.getPublicNetwork(center);
            List<String> subnetIds = network.getSubnets();
            if(subnetIds==null || subnetIds.size()<1){
                throw new BusiException("子网获取失败");
            }
            Subnet subnet = os.networking().subnet().get(subnetIds.get(0));
            //删除固定IP
            List<? extends Port> ports = os.networking().port().list();
            for (Port port : ports) {
                String portIp = port.getFixedIps().iterator().next().getIpAddress();
                //根据ip确定对应端口
                if (portIp.equals(server.getIp())) {
                    os.networking().port().delete(port.getId());
                    break;
                }
            }
            //删除挂载并记录挂载设备
            Map<String,String> vDevices = new HashMap<>();
            List<Volume> volumes = beanService.list(Volume.class
                    ,Where.create("serverId",Expression.eq,server.getId()));
            if(null!=volumes){
                for(Volume volume:volumes){
                    org.openstack4j.model.storage.block.Volume rVolume = os.blockStorage()
                            .volumes().get(volume.getVolumeId());
                    List<? extends VolumeAttachment>  attachments = rVolume.getAttachments();
                    if(null!=attachments){
                        for(VolumeAttachment volumeAttachment:attachments){
                            vDevices.put(rVolume.getId(),volumeAttachment.getDevice());
                            ActionResponse r=os.compute().servers().detachVolume(server.getCenterResourceId(),volumeAttachment.getId());
                            if(r==null || !r.isSuccess()){
                                throw new BusiException("解除挂载失败，请重试");
                            }
                        }

                    }
                }
            }
            //删除原来的虚拟机器
            os.compute().servers().delete(server.getCenterResourceId());

            Port port = os.networking().port().create(Builders.port()
                    .fixedIp(server.getIp(),subnetIds.get(0))
                    .networkId(network.getId())
                    .build());

            //创建新的虚拟机器
            Flavor flavor = beanService.getById(Flavor.class,server.getFlavorId());
            String centerFlavorId = flavor.getCenterResourceId(center,beanService,centerService);

            List<String> networkIds = new ArrayList<>();
            networkIds.add(network.getId());

            String centerImageId = snapshot.getSnapshotId();

            SecurityGroup securityGroup = beanService.getById(SecurityGroup.class,server.getSecurityGroupId());
            String centerSecurityGroupId = securityGroup.getCenterResourceId(center,beanService,centerService);
            String centerSecurityGroupName = os.compute().securityGroups().get(centerSecurityGroupId).getName();

            Keypair keypair = beanService.getById(Keypair.class,server.getKeypairId());
            String centerKeypairId = keypair.getCenterResourceId(center,beanService,centerService);

            ServerCreateBuilder sb = Builders.server()
                    .name(server.getName())
                    .flavor(centerFlavorId)
                    .addNetworkPort(port.getId())
                    .image(centerImageId)
                    .addSecurityGroup(centerSecurityGroupName)
                    .keypairName(centerKeypairId)
                    .addPersonality("/etc/motd", "Welcome to the BZH-CLoud VM!");

            ServerCreate sc = sb.build();
            org.openstack4j.model.compute.Server rServer = os.compute().servers().boot(sc);
            server.setStatus("sync");
            server.setCenterResourceId(rServer.getId());
            beanService.update(server);

            rServer =
                    os.compute().servers().get(server.getCenterResourceId());
            {
                long timeout=10*60*1000;
                long time=0;
                while(rServer==null || rServer.getStatus()==null
                    || !org.openstack4j.model.compute.Server.Status.ACTIVE.value()
                    .equals(rServer.getStatus().value())){
                    try{ Thread.sleep(1000);time+=1000;}catch (Exception ee){}
                    rServer = os.compute().servers().get(server.getCenterResourceId());
                    if(time>timeout) break;
                }
            }

            if(null!=volumes) {
                for (Volume volume : volumes) {
                    org.openstack4j.model.storage.block.Volume rVolume = os.blockStorage().volumes().get(volume.getVolumeId());
                    long timeout = 3 * 60 * 1000;
                    long time = 0;
                    while (rVolume == null || rVolume.getStatus() == null
                            || org.openstack4j.model.storage.block.Volume.Status.DETACHING.value()
                            .equals(rVolume.getStatus().value())) {
                        try {
                            Thread.sleep(1000);
                            time += 1000;
                        } catch (Exception ee) {
                        }
                        rVolume = os.blockStorage().volumes().get(volume.getVolumeId());
                        if (time > timeout) throw new BusiException("超时");
                    }
                    org.openstack4j.model.compute.VolumeAttachment r = os.compute().servers()
                            .attachVolume(rServer.getId(), volume.getVolumeId(), vDevices.get(volume.getVolumeId()));
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            throw new BusiException(e.getMessage());
        }
    }

    public void remove(String snapshotId) throws BusiException {
        try {
            Snapshot snapshot = beanService.getById(Snapshot.class,snapshotId);
            Server server = beanService.getById(Server.class,snapshot.getServerId());
            OSClient.OSClientV3 os = centerService.os(server.getCenterCode());
            ActionResponse r = os.imagesV2().delete(snapshot.getSnapshotId());
            if(r==null || !r.isSuccess()){
                throw new BusiException("删除失败");
            }
            beanService.remove(Snapshot.class,snapshot.getId());
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            throw new BusiException(e.getMessage());
        }
    }

    public List<Snapshot> getSynced(List<Snapshot> snapshots){
        if(null==snapshots||snapshots.size()<1) return new ArrayList<>();
        try{
            String serverId = snapshots.get(0).getServerId();
            Server server = beanService.getById(Server.class,serverId);
            String centerCode = server.getCenterCode();
            OSClient.OSClientV3 os = centerService.os(centerCode);
            List<? extends Image> rSnapshots = os.imagesV2().list();
            if(null!=rSnapshots){
                for(Image rSnapshot:rSnapshots){
                    for(Snapshot snapshot:snapshots) {
                        if (rSnapshot.getId().equals(snapshot.getSnapshotId())){
                            snapshot.setSize(rSnapshot.getSize());
                            snapshot.setCreateAt(rSnapshot.getCreatedAt());
                        }
                    }
                }
            }
        }catch (Exception e){
            throw new BusiException(e.getMessage());
        }
        return snapshots;
    }
}
