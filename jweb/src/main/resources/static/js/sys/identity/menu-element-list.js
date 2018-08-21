$(function(){
	var menuTable = $('#menuTable').bootstrapTreeTable({
        id: 'id',
        code: 'code',
        parentCode:'parentCode',
        rootCodeValue:'-1',
        url:'../rest/bean/Menu',
        method: 'get',
        toolbar: '#menuToolbar',
        striped: true,
        cache: false,
        expandColumn: 1,
        expandAll: true,
        columns:[
            {
                field: 'selectItem',
                radio: true
            }, {
                field: 'name',
                title: '菜单'
            }, {
                field: 'code',
                title: '编码'
            }, {
                field: 'href',
                title: 'url'
            }
        ],
        clickRow: function(){
        	var rows = menuTable.bootstrapTreeTable('getSelections');
        	if(rows.length>0){
        		menuTable.curRow = rows[0];
        		util.getById('Menu',menuTable.curRow.id,function(menu){
        			menuTable.curRow.code = menu.code;
        			elementTable.search();
    			});
        	}
        }
    });
	$('#search-btn').on('click', function () {
		menuTable.curRow = null;
		menuTable.bootstrapTreeTable('refresh',{
			conditions:JSON.stringify([
        		{
        		relation:"and",
        		expression:"like",
        		name:"name",
        		value:"%"+$("#name").val()+"%"
        		}])
        });
		elementTable.search();
    });
	$("#menu-add-btn").click(function(){
		if(menuTable.curRow){
			util.getById('Menu',menuTable.curRow.id,function(menu){
				addMenu(menu.code);
			});
		}else{
			addMenu(-1);
		}
	});
	$("#menu-edit-btn").click(function(){
	    if(menuTable.curRow){
	    	util.getById('Menu',menuTable.curRow.id,function(menu){
				editMenu(menu);
			});
	    }else{
	    	util.info("请选中一行");
	    	return;
	    }
	});
	$("#menu-del-btn").click(function(){
		 if(menuTable.curRow){
	    	var menu = menuTable.curRow;
	    	util.remove('Menu',menu.id,function(r){
	    		if(r&&r.ok){
					util.success("删除成功")
				}else{
					util.error(r.msg)
				}
				menuTable.bootstrapTreeTable('refresh');
	 		});
	     } else {
	    	 util.info("请选中一行");
	         return;
	     }
	});
	var elementTable = new LchTable({
		table:'#elementTable',
		toolbar:'#elementToolbar',
		url:'../rest/bean/ElementPager',
		columns:[
			{
				radio: true
		    }, {
		        field: 'name',
		        title: '资源'
		    }, {
		        field: 'code',
		        title: '资源编码'
		    }, {
		        field: 'uri',
		        title: '资源路径'
		    },{
		        field: 'method',
		        title: 'method'
		    }
		],
		params:function () {
			var menuId = '';
			if(menuTable.curRow)
				menuId = menuTable.curRow.code;
			return {
				conditions:JSON.stringify([
            		{
            		relation:"and",
            		expression:"eq",
            		name:"menuId",
            		value:menuId
            		}])
			};
		}
	});
	
	$("#element-add-btn").click(function(){
		if(menuTable.curRow){
			util.getById('Menu',menuTable.curRow.id,function(menu){
				addElement(menu.code);
			});
		}else{
			util.info('请先选择菜单');
		}
	});
	$("#element-edit-btn").click(function(){
		var rows = elementTable.view.bootstrapTable('getSelections');
		var id = null;
		if(rows.length>0){
			id = rows[0].id;
			util.getById('Element',id,function(element){
				editElement(element);
			});
     	}else{
     		util.info("请选中一行");
     	}
	});
	$("#element-del-btn").click(function(){
		var rows = elementTable.view.bootstrapTable('getSelections');
		var id = null;
		if(rows.length>0){
			id = rows[0].id;
	    	util.remove('Element',id,function(r){
	    		if(r&&r.ok){
					util.success("删除成功")
				}else{
					util.error(r.msg)
				}
	    		elementTable.view.bootstrapTable('refresh');
	 		});
     	}else{
     		util.info("请选中一行");
     	}
	});
	
	
	function addMenu(parentCode){
		util.deserialize($("#menuDialog"),{parentCode:parentCode});
		var d = Dialog.open({
			title:'增加菜单',
			content:'#menuDialog',
			buttons:[
				{
					text:'保存',
					click:function(){
						if(ValidatorUtil.check($("#menuDialog"))){
							var menu = util.serialize($("#menuDialog"));
							util.create('Menu',menu,function(r){
								if(r&&r.ok){
									util.success("新增成功");
									d.close();
								}else{
									util.error(r.msg)
								}
								menuTable.bootstrapTreeTable('refresh');
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
	function editMenu(menu){
		util.deserialize($("#menuDialog"),menu);
		var d = Dialog.open({
			title:'编辑菜单',
			content:'#menuDialog',
			buttons:[
				{
					text:'保存',
					click:function(){
						if(ValidatorUtil.check($("#menuDialog"))){
							var menu = util.serialize($("#menuDialog"));
							util.update('Menu',menu,function(r){
								if(r&&r.ok){
									util.success("编辑成功");
									d.close();
								}else{
									util.error(r.msg);
								}
								menuTable.bootstrapTreeTable('refresh');
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
	function addElement(menuId){
		util.deserialize($("#elementDialog"),{menuId:menuId});
		var d = Dialog.open({
			title:'增加资源',
			content:'#elementDialog',
			buttons:[
				{
					text:'保存',
					click:function(){
						if(ValidatorUtil.check($("#elementDialog"))){
							var element = util.serialize($("#elementDialog"));
							util.create('Element',element,function(r){
								if(r&&r.ok){
									util.success("新增成功");
									d.close();
								}else{
									util.error(r.msg)
								}
								elementTable.view.bootstrapTable('refresh');
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
	function editElement(element){
		util.deserialize($("#elementDialog"),element);
		var d = Dialog.open({
			title:'编辑资源',
			content:'#elementDialog',
			buttons:[
				{
					text:'保存',
					click:function(){
						if(ValidatorUtil.check($("#elementDialog"))){
							var element = util.serialize($("#elementDialog"));
							util.update('Element',element,function(r){
								if(r&&r.ok){
									util.success("编辑成功");
									d.close();
								}else{
									util.error(r.msg);
								}
								elementTable.view.bootstrapTable('refresh');
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
});

