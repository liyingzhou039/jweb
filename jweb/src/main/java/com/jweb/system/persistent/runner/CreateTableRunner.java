package com.jweb.system.persistent.runner;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.jweb.system.entity.Menu;
import com.jweb.system.entity.Role;
import com.jweb.system.entity.RoleMenu;
import com.jweb.system.entity.User;
import com.jweb.system.entity.UserRole;
import com.jweb.system.exception.BusiException;
import com.jweb.system.persistent.service.BeanService;
import com.jweb.system.util.MD5Util;
 /** 
 * @ClassName: CreateTableRunner 
 * @Description: TODO
 * @author: liyz
 * @date: 2018年1月31日 下午4:19:16  
 */
@Component
public class CreateTableRunner implements CommandLineRunner{
	@Autowired
	BeanService beanService;
	@Override
	public void run(String... args){
		System.out.println(">>>>>>>>>>>>>>>创建表<<<<<<<<<<<<<");
		beanService.createBeanTables();
		System.out.println(">>>>>>>>>>>>>>>初始化数据<<<<<<<<<<<<<");
		
		List<Object> beans = new ArrayList<Object>(100);
		//菜单数据
		try {
			beanService.removeBean(Menu.class, "1");
		}catch(Exception e) {}
		Menu system = new Menu();
		system.setId("1");
		system.setParentId("-1");
		system.setName("系统管理");
		system.setIcon("fa fa-cog");
		Menu function = new Menu();
		function.setId("11");
		function.setParentId("1");
		function.setName("功能管理");
		function.setUrl("/system/menu-element-list");
		Menu userManager = new Menu();
		userManager.setId("12");
		userManager.setParentId("1");
		userManager.setName("用户管理");
		userManager.setUrl("/system/user-list");
		beans.add(system);
		beans.add(function);
		beans.add(userManager);
		//初始化用户
		User user = new User();
		user.setId("1");
		user.setUsername("admin");
		user.setPassword(MD5Util.encode("admin"));
		beans.add(user);
		//初始化角色
		Role role = new Role();
		role.setId("1");
		role.setName("管理员");
		beans.add(role);
		//初始化用户角色关系
		UserRole userRole = new UserRole();
		userRole.setId("1");
		userRole.setUserId(user.getId());
		userRole.setRoleId(role.getId());
		beans.add(userRole);
		//初始化角色菜单关系
		RoleMenu roleMenu1 = new RoleMenu();
		roleMenu1.setId("1");
		roleMenu1.setMenuId(system.getId());
		roleMenu1.setRoleId(role.getId());
		beans.add(roleMenu1);
		RoleMenu roleMenu2 = new RoleMenu();
		roleMenu2.setId("2");
		roleMenu2.setMenuId(function.getId());
		roleMenu2.setRoleId(role.getId());
		beans.add(roleMenu2);
		RoleMenu roleMenu3 = new RoleMenu();
		roleMenu3.setId("3");
		roleMenu3.setMenuId(userManager.getId());
		roleMenu3.setRoleId(role.getId());
		beans.add(roleMenu3);
		
		for(Object bean:beans) {
			try {
				beanService.createBean(bean);
			} catch (BusiException e) {}
		}
		
		
		
	}
}
