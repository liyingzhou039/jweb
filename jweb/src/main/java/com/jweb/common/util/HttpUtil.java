package com.jweb.common.util;

import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * http工具类
 * 
 */
public class HttpUtil {
	private static final String GET = "GET";
	private static final String DELETE = "DELETE";
	private static final String POST = "POST";
	private static final String PUT = "PUT";
	
	private static Logger logger = LoggerFactory.getLogger(HttpUtil.class);
	public static String delete(String url) {
		return delete(url,null,null);
	}
	public static String delete(String url,Map<String, Object> data) {
		return get(url,data,null);
	}
	public static String get(String url) {
		return get(url,null,null);
	}
	public static String get(String url,Map<String, Object> data) {
		return get(url,data,null);
	}
	public static String post(String url) {
		return post(url,null,null);
	}
	public static String post(String url,Map<String, Object> data) {
		return post(url,data,null);
	}
	public static String postJson(String url) {
		return postJson(url,null,null);
	}
	public static String postJson(String url,Map<String, Object> data) {
		return postJson(url,data,null);
	}
	public static String postJson(String url,String json) {
		@SuppressWarnings("unchecked")
		Map<String,Object> data = JsonUtil.jsonToBean(json, new HashMap<String,Object>().getClass());
		return postJson(url,data,null);
	}
	public static String put(String url) {
		return put(url,null,null);
	}
	public static String put(String url,Map<String, Object> data) {
		return put(url,data,null);
	}
	public static String putJson(String url) {
		return putJson(url,null,null);
	}
	public static String putJson(String url,Map<String, Object> data) {
		return putJson(url,data,null);
	}
	public static String get(String url,Map<String, Object> params,Map<String, String> headers) {
		return request(url, GET, params, headers,false);
	}
	public static String delete(String url,Map<String, Object> params,Map<String, String> headers) {
		return request(url, DELETE, params, headers,false);
	}
	public static String post(String url,Map<String, Object> params,Map<String, String> headers) {
		return request(url, POST, params, headers,false);
	}
	public static String postJson(String url,Map<String, Object> params,Map<String, String> headers) {
		return request(url, POST, params, headers,true);
	}
	public static String put(String url,Map<String, Object> params,Map<String, String> headers) {
		return request(url, PUT, params, headers,false);
	}
	public static String putJson(String url,Map<String, Object> params,Map<String, String> headers) {
		return request(url, PUT, params, headers,true);
	}
	public static String request(String url,String methodType,Map<String, Object> params,Map<String, String> headers,boolean isJson) {
		logger.info("请求："+url);
		logger.info("Method:GET");
		logger.info("Header:"+headers);
		logger.info("Data:"+JsonUtil.beanToJson(params));
		
		CloseableHttpClient httpClient = null;
		String body = null;
		try {
			httpClient =HttpClients.createDefault();
			HttpUriRequest method = getMethod(url,methodType,params,headers,isJson);
            HttpResponse response= httpClient.execute(method); 
            int statusCode = response.getStatusLine().getStatusCode();
            body=EntityUtils.toString(response.getEntity());
            logger.info("响应:");
            logger.info("状态:"+statusCode);
            logger.info("Body：" + body);
		}catch(Exception e) {
			logger.info(e.getMessage());
		}finally{
			try {
				httpClient.close();
			}catch(Exception ee) {
				logger.info(ee.getMessage());
			}
		}
		
		return body;
	}
	private static HttpUriRequest getMethod(String url,String methodType,Map<String, Object> params,Map<String, String> headers,boolean isJson) throws ParseException, IOException {
		HttpUriRequest method = null;
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		if(null!=params) {
			for(String name:params.keySet()) {
				nameValuePairs.add(new BasicNameValuePair(name,String.valueOf(params.get(name))));
			}
		}
		
		if(GET.equalsIgnoreCase(methodType)) {
			if(url!=null&&url.indexOf("?")!=-1)
				url+="&";
			else
				url+="?";
			url+=EntityUtils.toString(new UrlEncodedFormEntity(nameValuePairs, Consts.UTF_8));
			method = new HttpGet(url);
		}else if(DELETE.equalsIgnoreCase(methodType)) {
			if(url!=null&&url.indexOf("?")!=-1)
				url+="&";
			else
				url+="?";
			url+=EntityUtils.toString(new UrlEncodedFormEntity(nameValuePairs, Consts.UTF_8));
			method = new HttpDelete(url);
		}else if(PUT.equalsIgnoreCase(methodType)) {
			HttpPut put = new HttpPut(url);
			if(isJson) {
				StringEntity entity = new StringEntity(JsonUtil.beanToJson(params),"utf-8");   
				entity.setContentEncoding("UTF-8");    
				entity.setContentType("application/json"); 
				put.setEntity(entity);
			}else {
				put.setEntity(new UrlEncodedFormEntity(nameValuePairs,Consts.UTF_8));
				
			}
			method = put;
		}else if(POST.equalsIgnoreCase(methodType)) {
			HttpPost post = new HttpPost(url);
			if(isJson) {
				StringEntity entity = new StringEntity(JsonUtil.beanToJson(params),"utf-8");   
				entity.setContentType("application/json"); 
				post.setEntity(entity);
			}else {
				post.setEntity(new UrlEncodedFormEntity(nameValuePairs,Consts.UTF_8));
			}
			method = post;
		}
		
		if(null!=headers) {
			for(String name:headers.keySet())
				method.setHeader(name, headers.get(name));
		}
		return method;
	}
	
	public static void main(String[] args) {
		
		String url1 ="http://192.168.3.11:7001/interfaces";
		Map<String,Object> params = new HashMap<>();
		params.put("system", "S02");
		params.put("method", "keyverify");
		List<Map<String,String>> reqdata = new ArrayList<>();
		Map<String,String> token1 = new HashMap<>();
		reqdata.add(token1);
		//token1.put("token", "token1");
		token1.put("domain", "http://127.0.1.1:8888");
		token1.put("path", "/dfsd");
		params.put("reqdata",reqdata );
		logger.info(HttpUtil.postJson(url1,params));
	}
}
