package com.jweb.system.persistent.rest;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.jweb.system.exception.BusiException;
import com.jweb.system.persistent.BeanPool;
import com.jweb.system.util.Result;
import com.jweb.system.persistent.service.BeanService;
import com.jweb.system.persistent.service.ValidatorService;
import com.jweb.system.util.JsonUtil;
 /** 
 * @ClassName: BeanRestController 
 * @Description: TODO
 * @author: liyz
 * @date: 2018年1月31日 下午4:19:09  
 */
@RestController
@RequestMapping("/rest/bean")
public class BeanRestController {
	@Autowired
	protected BeanService beanService;
	@Autowired
	protected ValidatorService validatorService;
	
	
	private String transCondition(String condition) {
		if(null==condition||condition.trim().equals("")) {
			return null;
		}
		condition = condition.replace(":eq", " = ");
		condition = condition.replace(":lt", " < ");
		condition = condition.replace(":gt", " > ");
		condition = condition.replace(":li", " like ");
		condition = condition.replace(":pr", "%");
		condition = condition.replace(":qt", "'");
		return " "+condition;
	}
	private String transOrder(String order) {
		if(null==order||order.trim().equals("")) {
			return null;
		}
		return " "+order;
	}
	
	@RequestMapping(value = "/list{beanName}Pager",method = RequestMethod.GET)
    @ResponseBody
    public Object listPager(
    		@PathVariable String beanName,
    		@RequestParam(defaultValue = "") String condition,
    		@RequestParam(defaultValue = "") String order,
    		@RequestParam(defaultValue = "0") int offset, 
    		@RequestParam(defaultValue = "10") int limit) throws BusiException {
        Class<?> beanClass = BeanPool.getBeanClassBySimpleName(beanName);
		return beanService.getPager(beanClass, offset, limit, transCondition(condition),transOrder(order));
    }
	@RequestMapping(value = "/list{beanName}",method = RequestMethod.GET)
    @ResponseBody
    public Object list(
    		@PathVariable String beanName,
    		@RequestParam(defaultValue = "") String condition,
    		@RequestParam(defaultValue = "") String order
    		) throws BusiException {
        Class<?> beanClass = BeanPool.getBeanClassBySimpleName(beanName);
		return beanService.list(beanClass,transCondition(condition),transOrder(order));
    }
	@RequestMapping(value = "/getById{beanName}/{id}",method = RequestMethod.GET)
    @ResponseBody
    public Object getById(
    		@PathVariable String beanName,
    		@PathVariable String id
    		) throws BusiException {
        Class<?> beanClass = BeanPool.getBeanClassBySimpleName(beanName);
		return beanService.getById(beanClass,id);
    }
	@RequestMapping(value = "/create{beanName}",method = RequestMethod.POST)
    @ResponseBody
    public Object create(@PathVariable String beanName,@RequestParam(defaultValue = "{}") String beanJson) throws BusiException {
		Result<?> r=new Result<>();
		try {
			Class<?> beanClass = BeanPool.getBeanClassBySimpleName(beanName);
			Object bean = JsonUtil.jsonToBean(beanJson, beanClass);
			Method method=beanClass.getDeclaredMethod("setId", String.class);
			method.invoke(bean, UUID.randomUUID().toString().replace("-", ""));
			//加入后端验证
			validatorService.check(bean);
			beanService.createBean(bean);
			r.setOk(true);
		} catch (Exception e) {
			r.setOk(false);
			r.setMsg(e.getMessage());
		}
        return r;
    }
	@RequestMapping(value = "/createBatch{beanName}",method = RequestMethod.POST)
    @ResponseBody
    public <T> Result<T> createBatch(@PathVariable String beanName,@RequestParam(defaultValue = "[]") String beansJson) throws BusiException {
		Result<T> r=new Result<T>();
		try {
			@SuppressWarnings("unchecked")
			Class<T> beanClass =(Class<T>) BeanPool.getBeanClassBySimpleName(beanName);
			List<T> beans = new ArrayList<T>();
			
			@SuppressWarnings("unchecked")
			List<Object> objects = (List<Object>) JsonUtil.jsonToBean(beansJson,new ArrayList<Object>().getClass());
			for(Object object:objects) {
				beans.add((T)JsonUtil.jsonToBean(JsonUtil.beanToJson(object), beanClass));
			}
			
			for(T bean:beans) {
				Method method=beanClass.getDeclaredMethod("setId", String.class);
				method.invoke(bean, UUID.randomUUID().toString().replace("-", ""));
			}
			beanService.createBeans(beans);
			r.setOk(true);
		} catch (Exception e) {
			r.setOk(false);
			r.setMsg(e.getMessage());
		}
        return r;
    }
	@RequestMapping(value = "/update{beanName}",method = RequestMethod.PUT)
    @ResponseBody
    public Object update(@PathVariable String beanName,@RequestParam(defaultValue = "{}") String beanJson) throws BusiException {
		Result<?> r=new Result<>();
		try {
			Class<?> beanClass = BeanPool.getBeanClassBySimpleName(beanName);
			Object bean = JsonUtil.jsonToBean(beanJson, beanClass);
			//加入后端验证
			validatorService.check(bean);
			beanService.updateBean(bean);
			r.setOk(true);
		} catch (Exception e) {
			r.setOk(false);
			r.setMsg(e.getMessage());
		}
        return r;
    }
    @RequestMapping(value = "/remove{beanName}/{id}",method = RequestMethod.DELETE)
    @ResponseBody
    public Object remove(@PathVariable String beanName,@PathVariable String id) throws BusiException {
    	Class<?> beanClass = BeanPool.getBeanClassBySimpleName(beanName);
		Result<?> r=new Result<>();
		try {
			beanService.removeBean(beanClass,id);
			r.setOk(true);
		} catch (Exception e) {
			r.setOk(false);
			r.setMsg(e.getMessage());
		}
		return r;
    }
    @RequestMapping(value = "/removeBatch{beanName}/{ids}",method = RequestMethod.DELETE)
    @ResponseBody
    public Object removeBatch(@PathVariable String beanName,@PathVariable String ids) throws BusiException {
    	Class<?> beanClass = BeanPool.getBeanClassBySimpleName(beanName);
		Result<?> r=new Result<>();
		try { 
			beanService.removeBeans(beanClass,ids.split(","));
			r.setOk(true);
		} catch (Exception e) {
			r.setOk(false);
			r.setMsg(e.getMessage());
		}
		return r;
    }
}