package com.jweb.busi.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("security")
public class SecurityController {
    @RequestMapping("/securityGroup-list")
    public String securityGroupList(){
        return "busi/security/securityGroup-list";
    }
    @RequestMapping("/securityGroupRule-list")
    public String securityGroupRuleList(){
        return "busi/security/securityGroupRule-list";
    }
    @RequestMapping("/keypair-list")
    public String keypairList(){
        return "busi/security/keypair-list";
    }
}
