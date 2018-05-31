function portal_nav_SideBar(){
	var h='';
	h+='<div id="'+this.id+'" class="'+this.clazz+'"></div>';
	$(this.parent).append(h);
	
	this.render=function(data){
		var menus = data;
		if(menus&&menus.length){
			var h='';
			for(var i=0;i<menus.length;i++){
				h+='<li menuId="'+menus[i].id+'">'+menus[i].name+menus[i].pId+'</li>';
			}
			var $bar = $("#"+this.id);
			$bar.empty().html(h);
			
			var _self=this;
			$bar.find("LI").click(function(){
				_message_sender(_self.output.nodeClick,{
					id:$(this).attr("menuId"),
					name:$(this).text
				});
			});
		}
	};
};