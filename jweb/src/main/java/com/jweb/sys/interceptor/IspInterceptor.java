package com.jweb.sys.interceptor;


import com.jweb.common.util.HttpUtil;
import com.jweb.common.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
* @ClassName: TokenInterceptor 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author liyz liyz@bzhcloud.com 
* @date 2018年1月30日 上午10:33:57 
*
 */
@Configuration
public class IspInterceptor extends HandlerInterceptorAdapter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${sys.isp.url:}")
    private String ispUrl;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    	
    	String uri = request.getRequestURI();
    	//所有对外接口必须到综合集成验证domain
    	String domain = request.getHeader("domain");
    	boolean validDomain = true;
    	//domain验证只针对/api 且只有在配置了sys.isp.url的时候才激活
    	if(
    			uri!=null 
    			&& 
    			uri.startsWith("/api") 
    			&& 
    			ispUrl!=null 
    			&& 
    			ispUrl.length()>0
    			&&
    			!uri.startsWith("/api/auth/isp/login")  
    			&&
    			!uri.startsWith("/api/auth/isp/logout") 
    			&&
    			!uri.startsWith("/api/auth/isp/syncUser") 
    	) {
    		//////////////////调用domain验证接口///////////////
        	Map<String,Object> params = new HashMap<>();
        	params.put("system", "S02");
        	params.put("method", "keyverify");
        	
        	List<Map<String,String>> reqdata = new ArrayList<>();
        	Map<String,String> data = new HashMap<>();
        	data.put("domain", domain);
        	data.put("path", uri);
        	reqdata.add(data);
        	
        	params.put("reqdata",reqdata );
        	String resJson = HttpUtil.postJson(ispUrl+"/interfaces",params);
        	@SuppressWarnings("unchecked")
			Map<String,Object>  res = JsonUtil.jsonToBean(resJson, new HashMap<String,Object>().getClass());
        	if(null!=res&&String.valueOf(res.get("status")).equals("801")) {
        		validDomain = true;
        	}else {
        		validDomain = false;
        	}
    		/////////////////////////////////////////////////
    	}
    	if(!validDomain) {
    		response.setContentType("application/json");
	    	response.setCharacterEncoding("UTF-8");
	    	response.getWriter().append("{\"code\":500,\"msg\":\"domain验证失败,请到综合集成配置权限！\"}");
			return false;
    	}
    	
        return super.preHandle(request, response, handler);
    }
}
