var userTable =null;
$(function(){
    userTable = new LchTable({
        url:'../rest/bean/listUserPager',
        columns:[
            {
                field: 'username',
                title: '用户名'
            }, {
                field: 'description',
                title: '描述'
            }, {
                field: 'username',
                title: '操作',
                class: 'control-box',
                formatter:function(username,row,index){
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
                            click:"removeUser('"+row.id+"','"+username+"')"
                        }
                    ]);
                }
            }
        ],
        params:function () {
            return {
                condition: "username :li '%"+$("#name").val()+"%'"
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
    $("#userDialog").find("INPUT[name='username']").attr('validType','length(0,20);exists(../rest/system/user/exists,username)');
    //初始化角色选择框
    util.get('../rest/bean/listRole',{},function(roles){
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
        width:'800px',
        title:'添加用户',
        buttons:[
            {
                text:'完成',
                click:function(){
                    if(ValidatorUtil.check($("#userDialog"))){
                        var bean = util.serialize($("#userDialog"));
                        bean.roleIds ='';
                        var roles = roleSelectBox.getSelected();
                        for(var i=0;i<roles.length;i++){
                            if(bean.roleIds.length>0) bean.roleIds+=',';
                            bean.roleIds+=roles[i].id;
                        }
                        util.post('../rest/system/user/add',bean,function(r){
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
    var row = userTable.getRowById(id);
    var roleSelectBox = null;
    util.deserialize($("#userDialog"),row);
    $("#userDialog").find("INPUT[name='username']").attr('validType','length(0,20)');
    //初始化角色选择框
    util.get('../rest/bean/listRole',{},function(roles){

        roleSelectBox = new SelectBox({
            content:'#roleSelectBox',
            id:'name',
            cols:[
                {field:'name',title:'名称'}
            ],
            src:roles,
            des:row.roles
        });
    });
    
    var d = Dialog.open({
        content:'#userDialog',
        width:'800px',
        title:'编辑用户',
        buttons:[
            {
                text:'完成',
                click:function(){
                    if(ValidatorUtil.check($("#userDialog"))){
                        var bean = util.serialize($("#userDialog"));
                        bean.roleIds ='';
                        var roles = roleSelectBox.getSelected();
                        for(var i=0;i<roles.length;i++){
                            if(bean.roleIds.length>0) bean.roleIds+=',';
                            bean.roleIds+=roles[i].id;
                        }
                        util.put('../rest/system/user/update',bean,function(r){
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
        util.remove({beanName:'User',id:id},function(r){
            if(r&&r.ok){
                util.success("删除成功");
                userTable.search();
            }else{
                util.error(r.msg);
            }
        });
    });
}