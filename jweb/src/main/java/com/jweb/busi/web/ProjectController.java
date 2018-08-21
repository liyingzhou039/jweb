package com.jweb.busi.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("project")
public class ProjectController {
	@RequestMapping("/project-list")
    public String projectList() {
    	return "busi/project/project-list";
    }
}
