package com.jweb.system.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.jweb.system.entity.User;
import com.jweb.system.exception.BusiException;
import com.jweb.system.util.Result;
import com.jweb.system.service.UserService;
import com.jweb.system.session.Session;
 /** 
 * @ClassName: BeanRestController 
 * @Description: TODO
 * @author: liyz
 * @date: 2018年1月31日 下午4:19:09  
 */
@RestController
@RequestMapping("rest/system")
public class SystemRestController {
	@Autowired
	protected UserService userService;
	
	@RequestMapping(value = "/login/in",method = RequestMethod.POST)
    @ResponseBody
    public Object in(@RequestParam String username,@RequestParam String password){
    	Result<User> r = new Result<User>();
	  	try {
	  		String token = userService.login(username, password);
	  		User user = Session.getCurrentUser();
	  		if(token==null) {
	  			throw new BusiException("登录失败");
	  		}
	  		user.setToken(token);
	  		r.setOk(true);
	  		r.setEntity(user);
	      	r.setMsg("登录成功");
	  	}catch(BusiException e) {
	  		r.setOk(false);
	  		r.setMsg(e.getMessage());
	  	}
	  	return r;
    }
    @RequestMapping(value = "/login/out",method = RequestMethod.GET)
    @ResponseBody
    public Object out(){
    	Result<User> r = new Result<User>();
	  	try {
	  		User user = Session.getCurrentUser();
	  		userService.logout();
	  		if(null==user) {
	  			throw new BusiException("退出失败");
	  		}
	  		r.setOk(true);
	  		r.setEntity(user);
	      	r.setMsg("退出成功");
	  	}catch(BusiException e) {
	  		r.setOk(false);
	  		r.setMsg(e.getMessage());
	  	}
	  	return r;
    }
    @RequestMapping(value = "/power/getUserPowers", method = RequestMethod.GET)
    @ResponseBody
    public Object getUserPowers() {
    	String userId = Session.getCurrentUser().getId();
		return userService.getUserPowers(userId);
    }
}