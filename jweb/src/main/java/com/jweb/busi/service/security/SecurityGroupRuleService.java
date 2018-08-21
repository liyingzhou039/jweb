package com.jweb.busi.service.security;

import com.jweb.busi.entity.center.Center;
import com.jweb.busi.entity.security.SecurityGroupRule;
import com.jweb.busi.service.center.CenterService;
import com.jweb.busi.service.sync.SyncBeanService;
import com.jweb.common.exception.BusiException;
import com.jweb.common.persistent.model.Expression;
import com.jweb.common.persistent.model.Where;
import com.jweb.common.service.BeanService;
import org.openstack4j.api.OSClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SecurityGroupRuleService {
    @Autowired
    BeanService beanService;
    @Autowired
    SyncBeanService syncBeanService;
    @Autowired
    CenterService centerService;
    @Autowired
    SecurityGroupService securityGroupService;

    public SecurityGroupRule create(SecurityGroupRule securityGroupRule) throws BusiException {
        SecurityGroupRule rule = null;
        try{
            //本地创建
            //判断安全组规则是否已经存在
            List<SecurityGroupRule> rules = beanService.list(SecurityGroupRule.class
                    , Where.create("protocol",Expression.eq,securityGroupRule.getProtocol())
                    .and("direction",Expression.eq,securityGroupRule.getDirection())
                            .and("portRangeMin",Expression.eq,securityGroupRule.getPortRangeMin())
                            .and("portRangeMax",Expression.eq,securityGroupRule.getPortRangeMax())
                            .and("remoteIpPrefix",Expression.eq,securityGroupRule.getRemoteIpPrefix())
                            .and("etherType",Expression.eq,securityGroupRule.getEtherType())
                            .and("securityGroupId",Expression.eq,securityGroupRule.getSecurityGroupId())
                    ,null);
            if(rules!=null && rules.size()>0){
                throw new Exception("规则已经存在");
            }
            rule = syncBeanService.create(securityGroupRule);
        }catch (Exception e){
            throw new BusiException(e.getMessage());
        }
        return rule;
    }
    public void remove(String securityGroupRuleId) throws BusiException{
        try{
            syncBeanService.remove(SecurityGroupRule.class,securityGroupRuleId);
        }catch (Exception e){
            throw new BusiException(e.getMessage());
        }
    }
}
