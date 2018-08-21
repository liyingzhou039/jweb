package com.jweb.sys.rest.page;

import com.jweb.common.service.BeanService;
import com.jweb.sys.entity.page.PageBean;
import com.jweb.common.exception.BusiException;
import com.jweb.common.persistent.model.Expression;
import com.jweb.common.persistent.model.Where;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
			pages = beanService.list(PageBean.class, Where.create("uri",Expression.eq, uri), null);
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