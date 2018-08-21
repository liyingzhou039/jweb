var userTable =null;
$(function(){
    userTable = new LchTable({
        url:'../rest/bean/UserPager',
        columns:[
            {
                field: 'username',
                title: '名称'
            }, {
                field: 'email',
                title: '邮箱'
            }, {
                field: 'description',
                title: '描述'
            }, {
                field: 'enabled',
                title: '激活',
                formatter:function(value,row,index){
                    var h='';
                    if(value==true){
                        h='';
                    }
                    return value==true?'已激活':'未激活';
                }
            }, {
                field: 'enabled',
                title: '操作',
                class: 'control-box',
                formatter:function(value,row,index){
                    var enabled ={
                        icon:"fa fa-toggle-on",
                        tips:"禁用",
                        click:"enableUser('"+row.id+"',false)"
                    };
                    if(!row.enabled){
                        enabled ={
                            icon:"fa fa-toggle-off",
                            tips:"激活",
                            click:"enableUser('"+row.id+"',true)"
                        };
                    }
                    return util.buttons.render([
                        {
                            icon:"fa fa-edit",
                            tips:"编辑",
                            click:"editUser('"+row.id+"',0)"
                        },{
                            icon:"fa fa-user-o",
                            tips:"分配角色",
                            click:"editUser('"+row.id+"',1)"
                        },{
                            icon:"fa fa-trash",
                            tips:"删除",
                            click:"removeUser('"+row.id+"','"+row.username+"')"
                        },
                        enabled
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
                    name:"username",
                    value:"%"+$("#name").val()+"%"
                }])
        	};
        }
    });

    $("#search-btn").click(function(){
        userTable.search();
    });
    $("#add-btn").click(function(){
        addUser();
    });
});
function addUser(){
    var roleSelectBox = null;
    util.deserialize($("#userDialog"),{});
    $("#userDialog").find("INPUT[name='username']").attr('validType','length(0,20);exists(../rest/bean/existsUser,username)');
    $("#userDialog").find("INPUT[name='username']").removeAttr('disabled');
    //初始化角色选择框
    util.list('Role',function(roles){
        roleSelectBox = new SelectBox({
            content:'#roleSelectBox',
            id:'name',
            cols:[
                {field:'name',title:'名称'}
            ],
            src:roles,
            des:[

            ]
        });
    });
    var d = Dialog.open({
        content:'#userDialog',
        title:'添加用户',
        buttons:[
            {
                text:'完成',
                click:function(){
                    if(ValidatorUtil.check(d.$dialog)){
                        var user = util.serialize(d.$dialog);
                        delete user.rePassword;
                        var roles = roleSelectBox.getSelected();
                        var roleIds=[];
                        for(var i=0;i<roles.length;i++){
                            roleIds.push(roles[i].id);
                        }
                        util.post('../rest/identity/user',
                            {roleIdsString:roleIds.join(','),userJson:JSON.stringify(user)},function(r){
                            if(r&&r.ok){
                                util.success("新增成功");
                                d.close();
                            }else{
                                util.error(r.msg)
                            }
                            userTable.search();
                        });
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

function editUser(id,activeIndex){
    var user = userTable.getRowById(id);
    user.password='';
    util.deserialize($("#userDialog"),user);
    var roleSelectBox = null;
    $("#userDialog").find("INPUT[name='username']").attr('validType','length(0,20)');
    $("#userDialog").find("INPUT[name='username']").attr('disabled','');
    //初始化角色选择框

    util.list('UserRole',function(userRoles){
        util.list('Role',function(roles){
            var des=[];
            for(var i=0;i<roles.length;i++){
                for(var j=0;j<userRoles.length;j++){
                    if(roles[i].id==userRoles[j].roleId){
                        des.push(roles[i]);
                    }
                }
            }
            roleSelectBox = new SelectBox({
                content:'#roleSelectBox',
                id:'name',
                cols:[
                    {field:'name',title:'名称'}
                ],
                src:roles,
                des:des
            });
        });
    },[{
        relation:"and",
        expression:"eq",
        name:"userId",
        value:id
    }]);

    var d = Dialog.open({
        content:'#userDialog',
        title:'编辑用户',
        buttons:[
            {
                text:'完成',
                click:function(){
                    if(ValidatorUtil.check(d.$dialog)){
                        if(ValidatorUtil.check(d.$dialog)){
                            var user = util.serialize(d.$dialog);
                            delete user.rePassword;
                            var roles = roleSelectBox.getSelected();
                            var roleIds=[];
                            for(var i=0;i<roles.length;i++){
                                roleIds.push(roles[i].id);
                            }
                            util.put('../rest/identity/user',
                                {roleIdsString:roleIds.join(','),userJson:JSON.stringify(user)},function(r){
                                if(r&&r.ok){
                                    util.success("更新成功");
                                    d.close();
                                }else{
                                    util.error(r.msg)
                                }
                                userTable.search();
                            });
                        }
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
    $("#userDialog").find(".box-step").find("LI").eq(activeIndex).trigger("click");
}
function removeUser(id,user){
    util.confirm('确定删除:'+user+'？',function(){
        util.delete('../rest/identity/user/'+id,{},function(r){
            if(r&&r.ok){
                util.success("删除成功");
                userTable.search();
            }else{
                util.error(r.msg);
            }
        });
    });
}
function enableUser(id,enabled){
    var user = userTable.getRowById(id);
    var tips = '确定激活:'+user.username+'？';
    if(!enabled){
        tips = '确定禁用:'+user.username+'？';
    }
    user.enabled=enabled;
    util.confirm(tips,function(){
        util.update('User',user,function(r){
            if(r&&r.ok){
                util.success(enabled?'激活成功':"禁用成功");
                userTable.search();
            }else{
                util.error(r.msg);
            }
        });
    });
}
