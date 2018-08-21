var securityGroupRuleTable =null;
$(function(){
    securityGroupRuleTable = new LchTable({
        url:'../rest/bean/SecurityGroupRulePager',
        columns:[
            {
                field: 'direction',
                title: '方向',
                formatter:function(direction){
                    return {
                        'ingress':'入方向',
                        'egress':'出方向'
                    }[direction];
                }
            }, {
                field: 'etherType',
                title: '网络类型'
            }, {
                field: 'protocol',
                title: 'IP协议',
                formatter:function(protocol){
                    return {
                        'any':'全部',
                        'tcp':'TCP',
                        'udp':'UDP',
                        'icmp':'ICMP',
                        undefined:'任何'
                    }[protocol];
                }
            }, {
                field: 'portRangeMin',
                title: '端口范围',
                formatter:function(value,row){
                    if(!value) return '任何';
                    if (row.portRangeMin == row.portRangeMax){
                        return row.portRangeMin;
                    }else{
                        return row.portRangeMin +'-'+row.portRangeMax;
                    }
                }
            }, {
                field: 'remoteIpPrefix',
                title: '地址段',
                formatter:function(remoteIpPrefix,row){
                    if(!remoteIpPrefix){
                        return {
                            'IPv4':'0.0.0.0/0',
                            'IPv6':'::/0'
                        }[row.etherType];
                    }else{
                        return remoteIpPrefix;
                    }
                }
            }, {
                field: 'id',
                title: '操作',
                class: 'control-box',
                formatter:function(value,rule){
                    var warning = rule.direction+' '+rule.etherType+' '+rule.portRangeMin+'-'
                        +rule.portRangeMax+'/'+rule.protocol+' from '+rule.remoteIpPrefix;
                    return util.buttons.render([
                        {
                            icon:"fa fa-trash",
                            tips:"删除规则",
                            click:"removeSecurityGroupRule('"+rule.id+"','"+warning+"')"
                        }]);
                }
            }
        ],
        params:function () {

            return {
                conditions:JSON.stringify([
                {
                	relation:"and",
                    expression:"eq",
                    name:"securityGroupId",
                    value:util.getUrlParam('securityGroupId')
                }])
            };
        }
    });

    $("#add-btn").click(function(){
        addSecurityGroupRule();
    });
});
function addSecurityGroupRule(){
    util.deserialize($("#dialog"),{
    etherType:'IPv4',
    direction:'ingress',
    remoteIpPrefix:'0.0.0.0/0'});
    //初始化协议下拉框
    var $protocol = $("#protocol");
    var h='';
    for(var i=0;i<ruleTemplates.length;i++){
        h+='<option index="'+i+'" value="'+ruleTemplates[i].protocol+'">'+ruleTemplates[i].name+'</option>';
    }
    $protocol.empty().html(h);
    $protocol.unbind().change(function(){
        var index = $(this).find("OPTION:selected").attr("index");
        var ruleTemplate = ruleTemplates[index];
        if(ruleTemplate.direction) $("#dialog").find("SELECT[name='direction']").val(ruleTemplate.direction);
        if(ruleTemplate.portRangeMin) $("#dialog").find("INPUT[name='portRangeMin']").val(ruleTemplate.portRangeMin);
        if(ruleTemplate.portRangeMax) $("#dialog").find("INPUT[name='portRangeMax']").val(ruleTemplate.portRangeMax);
    }).trigger('change');

    var d = Dialog.open({
        content:'#dialog',
        title:'添加安全组规则',
        buttons:[
            {
                text:'完成',
                click:function(){
                    if(ValidatorUtil.check(d.$dialog)){
                        var securityGroupRule = util.serialize(d.$dialog);
                        var index = $("#protocol").find("OPTION:selected").attr("index");
                        var ruleTemplate = ruleTemplates[index];
                        securityGroupRule.protocol = ruleTemplate.protocol;
                        securityGroupRule.securityGroupId = util.getUrlParam('securityGroupId');

                        util.post('../rest/security/securityGroupRule',JSON.stringify(securityGroupRule),function(r){
                            if(r&&r.ok){
                                util.success("新增成功");
                                d.close();
                            }else{
                                util.error(r.msg)
                            }
                            securityGroupRuleTable.search();
                        },{contentType:'application/json'});
                    }
                }
            },
            {
                text:'取消',
                click:function(){
                    d.close();
                }
            }
        ]
    });
}

function removeSecurityGroupRule(id,name){
    util.confirm('确定删除:'+name+'？',function(){
        util.delete('../rest/security/securityGroupRule/'+id,{},function(r){
            if(r&&r.ok){
                util.success("删除成功");
                securityGroupRuleTable.search();
            }else{
                util.error(r.msg);
            }
        });
    });
}

//所有协议规则
var ruleTemplates = [{
		name: '全部',
		protocol: null,
		portRangeMin: null,
		portRangeMax: null
	},
	{
		name: '自定义TCP',
		protocol: 'tcp',
		portRangeMin: 1,
		portRangeMax: 65535
	},
	{
		name: '自定义UDP',
		protocol: 'udp',
		portRangeMin: 1,
		portRangeMax: 65535
	},
	{
		name: '全部ICMP',
		protocol: 'icmp',
		portRangeMin: null,
		portRangeMax: null
	},
	{
		name: 'SSH (22)',
		protocol: 'tcp',
		portRangeMin: 22,
		portRangeMax: 22,
		direction:'ingress'
	},
	{
		name: 'telnet (23)',
		protocol: 'tcp',
		portRangeMin: 23,
		portRangeMax: 23,
		direction:'ingress'
	},
	{
		name: 'HTTP (80)',
		protocol: 'tco',
		portRangeMin: 80,
		portRangeMax: 80,
		direction:'ingress'
	},
	{
		name: 'HTTPS (443)',
		protocol: 'tcp',
		portRangeMin: 443,
		portRangeMax: 443,
		direction:'ingress'
	},
	{
		name: 'MS SQL (1433)',
		protocol: 'tcp',
		portRangeMin: 1433,
		portRangeMax: 1433,
		direction:'ingress'
	},
	{
		name: 'Oracle (1521)',
		protocol: 'tcp',
		portRangeMin: 1521,
		portRangeMax: 1521,
		direction:'ingress'
	},
	{
		name: 'MySQL (3306)',
		protocol: 'tcp',
		portRangeMin: 3306,
		portRangeMax: 3306,
		direction:'ingress'
	},
	{
		name: 'RDP (3389)',
		protocol: 'tcp',
		portRangeMin: 3389,
		portRangeMax: 3389,
		direction:'ingress'
	},
	{
		name: 'Redis (6379)',
		protocol: 'tcp',
		portRangeMin: 6379,
		portRangeMax: 6379,
		direction:'ingress'
	}
]