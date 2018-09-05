var ACTION={
        'stop':{
            icon:'fa fa-power-off',
            style:"color:red;",
            name:'关机',
            condition:{
                '-':['shutoff','stopped','migrating','resize','verify_resize','revert_resize']
            }
        },
        'start':{
            icon:'fa fa-power-off',
            style:"color:green;",
            name:'开机',
            condition:{
                '+':['stopped','shutoff']
            }
        }
};
function getActionButtons(server){
	var status = server.status;
	var actBtns=[];
	for(var a in ACTION){
		if(ACTION[a].hidden) continue;
		if(ACTION[a].condition){
			if(ACTION[a].condition['+']){
				if(!util.contains(status,ACTION[a].condition['+'])){
					continue;
				}
			}
			if(ACTION[a].condition['-']){
				if(util.contains(status,ACTION[a].condition['-'])){
					continue;
				}
			}
		}
		actBtns.push({
			icon:ACTION[a].icon?ACTION[a].icon:"fa fa-trash",
            tips:ACTION[a].name,
            style:ACTION[a].style,
            click:"action('"+server.id+"','"+a+"')"
		});
	}
	return actBtns;
}

var serverTable =null;
var snapshotTable=null;
var volumeTable=null;
var curServerId=null;
$(function(){
    serverTable = new LchTable({
        url:'../rest/compute/server/pager',
        columns:[
            {
                field: 'centerCode',
                title: '数据中心'
            },
            {
                field: 'name',
                title: '名称'
            }, {
                field: 'ip',
                title: 'IP'
            }, {
                field: 'status',
                title: '状态'
            }, {
                field: 'createAt',
                title: '创建时间'
            }, {
                field: 'id',
                title: '操作',
                class: 'control-box',
                formatter:function(id,server){
                    var actBtns=getActionButtons(server);
                    return util.buttons.render(
                    [
                        {
                            icon:"fa fa-desktop",
                            tips:"远程链接",
                            click:"terminal('"+id+"')"
                        },{
                            icon:"fa fa-desktop",
                            tips:"备份/还原",
                            click:"snapshots('"+id+"')"
                        },{
                            icon:"fa fa-desktop",
                            tips:"磁盘管理",
                            click:"volumes('"+id+"')"
                        },{
                            icon:"fa fa-desktop",
                            tips:"制作镜像",
                            click:"createImage('"+id+"')"
                        }
                    ].concat(actBtns).concat([
                        {
                            icon:"fa fa-trash",
                            tips:"删除",
                            click:"removeServer('"+server.id+"','"+server.name+"')"
                        }
                    ]));
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
        serverTable.search();
    });
    $("#add-btn").click(function(){
        addServer();
    });

    snapshotTable = new LchTable({
        url:'../rest/compute/snapshot/pager',
        table:'#snapshotTableId',
        toolbar:'#snapshotToolbar',
        columns:[
            {
                field: 'name',
                title: '名称'
            }, {
                field: 'size',
                title: '大小',
                formatter:function(size){
                    return util.fileSize(size);
                }
            }, {
                field: 'createAt',
                title: '时间'
            }, {
                field: 'id',
                title: '操作',
                class: 'control-box',
                formatter:function(id,snapshot){
                    return util.buttons.render([{
                        icon:"fa fa-desktop",
                        tips:"还原",
                        click:"restoreSnapshot('"+id+"','"+snapshot.createAt+"')"
                    },{
                        icon:"fa fa-trash",
                        tips:"删除",
                        click:"removeSnapshot('"+id+"','"+snapshot.name+"')"
                    }]);
                }
            }
        ],
        params:function () {
            return {
                serverId:curServerId
            };
        }
    });

    volumeTable = new LchTable({
        url:'../rest/compute/volume/pager',
        table:'#volumeTableId',
        toolbar:'#volumeToolbar',
        columns:[
            {
                field: 'name',
                title: '名称'
            }, {
                field: 'size',
                title: '大小',
                formatter:function(size){
                    return size+"GB";
                }
            }, {
                field: 'device',
                title: '设备'
            }, {
                field: 'id',
                title: '操作',
                class: 'control-box',
                formatter:function(id,volume){
                    return util.buttons.render([{
                        icon:"fa fa-trash",
                        tips:"删除",
                        click:"removeVolume('"+id+"','"+volume.name+"')"
                    }]);
                }
            }
        ],
        params:function () {
            return {
                serverId:curServerId
            };
        }
    });
});
function addServer(){
    util.deserialize($("#serverDialog"),{});
    //初始化实例类型选择框
    util.get('../rest/bean/Flavor',{},function(flavors){
    	flavorSelectBox=new SelectBox({
    		multiSelect:false,
               content:'#flavorSelectBox',
               id:'id',
               search:'name',
               cols:[
                   {field:'name',title:'名称'},
                   {field:'vcpus',title:'内核'},
                   {field:'ram',title:'内存'},
                   {field:'disk',title:'磁盘'}
               ],
               src:flavors?flavors:[],
               des:flavors&&flavors.length==1?[flavors[0]]:[]
           });
    });
    //初始化镜像选择框
    util.get('../rest/bean/Image',{},function(images){
    	imageSelectBox=new SelectBox({
    		multiSelect:false,
               content:'#imageSelectBox',
               id:'id',
               search:'name',
               cols:[
                   {field:'name',title:'名称'}
               ],
               src:images?images:[],
               des:images&&images.length==1?[images[0]]:[]
           });
    });
    //初始化安全组选择框
    util.get('../rest/security/securityGroup',{},function(securityGroups){
    	securityGroupSelectBox=new SelectBox({
    		multiSelect:false,
               content:'#securityGroupSelectBox',
               id:'id',
               search:'name',
               cols:[
                   {field:'name',title:'名称'}
               ],
               src:securityGroups?securityGroups:[],
               des:securityGroups&&securityGroups.length==1?[securityGroups[0]]:[]
           });
    });
    //初始化安全组选择框
    util.get('../rest/security/keypair',{},function(keypairs){
    	keypairSelectBox=new SelectBox({
    		multiSelect:false,
               content:'#keypairSelectBox',
               id:'id',
               search:'name',
               cols:[
                   {field:'name',title:'名称'}
               ],
               src:keypairs?keypairs:[],
               des:keypairs&&keypairs.length==1?[keypairs[0]]:[]
           });
    });
    var d = Dialog.open({
        content:'#serverDialog',
        width:'800px',
        title:'创建虚拟机',
        buttons:[
            {
                text:'完成',
                click:function(){
                    if(ValidatorUtil.check(d.$dialog)){
                        var server = util.serialize(d.$dialog);
                        var flavors = flavorSelectBox.getSelected();
                        var images = imageSelectBox.getSelected();
                        var keypairs = keypairSelectBox.getSelected();
                        var securityGroups = securityGroupSelectBox.getSelected();
                        if(!flavors || flavors.length<=0){
                            util.error("虚拟机规格必须选择");
                        }
                        server.flavorId=flavors[0].id;
                        if(!images || images.length<=0){
                            util.error("虚拟机规格必须选择");
                        }
                        server.imageId=images[0].id;
                        if(!securityGroups || securityGroups.length<=0){
                            util.error("虚拟机规格必须选择");
                        }
                        server.securityGroupId=securityGroups[0].id;
                        if(!keypairs || keypairs.length<=0){
                            util.error("密钥必须选择");
                        }
                        server.keypairId=keypairs[0].id;
                        util.post('../rest/compute/server',JSON.stringify(server),function(r){
                            if(r&&r.ok){
                                util.success("创建成功");
                                d.close();
                            }else{
                                util.error(r.msg)
                            }
                            serverTable.search();
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
function terminal(serverId){
	var d = Dialog.open({
        content:'#terminalDialog',
        width:'850px',
        title:'远程链接',
        buttons:[
            {
                text:'关闭',
                click:function(){
                    d.close();
                    d.$dialog.find("#vncWindow").attr("src","");
                }
            }
        ]
    });
	d.$dialog.find("#vncWindow").height(d.$dialog.height()-120);
	d.$dialog.find("#vncWindow").width(800);
	util.get('../rest/compute/server/'+serverId,{},function(data){
	    var server = data.entity;
		d.$dialog.find("#vncWindow").attr("src",server.vncUrl);
		d.$dialog.find("#vncWindow").focus();
	});
}
function snapshots(serverId){
    curServerId = serverId;
    var d = Dialog.open({
        content:'#snapshotDialog',
        width:'850px',
        title:'备份/还原',
        buttons:[
            {
                text:'关闭',
                click:function(){
                    d.close();
                    serverTable.search();
                }
            }
        ]
    });
    snapshotTable.search();
}
function createImage(serverId){
    util.post('../rest/compute/snapshot/image/'+serverId,{},function(r){
        if(r&&r.ok){
            util.success(r.msg);
        }else{
            util.error(r.msg);
        }
    });
}
function restoreSnapshot(snapshotId,createAt){
    util.confirm('确定将虚拟机还原到:'+createAt+'？',function(){
        util.put('../rest/compute/snapshot/'+snapshotId,{},function(r){
            if(r&&r.ok){
                util.success("还原成功");
                snapshotTable.search();
            }else{
                util.error(r.msg);
            }
        });
    });
}
function removeSnapshot(snapshotId,name){
    util.confirm('确定删除:'+name+'？',function(){
        util.delete('../rest/compute/snapshot/'+snapshotId,{},function(r){
            if(r&&r.ok){
                util.success("删除成功");
                snapshotTable.search();
            }else{
                util.error(r.msg);
            }
        });
    });
}
function volumes(serverId){
    curServerId = serverId;
    var d = Dialog.open({
        content:'#volumeDialog',
        width:'850px',
        title:'磁盘管理',
        buttons:[
            {
                text:'关闭',
                click:function(){
                    d.close();
                }
            }
        ]
    });
    volumeTable.search();
}
function createVolume(){
    var $volume=$("#volumeToolbar");
    var volume={
        serverId:curServerId,
        name:$volume.find("INPUT[name='name']").val(),
        size:$volume.find("INPUT[name='size']").val()
    };
    util.post('../rest/compute/volume',JSON.stringify(volume),function(r){
        if(r&&r.ok){
            util.success(r.msg);
        }else{
            util.error(r.msg)
        }
        volumeTable.search();
    },{contentType:'application/json'});
}
function removeVolume(volumeId,name){
    util.confirm('确定删除:'+name+'？',function(){
        util.delete('../rest/compute/volume/'+volumeId,{},function(r){
            if(r&&r.ok){
                util.success("删除成功");
                volumeTable.search();
            }else{
                util.error(r.msg);
            }
        });
    });
}
function createSnapshot(){
	util.post('../rest/compute/snapshot/'+curServerId,{},function(r){
        if(r&&r.ok){
            util.success("备份成功");
        }else{
            util.error(r.msg)
        }
        snapshotTable.search();
    });
}
function action(serverId,action){
	var actionName =(ACTION[action]&&ACTION[action].name)?ACTION[action].name:action;
	util.confirm('确定:'+actionName+'？',function(){
        util.put('../rest/compute/server/action/'+serverId,{action:action},function(r){
            if(r&&r.ok){
                util.success(actionName+"成功");
                setTimeout(function(){serverTable.search();},2000);
            }else{
                util.error(r.msg);
            }
        });
    });
}
function removeServer(id,name){
    util.confirm('删除虚拟机将删除所有备份,确定删除:'+name+'？',function(){
        util.delete('../rest/compute/server/'+id,{},function(r){
            if(r&&r.ok){
                util.success("删除成功");
                serverTable.search();
            }else{
                util.error(r.msg);
            }
        });
    });
}