var flavorTable =null;
$(function(){
    flavorTable = new LchTable({
        url:'../rest/bean/FlavorPager',
        columns:[
            {
                field: 'name',
                title: '名称'
            }, {
                field: 'vcpus',
                title: 'VCPU数量'
            }, {
                field: 'ram',
                title: '内存',
                formatter:function(ram){
                	return ram+'GB';
                }
            }, {
                field: 'disk',
                title: '根磁盘',
                formatter:function(disk){
                	return disk+'GB';
                }
            }, {
                field: 'ephemeral',
                title: '临时磁盘',
                formatter:function(ephemeral){
                	return ephemeral+'GB';
                }
            }, {
                field: 'swap',
                title: 'Swap磁盘',
                formatter:function(swap){
                	return swap+'GB';
                }

            }, {
                field: 'rxtxFactor',
                title: 'RX/TX 因子'
            }, {
                field: 'id',
                title: '操作',
                class: 'control-box',
                formatter:function(id,flavor){
                    return util.buttons.render([
                        {
                            icon:"fa fa-trash",
                            tips:"删除",
                            click:"removeFlavor('"+flavor.id+"','"+flavor.name+"')"
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
                    value:'%'+$("#name").val()+'%'
                }])
            };
        }
    });
    $("#search-btn").click(function(){
        flavorTable.search();
    });
    $("#add-btn").click(function(){
        addFlavor();
    });
});
function addFlavor(){
    util.deserialize($("#flavorDialog"),{
    	ephemeral:0,
    	swap:0,
    	rxtxFactor:1
    });
    var d = Dialog.open({
        content:'#flavorDialog',
        width:'800px',
        title:'添加虚拟机规格',
        buttons:[
            {
                text:'完成',
                click:function(){
                    if(ValidatorUtil.check(d.$dialog)){
                        var flavor = util.serialize(d.$dialog);
                        util.post('../rest/compute/flavor',JSON.stringify(flavor),function(r){
                            if(r&&r.ok){
                                util.success("新增成功");
                                d.close();
                            }else{
                                util.error(r.msg)
                            }
                            flavorTable.search();
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

function removeFlavor(id,name){
    util.confirm('确定删除:'+name+'？',function(){
        util.delete('../rest/compute/flavor/'+id,{},function(r){
            if(r&&r.ok){
                util.success("删除成功");
                flavorTable.search();
            }else{
                util.error(r.msg);
            }
        });
    });
}