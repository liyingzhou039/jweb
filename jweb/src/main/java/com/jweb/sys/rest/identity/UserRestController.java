package com.jweb.sys.rest.identity;

import com.jweb.common.persistent.model.Expression;
import com.jweb.common.persistent.model.Where;
import com.jweb.common.service.BeanService;
import com.jweb.common.session.Session;
import com.jweb.common.util.JsonUtil;
import com.jweb.common.util.MD5Util;
import com.jweb.common.util.Result;
import com.jweb.sys.dto.identity.LoginUser;
import com.jweb.sys.entity.identity.*;
import com.jweb.sys.service.identity.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
* @Description: TODO
* @author: liyz
* @date: 2018年1月31日 下午4:19:09
*/
@RestController
@RequestMapping("/rest/identity/user")
public class UserRestController {
   @Autowired
   protected UserService userService;
   @Autowired
   BeanService beanService;

   @RequestMapping(value = "",method = RequestMethod.POST)
   @ResponseBody
   public Object create(
           @RequestParam String roleIdsString
           ,@RequestParam(defaultValue = "{}") String userJson){
       Result<?> r=new Result<>();
       try {
           User user = JsonUtil.jsonToBean(userJson, User.class);
           String [] roleIds = roleIdsString.split(",");
           user.setPassword(MD5Util.encode(user.getPassword()));
           userService.create(user,roleIds);
           r.setOk(true);
       } catch (Exception e) {
           r.setOk(false);
           r.setMsg(e.getMessage());
       }
       return r;
   }

    @RequestMapping(value = "",method = RequestMethod.PUT)
    @ResponseBody
    public Object update(
            @RequestParam String roleIdsString
            ,@RequestParam(defaultValue = "{}") String userJson){
        Result<?> r=new Result<>();
        try {
            User user = JsonUtil.jsonToBean(userJson, User.class);
            String [] roleIds = roleIdsString.split(",");
            userService.update(user,roleIds);
            r.setOk(true);
        } catch (Exception e) {
            r.setOk(false);
            r.setMsg(e.getMessage());
        }
        return r;
    }

    @RequestMapping(value = "/{userId}",method = RequestMethod.DELETE)
    @ResponseBody
    public Object remove(@PathVariable String userId){
        Result<User> r = new Result<>();
        try {
            userService.remove(userId);
            r.setOk(true);
            r.setMsg("删除成功");
        }catch(Exception e) {
            r.setOk(false);
            r.setMsg(e.getMessage());
        }
        return r;
    }

    @RequestMapping(value = "/menus",method = RequestMethod.GET)
    @ResponseBody
    public Object userMenus(){
        LoginUser user = Session.getCurrentUser();
        List<UserRole>  userRoles = beanService.list(UserRole.class,
                Where.create("userId",Expression.eq,user.getId()),null);
        List<String> roleIds = new ArrayList<>();
        if(null!=userRoles){
            for(UserRole userRole:userRoles){
                roleIds.add(userRole.getRoleId());
            }
        }

        List<RoleMenu> roleMenus = beanService.list(RoleMenu.class,
                Where.create("roleId",Expression.in,roleIds),null);

        List<String> menusIds = new ArrayList<>();
        if(null!=roleMenus){
            for(RoleMenu roleMenu:roleMenus){
                menusIds.add(roleMenu.getMenuId());
            }
        }

        List<Menu> menus = beanService.list(Menu.class,
                Where.create("code",Expression.in,menusIds),null);

        return menus;
    }

}