package com.jweb;

import com.jweb.common.util.HttpUtil;
import io.leopard.javahost.JavaHost;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.openstack.OSFactory;

import java.util.HashMap;
import java.util.Map;

public class Test {
    public static OSClient.OSClientV3 os(String username,String password) {
        System.out.println(">>>>>>>>>>>>>>>虚拟DNS配置<<<<<<<<<<<<<");
        String url = "http://10.150.10.10:5000/v3";
        String ip = url.replace("http://", "").replace(":5000/v3", "");
        JavaHost.updateVirtualDns("controller", ip);
        JavaHost.printAllVirtualDns();
        OSFactory.enableHttpLoggingFilter(true);
        OSClient.OSClientV3 os = OSFactory.builderV3()
                .endpoint(url)
                .credentials("admin", "123456", Identifier.byName("Default"))
                .scopeToProject(Identifier.byName("admin") , Identifier.byName("Default"))
                .authenticate();
        return os;
    }


    public static void main(String[] args){
        OSClient.OSClientV3 os = os("admin","123456");
        String token = os.getToken().getId();
        System.out.println(token);
        //String sUrl = "http://192.168.1.10:8004/v1/5cd6ec64b5504fc481de338c6e7e60fb/stacks/test/02462292-7ea0-41ba-b103-d26887ce6f8c/actions";
        String sUrl="http://192.168.1.10:8000/v1/signal/arn%3Aopenstack%3Aheat%3A%3A8a2257b3524148019edf7dc53bcbad4c%3Astacks%2Fexample%2F23ad1fdd-53be-4725-81a5-1248080abe9e%2Fresources%2Fscaleup_policy?Timestamp=2018-08-24T07%3A26%3A18Z&SignatureMethod=HmacSHA256&AWSAccessKeyId=763b0cd5b5374ca2a69354f905ba0503&SignatureVersion=2&Signature=o700OCWBmIOq50tkNhbimN%2F8k0j47meEm7GthlyEMm8%3D";
        //X-Auth-Token
        Map<String,String> headers = new HashMap<>();
        headers.put("X-Auth-Token",token);
        System.out.println(HttpUtil.post(sUrl,new HashMap<String,Object>(),headers));


    }

}
