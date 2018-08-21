package com.jweb.busi.rest.security;

import com.jweb.busi.entity.security.SecurityGroupRule;
import com.jweb.busi.service.security.SecurityGroupRuleService;
import com.jweb.common.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/security/securityGroupRule")
public class SecurityGroupRuleRestController {
    @Autowired
    protected SecurityGroupRuleService securityGroupRuleService;

    @RequestMapping(value = "",method = RequestMethod.POST)
    @ResponseBody
    public Object create(
            @RequestBody SecurityGroupRule securityGroupRule
    ){
        Result<?> r=new Result<>();
        try {
            securityGroupRuleService.create(securityGroupRule);
            r.setOk(true);
        } catch (Exception e) {
            r.setOk(false);
            r.setMsg(e.getMessage());
        }
        return r;
    }

    @RequestMapping(value = "/{securityGroupRuleId}",method = RequestMethod.DELETE)
    @ResponseBody
    public Object remove(@PathVariable String securityGroupRuleId){
        Result<?> r = new Result<>();
        try {
            securityGroupRuleService.remove(securityGroupRuleId);
            r.setOk(true);
            r.setMsg("删除成功");
        }catch(Exception e) {
            r.setOk(false);
            r.setMsg(e.getMessage());
        }
        return r;
    }

}
