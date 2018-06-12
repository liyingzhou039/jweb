package com.jweb.system.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.jweb.system.entity.PageBean;
import com.jweb.system.exception.BusiException;
import com.jweb.system.persistent.model.Expression;
import com.jweb.system.persistent.model.Where;
import com.jweb.system.service.BeanService;

@RestController
@RequestMapping("/rest/page")
public class PageRestController {
	@Autowired
	BeanService beanService;
	@RequestMapping(value = "/getByURI",method = RequestMethod.GET)
    @ResponseBody
    public Object getByURI(
    		@RequestParam String uri){
		List<PageBean> pages =null;
		if(uri!=null && uri.startsWith("/")) {
			uri = uri.substring(1);
		}
		try {
			pages = beanService.list(PageBean.class, Where.create().and("uri",Expression.eq, uri), null);
		} catch (BusiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(null==pages || pages.size()<=0) {
			return "{}";
		}
		return	pages.get(0);
    }
}