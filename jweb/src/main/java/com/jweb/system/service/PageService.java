package com.jweb.system.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.jweb.system.util.JsonUtil;
import com.jweb.system.dto.page.Page;

@Service
public class PageService{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public Page getByURI(String requestURI) {
		logger.info(requestURI);
		Page page = null;
		InputStream stream = getClass().getClassLoader().getResourceAsStream("static/js/page-edit.json");
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
		String line=null;
		StringBuffer json = new StringBuffer();
		try {
			while((line=br.readLine())!=null) {
				json.append(line);
			}
			System.out.print(json);
			page = JsonUtil.jsonToBean(json.toString(), Page.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		page.setRequestURI(requestURI);
		return page;
	}
	
}
