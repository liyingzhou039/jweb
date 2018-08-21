package com.jweb.sys.dto.page;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Portal {
	private String id;
	private String type;
	private String clazz;
	List<Portal> children = new ArrayList<>();
	private List<Map<String,String>> input = new ArrayList<>();
	private List<Map<String,String>> output = new ArrayList<>();
	
	public Portal() {}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public List<String> getJs() {
		List<String> jses = new ArrayList<>();
		String js = type.replace(".", "/")+".js";
		if(null!=type&&!jses.contains(js)) {
			jses.add(js);
		}
		for(Portal cp:children) {
			jses.addAll(cp.getJs());
		}
		return jses;
	}
	public List<Portal> getChildren() {
		return children;
	}
	public void setChildren(List<Portal> children) {
		this.children = children;
	}

	public List<Map<String, String>> getInput() {
		return input;
	}

	public void setInput(List<Map<String, String>> input) {
		this.input = input;
	}

	public List<Map<String, String>> getOutput() {
		return output;
	}

	public void setOutput(List<Map<String, String>> output) {
		this.output = output;
	}
}
