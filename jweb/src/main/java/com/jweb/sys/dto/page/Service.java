package com.jweb.sys.dto.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Service {
	private String id;
	private String name;
	private String type;
	private Map<String,Object> data = new HashMap<>();
	private List<Map<String,String>>  input = new ArrayList<>();
	private List<Map<String,String>>  output = new ArrayList<>();
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
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

	public List<String> getJs() {
		List<String> jses = new ArrayList<>();
		String js = type.replace(".", "/")+".js";
		if(null!=type&&!jses.contains(js)) {
			jses.add(js);
		}
		return jses;
	}
	
}
