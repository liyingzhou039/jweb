package com.jweb.busi.service.security;

import com.jweb.busi.entity.security.SecurityGroup;
import com.jweb.busi.entity.security.SecurityGroupRule;
import com.jweb.busi.service.sync.SyncBeanService;
import com.jweb.common.exception.BusiException;
import com.jweb.common.persistent.model.Expression;
import com.jweb.common.persistent.model.Where;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SecurityGroupService {
    @Autowired
    SyncBeanService syncBeanService;
    @Transactional
    public SecurityGroup create(SecurityGroup securityGroup) throws BusiException{
        SecurityGroup group = null;
        try{
            //本地创建
            group = syncBeanService.create(securityGroup);
            //默认添加两条安全组规则
            SecurityGroupRule anyIPv4 = new SecurityGroupRule();
            anyIPv4.setDirection("egress");
            anyIPv4.setProtocol(null);
            anyIPv4.setEtherType("IPv4");
            anyIPv4.setPortRangeMax(null);
            anyIPv4.setPortRangeMin(null);
            anyIPv4.setRemoteIpPrefix(null);
            anyIPv4.setSecurityGroupId(group.getId());
            SecurityGroupRule anyIPv6 = new SecurityGroupRule();
            anyIPv6.setDirection("egress");
            anyIPv6.setProtocol(null);
            anyIPv6.setEtherType("IPv6");
            anyIPv6.setPortRangeMax(null);
            anyIPv6.setPortRangeMin(null);
            anyIPv6.setRemoteIpPrefix(null);
            anyIPv6.setSecurityGroupId(group.getId());
            syncBeanService.create(anyIPv4);
            syncBeanService.create(anyIPv6);
        }catch (Exception e){
            throw new BusiException(e.getMessage());
        }
        return group;
    }
    @Transactional
    public void remove(String securityGroupId) throws BusiException{
        try{
            syncBeanService.remove(SecurityGroup.class,securityGroupId);
            syncBeanService.remove(SecurityGroupRule.class,
                    Where.create("securityGroupId",Expression.eq,securityGroupId));
        }catch (Exception e){
            throw new BusiException(e.getMessage());
        }
    }
}
