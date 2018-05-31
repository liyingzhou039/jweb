package com.jweb.system.rest;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.jweb.system.exception.BusiException;
import com.jweb.system.persistent.BeanPool;
import com.jweb.system.persistent.model.Condition;
import com.jweb.system.persistent.model.Where;
import com.jweb.system.util.Result;


import com.jweb.system.service.BeanService;
import com.jweb.system.service.ValidatorService;
import com.jweb.system.util.JsonUtil;
 /** 
 * @ClassName: BeanRestController 
 * @Description: TODO
 * @author: liyz
 * @date: 2018年1月31日 下午4:19:09  
 */
@RestController
@RequestMapping("/rest/node")
public class NodeRestController {
	@RequestMapping(value = "/menus",method = RequestMethod.GET)
    @ResponseBody
    public Object menus(
    		@RequestParam(defaultValue = "0") int pId) throws BusiException {
        List<Map<String,Object>> menus = new ArrayList<>();
        for(int i=0;i<5;i++) {
        	Map<String,Object> menu = new HashMap<>();
        	menu.put("id", i);
        	menu.put("name", "菜单"+i);
        	menu.put("pId", pId);
        	menus.add(menu);
        }
        return menus;
    }
}