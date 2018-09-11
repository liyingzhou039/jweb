package com.jweb.sys.web;

import com.jweb.common.session.Session;
import com.jweb.common.util.JsonUtil;
import com.jweb.sys.dto.identity.LoginUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * 
* @ClassName: SysController
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author liyz liyingzhou@bzhcloud.com 
* @date 2017年8月4日 上午11:56:39 
*
 */
@Controller
@RequestMapping("")
public class SysController{
	@Value("${sys.company.name:Jweb}")
	private String companyName;
	@Value("${sys.name:Jweb}")
	private String sysName;
	@Value("${sys.isp.url:/login}")
	private String ispUrl;
	@RequestMapping("")
	public String index(HttpServletRequest req) {
		req.setAttribute("sysName", sysName);
		req.setAttribute("companyName", companyName);
		return "sys/index";
	}
	@RequestMapping("login")
	public String login(HttpServletRequest req) {
		req.setAttribute("sysName", sysName);
		return "sys/login";
	}
	@RequestMapping("toLogin")
	public String toLogin(HttpServletRequest req) {
		req.setAttribute("ispUrl", ispUrl);
		return "sys/to-login";
	}
	@RequestMapping("toIndex")
	public String toIndex(HttpServletRequest req) {
		LoginUser user = null;
		try {
			user = Session.getCurrentUser();
			user.setToken(req.getParameter("_token"));
		}catch(Exception e) {}
		req.setAttribute("user", null==user?"{}":JsonUtil.beanToJson(user));
		return "sys/to-index";
	}

}
