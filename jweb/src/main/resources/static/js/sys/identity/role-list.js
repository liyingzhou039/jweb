var roleTable = null;
$(function(){
	roleTable = new LchTable({
		url:'../rest/bean/RolePager',
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
		        formatter:function(value,row,index){
		        	return util.buttons.render([
		        		{
		        			icon:"fa fa-trash",
		        			tips:"删除",
		        			click:"removeRole('"+row.id+"','"+row.name+"')"
		        		},
		        		{
		        			icon:"fa fa-check-square",
		        			tips:"赋权",
		        			click:"updatePower('"+row.id+"')"
		        		}
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
		roleTable.search();
	});
	$("#add-btn").click(function(){
	    util.deserialize($("#dialog"),{});
		var d = Dialog.open({
			content:'#dialog',
			title:'添加角色',
			buttons:[
				{
					text:'保存',
					click:function(){
						if(ValidatorUtil.check(d.$dialog)){
                            var role = util.serialize(d.$dialog);
                            util.create('Role',
                                role,function(r){
                                if(r&&r.ok){
                                    util.success("新增成功");
                                    d.close();
                                }else{
                                    util.error(r.msg)
                                }
                                roleTable.search();
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
	});
});
function updatePower(roleId){
    initMenuTree("menuTreeTable",roleId);
	var d = Dialog.open({
		content:'#powerDialog',
		title:'角色赋权',
		buttons:[
			{
				text:'保存',
				click:function(){
					var nodes = $('#menuTreeTable').treeview(true).getChecked();
					var menuIds=[];
					var elementIds=[];
					if(nodes){
						for(var i=0;i<nodes.length;i++){
							if(nodes[i].funcType=='menu'){
								menuIds.push(nodes[i].id);
							}else if(nodes[i].funcType=='element'){
								elementIds.push(nodes[i].id);
							}
						}
					}
					util.post(
						'../rest/identity/role/updatePower/'+roleId,
						{
							menuIds:menuIds.join(","),
							elementIds:elementIds.join(",")
						},
						function(r){
							if(r&&r.ok){
								util.success("已更新");
								d.close();
								initMenuTree("menuTreeTable",roleId);
							}else{
								util.error(r.msg);
							}
							
						}
					);
					
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
function removeRole(id,role){
	util.confirm('确定删除角色:'+role+'？',function(){
		util.remove('Role',id,function(r){
			if(r&&r.ok){
				util.success('删除成功');
				roleTable.search();
			}else{
				util.error(r.msg);
			}
		});
	})
}
function updatePower(roleId){
    initMenuTree("menuTreeview",roleId);
	var d = Dialog.open({
		content:'#powerDialog',
		title:'角色赋权',
		buttons:[
			{
				text:'保存',
				click:function(){
					var nodes = $('#menuTreeview').treeview(true).getChecked();
					var menuIds=[];
					var elementIds=[];
					if(nodes){
						for(var i=0;i<nodes.length;i++){
							if(nodes[i].funcType=='menu'){
								menuIds.push(nodes[i].id);
							}else if(nodes[i].funcType=='element'){
								elementIds.push(nodes[i].id);
							}
						}
					}
					util.post(
						'../rest/identity/role/updatePower/'+roleId,
						{
							menuIds:menuIds.join(","),
							elementIds:elementIds.join(",")
						},
						function(r){
							if(r&&r.ok){
								util.success("已更新");
								d.close();
								initMenuTree("menuTreeview",roleId);
							}else{
								util.error(r.msg);
							}

						}
					);

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
function removeRole(id,role){
    util.confirm('确定删除:'+role+'？',function(){
        util.delete('../rest/identity/role/'+id,{},function(r){
            if(r&&r.ok){
                util.success("删除成功");
                roleTable.search();
            }else{
                util.error(r.msg);
            }
        });
    });
}

function initMenuTree(domId,roleId){
	function hasMenu(roleMenus,menuId){
		for(var i=0;i<roleMenus.length;i++){
			if(roleMenus[i].menuId==menuId)
				return true;
		}
		return false;
	}
	function hasElement(roleElements,elementId){
		for(var i=0;i<roleElements.length;i++){
			if(roleElements[i].elementId==elementId)
				return true;
		}
		return false;
	}
	util.get('../rest/identity/role/menu-element-list',{roleId:roleId},function(data){
	    var roleMenus = data.roleMenus;
	    var roleElements = data.roleElements;
	    var menus = data.menus;
	    var elements = data.elements;
        $.each(menus,function(){
        	this.id=this.code;
        	this.funcType='menu';
        	this.state={
        			checked : hasMenu(roleMenus,this.id)?true:false,
        			disabled : false,
        			expanded : true,
        			selected : false
        	};
        });
        $.each(elements,function(){
        	this.id=this.code;
        	this.parentCode=this.menuId;
        	this.funcType='element';
        	this.state={
        			checked : hasElement(roleElements,this.id)?true:false,
        			disabled : false,
        			expanded : true,
        			selected : false
        	};
        });
        var nodes = menus.concat(elements);
        nodes = util.list2Tree(nodes,{nodeId:'id',id:'id',pId:'parentCode',text:'name'});
        renderMenuTree(domId,nodes);
	});
}
function renderMenuTree(domId,nodes){
	var $menuTree = $('#'+domId).treeview({
		data : nodes,
		showIcon : false,
		showCheckbox : true,
		multiSelect : false,
		levels : 5,
		state : {
			checked : false,
			disabled : false,
			expanded : true,
			selected : false
		},
		onNodeChecked : function(event, data) {
			var selectNodes = treeViewHelper.getChildrenNodeIdArr(data);// 获取所有子节点
			if (selectNodes) {
				$('#'+domId).treeview('checkNode',
						[ selectNodes, {
							silent : true
						} ]);
			}
			var parNodes = treeViewHelper.getParentIdArr(domId, data);
			if (parNodes) {
				$('#'+domId).treeview('checkNode',
						[ parNodes, {
							silent : true
						} ]);

			}

		},
		onNodeUnchecked : function(event, data) {
			var selectNodes = treeViewHelper.getChildrenNodeIdArr(data);// 获取所有子节点
			if (selectNodes) {
				$('#'+domId).treeview('uncheckNode',
						[ selectNodes, {
							silent : true
						} ]);
			}
		}
	});
}