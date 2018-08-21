package com.jweb.busi.service.center;

import com.jweb.busi.entity.center.Center;
import com.jweb.common.service.BeanService;
import io.leopard.javahost.JavaHost;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.openstack.OSFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CenterService {
    public static final String CONTROLLER = "controller";
    public static final String PREFFIX = "_cloud-";
    @Autowired
    BeanService beanService;

    /**
     * 获取所有数据中心
     * @return
     */
    public List<Center> all(){
        List<Center> centers = beanService.list(Center.class);
        if(null == centers) centers = new ArrayList<>();
        return centers;
    }

    public List<String> allCodes(){
        List<String> allCenterCodes = new ArrayList<>();
        for(Center center : all()) {
            allCenterCodes.add(center.getCode());
        }
        return allCenterCodes;
    }

    public OSClientV3 os(Center center) {
        System.out.println(">>>>>>>>>>>>>>>虚拟DNS配置<<<<<<<<<<<<<");
        String ip = center.getEndpointUrl().replace("http://", "").replace(":5000/v3", "");
        JavaHost.updateVirtualDns(CONTROLLER, ip);
        JavaHost.printAllVirtualDns();
        OSFactory.enableHttpLoggingFilter(true);
        OSClient.OSClientV3 os = OSFactory.builderV3()
                .endpoint(center.getEndpointUrl())
                .credentials(center.getEndpointUsername(), center.getEndpointPassword(), Identifier.byName("Default"))
                .scopeToProject(Identifier.byName("admin") , Identifier.byName("Default"))
                .authenticate();
        return os;
    }

}
