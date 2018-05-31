package com.jweb.system.dto.page;

import java.util.ArrayList;
import java.util.List;

import com.jweb.system.util.JsonUtil;

public class Page {
	private String requestURI;
	private String prefix;
	private List<Portal> portals = new ArrayList<>();
	private List<Service> services = new ArrayList<>();
	
	public Page() {
		
	}
	
	public String getRequestURI() {
		return requestURI;
	}
	public void setRequestURI(String requestURI) {
		this.requestURI = requestURI;
	}
	
	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public List<Portal> getPortals() {
		return portals;
	}
	public void setPortals(List<Portal> portals) {
		this.portals = portals;
	}
	
	public List<Service> getServices() {
		return services;
	}

	public void setServices(List<Service> services) {
		this.services = services;
	}

	public String getScript() {
		return "var page={}; page.prefix='"+this.prefix+"'; page.portals = "+JsonUtil.beanToJson(this.portals)+"; page.services = "+JsonUtil.beanToJson(this.services)+";";
	}
	public String getHeadHTML() {
		String[] paths = getRequestPaths();
		this.prefix = getPrefix(paths);
		
		List<String> jses = new ArrayList<>();
		List<String> csses = new ArrayList<>();
		//required css
		csses.add("plugins/bootstrap-4.0.0/css/bootstrap.min.css");
		//required js
		jses.add("plugins/jquery-2.1.4/jquery.min.js");
		jses.add("plugins/proper/popper.min.js");
		jses.add("plugins/bootstrap-4.0.0/js/bootstrap.min.js");
		jses.add("js/base.js");
		
		for(Portal portal : portals) {
			for(String js:portal.getJs()) {
				if(!jses.contains(js)) {
					jses.add(js);
				}
			}
		}
		
		StringBuffer head = new StringBuffer();
		head.append("\n\t<meta charset='utf-8'/>\n");
		head.append("\t<meta name='viewport' content='width=device-width, initial-scale=1, shrink-to-fit=no'/>\n");
	    for(String css:csses) {
	    	head.append("\t<link rel='stylesheet' href='"+prefix+css+"?only="+System.currentTimeMillis()+"'/>\n");
	    }
	    for(String js:jses) {
	    	head.append("\t<script src='"+prefix+js+"?only="+System.currentTimeMillis()+"'></script>\n");
	    }
	    head.append("\t<title>页面</title><style>.row DIV{border:dotted black 1px;}</style>\n");
	    return head.toString();
	}
	
	private String[] getRequestPaths() {
		if(requestURI==null) return new String[] {};
		if(requestURI.indexOf("?")!=-1) {
			requestURI.substring(0,requestURI.indexOf("?"));
		}
		requestURI = requestURI.replace("//", "/").trim();
		if(requestURI.startsWith("/")) requestURI = requestURI.substring(1);
		if(requestURI.endsWith("/")) requestURI = requestURI.substring(0,requestURI.length());
		return requestURI.split("/");
	}
	private String getPrefix(String[] paths) {
		String prefix = "";
		for(int i=0;i<paths.length-1;i++)
			prefix +="../";
		return prefix;
	}
	public String getPath() {
		String[] paths = getRequestPaths();
		String pagePath = "";
		for(int i=1;i<paths.length;i++)
			pagePath+="/"+paths[i];
		return pagePath;
	}
}
