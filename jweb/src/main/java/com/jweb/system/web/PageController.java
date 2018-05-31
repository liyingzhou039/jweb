package com.jweb.system.web;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.jweb.system.dto.page.Page;
import com.jweb.system.service.PageService;
@Controller
@RequestMapping("")
public class PageController{
	@Autowired
	PageService pageService;
	
	@RequestMapping("page/**")
    public String view(HttpServletRequest request) {
		Page page = pageService.getByURI(request.getRequestURI());
	    request.setAttribute("page",page);
    	return "system/page";
    }
	@RequestMapping("page-edit/**")
    public String edit(HttpServletRequest request) {
		Page page = pageService.getByURI(request.getRequestURI());
	    request.setAttribute("page",page);
    	return "system/page";
    }
}
