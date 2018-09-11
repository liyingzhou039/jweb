package com.jweb.sys.rest.identity;

import com.jweb.common.exception.BusiException;
import com.jweb.common.persistent.model.Expression;
import com.jweb.common.persistent.model.Where;
import com.jweb.common.service.BeanService;
import com.jweb.common.session.Session;
import com.jweb.common.util.MD5Util;
import com.jweb.common.util.Result;
import com.jweb.sys.dto.identity.LoginUser;
import com.jweb.sys.entity.identity.Role;
import com.jweb.sys.entity.identity.User;
import com.jweb.sys.entity.identity.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liyz liyingzhou@bzhcloud.com
 * @ClassName: ProjectRestController
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @date 2017年8月4日 上午11:56:39
 */
@Controller
@RequestMapping("rest/identity/login")
public class LoginRestController {
    @Autowired
    private BeanService beanService;
   
    @RequestMapping(value = "/in",method = RequestMethod.POST)
    @ResponseBody
    public Object in(@RequestParam String username, @RequestParam String password){
    	Result<LoginUser> r = new Result<>();
	  	try {
	  		String md5Password = MD5Util.encode(password);
	  		List<User> users = beanService.list(User.class,
					Where.create("username",Expression.eq,username)
					.and("password",Expression.eq,md5Password),null);
			if(users==null||users.size()<=0){
				throw new BusiException("用户名或密码错误");
			}
			User user = users.get(0);
			LoginUser loginUser = new LoginUser();
			loginUser.setId(user.getId());
			loginUser.setUsername(user.getUsername());
			//获取角色信息
			List<String> roleIds = new ArrayList<>();
			List<UserRole> userRoles = beanService.list(UserRole.class,
					Where.create("userId",Expression.eq,user.getId()),
					null);
			if(userRoles!=null){
				for(UserRole userRole : userRoles){
					roleIds.add(userRole.getRoleId());
				}
			}
			List<Role> roles = beanService.list(Role.class,
					Where.create("id",Expression.in,roleIds),null);
			loginUser.setRoles(roles);

			String token = Session.setCurrentUser(loginUser);
			loginUser = Session.getCurrentUser();
	  		if(token==null) {
	  			throw new BusiException("登录失败");
	  		}
			loginUser.setToken(token);
	  		r.setOk(true);
	  		r.setEntity(loginUser);
	      	r.setMsg("登录成功");
	  	}catch(BusiException e) {
	  		r.setOk(false);
	  		r.setMsg(e.getMessage());
	  	}
	  	return r;
    }
    @RequestMapping(value = "/out",method = RequestMethod.GET)
    @ResponseBody
    public Object out(){
    	Result<LoginUser> r = new Result<>();
	  	try {
	  		LoginUser user = Session.getCurrentUser();
	  		if(null==user) {
	  			throw new BusiException("退出失败");
	  		}else{
				Session.removeCurrentUser();
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
   
}