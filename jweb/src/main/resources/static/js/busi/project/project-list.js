var projectTable = null;
var userSelectBox = null;
$(function(){
    projectTable = new LchTable({
        url:'../rest/bean/ProjectPager',
        columns:[
            {
                field: 'name',
                title: '名称'
            }, {
                field: 'description',
                title: '描述'
            }, {
                field: 'enabled',
                title: '是否激活',
                formatter:function(value){
                	return value?'是':'否';
                }
            }, {
                field: 'id',
                title: '操作',
                class: 'control-box',
                formatter:function(value,bean,index){
                    var enabled ={
                        icon:"fa fa-toggle-on",
                        tips:"禁用",
                        click:"enableProject('"+bean.id+"','"+bean.name+"',false)"
                    };
                    if(!bean.enabled){
                        enabled ={
                            icon:"fa fa-toggle-off",
                            tips:"激活",
                            click:"enableProject('"+bean.id+"','"+bean.name+"',true)"
                        };
                    }
                    return util.buttons.render([
                        {
                            icon:"fa fa-edit",
                            tips:"修改",
                            click:"editProject('"+bean.id+"')"
                        },{
                            icon:"fa fa-trash",
                            tips:"删除",
                            click:"removeProject('"+bean.id+"','"+bean.name+"')"
                        },
                        enabled
                    ]);
                }
            }
        ],
        params:function () {
            return {
        		condition:"name=%"+$("#name").val()+"%"
        	};
        }
    });

    $("#search-btn").click(function(){
        projectTable.search();
    });
    $("#add-btn").click(function(){
        addProject();
    });
});
function addProject(){
    util.deserialize($("#dialog"),{
    	cores:20,
    	instances:10,
    	ram:50,
    	volumes:10,
    	snapshots:10,
    	gigabytes:1000
    });
    $("#dialog").find("INPUT[name='name']").attr('validType','length(0,20);exists(../rest/bean/existsProject,name)');

    //初始化用户选择框
    util.get('../rest/bean/User',{},function(users){
        userSelectBox = new SelectBox({
            content:'#userSelectBox',
            id:'id',
            cols:[
                {field:'username',title:'名称'}
            ],
            src:users,
            des:[]
        });
    });

    var d = Dialog.open({
    	content:'#dialog',
    	width:'600px',
    	title:'新增项目',
    	buttons:[
    		{
    			text:'完成',
    			click:function(){
    				if(ValidatorUtil.check(d.$dialog)){
    					var project = util.serialize(d.$dialog.find(".project-info"));
                        project.quota = util.serialize(d.$dialog.find(".project-quota"));
                        project.userIds = [];
                        var users = userSelectBox.getSelected();
                        for(var i=0;i<users.length;i++){
                            project.userIds.push(users[i].id);
                        }

    					util.post('../rest/project/project',JSON.stringify(project),function(r){
    						if(r&&r.ok){
    							util.success("新增成功");
    							d.close();
    						}else{
    							util.error(r.msg)
    						}
    						projectTable.search();
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

function editProject(id){
    var project = projectTable.getRowById(id);
    util.deserialize($("#dialog").find(".project-info"),project);
    util.get('../rest/bean/Quota',{condition:"projectId="+id},function(quotas){
        if(quotas && quotas.length){
            util.deserialize($("#dialog").find(".project-quota"),quotas[0]);
        }
    });

    $("#dialog").find("INPUT[name='name']").attr('validType','length(0,20)');

    //初始化用户选择框
    util.get('../rest/bean/User',{},function(users){
        util.get('../rest/bean/ProjectUser',{condition:"projectId="+id},function(projectUsers){
            var des = [];
            for(var i=0;i<users.length;i++){
                for(var j=0;j<projectUsers.length;j++){
                    if(users[i].id == projectUsers[j].userId){
                        des.push(users[i]);
                        continue;
                    }
                }
            }
            userSelectBox = new SelectBox({
                content:'#userSelectBox',
                id:'id',
                cols:[
                    {field:'username',title:'名称'}
                ],
                src:users,
                des:des
            });
        });
    });

    var d = Dialog.open({
    	content:'#dialog',
    	width:'600px',
    	title:'修改项目',
    	buttons:[
    		{
    			text:'完成',
    			click:function(){
    				if(ValidatorUtil.check(d.$dialog)){
    					var project = util.serialize(d.$dialog.find(".project-info"));
                        project.quota = util.serialize(d.$dialog.find(".project-quota"));
                        project.userIds = [];
                        var users = userSelectBox.getSelected();
                        for(var i=0;i<users.length;i++){
                            project.userIds.push(users[i].id);
                        }

    					util.put('../rest/project/project',JSON.stringify(project),function(r){
    						if(r&&r.ok){
    							util.success("修改成功");
    							d.close();
    						}else{
    							util.error(r.msg)
    						}
    						projectTable.search();
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
function removeProject(id,name){
    util.confirm('确定删除:'+name+'？',function(){
        util.delete('../rest/project/project/'+id,{},function(r){
            if(r&&r.ok){
                util.success("删除成功");
                projectTable.search();
            }else{
                util.error(r.msg);
            }
        });
    });
}

function enableProject(id,name,enabled){
    var tips = '确定激活:'+name+'？';
    if(!enabled){
        tips = '确定禁用:'+name+'？';
    }
    util.confirm(tips,function(){
        util.put('../rest/project/project/'+id,{enabled:enabled},function(r){
            if(r&&r.ok){
                util.success(enabled?'激活成功':"禁用成功");
                projectTable.search();
            }else{
                util.error(r.msg);
            }
        });
    });
}
