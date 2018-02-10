var projectTable =null;
$(function() {
	projectTable = new LchTable({
        url: '../rest/identity/project/listPager',
        removeUrl:'../rest/identity/project/del/{id}',
        columns: [
        	{
                field: 'name',
                title: '名称'
            }, {
                field: 'description',
                title: '描述'
            }, {
                field: 'id',
                title: '项目ID'
            }, {
                field: 'enabled',
                title: '激活',
                formatter:function(value,row,index){ 
                	return value?'是':'否';
                }
            }, {
                field: 'enabled',
                title: '操作',
                class: 'control-box',
                formatter:function(value,row,index){
                    var enabled ={
                        icon:"fa fa-toggle-on",
                        tips:"禁用",
                        click:"enableProject('"+row.id+"','"+row.name+"',false)"
                    };
                    if(!row.enabled){
                        enabled ={
                            icon:"fa fa-toggle-off",
                            tips:"激活",
                            click:"enableProject('"+row.id+"','"+row.name+"',true)"
                        };
                    }
                    return util.buttons.render([
                    	{
                            icon:"fa fa-edit",
                            tips:"编辑",
                            click:"editProject('"+row.id+"',0)"
                        },{
                            icon:"fa fa-quora",
                            tips:"修改配额",
                            click:"editProject('"+row.id+"',1)"
                        },{
                            icon:"fa fa-trash",
                            tips:"删除",
                            click:"removeProject('"+row.id+"','"+row.name+"')"
                        },
                        enabled
                    ]);
                }
            }
        ],
        params: function () {
            return {
                name: $("#name").val()
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
	util.deserialize($("#projectDialog"),{
		metadataItems:128,
		cores:20,
		instances:10,
		injectedFiles:5,
		injectedFileContentBytes:10240,
		keyPairs:100,
		injectedFilePathBytes:255,
		ram:51200,
		volumes:10,
		snapshots:10,
		gigabytes:1000,
		securityGroup:10,
		securityGroupRule:94,
		floatingIP:50,
		network:10,
		port:50,
		router:10,
		subnet:10
	});
	$("#projectDialog").find("INPUT[name='name']").attr('validType','length(0,20);exists(../rest/identity/project/exists,name)');

	var d = Dialog.open({
		content:'#projectDialog',
		width:'600px',
		title:'添加项目',
		buttons:[
			{
				text:'完成',
				click:function(){
					if(ValidatorUtil.check($("#projectDialog"))){
						var bean = util.serialize($("#projectDialog"));
						util.post('../rest/identity/project/add',bean,function(r){
							if(r&&r.ok){
								util.success("新增成功");
								d.close();
							}else{
								util.error(r.msg)
							}
							projectTable.search();
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
function editProject(id,activeIndex){
	util.get('../rest/identity/project/get/'+id,{},function(project){
		util.deserialize($("#projectDialog"),project);
	    $("#projectDialog").find("INPUT[name='name']").attr('validType','length(0,20)');
		var d = Dialog.open({
			content:'#projectDialog',
			width:'600px',
			title:'编辑项目',
			buttons:[
				{
					text:'完成',
					click:function(){
						if(ValidatorUtil.check($("#projectDialog"))){
							var bean = util.serialize($("#projectDialog"));
							util.post('../rest/identity/project/update',bean,function(r){
								if(r&&r.ok){
									util.success("编辑成功");
									d.close();
								}else{
									util.error(r.msg)
								}
								projectTable.search();
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
		$("#projectDialog").find(".box-step").find("LI").eq(activeIndex).trigger("click");
	});
}
function removeProject(id,project){
    util.confirm('确定删除:'+project+'？',function(){
    	projectTable.remove({id:id},function(r){
            if(r&&r.ok){
                util.success("删除成功");
                projectTable.search();
            }else{
                util.error(r.msg);
            }
        });
    });
}
function enableProject(id,project,enabled){
    var tips = '确定激活:'+project+'？';
    if(!enabled){
        tips = '确定禁用:'+project+'？';
    }
    util.confirm(tips,function(){
        util.put('../rest/identity/project/enabled/'+id+'/'+enabled,{},function(r){
            if(r&&r.ok){
                util.success(enabled?'激活成功':"禁用成功");
                projectTable.search();
            }else{
                util.error(r.msg);
            }
        });
    });
}