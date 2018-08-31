package com.jweb.busi.service.center;

import com.jweb.busi.entity.center.Center;
import com.jweb.common.exception.BusiException;
import com.jweb.common.persistent.model.Expression;
import com.jweb.common.persistent.model.Where;
import com.jweb.common.service.BeanService;
import groovy.transform.ASTTest;
import io.leopard.javahost.JavaHost;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.model.network.Network;
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
    public Center getBestCenter() throws BusiException {
        List<Center> centers = this.all();
        if(centers.size()>0){
            //暂时采用随机分配方案
            //@todo 需要根据资源使用情况等进行最优中心选择
            long index = Math.round(Math.random()*(centers.size()-1));
            return centers.get((int)index);
        }
        throw new BusiException("没有可以使用的数据中心");
    }

    public Network getPublicNetwork(Center center) throws BusiException{
        Network rNetwork = null;
        try {
            OSClientV3 os = this.os(center);
            List<? extends Network> networks = os.networking().network().list();
            for(Network network : networks){
                if(network.isRouterExternal()){
                    rNetwork = network;
                }
            }
        }catch (Exception e){
            throw new BusiException("中心["+center.getName()+"]没有有效的网络");
        }
        return rNetwork;
    }
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
    public Center getByCode(String centerCode) throws BusiException{
        List<Center> centers = beanService.list(Center.class,
                Where.create("code",Expression.eq,centerCode));
        if(centers==null || centers.size()<=0)
            throw new BusiException("中心不存在:"+centerCode);

        return centers.get(0);
    }
    public OSClientV3 os(String centerCode) throws BusiException{
        return this.os(this.getByCode(centerCode));
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
