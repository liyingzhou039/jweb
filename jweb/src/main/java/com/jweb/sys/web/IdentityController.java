package com.jweb.sys.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 
* @ClassName: IdentityController 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author liyz liyingzhou@bzhcloud.com 
* @date 2017年8月4日 上午11:56:39 
*
 */
@Controller
@RequestMapping("identity")
public class IdentityController {
	@RequestMapping("/role-list")
    public String roleList() {
    	return "sys/identity/role-list";
    }
	@RequestMapping("/user-list")
    public String userList() {
    	return "sys/identity/user-list";
    }
	@RequestMapping("/menu-element-list")
	public String menuElementList() {
		return "sys/identity/menu-element-list";
	}
}
