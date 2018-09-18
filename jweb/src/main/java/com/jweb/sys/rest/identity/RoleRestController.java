package com.jweb.sys.rest.identity;

import com.jweb.common.persistent.model.Expression;
import com.jweb.common.persistent.model.Where;
import com.jweb.common.service.BeanService;
import com.jweb.common.util.Result;
import com.jweb.sys.entity.identity.*;
import com.jweb.sys.service.identity.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @Description: TODO
* @author: liyz
* @date: 2018年1月31日 下午4:19:09
*/
@RestController
@RequestMapping("/rest/identity/role")
public class RoleRestController {
   @Autowired
   BeanService beanService;
   @Autowired
   RoleService roleService;

   @RequestMapping(value = "menu-element-list",method = RequestMethod.GET)
   @ResponseBody
   public Object menuAndElementList(@RequestParam String roleId){

       List<Menu> menus = beanService.list(Menu.class);
       List<Element> elements = beanService.list(Element.class);
       List<RoleMenu> roleMenus = beanService.list(RoleMenu.class,
               Where.create("roleId",Expression.eq,roleId));
       List<RoleElement> roleElements = beanService.list(RoleElement.class,
               Where.create("roleId",Expression.eq,roleId));

       Map<String,List> data = new HashMap<>();
       data.put("menus",menus);
       data.put("elements",elements);
       data.put("roleMenus",roleMenus);
       data.put("roleElements",roleElements);

       return data;
   }
    @RequestMapping(value = "/updatePower/{roleId}",method = RequestMethod.POST)
    @ResponseBody
    public Object updatePower(
            @PathVariable String roleId,
            @RequestParam(defaultValue="") String menuIds,
            @RequestParam(defaultValue="") String elementIds
    ){
        Result<Object> r = new Result<Object>();
        try {
            beanService.remove(RoleMenu.class,Where.create("roleId", Expression.eq, roleId));
            beanService.remove(RoleElement.class,Where.create("roleId", Expression.eq, roleId));
            final String comma =  ",";
            for(String menuId:menuIds.split(comma)) {
                if(menuId==null||"".equals(menuId)) {
                    continue;
                }
                RoleMenu menu = new RoleMenu();
                menu.setMenuId(menuId);
                menu.setRoleId(roleId);
                beanService.create(menu);
            }
            final String commas =  ",";
            for(String elementId:elementIds.split(commas)) {
                if(elementId==null||"".equals(elementId)) {
                    continue;
                }
                RoleElement element = new RoleElement();
                element.setElementId(elementId);
                element.setRoleId(roleId);
                beanService.create(element);
            }
            r.setOk(true);
        } catch (Exception e) {
            r.setOk(false);
            r.setMsg(e.getMessage());
        }
        return r;
    }
    @RequestMapping(value = "/{roleId}",method = RequestMethod.DELETE)
    @ResponseBody
    public Object remove(@PathVariable String roleId){
        Result<User> r = new Result<>();
        try {
            roleService.remove(roleId);
            r.setOk(true);
            r.setMsg("删除成功");
        }catch(Exception e) {
            r.setOk(false);
            r.setMsg(e.getMessage());
        }
        return r;
    }
}