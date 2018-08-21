var util={
        uuid:function(){
		    function S4(){
		        return (((1+Math.random())*0x10000)|0).toString(16).substring(1);
		    }
		    return (S4()+S4()+S4()+S4()+S4()+S4()+S4()+S4());
	    },
		polling:function(settings){
			util.loading.enable=false;
			var defaultSetting = {
				interval:5000,
				type:'GET',
				url:'',
				dataType:'json',
				data:{},
				success:function(json){}
			}
			$.extend(defaultSetting,settings);
			
			setTimeout(function(){
				var _self = this;
				$.ajax({
					type:_self.type,
					url:_self.url,
					dataType:_self.dataType,
					data:_self.data,
					success : function(json) {
						var r = _self.success(json);
						if(!r){
							util.polling(_self);
						}else{
							util.loading.enable=true;
						}
					}
				});
			}.bind(defaultSetting),defaultSetting.interval);
		},
		contains:function(value,array){
			if(array&&array.length){
				for(var i=0;i<array.length;i++){
					if(array[i]==value)
						return true;
				}
			}
			return false;
		},
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
			if(isNaN(fileSize)||!fileSize){
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
		confirm:function(msg,ok,cancel){
			$("BODY",window.top.document).find("#_model-box").remove();
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
			
			var $confirmBox=$("BODY",window.top.document).find("#_model-box");
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
			if(window.top !== window.self){
				window.top.util.msg(msg,type);
				return ;
			}
			//判断body中有没有消息层，没有则加入
			var $msgBox=$("BODY",window.top.document).find("#_msg_box");
			if($msgBox.length==0){
				$msgBox = $('<div id="_msg_box" style="z-index:9999;width:400px;position:absolute;"></div>');
				$("BODY",window.top.document).append($msgBox);
			}
			var cx = $(window.top).width();
			$msgBox.css({top:60,left:(cx-400)/2});
			
			var $h=$('<div class="alert '+type+'" role="alert">'+msg+'</div>');
			$msgBox.append($h);
			
			var timeout = 3000;
			if("alert-danger"==type){
				timeout = 10000;
			}
			
			setTimeout(function(){
				$h.fadeOut(300,function(){
					$h.remove();
				}.bind(this));
			}.bind(this),timeout);
		},
		loading:{
			show:function(){
				if(!util.loading.enable) return;
				var $loading=$("BODY",window.document).find("#_loading");
				if($loading.length==0){
					$loading = $('<div id="_loading" style="text-align:center;opacity: .1;background-color: #000000;left:0;top:0;z-index:10000;position: fixed;float:left;"></div>');
					$loadingIcon=$('<div id="_loading_icon" style="text-align:center;left:0;top:0;z-index:10001;position: fixed;float:left;"><i style="color:#3870a8;font-size:60px;" class="fa fa-spinner fa-spin"></i></div>');
					$("BODY",window.document).append($loading);
					$("BODY",window.document).append($loadingIcon);
				}
				var cx = $(window).width();
				var cy = $(window).height();
				$loading.css({width:cx,height:cy});
				$loadingIcon.css({left:cx/2-30,top:cy/2-30});
			},
			hide:function(){
				$("BODY",window.document).find("#_loading").remove();
				$("BODY",window.document).find("#_loading_icon").remove();
			},
			enable:true
		},
		put:function(url,params,callback,options){
		    if(!options) options={};
		    options.type='PUT';
			return this.request(url,params,callback,options);
		},
		get:function(url,params,callback,options){
		    if(!options) options={};
            options.type='GET';
			return this.request(url,params,callback,options);
		},
		post:function(url,params,callback,options){
		    if(!options) options={};
            options.type='POST';
			return this.request(url,params,callback,options);
		},
		delete:function(url,params,callback,options){
		    if(!options) options={};
            options.type='DELETE';
			return this.request(url,params,callback,options);
		},
		request:function(url,params,callback,options){
		    if(!options) options={};
		    var defaultOptions={
		        type:'GET',
		        url:url,
		        dataType:'json',
		        data:params,
		        success : function(data) {
                    callback(data);
                },
                error : function() {
                	util.error("系统错误");
                }
		    };
		    for(var p in options){
		        defaultOptions[p] = options[p];
		    }
		    $.ajax(defaultOptions);
		},
		list:function(beanName,callback,conditions,order){
			if(!conditions) conditions=[];
			if(!order) order='';
			$.ajax({
				type :'GET',
				url : '/rest/bean/'+beanName,
				dataType : 'json',
				data : {
					conditions:JSON.stringify(conditions),
					order:order
				},
				success : function(data) {
					callback(data);
				},
				error : function() {
					util.error("系统错误");
				}
			});
		},
		getById:function(beanName,id,callback){
			$.ajax({
				type :'GET',
				url : '/rest/bean/'+beanName+'/'+id,
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
		create:function(beanName,bean,callback){
			$.ajax({
				type :'POST',
				url : '/rest/bean/'+beanName,
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
		update:function(beanName,bean,callback){
			$.ajax({
				type :'PUT',
				url : '/rest/bean/'+beanName,
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
		remove:function(beanName,id,callback){
			$.ajax({
				type :'DELETE',
				url : '/rest/bean/'+beanName+'/'+id,
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
			click:function(target,eType,e){
				var id = e.target.getAttribute('data-id');
				var menuList = util.buttons.dataMap[id];
				if(!menuList) menuList = [];
				for(var i=0;i<menuList.length;i++){
					menuList[i]['text']=menuList[i].tips;
				}
				new TargetMenu({
	                x: e.clientX,
	                y: e.clientY,
	                handleClick: function (item) {
	                    eval(item.click);
	                },
	                list: menuList
	            })
			},
			render:function(buttons){
				if(!buttons||buttons.length<=0){
					return '-';
				}
				var id='';
				for(var i=0;i<buttons.length;i++){
					id+=buttons[i].click.replace(/[\'\"]/g,'');
				}
				var h='';
				h+='<button class="btn btn-primary fa fa-angle-down" data-id="'+id+'" onclick="util.buttons.click(this,type,event)">操作</button>';
				if(!util.buttons.dataMap) util.buttons.dataMap={};
				util.buttons.dataMap[id]=buttons;
				/*for(var i=0;i<buttons.length;i++){
                    h+='<li><a onclick="'+buttons[i].click+'">'+buttons[i].tips+'</a></li>';
				}*/
                return h;
                
            
				/*if(!buttons||buttons.length<=0){
					return '<ul class="control-group"></ul>';
				}
				var h='';
				if(buttons.length<=3){
					h='<ul class="control-group">';
					for(var i=0;i<buttons.length;i++){
						var style=buttons[i].style?buttons[i].style:'';
						h+='<li onclick="'+buttons[i].click+'" title="'+buttons[i].tips+'"><i style="'+style+'"  class="'+buttons[i].icon+'"></i></li>';
					}
					h+='</ul>';
				}else{
					var h='<ul class="control-group">';
					for(var i=0;i<2;i++){
						var style=buttons[i].style?buttons[i].style:'';
						h+='<li onclick="'+buttons[i].click+'" title="'+buttons[i].tips+'"><i style="'+style+'"   class="'+buttons[i].icon+'"></i></li>';
					}
					h+='<li onclick="util.buttons.more(this)" title="更多"><i class="fa fa-ellipsis-v"></i></li>';
					h+='</ul>';
					
					h+='<ul class="control-group-all" style="display:none;width:'+(8+(buttons.length+1)*30)+'px;">';
					h+='<li onclick="util.buttons.little(this)" title="收缩"><i class="fa fa-chevron-right"></i></li>';
					for(var i=0;i<buttons.length;i++){
						var style=buttons[i].style?buttons[i].style:'';
						h+='<li onclick="'+buttons[i].click+'" title="'+buttons[i].tips+'"><i style="'+style+'" class="'+buttons[i].icon+'"></i></li>';
					}
					h+='</ul>'
				}
				return h;*/
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
    beforeSend: function(request,setting) {
    	var token = localStorage.getItem('_token');
    	var from = "ajax";
    	var url = setting.url;
    	if(url.indexOf("?")!=-1){
    		url+="&_token="+token+"&_from="+from;
    	}else{
    		url+="?_token="+token+"&_from="+from;
    	}
    	setting.url = url;
        request.setRequestHeader("_token",token);
        request.setRequestHeader("_from",from);
        util.loading.show();
    },
    complete:function(XHR, TS){
    	util.loading.hide();
    	if(XHR&&XHR.responseJSON){
	    	switch (XHR.responseJSON.status){
		        case(40101):
		            top.location.href="/toLogin";
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
	$("BODY",window.top.document).find("#_msg_box").empty();
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
};

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

/* global window */
var TargetMenu = function TargetMenu(option) {
    _classCallCheck(this, TargetMenu);

    // 最外层容器
    var box = document.createElement('div');
    box.setAttribute('class', 'target-menu');
    box.setAttribute('style', 'position: fixed; z-index: 9999; top: 0; left: 0; width: 100%; height: 100%;');

    // 遮罩
    var mask = document.createElement('div');
    mask.setAttribute('class', 'target-menu-mask');
    mask.setAttribute('style', 'position: absolute; z-index: 0; top: 0; left: 0; width: 100%; height: 100%;');
    mask.addEventListener('click', function () {
        document.body.removeChild(box);
    }, false);
    box.appendChild(mask);

    // menu
    var winWidth = document.documentElement.clientWidth;
    var winHeight = document.documentElement.clientHeight;
    var menu = document.createElement('ul');
    var menuWidth = option.maxWidth ? option.maxWidth : 102; // 默认最大宽102,5个14px汉字
    var menuHeight = 16 + 32 * option.list.length;
    var x = option.x;
    var y = option.y;
    if (x + menuWidth >= winWidth) {
        x = winWidth - menuWidth;
    }
    if (y + menuHeight >= winHeight) {
        y = winHeight - menuHeight;
    }
    menu.setAttribute('class', 'target-menu-menu');
    menu.setAttribute('style', 'position: absolute; z-index: 1; list-style: none; max-width: ' + menuWidth + 'px; background-color: #fff; left: ' + x + 'px; top: ' + y + 'px;');
    box.appendChild(menu);

    // 菜单项
    var fragment = document.createDocumentFragment();
    option.list.forEach(function (item) {
        var li = document.createElement('li');
        li.setAttribute('class', 'target-menu-item');
        li.setAttribute('style', 'overflow: hidden; text-overflow: ellipsis; white-space: nowrap;');
        li.addEventListener('click', function (e) {
            e.stopPropagation();
            document.body.removeChild(box);
            option.handleClick(item);
        }, false);
        li.innerText = item.text;
        fragment.appendChild(li);
    });
    menu.appendChild(fragment);

    document.body.appendChild(box);
};
