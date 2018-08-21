package com.jweb.busi.entity.security;

import com.jweb.busi.entity.center.Center;
import com.jweb.busi.entity.sync.LocalCenterRelation;
import com.jweb.busi.service.center.CenterService;
import com.jweb.busi.service.sync.SyncBean;
import com.jweb.common.exception.BusiException;
import com.jweb.common.persistent.annotation.Bean;
import com.jweb.common.persistent.annotation.Field;
import com.jweb.common.persistent.model.Expression;
import com.jweb.common.persistent.model.Where;
import com.jweb.common.service.BeanService;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.network.builder.NetSecurityGroupRuleBuilder;

import java.util.List;

@Bean(table="jweb_busi_security_group_rule",name="安全组规则")
public class SecurityGroupRule extends SyncBean {
    @Field(name="ID")
    private String id;
    @Field(name="协议")
    private String protocol;
    @Field(name="方向",required = true)
    private String direction;
    @Field(name="最小端口")
    private Integer portRangeMin;
    @Field(name="最大端口")
    private Integer portRangeMax;
    @Field(name="允许的IP前缀")
    private String remoteIpPrefix;
    @Field(name="安全组ID",required = true)
    private String securityGroupId;
    @Field(name="以太网类型",required = true)
    private String etherType;

    public String getId() {
        return id;
    }

    @Override
    protected String createCenterResource(Center center, BeanService beanService, CenterService centerService) throws BusiException {
        String centerRuleId = null;
        try {
            String centerSecurityGroupId = null;
            List<LocalCenterRelation> lcs = beanService.list(LocalCenterRelation.class,
                    Where.create("centerCode",Expression.eq,center.getCode())
                    .and("beanName",Expression.eq,SecurityGroup.class.getSimpleName())
                    .and("localResourceId",Expression.eq,this.getSecurityGroupId())
                    ,null);
            if(lcs!=null && lcs.size()>0){
                centerSecurityGroupId = lcs.get(0).getCenterResourceId();
            }else{
                throw new Exception("安全组ID获取失败");
            }
            OSClient.OSClientV3 os = centerService.os(center);
            NetSecurityGroupRuleBuilder nb = Builders.securityGroupRule();
            if(null!=this.getProtocol()) nb.protocol(this.getProtocol());
            if(null!=this.getDirection()) nb.direction(this.getDirection());
            if(null!=this.getPortRangeMin()) nb.portRangeMin(this.getPortRangeMin());
            if(null!=this.getPortRangeMax()) nb.portRangeMax(this.getPortRangeMax());
            if(null!=this.getRemoteIpPrefix()) nb.remoteIpPrefix(this.getRemoteIpPrefix());
            nb.securityGroupId(centerSecurityGroupId)
                    .ethertype(this.getEtherType());
            org.openstack4j.model.network.SecurityGroupRule centerRule = os.networking().securityrule().create(nb.build());
            centerRuleId = centerRule.getId();
        }catch (Exception e){
            e.getMessage();
            throw new BusiException("中心["+center.getName()+"]同步新增["+this.getId()+"]失败:"+e.getMessage());
        }
        return centerRuleId;
    }

    @Override
    protected void updateCenterResource(Center center, BeanService beanService, CenterService centerService) throws BusiException {
        //无需操作
    }

    @Override
    protected void removeCenterResource(Center center, BeanService beanService, CenterService centerService) throws BusiException {
        try {
            OSClient.OSClientV3 os = centerService.os(center);
            List<LocalCenterRelation> removeLcs = beanService.list(
                    LocalCenterRelation.class
                    , Where.create("centerCode",Expression.eq,center.getCode())
                            .and("beanName",Expression.eq,this.getClass().getSimpleName())
                            .and("localResourceId",Expression.eq,null)
                            .and("synced",Expression.eq,false)
                    , null);
            if(null!=removeLcs&&removeLcs.size()>0){
                for(LocalCenterRelation removeLc : removeLcs){
                    try{
                        os.networking().securityrule().delete(removeLc.getCenterResourceId());
                        beanService.remove(removeLc.getClass(),removeLc.getId());
                    }catch(Exception e){e.printStackTrace();}
                }
            }
        }catch (Exception e){
            throw new BusiException("中心["+center.getName()+"]同步删除["+this.getClass().getSimpleName()+"]失败:"+e.getMessage());
        }
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public Integer getPortRangeMin() {
        return portRangeMin;
    }

    public void setPortRangeMin(Integer portRangeMin) {
        this.portRangeMin = portRangeMin;
    }

    public Integer getPortRangeMax() {
        return portRangeMax;
    }

    public void setPortRangeMax(Integer portRangeMax) {
        this.portRangeMax = portRangeMax;
    }

    public String getRemoteIpPrefix() {
        return remoteIpPrefix;
    }

    public void setRemoteIpPrefix(String remoteIpPrefix) {
        this.remoteIpPrefix = remoteIpPrefix;
    }

    public String getSecurityGroupId() {
        return securityGroupId;
    }

    public void setSecurityGroupId(String securityGroupId) {
        this.securityGroupId = securityGroupId;
    }

    public String getEtherType() {
        return etherType;
    }

    public void setEtherType(String etherType) {
        this.etherType = etherType;
    }

}
