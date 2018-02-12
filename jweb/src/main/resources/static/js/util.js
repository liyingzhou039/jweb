var util={
		isAdmin:function(){
			var isAdmin = localStorage.getItem('isAdmin');
			if(isAdmin=='true'){
				return true;
			}
			return false;
		},
		getUrlParam:function(name){
			var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");  
			var search = window.location.search;
			if(search.length>0){
				search=search.substr(1);
			}
			var r = search.match(reg);
			//返回参数值  
			if (r!=null) return unescape(r[2]);  
			return null;  
		},
		fileSize:function(fileSize)
		{
			if(isNaN(fileSize)){
				return 0;
			}
			var units =['B','KB','MB','GB','TB'];
			var sizes =[0,1<<10,1<<20,1<<30,Math.pow(2,40)];//超多32位的左移溢出
			size=fileSize<0?0:fileSize;
			for(var i=sizes.length-1;i>=0;i--){
				if(size>=sizes[i]){
					return (size/sizes[i]).toFixed(2)+units[i];
				}
			}
		},
		serialize:function($fm) {
			var serializeObj = {};
			$fm.find("[name]").each(function(){
				var type=$(this).attr('type');
				if(type&&type.toLowerCase()=='checkbox'){
					if($(this).is(":checked")){
						serializeObj[$(this).attr('name')]=true;
					}else{
						serializeObj[$(this).attr('name')]=false;
					}
				}else{
					serializeObj[$(this).attr('name')]=$(this).val();
				}
			});
			return serializeObj;
		},
		deserialize:function($fm,bean) {
			$fm.find("[name]").each(function(){
				var name = $(this).attr("name");
				if(bean[name]==undefined||bean[name]==null)
					bean[name] = '';
				var type=$(this).attr('type');
				if(type&&type.toLowerCase()=='checkbox'){
					if(bean[name]){
						this.checked=true;
					}else{
						this.checked=false;
					}
				}else{
					$(this).val(bean[name]);
				}
			});
		},
		confirm(msg,ok,cancel){
			var $confirmBox=$("BODY",window.top.document).find("#_model-box");
			if($confirmBox.length==0){
				var h='';
				h+='	<div class="modal fade" id="_model-box"  role="dialog" style="top:30px;">                                                                      ';
				h+='	  <div class="modal-dialog" role="document">                                                                                                   ';
				h+='	    <div class="modal-content">                                                                                                                ';
				h+='	      <div class="modal-body">                                                                                                                 ';
				h+='	      </div>                                                                                                                                   ';
				h+='	      <div class="modal-footer">                                                                                                               ';
				h+='	        <button type="button" class="btn btn-default cancel" data-dismiss="modal">取消</button>                                                 ';
				h+='	        <button type="button" class="btn btn-primary ok">确定</button>                                                                          ';
				h+='	      </div>                                                                                                                                   ';
				h+='	    </div>                                                                                                                                     ';
				h+='	  </div>                                                                                                                                       ';
				h+='	</div>                                                                                                                                         ';
				$("BODY",window.top.document).append(h);
			}
			$confirmBox.find(".modal-body").html(msg);
			$confirmBox.find(".ok").unbind().click(function(){
				$confirmBox.modal("hide");
				if(ok) ok();
			}.bind(this));
			$confirmBox.find(".cancel").unbind().click(function(){
				if(cancel) cancel();
			}.bind(this));
			$confirmBox.modal({backdrop: "static"});
		},
		info:function(msg){
			util.msg(msg,'alert-info');
		},
		success:function(msg){
			util.msg(msg,'alert-success');
		},
		error:function(msg){
			util.msg(msg,'alert-danger');
		},
		msg:function(msg,type){
			//判断body中有没有消息层，没有则加入
			var $msgBox=$("BODY",window.top.document).find("#_msg_box");
			if($msgBox.length==0){
				$msgBox = $('<div id="_msg_box" style="z-index:9999;width:400px;position:absolute;"></div>');
				$("BODY",window.top.document).append($msgBox);
				$(window.top).resize(function(){
					var cx = $(window.top).width();
					$msgBox.css({top:60,left:(cx-400)/2});
				}).resize();
			}
			$(window.top).resize();
			var $h=$('<div class="alert '+type+'" role="alert">'+msg+'</div>');
			$msgBox.append($h);
			
			setTimeout(function(){
				$h.fadeOut(300,function(){
					$h.remove();
				}.bind(this));
			}.bind(this),3000);
		},
		put:function(url,params,callback){
			return this.request(url,params,callback,'PUT');
		},
		get:function(url,params,callback){
			return this.request(url,params,callback,'GET');
		},
		post:function(url,params,callback){
			return this.request(url,params,callback,'POST');
		},
		delete:function(url,params,callback){
			return this.request(url,params,callback,'DELETE');
		},
		request:function(url,params,callback,method){
			$.ajax({
				type :method,
				url : url,
				dataType : 'json',
				data : params,
				success : function(data) {
					callback(data);
				},
				error : function() {
					util.error("系统错误");
				}
			});
		},
		list:function(bean,callback){
			if(!bean.condition) bean.condition='';
			if(!bean.order) bean.order='';
			$.ajax({
				type :'GET',
				url : '/rest/bean/list'+bean.beanName,
				dataType : 'json',
				data : {
					conditions:JSON.stringify(bean.conditions),
					order:bean.order
				},
				success : function(data) {
					callback(data);
				},
				error : function() {
					util.error("系统错误");
				}
			});
		},
		getById:function(bean,callback){
			$.ajax({
				type :'GET',
				url : '/rest/bean/getById'+bean.beanName+'/'+bean.id,
				dataType : 'json',
				data : {},
				success : function(data) {
					callback(data);
				},
				error : function() {
					util.error("系统错误");
				}
			});
		},
		create:function(bean,callback){
			$.ajax({
				type :'POST',
				url : '/rest/bean/create'+bean.beanName,
				dataType : 'json',
				data : {beanJson:JSON.stringify(bean)},
				success : function(data) {
					callback(data);
				},
				error : function() {
					util.error("系统错误");
				}
			});
		},
		createBatch:function(beans,callback){
			$.ajax({
				type :'POST',
				url : '/rest/bean/createBatch'+beans.beanName+"",
				dataType : 'json',
				data : {beansJson:JSON.stringify(beans)},
				success : function(data) {
					callback(data);
				},
				error : function() {
					util.error("系统错误");
				}
			});
		},
		update:function(bean,callback){
			$.ajax({
				type :'PUT',
				url : '/rest/bean/update'+bean.beanName,
				dataType : 'json',
				data : {beanJson:JSON.stringify(bean)},
				success : function(data) {
					callback(data);
				},
				error : function() {
					util.error("系统错误");
				}
			});
		},
		remove:function(bean,callback){
			$.ajax({
				type :'DELETE',
				url : '/rest/bean/remove'+bean.beanName+'/'+bean.id,
				dataType : 'json',
				data : {},
				success : function(data) {
					callback(data);
				},
				error : function() {
					util.error("系统错误");
				}
			});
		},
		removeBatch:function(beans,callback){
			var ids='';
			for(var i=0;i<beans.length;i++){
				if(ids.length>0) ids+=",";
				ids+=beans[i].id;
			}
			$.ajax({
				type :'DELETE',
				url : '/rest/bean/removeBatch'+beans.beanName+'/'+ids,
				dataType : 'json',
				data : {},
				success : function(data) {
					callback(data);
				},
				error : function() {
					util.error("系统错误");
				}
			});
		},
		/**
		 *
		 * 列表数据转为树数据
		 * 使用demo：
		 * var data=[
		 {
		     id:'sys1',
		     p_id:'-1',
		     title:'-sys1',
		     fn:'fn1'
		 },
		 {
		     id:'sys2',
		     p_id:'-1',
		     title:'-sys2',
		     fn:'fn2'
		 },
		 {
		     id:'sys11',
		     p_id:'sys1',
		     title:'-sys1-1',
		     fn:'fn3'
		 },
		 {
		     id:'sys12',
		     p_id:'sys1',
		     title:'-sys1-2',
		     fn:'fn4'
		 }
		 ];

		 var r = list2Tree(data,{nodeId:'id',pId:'p_id',text:'title'});
		 */
		list2Tree:function(list,opt){

		    function getRoot(list,pId){
		        var roots = [];
		        for(var i=0;i<list.length;i++){
		            if(list[i].pId==pId){
		                roots.push(list[i]);
		            }
		        }
		        return roots;
		    }
		    function buildTree(roots,list){
		        for(var i=0;i<roots.length;i++){
		            var root = roots[i];
		            var nodes = [];
		            for(var j=0;j<list.length;j++){
		                var node = list[j];
		                if(node.pId==root.id){
		                    nodes.push(node);
		                }
		            }
		            root.nodes = nodes;
		            buildTree(root.nodes,list);
		        }

		        return roots;
		    }

		    if(!list) return [];
		    if(!opt) opt = {};

		    for(var i=0;i<list.length;i++){
		        for(var p in opt){
		            list[i][p] = list[i][opt[p]];
		        }
		        if(!list[i].pId) list[i].pId=-1;
		    }

		    var tree = buildTree(getRoot(list,-1),list);

		    return tree;
		},
		buttons:{
			render:function(buttons){
				if(!buttons||buttons.length<=0){
					return '<ul class="control-group"></ul>';
				}
				var h='';
				if(buttons.length<=3){
					h='<ul class="control-group">';
					for(var i=0;i<buttons.length;i++){
						h+='<li onclick="'+buttons[i].click+'" title="'+buttons[i].tips+'"><i class="'+buttons[i].icon+'"></i></li>';
					}
					h+='</ul>';
				}else{
					var h='<ul class="control-group">';
					for(var i=0;i<2;i++){
						h+='<li onclick="'+buttons[i].click+'" title="'+buttons[i].tips+'"><i class="'+buttons[i].icon+'"></i></li>';
					}
					h+='<li onclick="util.buttons.more(this)" title="更多"><i class="fa fa-ellipsis-v"></i></li>';
					h+='</ul>';
					
					h+='<ul class="control-group-all" style="display:none;width:'+(8+(buttons.length+1)*30)+'px;">';
					h+='<li onclick="util.buttons.little(this)" title="收缩"><i class="fa fa-chevron-right"></i></li>';
					for(var i=0;i<buttons.length;i++){
						h+='<li onclick="'+buttons[i].click+'" title="'+buttons[i].tips+'"><i class="'+buttons[i].icon+'"></i></li>';
					}
					h+='</ul>'
				}
				return h;
			},
			more:function(bt){
				$(bt).parent().hide().next().show();
			},
			little:function(bt){
				$(bt).parent().hide().prev().show();
			}
		}
}
$.ajaxSetup({
    //发送之前向请求头里面塞了一个token
    beforeSend: function(request) {
        request.setRequestHeader("_token", localStorage.getItem('_token'));
        request.setRequestHeader("_from", "ajax");
    },
    complete(XHR, TS){
    	if(XHR&&XHR.responseJSON){
	    	switch (XHR.responseJSON.status){
		        case(40101):
		            top.location.href="/login";
		            break;
		        default:
		            ;
		    }
    	}
    }
});
function initTitle(){
	var title=document.title;
	var h='';
	h+='	<div class="content-title">                  ';
	h+='       <div class="bg">                          ';
	h+='           <span class="text">'+title+'</span>   ';
	h+='       </div>                                    ';
	h+='   </div>                                        ';
	$(".container-fluid").find(".content-title").remove();
	$(".container-fluid").prepend(h);
}
//标题初始化
$(function(){
	initTitle();
});

var treeViewHelper = {};
/**
 * tree view遍历节点值
 * @param node
 * @returns {Array}
 */
treeViewHelper.getChildrenNodeIdArr = function ( node ){
    var ts = [];
    if(node.nodes){
        for(x in node.nodes){
            ts.push(node.nodes[x].nodeId)
            if(node.nodes[x].nodes){
                var getNodeDieDai = this.getChildrenNodeIdArr(node.nodes[x]);
                for(j in getNodeDieDai){
                    ts.push(getNodeDieDai[j]);
                }
            }
        }
    }else{
        ts.push(node.nodeId);
    }
    return ts;
}
/**
 * 获取treeview的父级节点
 * @param treeId
 * @param node
 * @returns {Array}
 */
treeViewHelper.getParentIdArr = function (treeId,node){
    var ts = [];
    var parent  =   $('#'+treeId).treeview('getParent', node);
    while(parent.id&&parent.id!=0){
        ts.push(parent);
        parent = $('#'+treeId).treeview('getParent', parent);
    }
    return ts;
}