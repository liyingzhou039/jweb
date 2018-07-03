package com.jweb.system.dto.page;

import java.util.ArrayList;
import java.util.List;

import com.jweb.system.util.JsonUtil;

public class Page {
	private String id;
	private String name;
	private String uri;
	private String prefix;
	private List<Portal> portals = new ArrayList<>();
	private List<Service> services = new ArrayList<>();
	
	private List<String> jses = new ArrayList<>();
	private List<String> csses = new ArrayList<>();
	
	public Page() {
		
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
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

	public List<String> jses() {
		return jses;
	}

	public void setJses(List<String> jses) {
		this.jses = jses;
	}

	public List<String> csses() {
		return csses;
	}

	public void setCsses(List<String> csses) {
		this.csses = csses;
	}

	public String getScript() {
		return "var page={};page.uri='"+this.uri+"'; page.prefix='"+this.prefix+"'; page.portals = "+JsonUtil.beanToJson(this.portals)+"; page.services = "+JsonUtil.beanToJson(this.services)+";";
	}
	public String getHeadHTML() {
		String[] paths = getRequestPaths();
		this.prefix = getPrefix(paths);
		
		//required css
		csses.add(0,"plugins/bootstrap-4.0.0/css/bootstrap.min.css");
		csses.add("css/base.css");
		//required js
		jses.add(0,"plugins/jquery-2.1.4/jquery.min.js");
		jses.add(1,"plugins/proper/popper.min.js");
		jses.add(2,"plugins/bootstrap-4.0.0/js/bootstrap.min.js");
		jses.add(3,"js/base.js");
		
		for(Portal portal : portals) {
			for(String js:portal.getJs()) {
				if(!jses.contains(js)) {
					jses.add(js);
				}
			}
		}

		for(Service service : services) {
			for(String js:service.getJs()) {
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
	    head.append("\t<title>页面</title>\n");
	    return head.toString();
	}
	
	private String[] getRequestPaths() {
		if(uri==null) return new String[] {};
		if(uri.indexOf("?")!=-1) {
			uri.substring(0,uri.indexOf("?"));
		}
		uri = uri.replace("//", "/").trim();
		if(uri.startsWith("/")) uri = uri.substring(1);
		if(uri.endsWith("/")) uri = uri.substring(0,uri.length());
		return uri.split("/");
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
