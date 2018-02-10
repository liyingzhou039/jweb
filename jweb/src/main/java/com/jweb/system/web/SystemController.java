package com.jweb.system.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping("")
public class SystemController{
	@RequestMapping("")
    public String form() {
    	return "index";
    }
	@RequestMapping("login")
    public String login() {
    	return "login";
    }
	@RequestMapping("toLogin")
    public String toLogin() {
    	return "to-login";
    }
	@RequestMapping("system/role-list")
    public String roleList() {
    	return "system/role-list";
    }
	@RequestMapping("system/user-list")
    public String userList() {
    	return "system/user-list";
    }
	@RequestMapping("system/menu-element-list")
    public String menuElementList() {
    	return "system/menu-element-list";
    }
}
