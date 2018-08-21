package com.jweb.busi.rest.security;

import com.jweb.busi.entity.security.SecurityGroup;
import com.jweb.busi.service.center.CenterService;
import com.jweb.busi.service.security.SecurityGroupService;
import com.jweb.busi.service.sync.SyncBeanService;
import com.jweb.common.exception.BusiException;
import com.jweb.common.persistent.model.Expression;
import com.jweb.common.persistent.model.Where;
import com.jweb.common.service.BeanService;
import com.jweb.common.session.Session;
import com.jweb.common.util.Result;
import com.jweb.sys.dto.identity.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/security/securityGroup")
public class SecurityGroupRestController {
    @Autowired
    BeanService beanService;
    @Autowired
    CenterService centerService;
    @Autowired
    SyncBeanService syncBeanService;
    @Autowired
    protected SecurityGroupService  securityGroupService;

    @RequestMapping(value = "",method = RequestMethod.POST)
    @ResponseBody
    public Object create(
            @RequestBody SecurityGroup securityGroup
    ){
        Result<?> r=new Result<>();
        try {
            LoginUser user = Session.getCurrentUser();
            securityGroup.setUserId(user.getId());
            securityGroup.setProjectId(user.getProjectId());
            securityGroupService.create(securityGroup);
            r.setOk(true);
        } catch (Exception e) {
            r.setOk(false);
            r.setMsg(e.getMessage());
        }
        return r;
    }

    @RequestMapping(value = "/{securityGroupId}",method = RequestMethod.DELETE)
    @ResponseBody
    public Object remove(@PathVariable String securityGroupId){
        Result<?> r = new Result<>();
        try {
            securityGroupService.remove(securityGroupId);
            r.setOk(true);
            r.setMsg("删除成功");
        }catch(Exception e) {
            r.setOk(false);
            r.setMsg(e.getMessage());
        }
        return r;
    }

    @RequestMapping(value = "/existsSecurityGroup", method = RequestMethod.GET)
    @ResponseBody
    public Object exists(@RequestParam String name) throws BusiException {
        Result<?> r = new Result<>();
        try {
            LoginUser user = Session.getCurrentUser();
            List beans = beanService.list(SecurityGroup.class,
                    Where.create("name",Expression.eq,name)
                    .and("projectId",Expression.eq,user.getProjectId()), null);
            if (beans == null || beans.size() <= 0) {
                throw new BusiException("不存在");
            }
            r.setOk(true);
        } catch (Exception e) {
            r.setOk(false);
            r.setMsg(e.getMessage());
        }
        return r;
    }
}
