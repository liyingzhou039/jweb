var centerTable =null;
$(function(){
    centerTable = new LchTable({
        url:'../rest/bean/CenterPager',
        columns:[
            {
                field: 'name',
                title: '名称'
            }, {
                field: 'code',
                title: '编码'
            }, {
                field: 'type',
                title: '类型'
            }, {
                field: 'endpointUrl',
                title: '控制节点'
            }, {
                field: 'gangliaUrl',
                title: '监控节点'
            }, {
                field: 'id',
                title: '操作',
                class: 'control-box',
                formatter:function(value,bean,index){
                    return util.buttons.render([
                        {
                            icon:"fa fa-edit",
                            tips:"修改",
                            click:"editCenter('"+bean.id+"')"
                        },{
                            icon:"fa fa-trash",
                            tips:"删除",
                            click:"removeCenter('"+bean.id+"','"+bean.name+"')"
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
                    value:"%"+$("#name").val()+"%"
                }])
        	};
        }
    });

    $("#search-btn").click(function(){
        centerTable.search();
    });
    $("#add-btn").click(function(){
        addCenter();
    });
});
function addCenter(){
    util.deserialize($("#dialog"),{type:'openstack'});
    $("#dialog").find("INPUT[name='code']").attr('validType','length(0,75);exists(../rest/bean/existsCenter,code)');
    $("#dialog").find("INPUT[name='code']").removeAttr('disabled');
    var d = Dialog.open({
        content:'#dialog',
        title:'添加数据中心',
        buttons:[
            {
                text:'完成',
                click:function(){
                    if(ValidatorUtil.check(d.$dialog)){
                        var center = util.serialize(d.$dialog);
                        util.create('Center',
                            center,function(r){
                            if(r&&r.ok){
                                util.success("新增成功");
                                d.close();
                            }else{
                                util.error(r.msg)
                            }
                            centerTable.search();
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

function editCenter(id){
    var user = centerTable.getRowById(id);
    util.deserialize($("#dialog"),user);
    var roleSelectBox = null;
    $("#dialog").find("INPUT[name='code']").attr('validType','length(0,75)');
    $("#dialog").find("INPUT[name='code']").attr('disabled','');

    var d = Dialog.open({
        content:'#dialog',
        title:'修改数据中心',
        buttons:[
            {
                text:'完成',
                click:function(){
                   if(ValidatorUtil.check(d.$dialog)){
                       var center = util.serialize(d.$dialog);
                       util.update('Center',
                           center,function(r){
                           if(r&&r.ok){
                               util.success("修改成功");
                               d.close();
                           }else{
                               util.error(r.msg)
                           }
                           centerTable.search();
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
function removeCenter(id,name){
    util.confirm('确定删除:'+name+'？',function(){
        util.remove('Center',id,function(r){
            if(r&&r.ok){
                util.success("删除成功");
                centerTable.search();
            }else{
                util.error(r.msg);
            }
        });
    });
}
