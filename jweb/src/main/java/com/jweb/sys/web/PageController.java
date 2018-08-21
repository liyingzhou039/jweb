package com.jweb.sys.web;

import com.jweb.sys.dto.page.Page;
import com.jweb.sys.service.page.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("")
public class PageController {
	@Autowired
	PageService pageService;
	
	@RequestMapping("page/**")
    public String view(HttpServletRequest request) {
		Page page = pageService.getByURI(request.getRequestURI());
	    request.setAttribute("page",page);
    	return "sys/page/view";
    }
	@RequestMapping("page-edit/**")
    public String edit(HttpServletRequest request) {
		Page page = pageService.getByURI(request.getRequestURI());
	    request.setAttribute("page",page);
    	return "sys/page/view";
    }
}
