package com.jweb.system.service;


import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jweb.system.dto.PowerDto;
import com.jweb.system.entity.Element;
import com.jweb.system.entity.Menu;
import com.jweb.system.entity.RoleElement;
import com.jweb.system.entity.RoleMenu;
import com.jweb.system.entity.User;
import com.jweb.system.entity.UserRole;
import com.jweb.system.exception.BusiException;
import com.jweb.system.persistent.model.Expression;
import com.jweb.system.persistent.model.Where;
import com.jweb.system.persistent.service.BeanService;
import com.jweb.system.session.Session;
import com.jweb.system.util.MD5Util;

@Service
public class UserService{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	BeanService beanService;
	public String login(String username,String password) throws BusiException {
		String token = null;
		try {
			List<User> users = beanService.list(User.class, 
					Where.create()
					.and("username", Expression.eq, username)
					.and("password", Expression.eq, MD5Util.encode(password))
					, null);
			User user = null;
			if(null!=users&&users.size()>0) {
				user = users.get(0);
			}else {
				throw new BusiException("用户名或密码错误");
			}
			token = Session.setCurrentUser(user);
		}catch(Exception e) {
			token = null;
			throw new BusiException("失败："+e.getMessage());
		}
		return token;
	}
	
	public void logout() {
		Session.removeCurrentUser();
	}
	public PowerDto getUserPowers(String id){
		PowerDto power = new PowerDto();
		try {
			User user = beanService.getById(User.class, id);
			
			if(null!=user) {
				
				List<UserRole> userRoles = beanService.list(UserRole.class, Where.create().and("userId", Expression.eq, id), null);
				List<String> roleIds = new ArrayList<>();
				for(UserRole userRole:userRoles) {
					roleIds.add(userRole.getRoleId());
				}
				List<RoleMenu> roleMenus = beanService.list(RoleMenu.class, Where.create().and("roleId", Expression.in, roleIds), null);
				List<RoleElement> roleElements = beanService.list(RoleElement.class, Where.create().and("roleId", Expression.in, roleIds), null);
				//查询出所有菜单
				List<String> menuIds = new ArrayList<>();
				for(RoleMenu roleMenu:roleMenus) {
					menuIds.add(roleMenu.getMenuId());
				}
				List<Menu> menus = beanService.list(Menu.class, Where.create().and("id", Expression.in, menuIds), null);
				//查询出所有资源
				List<String> elementIds =new ArrayList<>();
				for(RoleElement roleElement:roleElements) {
					elementIds.add(roleElement.getElementId());
				}
				List<Element> elements = beanService.list(Element.class, Where.create().and("id",Expression.in, elementIds), null);
					
				power.setMenus(menus);
				power.setElements(elements);
			}
		}catch(Exception e) {
			logger.error(e.getMessage());
		}
		if(null==power.getMenus()) {
			power.setMenus(new ArrayList<Menu>());
		}
		if(null==power.getElements()) {
			power.setElements(new ArrayList<Element>());
		}
		return power;
	}
}
