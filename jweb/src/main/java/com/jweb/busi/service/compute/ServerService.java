package com.jweb.busi.service.compute;

import com.jweb.busi.entity.center.Center;
import com.jweb.busi.entity.compute.*;
import com.jweb.busi.entity.compute.Flavor;
import com.jweb.busi.entity.compute.Image;
import com.jweb.busi.entity.compute.Server;
import com.jweb.busi.entity.project.Quota;
import com.jweb.busi.entity.security.Keypair;
import com.jweb.busi.entity.security.SecurityGroup;
import com.jweb.busi.service.center.CenterService;
import com.jweb.busi.service.project.ProjectService;
import com.jweb.common.exception.BusiException;
import com.jweb.common.persistent.model.Expression;
import com.jweb.common.persistent.model.Where;
import com.jweb.common.service.BeanService;
import com.jweb.common.session.Session;
import com.jweb.sys.dto.identity.LoginUser;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.compute.*;
import org.openstack4j.model.compute.builder.ServerCreateBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ServerService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    BeanService beanService;
    @Autowired
    CenterService centerService;
    @Autowired
    VolumeService volumeService;
    @Autowired
    SnapshotService snapshotService;
    @Autowired
    ProjectService projectService;

    public Server create(Server server) throws BusiException {
        try {
            Center bestCenter = centerService.getBestCenter();
            OSClient.OSClientV3 os = centerService.os(bestCenter);

            Flavor flavor = beanService.getById(Flavor.class,server.getFlavorId());
            String centerFlavorId = flavor.getCenterResourceId(bestCenter,beanService,centerService);

            Quota quota = new Quota();
            quota.setCores(flavor.getVcpus());
            quota.setGigabytes(flavor.getDisk());
            quota.setInstances(1);
            quota.setRam(flavor.getRam());
            projectService.checkQuota(quota);

            List<String> networkIds = new ArrayList<>();
            networkIds.add(centerService.getPublicNetwork(bestCenter).getId());

            Image image = beanService.getById(Image.class,server.getImageId());
            String centerImageId = image.getCenterResourceId(bestCenter,beanService,centerService);

            SecurityGroup securityGroup = beanService.getById(SecurityGroup.class,server.getSecurityGroupId());
            String centerSecurityGroupId = securityGroup.getCenterResourceId(bestCenter,beanService,centerService);
            String centerSecurityGroupName = os.compute().securityGroups().get(centerSecurityGroupId).getName();

            Keypair keypair = beanService.getById(Keypair.class,server.getKeypairId());
            String centerKeypairId = keypair.getCenterResourceId(bestCenter,beanService,centerService);

            ServerCreateBuilder sb = Builders.server()
                    .name(CenterService.PREFFIX+bestCenter.getCode()+"-"+server.getName())
                    .flavor(centerFlavorId)
                    .networks(networkIds)
                    .image(centerImageId)
                    .addSecurityGroup(centerSecurityGroupName)
                    .keypairName(centerKeypairId)
                    .addPersonality("/etc/motd", "Welcome to the BZH-CLoud VM!");

            ServerCreate sc = sb.build();
            org.openstack4j.model.compute.Server rServer = os.compute().servers().boot(sc);
            server.setStatus("sync");
            server.setCenterResourceId(rServer.getId());
            server.setCenterCode(bestCenter.getCode());
            LoginUser user = Session.getCurrentUser();
            server.setUserId(user.getId());
            server.setProjectId(user.getProjectId());
            beanService.create(server);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new BusiException(e.getMessage());
        }
        return server;
    }
    public Server getSynced(Server server){
        List<Server> servers = new ArrayList<>();
        servers.add(server);
        servers = getSynced(servers);
        return servers.get(0);
    }
    public List<Server> getSynced(List<Server> servers){
        if(null==servers) return new ArrayList<>();
        try{
            //先得到所有中心编码
            List<String> centerCodes = new ArrayList<>();
            for(Server server:servers){
                if(!centerCodes.contains(server.getCenterCode())){
                    centerCodes.add(server.getCenterCode());
                }
            }
            //得到所有虚拟机器列表
            for(String centerCode:centerCodes){
                OSClient.OSClientV3 os = centerService.os(centerCode);
                List<? extends org.openstack4j.model.compute.Server> rServers = os.compute().servers().listAll(true);
                if(null!=rServers){
                    for(org.openstack4j.model.compute.Server rServer:rServers){
                        for(Server server:servers){
                            if(rServer.getId().equals(server.getCenterResourceId())
                                    && centerCode.equals(server.getCenterCode())){
                                //同步
                                org.openstack4j.model.compute.Server.Status status = rServer.getStatus();
                                if(null!=status){
                                    server.setStatus(status.value());
                                }
                                server.setCreateAt(rServer.getCreated());

                                Addresses addres =rServer.getAddresses();
                                if(addres!=null && addres.getAddresses()!=null) {
                                    Map<String, List<? extends Address >> netsAddr = addres.getAddresses();
                                    if(null!=netsAddr){
                                        for(String net:netsAddr.keySet()){
                                            List<? extends Address > addrs = netsAddr.get(net);
                                            if(addrs!=null && addrs.size()>0){
                                                server.setIp(addrs.get(0).getAddr());
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            throw new BusiException(e.getMessage());
        }
        return servers;
    }
    public Server getById(String serverId) throws BusiException{
        Server server = beanService.getById(Server.class,serverId);
        server = getSynced(server);
        //获取vnc
        try {
            Center center = centerService.getByCode(server.getCenterCode());
            VNCConsole console = centerService.os(center).compute().servers()
                    .getVNCConsole(server.getCenterResourceId(), VNCConsole.Type.NOVNC);
            String endpoint = center.getEndpointUrl();
            String ip = endpoint.replace("http://", "").replace(":5000/v3", "");
            server.setVncUrl(console.getURL()
                    .replaceFirst("controller", ip)
                    .replaceFirst("://[\\d\\.]+","://"+ip)
            );
        } catch (Exception e) { }
        return server;
    }
    public void remove(String serverId) throws BusiException{
        try{
            Server server = beanService.getById(Server.class,serverId);
            OSClient.OSClientV3 os = centerService.os(server.getCenterCode());
            //删除快照
            List<Snapshot> snapshots = beanService.list(Snapshot.class,
                    Where.create("serverId",Expression.eq,serverId));
            if(null!=snapshots){
                for(Snapshot snapshot : snapshots){
                    snapshotService.remove(snapshot.getId());
                }
            }
            //删除磁盘
            List<Volume> volumes = beanService.list(Volume.class,
                    Where.create("serverId",Expression.eq,serverId));
            if(null!=volumes){
                for(Volume volume : volumes){
                    volumeService.remove(volume.getId());
                }
            }
            //删除自己
            ActionResponse r =os.compute().servers().delete(server.getCenterResourceId());
            if(r!=null || r.isSuccess()){
                beanService.remove(server.getClass(),server.getId());
            }else{
                throw new BusiException("删除失败");
            }
        }catch (Exception e){
            throw new BusiException(e.getMessage());
        }
    }
    public void action(String serverId, String actionName) throws BusiException {
        try {
            Action action = null;
            for (Action a : Action.values()) {
                if (a.name().equalsIgnoreCase(actionName)) {
                    action = a;
                    break;
                }
            }
            Server server = beanService.getById(Server.class,serverId);
            OSClient.OSClientV3 os = centerService.os(server.getCenterCode());
            ActionResponse res = os.compute().servers().action(server.getCenterResourceId(), action);
            if (res == null || !res.isSuccess()) {
                throw new BusiException("失败");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            throw new BusiException(e.getMessage());
        }
    }
}
