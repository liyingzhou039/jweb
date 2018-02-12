$(function(){
	var menuTable = $('#menuTable').bootstrapTreeTable({
        id: 'id',
        code: 'id',
        parentCode:'parentId',
        rootCodeValue:'-1',
        url:'../rest/bean/listMenu',
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
                field: 'URL',
                title: 'url'
            }
        ],
        clickRow: function(){
        	var rows = menuTable.bootstrapTreeTable('getSelections');
        	if(rows.length>0){
        		menuTable.curRow = rows[0];
        		util.getById({id:menuTable.curRow.id,beanName:'Menu'},function(bean){
        			menuTable.curRow.id = bean.id;
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
			util.getById({id:menuTable.curRow.id,beanName:'Menu'},function(bean){
				addMenu({parentId:bean.id});
			});
		}else{
			addMenu({parentId:-1});
		}
	});
	$("#menu-edit-btn").click(function(){
	    if(menuTable.curRow){
	    	util.getById({id:menuTable.curRow.id,beanName:'Menu'},function(bean){
	    		bean.beanName='Menu';
				editMenu(bean);
			});
	    }else{
	    	util.info("请选中一行");
	    	return;
	    }
	});
	$("#menu-del-btn").click(function(){
		 if(menuTable.curRow){
	    	var bean = menuTable.curRow;
			bean.beanName="Menu";
	    	util.remove(bean,function(r){
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
		url:'../rest/bean/listElementPager',
		columns:[
			{
				radio: true
		    }, {
		        field: 'name',
		        title: '按钮'
		    }, {
		        field: 'url',
		        title: 'URL'
		    },{
		        field: 'method',
		        title: 'method'
		    }
		],
		params:function () {
			var menuId = '';
			if(menuTable.curRow)
				menuId = menuTable.curRow.id;
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
			util.getById({id:menuTable.curRow.id,beanName:'Menu'},function(bean){
				addElement({menuId:bean.id});
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
			util.getById({id:id,beanName:'Element'},function(bean){
	    		bean.beanName='Element';
				editElement(bean);
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
			var bean = {};
			bean.beanName="Element";
			bean.id=id;
	    	util.remove(bean,function(r){
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
	
	
	function addMenu(bean){
		util.deserialize($("#menuDialog"),bean);
		var d = Dialog.open({
			title:'增加菜单',
			content:'#menuDialog',
			buttons:[
				{
					text:'保存',
					click:function(){
						if(ValidatorUtil.check($("#menuDialog"))){
							var bean = util.serialize($("#menuDialog"));
							bean.beanName="Menu";
							util.create(bean,function(r){
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
	function editMenu(bean){
		util.deserialize($("#menuDialog"),bean);
		var d = Dialog.open({
			title:'编辑菜单',
			content:'#menuDialog',
			buttons:[
				{
					text:'保存',
					click:function(){
						if(ValidatorUtil.check($("#menuDialog"))){
							var bean = util.serialize($("#menuDialog"));
							bean.beanName="Menu";
							util.update(bean,function(r){
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
	function addElement(bean){
		util.deserialize($("#elementDialog"),bean);
		var d = Dialog.open({
			title:'增加资源',
			content:'#elementDialog',
			buttons:[
				{
					text:'保存',
					click:function(){
						if(ValidatorUtil.check($("#elementDialog"))){
							var bean = util.serialize($("#elementDialog"));
							bean.beanName="Element";
							util.create(bean,function(r){
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
	function editElement(bean){
		util.deserialize($("#elementDialog"),bean);
		var d = Dialog.open({
			title:'编辑资源',
			content:'#elementDialog',
			buttons:[
				{
					text:'保存',
					click:function(){
						if(ValidatorUtil.check($("#elementDialog"))){
							var bean = util.serialize($("#elementDialog"));
							bean.beanName="Element";
							util.update(bean,function(r){
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

