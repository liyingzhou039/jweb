package com.jweb.system.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping("/page")
public class PageController{
	@RequestMapping("view")
    public String view() {
    	return "page/view";
    }
	@RequestMapping("edit")
    public String edit() {
    	return "page/edit";
    }
}
