package com.jweb.sys.service.page;

import com.jweb.common.service.BeanService;
import com.jweb.sys.dto.page.Page;
import com.jweb.sys.entity.page.PageBean;
import com.jweb.common.exception.BusiException;
import com.jweb.common.persistent.model.Expression;
import com.jweb.common.persistent.model.Where;
import com.jweb.common.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

@Service
public class PageService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	BeanService beanService;
	
	public Page getByURI(String uri) {
		String pageURI = uri.replace("-edit", "");
		logger.info("获取page："+pageURI);
		Page page = null;
		if(!uri.startsWith("/page-edit")) {
			try {
				List<PageBean> pages = beanService.list(PageBean.class,
						Where.create("uri",Expression.eq, pageURI.substring(1)), null);
				if(pages!=null && pages.size()>0) {
					page = JsonUtil.jsonToBean(pages.get(0).getPage(), Page.class);
					page.setId(pages.get(0).getId());
				}
			} catch (BusiException e1) {}
		}else {
			InputStream stream = getClass().getClassLoader().getResourceAsStream("static/plugins/page/js/page-edit.json");
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String line=null;
			StringBuffer json = new StringBuffer();
			try {
				while((line=br.readLine())!=null) {
					json.append(line);
				}
				System.out.print(json);
				page = JsonUtil.jsonToBean(json.toString(), Page.class);
			} catch (IOException e) {}finally {
				try {
					br.close();
				} catch (IOException e) {}
			}
		}
		if(page==null){
			page = new Page();
		}
		page.setUri(pageURI);
		return page;
	}
	
}
