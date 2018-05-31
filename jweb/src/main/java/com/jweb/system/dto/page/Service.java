package com.jweb.system.dto.page;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Service {
	private String id;
	private String name;
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
