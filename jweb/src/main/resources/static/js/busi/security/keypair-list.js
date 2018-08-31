var keypairTable =null;
$(function(){
    keypairTable = new LchTable({
        url:'../rest/security/keypair/pager',
        columns:[
            {
                field: 'name',
                title: '名称'
            }, {
                field: 'id',
                title: '操作',
                class: 'control-box',
                formatter:function(id,keypair){
                    return util.buttons.render([
                        {
                            icon:"fa fa-trash",
                            tips:"删除",
                            click:"removeKeypair('"+keypair.id+"','"+keypair.name+"')"
                        },{
                            icon:"fa fa-download",
                            tips:"导出",
                            click:"exportKeypair('"+id+"')"
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
        keypairTable.search();
    });
    $("#add-btn").click(function(){
        addKeypair();
    });
});
function addKeypair(){
    util.deserialize($("#keypairDialog"),{});
    var d = Dialog.open({
        content:'#keypairDialog',
        width:'800px',
        title:'新增密钥',
        buttons:[
            {
                text:'完成',
                click:function(){
                    if(ValidatorUtil.check(d.$dialog)){
                        var keypair = util.serialize(d.$dialog);
                        util.post('../rest/security/keypair',JSON.stringify(keypair),function(r){
                            if(r&&r.ok){
                                util.success("新增成功");
                                d.close();
                            }else{
                                util.error(r.msg)
                            }
                            keypairTable.search();
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

function removeKeypair(id,name){
    util.confirm('确定删除:'+name+'？',function(){
        util.delete('../rest/security/keypair/'+id,{},function(r){
            if(r&&r.ok){
                util.success("删除成功");
                keypairTable.search();
            }else{
                util.error(r.msg);
            }
        });
    });
}

function exportKeypair(id) {
    window.location.href ='../rest/security/keypair/export/'+id+'?_token='+localStorage.getItem('_token');
}