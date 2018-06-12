package com.jweb.system.dto.page;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Service {
	private String id;
	private String name;
	private String uri;
	private String method;
	private String contentType;
	
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
	
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
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
