window.onload = function () {
	util.get('rest/system/power/getUserPowers',{},function(power){
		var menus = power.menus;
		var menuNodes = util.list2Tree(menus,{id:'id',pId:'parentId'});
		var h='';
		for(var i=0;i<menuNodes.length;i++){
			var click='';
			var bottom='';
			var children = menuNodes[i].nodes;
			if(!children||children.length<1){
				click='onclick="sys.openWindow({id:\''+menuNodes[i].id+'\',title:\''+menuNodes[i].name+'\',url:\''+menuNodes[i].url+'\'})"';
			}
			if(i>3){
				bottom='item-bottom';
			}
			h+='<div class="menu-btn '+bottom+'" '+click+'>';
			h+='	<i class="fa '+menuNodes[i].icon+'"></i>';
			h+='	<span class="text">'+menuNodes[i].name+'</span>';
			if(children&&children.length){
				h+='<ul class="menu-children">';
				for(var ii=0;ii<children.length;ii++){
					var click2='';
					var three = children[ii].nodes;
					if(!three||three.length<1){
						click2='onclick="sys.openWindow({id:\''+children[ii].id+'\',title:\''+children[ii].name+'\',url:\''+children[ii].url+'\'})"';
					}
					h+='<li '+click2+'>'+children[ii].name;
					if(three&&three.length){
						h+='<ul class="menu-third">';
						for(var iii=0;iii<three.length;iii++){
							var click3 = 'onclick="sys.openWindow({id:\''+three[iii].id+'\',title:\''+three[iii].name+'\',url:\''+three[iii].url+'\'})"';
							h+='<li '+click3+'>'+three[iii].name+'</li>';
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
		$('#appSider ul.menu-children > li').click(function () {
            if($(this).hasClass('li-clicked')){
                $(this).removeClass('li-clicked')
            }else {
                $(this).addClass('li-clicked')
            }
        }).hover(function(){}, function(){
            if($(this).hasClass('li-clicked')){
                $(this).removeClass('li-clicked')
            }
        });
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
    document.getElementById('appLogo').setAttribute('style', getTransition('all .5s ease-out') + 'opacity: 1;');
    document.getElementById('appHeader').setAttribute('style', getTransition('all .5s ease-out') + getTransform('translateY(0)') + 'opacity: 1;');
    document.getElementById('appSider').setAttribute('style', getTransition('all .5s ease-out') + getTransform('translateX(0)') + 'opacity: 1;');
	
  	//菜单扩展收缩
    (function(){
        var btn = document.getElementById('btnMenuExtend'),
            menu = document.getElementById('appSider')
            ;
        btn.onclick = function(){
            var className = menu.getAttribute('class');
            if(!className || className === ''){
                menu.setAttribute('class', 'extend');
                $("#appContent").animate({left:70},'slow','swing',function(){
                	 $("#appContent").width($("#appContent").width()-70);
                });
            }else {
                menu.setAttribute('class', '');
                $("#appContent").animate({left:0});
                $("#appContent").width($("#appContent").width()+70);
            }
        }
        btn.click();
    })();
    //收藏夹
    (function(){
        var btn = document.getElementById('btnCollection'),
            btnEdit = document.getElementById('btnCollectionEdit'),
            btnClose = document.getElementById('btnCollectionClose'),
            menu = document.getElementById('appCollection')
        ;
        btn.onclick = function(){
            var className = menu.getAttribute('class');
            if(!className || className === '' || className === 'edit'){
                menu.setAttribute('class', 'active');
            }else {
                menu.setAttribute('class', '');
            }
        };
        btnEdit.onclick = function(){
            var className = menu.getAttribute('class');
            if(className === 'active'){
                menu.setAttribute('class', 'edit');
            }else {
                menu.setAttribute('class', 'active');
            }
        };
        btnClose.onclick = function(){
            menu.setAttribute('class', '');
        }
    })();

    //刷新收藏
    refreshFavorites();
};
//刷新收藏夹
function refreshFavorites(){
	//@todo
	//收藏列表需要重新修改为-当前用户的收藏
	util.list({beanName:'Favorite',conditions:[
		{
		relation:"and",
		expression:"eq",
		name:"username",
		value:localStorage.getItem('username')
		}]
	},function(data){
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
			util.remove({beanName:'Favorite',id:favoriteId},function(r){
				refreshFavorites();
			});
			event.stopPropagation();
		});
	});
    
}
var sys={
	windows:[],
	data:{},
	openWindow:function(opt/*{id:x,title:x,url:x}*/){
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
        	
        	
        	var fa ={beanName:'Favorite'};
        	fa.username=localStorage.getItem('username');
        	fa.winId=$(this).parent().attr("winid");
        	fa.name=$(this).parent().attr("wintitle");
        	fa.url=$(this).parent().attr("winurl");
        	fa.conditions=[
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
        	];
        	
        	util.list(fa,function(r){
        		if(r&&r.length){
        			$like.animate(end,1000,'swing',function(){
        				$like.remove();
        				util.info('您已经收藏过该功能');
        			});
        		}else{
        			util.create(fa,function(rr){
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
        	});
        });
        
        var self = this;
        $title.click(function(){
        	self.activeWindow($(this).attr("winid"));
        });
        $title.find(".btn-close").click(function(){
        	self.closeWindow($(this).parent().attr("winid"));
        });
        
        var contentIframe = document.createElement('iframe');
        contentIframe.width = '100%';
        contentIframe.height = '100%';
        contentIframe.frameBorder = '0';
        contentIframe.src = opt.url;
        contentIframe.name = opt.title;
        contentIframe.id = "window_"+opt.id;
        document.getElementById('appContent').appendChild(contentIframe);
        
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

function logout(){
	$.ajax({  
		type: "get",  
		url: "../rest/system/login/out",  
		dataType: "json",  
		success: function(r){ 
			if(r&&r.ok){
				localStorage.setItem('_token',null);
				localStorage.setItem('username',null);
				location.href="/login";
			}else{
				alert(r.msg);
			}
		},  
		error: function() {   
			location.href="/login";  
		}    
	});  
}