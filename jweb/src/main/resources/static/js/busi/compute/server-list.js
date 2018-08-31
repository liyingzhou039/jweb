var serverTable =null;
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
                field: 'id',
                title: '操作',
                class: 'control-box',
                formatter:function(id,server){
                    return util.buttons.render([
                        {
                            icon:"fa fa-trash",
                            tips:"删除",
                            click:"removeServer('"+server.id+"','"+server.name+"')"
                        }
                    ]);
                }
            }
        ],
        params:function () {

            return {
                conditions:"name=%"+$("#name").val()+"%"
            };
        }
    });
    $("#search-btn").click(function(){
        serverTable.search();
    });
    $("#add-btn").click(function(){
        addServer();
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

function removeServer(id,name){
    util.confirm('确定删除:'+name+'？',function(){
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