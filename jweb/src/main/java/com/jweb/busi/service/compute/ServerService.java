package com.jweb.busi.service.compute;

import com.jweb.busi.entity.center.Center;
import com.jweb.busi.entity.compute.Flavor;
import com.jweb.busi.entity.compute.Image;
import com.jweb.busi.entity.compute.Server;
import com.jweb.busi.entity.security.Keypair;
import com.jweb.busi.entity.security.SecurityGroup;
import com.jweb.busi.service.center.CenterService;
import com.jweb.common.exception.BusiException;
import com.jweb.common.service.BeanService;
import com.jweb.common.session.Session;
import com.jweb.sys.dto.identity.LoginUser;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.compute.ServerCreate;
import org.openstack4j.model.compute.builder.ServerCreateBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class ServerService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    BeanService beanService;
    @Autowired
    CenterService centerService;

    public Server create(Server server) throws BusiException {
        try {
            Center bestCenter = centerService.getBestCenter();
            OSClient.OSClientV3 os = centerService.os(bestCenter);

            Flavor flavor = beanService.getById(Flavor.class,server.getFlavorId());
            String centerFlavorId = flavor.getCenterResourceId(bestCenter,beanService,centerService);

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
                    .name(server.getName())
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
            e.printStackTrace();
            throw new BusiException(e.getMessage());
        }
        return server;
    }

    public void remove(String serverId) throws BusiException{
        try{
            Server server = beanService.getById(Server.class,serverId);
            OSClient.OSClientV3 os = centerService.os(server.getCenterCode());
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
}
