package com.jweb.system.web;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jweb.busi.entiry.AccessBean;
import com.jweb.system.service.BeanService;
@Controller
@RequestMapping("access")
public class AccessController{
	@Autowired
	BeanService beanService;
	
	@RequestMapping("test/**")
    public String test(HttpServletRequest request) throws UnknownHostException {
		String uri = request.getRequestURI();
		InetAddress addr = InetAddress.getLocalHost();
        String ip=addr.getHostName(); 
       
        AccessBean bean = new AccessBean();
        bean.setId(UUID.randomUUID().toString());
        bean.setIp(ip);
        bean.setUri(uri);
        bean.setTime(new Date());
        
        beanService.createBean(bean);
        
        List<AccessBean> beans = beanService.list(
        		AccessBean.class, null, "time desc");
        
        request.setAttribute("accesses", beans);
        
    	return "busi/access/test";
    }
}
