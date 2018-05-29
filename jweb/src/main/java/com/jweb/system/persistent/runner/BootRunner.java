package com.jweb.system.persistent.runner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.jweb.system.util.JsonUtil;
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
public class BootRunner implements CommandLineRunner{
	@Autowired
	BeanService beanService;
	@Override
	public void run(String... args){
		System.out.println(">>>>>>>>>>>>>>>>>创建表<<<<<<<<<<<<<<<");
		beanService.createBeanTables();
		System.out.println(">>>>>>>>>>>>>>>初始化数据<<<<<<<<<<<<<<");
		try {
			//先判断菜单表是否为空，空则说明是空库，需要进行初始化
			List<Menu> oldMenus = beanService.list(Menu.class, null, null);
			if(null==oldMenus || oldMenus.size()<=0) {
				String menusJson = readMenus();
				//如果菜单备份文件不存在，新建用户管理、功能管理
				if(menusJson==null || "".equals(menusJson)) {
					List<Menu> newMenus = new ArrayList<>();
					Menu sysMenu = new Menu();
					sysMenu.setId("sys0");
					sysMenu.setName("系统管理");
					sysMenu.setParentId("-1");
					newMenus.add(sysMenu);
					
					Menu funcMenu = new Menu();
					funcMenu.setId("sys01");
					funcMenu.setName("功能管理");
					funcMenu.setParentId("sys0");
					newMenus.add(funcMenu);
					
					Menu userMenu = new Menu();
					userMenu.setId("sys02");
					userMenu.setName("用户管理");
					userMenu.setParentId("sys0");
					newMenus.add(userMenu);
					
				}
				@SuppressWarnings("unchecked")
				List<Map<String,Object>> menuObjs = JsonUtil.jsonToBean(menusJson, new ArrayList<Map<String,Object>>().getClass());
				List<Menu> menus = new ArrayList<Menu>(100);
				for(Map<String,Object> menuObj:menuObjs) {
					menus.add(JsonUtil.jsonToBean(JsonUtil.beanToJson(menuObj),Menu.class));
				}
				for(Object menu:menus) {
					try {
						beanService.createBean(menu);
					}catch(Exception e) {}
				}
				//将所有菜单分配给管理员角色
				for(Menu menu:menus) {
					RoleMenu roleMenu = new RoleMenu();
					roleMenu.setId(UUID.randomUUID().toString().replace("-", ""));
					roleMenu.setRoleId("admin");
					roleMenu.setMenuId(menu.getId());
					try {
						beanService.createBean(roleMenu);
					}catch(Exception e) {}
				}
			}
		} catch (Exception e) {e.printStackTrace();}
		/*System.out.println(">>>>>>>>>>>>>>>备份菜单数据<<<<<<<<<<<<<");
		try{
			String menusJson = JsonUtil.beanToJson(beanService.list(Menu.class, null, null));
			FileWriter writer = new FileWriter("menus.json");
		    writer.write(menusJson);
		    writer.flush();
		    writer.close();
		}catch(Exception e) {}*/
		
	}
	private String readMenus() {
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader bReader = new BufferedReader(new FileReader(new File("menus.json")));
			String s = "";
			while ((s =bReader.readLine()) != null) {
				sb.append(s + " ");
			}
			bReader.close();
		}catch(Exception e) {}
		return sb.toString();
	}
}
