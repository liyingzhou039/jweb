var securityGroupTable =null;
$(function(){
    securityGroupTable = new LchTable({
        url:'../rest/bean/SecurityGroupPager',
        columns:[
            {
                field: 'name',
                title: '名称'
            }, {
                field: 'description',
                title: '描述'
            }, {
                field: 'id',
                title: '操作',
                class: 'control-box',
                formatter:function(value,bean,index){
                    return util.buttons.render([
                        {
                            icon:"fa fa-balance-scale",
                            tips:"安全组规则",
                            click:"toRules('"+bean.id+"')"
                        },
                        {
                            icon:"fa fa-trash",
                            tips:"删除",
                            click:"removeSecurityGroup('"+bean.id+"','"+bean.name+"')"
                        }
                    ]);
                }
            }
        ],
        params:function () {
            return {
        		conditions:JSON.stringify([
                {
                	relation:"and",
                    expression:"like",
                    name:"name",
                    value:"%"+$("#name").val()+"%"
                },{
                	relation:"and",
                    expression:"eq",
                    name:"projectId",
                    value:localStorage.getItem('projectId')
                }])
        	};
        }
    });

    $("#search-btn").click(function(){
        securityGroupTable.search();
    });
    $("#add-btn").click(function(){
        addSecurityGroup();
    });
});
function addSecurityGroup(){
    util.deserialize($("#dialog"),{});
    var d = Dialog.open({
        content:'#dialog',
        title:'添加安全组',
        buttons:[
            {
                text:'完成',
                click:function(){
                    if(ValidatorUtil.check(d.$dialog)){
                        var securityGroup = util.serialize(d.$dialog);
                       util.post('../rest/security/securityGroup',JSON.stringify(securityGroup),function(r){
                       	if(r&&r.ok){
                       		util.success("新增成功");
                       		d.close();
                       	}else{
                       		util.error(r.msg)
                       	}
                       	securityGroupTable.search();
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

function removeSecurityGroup(id,name){
    util.confirm('确定删除:'+name+'？',function(){
        util.delete('../rest/security/securityGroup/'+id,{},function(r){
            if(r&&r.ok){
                util.success("删除成功");
                securityGroupTable.search();
            }else{
                util.error(r.msg);
            }
        });
    });
}

function toRules(securityGroupId) {
    window.location.href = '/security/securityGroupRule-list?securityGroupId='+securityGroupId+'&_token='+localStorage.getItem('_token');
}