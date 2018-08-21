package com.jweb.busi.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 
* @ClassName: CenterController
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author liyz liyingzhou@bzhcloud.com 
* @date 2017年8月4日 上午11:56:39 
*
 */
@Controller
@RequestMapping("center")
public class CenterController {
	@RequestMapping("/center-list")
    public String centerList() {
    	return "busi/center/center-list";
    }
}
