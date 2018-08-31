var imageTable =null;
var uploader = null;
var vbox = null;
$(function(){
    imageTable = new LchTable({
        url:'../rest/bean/ImagePager',
        columns: [
            {
                field: 'name',
                title: '名称'
            }, {
                field: 'fileName',
                title: '文件名'
            }, {
                field: 'diskFormat',
                title: '磁盘格式',
                formatter:function(value){
                    return value?value.toUpperCase():"";
                }
            }, {
                field: 'minDisk',
                title: '最小磁盘(GB)'
            }, {
                field: 'minRam',
                title: '最小内存(GB)'
            }, {
                field: 'size',
                title: '大小',
                formatter:function(value){
                    return util.fileSize(value);
                }
            }, {
                field: 'status',
                title: '状态'
            }, {
                field: 'enabled',
                title: '操作',
                class: 'control-box',
                formatter:function(value,image){
                    return util.buttons.render([
                        {
                            icon:"fa fa-edit",
                            tips:"编辑",
                            click:"updateImage('"+image.id+"')"
                        },{
                            icon:"fa fa-trash",
                            tips:"删除",
                            click:"removeImage('"+image.id+"','"+image.name+"')"
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

    $("#add-btn").click(function(){
        addImage();
    });
    $("#search-btn").click(function(){
    	imageTable.search();
    });

    uploader = WebUploader.create({
    	auto:true,
        pick: {
        	id: '#fileUpload',
        	name:"imageFile",
            label: '+',
            multiple:false
         },
         swf: '../plugins/webuploader/swf/Uploader.swf',
         fileVal:'imageFile',
         server: '../rest/compute/image/uploadImageFile',
         resize: false
    });
    uploader.on( "fileQueued", function(file){
        util.loading.show();
    });
    uploader.on( "uploadSuccess", function(file,res) {
        if(res && res.ok){
            initFileSelect();
        }else{
            util.error(res.msg);
        }
        util.loading.hide();
    });
    $("#removeFile").click(function(){
        var fileName = $("#imageDialog").find("SELECT[name='fileName']").val();
        if(!fileName){
            util.info("请选择镜像文件");
        }
        util.delete('../rest/compute/image/removeImageFile/'+encodeURI(encodeURI(fileName))+"/",{},function(r){
            if(r && r.ok){
                initFileSelect();
            }else{
                util.error(r.msg);
            }
        });
    });
    //元数据框初始化
    vbox = new KeyValueBox({
      	value:{}
    });
});
function initFileSelect(){
    util.get('../rest/compute/image/listImageFile',{},function(files){
        var h = '';
        for(var i=0;i<files.length;i++){
            h+='<option value="'+files[i]+'">'+files[i]+'</option>';
        }
        $("#imageDialog").find("SELECT[name='fileName']").empty().html(h);
    });
}
function addImage(){
    util.deserialize($("#imageDialog"),{
        diskFormat:'ISO',
        kernel:'LINUX',
        minDisk:0,
        minRam:0
    });
    initFileSelect();
    vbox.val({});
    $("#imageDialog").find("SELECT[name='fileName']").parent().show();
    var d = Dialog.open({
        content:'#imageDialog',
        width:'800px',
        title:'添加镜像',
        buttons:[
            {
                text:'完成',
                click:function(){
                    if(ValidatorUtil.check(d.$dialog)){
                        var image = util.serialize(d.$dialog);
                        image.metadata = vbox.val();
                        util.post('../rest/compute/image',JSON.stringify(image),function(r){
                            if(r&&r.ok){
                                util.success("新增成功");
                                d.close();
                            }else{
                                util.error(r.msg)
                            }
                            imageTable.search();
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
function updateImage(imageId){
    var image = imageTable.getRowById(imageId);
    util.deserialize($("#imageDialog"),image);
    vbox.val(JSON.parse(image.metadataJson));
    initFileSelect();
    $("#imageDialog").find("SELECT[name='fileName']").parent().hide();
    var d = Dialog.open({
        content:'#imageDialog',
        width:'800px',
        title:'编辑镜像',
        buttons:[
            {
                text:'完成',
                click:function(){
                    if(ValidatorUtil.check(d.$dialog)){
                        var image = util.serialize(d.$dialog);
                        image.metadata = vbox.val();
                        util.put('../rest/compute/image',JSON.stringify(image),function(r){
                            if(r&&r.ok){
                                util.success("修改成功");
                                d.close();
                            }else{
                                util.error(r.msg)
                            }
                            imageTable.search();
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

function removeImage(id,name){
    util.confirm('确定删除:'+name+'？',function(){
        util.delete('../rest/compute/image/'+id,{},function(r){
            if(r&&r.ok){
                util.success("删除成功");
                imageTable.search();
            }else{
                util.error(r.msg);
            }
        });
    });
}