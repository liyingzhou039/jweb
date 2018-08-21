package com.jweb.sys.interceptor;


import com.jweb.common.session.Session;
import com.jweb.sys.dto.identity.LoginUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Pattern;

/**
 * 
* @ClassName: TokenInterceptor 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author liyz liyz@bzhcloud.com 
* @date 2018年1月30日 上午10:33:57 
*
 */
@Configuration
public class TokenInterceptor extends HandlerInterceptorAdapter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${security.ignoredURI}")
    private String[] ignoredURI;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    	String uri = request.getRequestURI();
	    try {
	    	String from = request.getHeader(Session.FROM);
			String token = request.getHeader(Session.TOKEN_NAME);
			if(null==from || "".equals(from.trim())) {
				from = request.getParameter(Session.FROM);
			}
			if(null==token || "".equals(token.trim())) {
				token = request.getParameter(Session.TOKEN_NAME);
			}
			if(token!=null) {
				Session.setCurrentToken(token);
			}
			
			if(!isIgnored(uri)) {
				//登录验证
				LoginUser user = Session.getCurrentUser();
				if(user==null) {
					response.setContentType("application/json");
			    	response.setCharacterEncoding("UTF-8");
			    	try {
			    		if(from!=null && Session.FROM_TYPE_AJAX.equals(from)) {
					    	response.getWriter().append("{\"ok\":false,\"msg\":\"无效Token！\",\"status\":40101}");
					    	response.flushBuffer();
					    	return false;
			    		}else {
			    			if(uri.startsWith("/api")) {
			    				response.getWriter().append("{\"code\":500,\"msg\":\"无效Token！\"}");
			    				return false;
			    			}else {
			    				response.sendRedirect("/toLogin");
			    				return false;
			    			}
			    		}
			    	}catch(Exception e) {
			    		logger.info("response error！");
			    	}
				}
			}else {
			}
		} catch (Exception e) {}
        return super.preHandle(request, response, handler);
    }
    private boolean isIgnored(String uri) {
    	for(String ignored:ignoredURI) {
    		if(ignored==null) {
    			continue;
    		}
    		//相等则忽略
    		if(uri.trim().equalsIgnoreCase(ignored.trim())) {
    			return true;
    		}
    		//正则匹配则忽略
    		if(Pattern.matches(ignored, uri)) {
    			return true;
    		}
    	}
    	return false;
    }
}
