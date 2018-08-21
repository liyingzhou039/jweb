window.onload = function () {
	$("#username").text("  "+localStorage.getItem('username'));
	util.get('rest/identity/user/menus',{},function(menus){
		util.loading.enable = false;
		var menuNodes = util.list2Tree(menus,{id:'code',pId:'parentCode'});
		var h='';
		for(var i=0;i<menuNodes.length;i++){
			var click='';
			var bottom='';
			var children = menuNodes[i].nodes;
			if(!children||children.length<1){
				if(menuNodes[i].name=="修改密码"||menuNodes[i].name=="首页"){
					click='onclick="sys.openWindow({id:\''+menuNodes[i].code+'\',url:\''+menuNodes[i].href+'\'})"';
				}else{
					click='onclick="sys.openWindow({id:\''+menuNodes[i].code+'\',title:\''+menuNodes[i].name+'\',url:\''+menuNodes[i].href+'\'})"';
				}
			}
			if(i>3){
				bottom='d-up';
			}
			h+='<div class="menu-btn" '+click+'>';
			h+='	<i class="fa '+menuNodes[i].icon+'"></i>';
			h+='	<span class="text">'+menuNodes[i].name+'</span>';
			if(children&&children.length){
				h+='<ul class="menu-children '+bottom+'" >';
				for(var ii=0;ii<children.length;ii++){
					var click2='';
					var three = children[ii].nodes;
					if(!three||three.length<1){
						if(children[ii].name=='修改密码'||children[ii].name=='首页'){
							click2='onclick="sys.openWindow({id:\''+children[ii].code+'\',url:\''+children[ii].href+'\'})"';
						}else{
							click2='onclick="sys.openWindow({id:\''+children[ii].code+'\',title:\''+children[ii].name+'\',url:\''+children[ii].href+'\'})"';
						}
					}
					h+='<li '+click2+'>'+children[ii].name;
					if(three&&three.length){
						h+='<ul class="menu-children">';
						for(var iii=0;iii<three.length;iii++){
							var click3=null;
							if(three[iii].name=='修改密码'||three[iii].name=='首页'){
								click3 = 'onclick="sys.openWindow({id:\''+three[iii].code+'\',url:\''+three[iii].href+'\'})"';
							h+='<li '+click3+'>'+three[iii].name+'</li>';
							}else{
								click3 = 'onclick="sys.openWindow({id:\''+three[iii].code+'\',title:\''+three[iii].name+'\',url:\''+three[iii].href+'\'})"';
								h+='<li '+click3+'>'+three[iii].name+'</li>';
							}
						}
						h+='</ul>';
					}
					h+='</li>';
				}
				h+='</ul>';
			}
			h+='</div>';
		}
		$('#appSider').prepend(h);
	});
    //获取css前缀
    function getPrefix(){
        var styles = {};
        if(window.getComputedStyle){
            styles = window.getComputedStyle(document.documentElement)//IE9+
        }else {
            return ''
        }
        if(styles['-webkit-transform']){
            return '-webkit-'
        }
        if(styles['-moz-transform']){
            return '-moz-'
        }
        if(styles['-ms-transform']){
            return '-ms-'
        }
        return ''
    }
    function getTransition(v){
        return [getPrefix(), 'transition: ', v, ';'].join('');
    }
    function getTransform(v){
        return [getPrefix(), 'transform: ', v, ';'].join('');
    }
    $(function(){
    	if(document.getElementById('appLogo'))
    		document.getElementById('appLogo').setAttribute('style', getTransition('all .5s ease-out') + 'opacity: 1;');
        if(document.getElementById('appHeader'))
    		document.getElementById('appHeader').setAttribute('style', getTransition('all .5s ease-out') + getTransform('translateY(0)') + 'opacity: 1;');
        if(document.getElementById('appSider'))
        	document.getElementById('appSider').setAttribute('style', getTransition('all .5s ease-out') + getTransform('translateX(0)') + 'opacity: 1;');
    });
    
  	//菜单扩展收缩
    (function(){
        var btn = document.getElementById('btnMenuExtend'),
            menu = document.getElementById('appSider')
            ;
        if(btn) btn.onclick = function(){
            var className = menu.getAttribute('class');
            if(!className || className === ''){
                menu.setAttribute('class', 'extend');
                $("#appContent").css({left:70});
                var width = $(window).width()-172;
	            $("#appContent").width(width);
	            $("#appContent").find("IFRAME").width(width);
	            $("#appContent").find("IFRAME").resize();
            }else {
                menu.setAttribute('class', '');
                var width = $(window).width()-102;
                $("#appContent").css({left:0});
                $("#appContent").width(width);
                $("#appContent").find("IFRAME").width(width);
                $("#appContent").find("IFRAME").resize();
            }
        }
        
        $(window).resize(function(){
        	var menu = document.getElementById('appSider');
        	if(!menu) return ;
        	var className = menu.getAttribute('class');
            if(!className || className === ''){
                $("#appContent").animate({left:0});
                var width = $(window).width()-102;
	            $("#appContent").width(width);
	            $("#appContent").find("IFRAME").width(width);
            }else {
                var width = $(window).width()-(172+34);
                $("#appContent").animate({left:(70+34)});
                $("#appContent").width(width);
                $("#appContent").find("IFRAME").width(width);
            }
        });
    })();
    //收藏夹
    (function(){
        var btn = document.getElementById('btnCollection'),
            btnEdit = document.getElementById('btnCollectionEdit'),
            btnClose = document.getElementById('btnCollectionClose'),
            menu = document.getElementById('appCollection')
        ;
        if(btn) btn.onclick = function(){
            var className = menu.getAttribute('class');
            if(!className || className === '' || className === 'edit'){
                menu.setAttribute('class', 'active');
            }else {
                menu.setAttribute('class', '');
            }
        };
        if(btnEdit) btnEdit.onclick = function(){
            var className = menu.getAttribute('class');
            if(className === 'active'){
                menu.setAttribute('class', 'edit');
            }else {
                menu.setAttribute('class', 'active');
            }
        };
        if(btnClose) btnClose.onclick = function(){
            menu.setAttribute('class', '');
        }
    })();

    //刷新收藏
    refreshFavorites();
    
    //sys.openWindow({id:'_home',url:'/home'});
    
    $("#btnMenuExtend").trigger("click");
};
//刷新收藏夹
function refreshFavorites(){
	//@todo
	//收藏列表需要重新修改为-当前用户的收藏
	util.list(
	    'Favorite',
	    function(data){
            var h ='';
            for(var i=0;i<data.length;i++){
            	var onclickHtml='onclick="sys.openWindow({id:\''+data[i].winId+'\',title:\''+data[i].name+'\',url:\''
            	+data[i].url+'\'});document.getElementById(\'btnCollectionClose\').click();"';
            	h+='<li '+onclickHtml+' ><i class="fa fa-tag"></i>'+data[i].name+'<i favoriteId="'+data[i].id+'" class="fa fa-close"></i></li>';
            }
		    $('#items').empty().append(h);
            //绑定删除事件
            $('#items').find("LI").find(".fa-close").unbind().click(function(event){
                var favoriteId = $(this).attr("favoriteId");
                util.remove('Favorite',favoriteId,function(r){
                    refreshFavorites();
                });
                event.stopPropagation();
            });
        },
        [{
            relation:"and",
            expression:"eq",
            name:"username",
            value:localStorage.getItem('username')
        }]
	);
    
}
var sys={
	windows:[],
	data:{},
	openWindow:function(opt/*{id:x,title:x,url:x}*/){
		if(!opt.url){
			opt.url="error";
		}
		if(this.windows.length>6&&$.inArray(opt.id, this.windows)==-1){
			util.info('打开窗口太多，请关闭部分窗口');
			return;
		}
		var token = localStorage.getItem('_token');
		if(opt.url.indexOf("_token=")!=-1){
			opt.url = opt.url.substring(0,opt.url.indexOf("_token=")-1);
		}
		if(opt.url&&opt.url.indexOf("?")!=-1){
			opt.url = opt.url +"&_token="+token;
		}else{
			opt.url = opt.url +"?_token="+token;
		}
		if($.inArray(opt.id, this.windows)!=-1){
			//如果已经打开则切换为当前窗口
			this.activeWindow(opt.id);
			return ;
		}
		this.windows.push(opt.id);
		this.data[opt.id] = opt;
		if(opt.title){
			var titleHtml = '';
			titleHtml+='	<div winid="'+opt.id+'" wintitle="'+opt.title+'" winurl="'+opt.url+'" id="title_'+opt.id+'" class="tab-title tab-title-active">      ';
			titleHtml+='        <span class="title">'+opt.title+'</span>                           				   ';
			titleHtml+='        <i class="fa fa-remove btn-close" title="关闭"></i>                                 ';
			titleHtml+='        <i class="fa fa-star-o btn-like" title="收藏"></i>                                  ';
			titleHtml+='    </div>                                                                                 ';
	        
			var $title=$(titleHtml);
			$("#appHeader").find(".tab-titles").find(".tab-title-active").removeClass("tab-title-active");
	        $("#appHeader").find(".tab-titles").append($title);
	        //点击收藏
	        $title.find(".btn-like").click(function(){
	        	var $like = $(this).clone().css("color","orange");
	        	var $likeBag = $("#btnCollection").find(".fa-star-o");
	        	var start={position:'absolute',zIndex:'99999',left:$(this).offset().left,top:$(this).offset().top};
	        	var end={left:$likeBag.offset().left+28,top:$likeBag.offset().top+22};
	        	$like.appendTo($("BODY"));
	        	$like.css(start);
	        	
	        	
	        	var fa ={};
	        	fa.username=localStorage.getItem('username');
	        	fa.winId=$(this).parent().attr("winid");
	        	fa.name=$(this).parent().attr("wintitle");
	        	fa.url=$(this).parent().attr("winurl");
	        	util.list('Favorite',function(r){
	        		if(r&&r.length){
	        			$like.animate(end,1000,'swing',function(){
	        				$like.remove();
	        				util.info('您已经收藏过该功能');
	        			});
	        		}else{
	        			util.create('Favorite',fa,function(rr){
	        				$like.animate(end,1000,'swing',function(){
	                    		$like.remove();
	                    		if(rr&&rr.ok){
	                    			util.success("收藏成功");
	                    			refreshFavorites();
	                    		}else{
	                    			util.error("收藏失败");
	                    		}
	                    	});
	                	});
	        		}
	        	},[
                	{
                		relation:"and",
                		expression:"eq",
                		name:"winId",
                		value:fa.winId
                	},{
                  		relation:"and",
                  		expression:"eq",
                  		name:"username",
                  		value:fa.username
                  	}
                ]);
	        });
	        
	        var self = this;
	        $title.click(function(){
	        	self.activeWindow($(this).attr("winid"));
	        });
	        $title.find(".btn-close").click(function(){
	        	self.closeWindow($(this).parent().attr("winid"));
	        });
		}
        
        var contentIframe = document.createElement('iframe');
        contentIframe.width = '100%';
        contentIframe.height = '100%';
        contentIframe.frameBorder = '0';
        contentIframe.src = opt.url;
        contentIframe.name = opt.title;
        contentIframe.id = "window_"+opt.id;
        if(document.getElementById('appContent'))
        	document.getElementById('appContent').appendChild(contentIframe);
        $(contentIframe).load(function(){
        	var src = $(this).attr("src");
        	if(src&&src.indexOf("swagger-ui.html")!=-1){
        		$(contentIframe.contentWindow.document).find("#header").css("display","none");
        	}
        });
        this.activeWindow(opt.id);
	},
	closeWindow:function(id){
		this.windows.splice($.inArray(id, this.windows),1);
		this.data[id] = null;
		$("#title_"+id).remove();
    	$("#window_"+id).remove();
    	if(this.windows.length)
    		this.activeWindow(this.windows[this.windows.length-1]);
	},
	activeWindow:function(id){
		$("#appHeader").find(".tab-titles").find(".tab-title-active").removeClass("tab-title-active");
		$("#title_"+id).addClass("tab-title-active");
		for(var i=0;i<this.windows.length;i++){
			$("#window_"+this.windows[i]).hide();
		}
		$("#window_"+id).show();
	}
};

/*
* function 为id标签绑定输入监听，并根据输入字符串进行模糊查询
* params id 需要模糊查询的标签id
* params source 模糊查询的数据源
* params list 提示下拉菜单id
* */
var fuzzyFind = function fuzzyFind(id, source) {
    //如果中文还没有输完，
    if ($(this).prop('comStart')) return;
    //class中含有id的标签
    var $tag = $('#' + id);

    //阻止自带的提示功能
    $tag.attr('autocomplete', 'off');

    //提示列表默认id
    var listId = 'tipList';
    //如果已经有id为list的标签，重新生成一个id
    if ($('#' + listId).length > 0) listId = newId(listId);

    //添加校验正则，取消非法输入
    // $tag.attr('onkeyup','this.value=this.value.replace(/[^\'\u4e00-\u9fa5a-zA-Z0-9]/,"")');
    //绑定事件
    $tag.on('input', function () {
        //获取最新的输入参数
        var inputData = $tag.val();
        //非中文字母数字和'置空，因为中文输入法会自动加'
        // inputData = inputData.replace(/[^\'\u4e00-\u9fa5a-zA-Z0-9]/,'');
        //输入清空时不进行操作
        if (inputData === "") return tipList(id, "", listId);
        //遍历数据源
        var result = [];
        $.each(source, function (key, value) {
            //全称简称名称都有才匹配
            if (value.jmpn && value.qrpn && value.name) {
                if (value.jmpn.search(inputData) !== -1 || value.qrpn.search(inputData) !== -1 || value.name.search(inputData) !== -1) {
                    result.push(value.name);
                }
            }
        });
        //添加list属性，作为提示下拉菜单id
        $(this).attr('list', listId);
        //生成下拉列表
        tipList(id, result, listId);

        //中文输入开始
    }).on('compositionstart', function () {
        $(this).prop('comStart', true);
        //中文输入结束
    }).on('compositionend', function () {
        $(this).prop('comStart', false);
        //阻止默认的enter键提交行为
    }).on('keydown', function () {
        if (event.keyCode === 13) return false;
        //监听快捷键
    }).keyup(function (k) {
        var key = k.which;
        var tiplist = $('#' + listId).find('li');
        //回车
        if (key === 13) {
            $('#' + listId).find('li.selected').click();
            //上箭头
        } else if (key === 38) {
            var selected = $('#' + listId).find('li.selected');
            if (selected.length > 0) {
                selected.trigger('mouseleave');
                if (selected.prev().length > 0) {
                    selected.prev().trigger('mouseenter');
                } else {
                    return false;
                }
            } else {
                tiplist.last().trigger('mouseenter');
            }
            //下箭头
        } else if (key === 40) {
            var _selected = $('#' + listId).find('li.selected');
            if (_selected.length > 0) {
                _selected.trigger('mouseleave');
                if (_selected.next().length > 0) {
                    _selected.next().trigger('mouseenter');
                } else {
                    return false;
                }
            } else {
                tiplist.first().trigger('mouseenter');
            }
        } else return false;
    });
    //id标签失焦事件
    $tag.blur(function () {
        //删除dataList
        $('#'+listId).remove();
        //清空用户输入
        $tag.val("");
        //$tag.trigger('sdsdsdsd sdsd');
    });
};

/*
* function 生成提示下拉菜单
* params id 与之绑定的标签的id
* params array 生成提示菜单的json数组
* params list 提示下拉菜单id
* */
var tipList = function tipList(id, jsonArray, list) {
    var $exist = $('#' + list);
    //如果已经存在，删除
    if ($exist) $exist.remove();
    //没有数据什么都不用做
    if (jsonArray === '') return false;

    var tipList = "<ul id=\"" + list + "\" class=\"tipListStyle\"></ul>";
    $('#' + id).after(tipList);
    var options = "";
    $.each(jsonArray, function (key, value) {
        options += "<li class=\"shortcut\" value=\"" + value + "\">" + value + "</li>";
    });
    //取新生成的列表
    var $tag = $('#' + list);
    $tag.append(options);
    //绑定li标签点击事件
    $tag.find($('li')).on('click', function () {
        var changeValue = $('#' + id);
        changeValue.val($(this).html());
        //跳转至当前选中项的href
    });
    //绑定li标签选中事件
    $tag.find($('li')).hover(function () {
        var selected = $('#' + list).find('li.selected');
        //已经有选中时移除原来的选中
        if (selected.length > 0) selected.trigger('mouseleave');
        $(this).toggleClass('selected');
    }, function () {
        $(this).toggleClass('selected');
    });

};

/*
* function 末尾数字+1生成新id
* description 传入重复id，如果是纯英文字符串，在末尾加1，如果末尾有数字，数字+1
* params id 重复id
* */
var newId = function newId(id) {
    var ID = id.replace(/^(\D+)(\d*)$/, function (id, head, tail) {
        tail = tail ? tail : 0;
        var a = parseInt(tail);
        return head + ++a;
    });
    if ($('#' + ID).length > 0) {
        ID = newId(ID);
    }
    return ID;
};

function logout(){
	$.ajax({  
		type: "get",  
		url: "../rest/identity/login/out",  
		dataType: "json",  
		success: function(r){ 
			if(r&&r.ok){
				location.href="/toLogin";
			}else{
				alert(r.msg);
			}
		},  
		error: function() {   
			location.href="/toLogin";  
		}    
	});  
}

function goHome(){
	sys.activeWindow('_home');
}