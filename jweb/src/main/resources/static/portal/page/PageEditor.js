function portal_page_PageEditor(){
	_self = this;
	
	this.guid=function(){
		  function S4(){
		    return (((1+Math.random())*0x10000)|0).toString(16).substring(1);
		  }
		  return (S4()+S4()+"-"+S4()+"-"+S4()+"-"+S4()+"-"+S4()+S4()+S4());
	};
	
	var h='';
	h+='<div id="'+this.id+'" class="'+this.clazz+'"></div>';
	$(this.parent).append(h);
	
	this.render=function(page){
		this.page = page;
		this.page.portalsMap={};
		
		var h = '';
		h+='<div class="row" style="min-height:100%;">';
		h+='	<div class="_root _services col-sm-2"></div>';
		h+='	<div class="_root _portals col-sm"></div>';
		h+='</div>';
		
		var $this = $("#"+this.id);
		$this.empty().html(h);
		
		var $services = $("._services");
		for(var i=0;i<$services.length;i++){
			this.initService($services[i]);
		}
		
		var $portals = $("._portals");
		for(var i=0;i<$portals.length;i++){
			this.initPortal($portals[i]);
		}
	};
	this.initService=function(serviceDom){
		serviceDom.ondragenter=function(e){
			var service = window._current_drag;
			if(!service) return ;
			var success = false;
			if(
					service.type=='service'
					&&
					$(e.target).hasClass("_services")
					&&
					!$(e.target).find("._service[serviceId='"+service.id+"']").length
			){
				success = true;
			}
			if(success){
				$(e.target).addClass("_success");
				_self.createTempService(service,$(e.target));
			}
		};
		serviceDom.ondragleave=function(e){
		};
		serviceDom.ondragover=function(e){
			e.preventDefault();
		};
		serviceDom.ondrop=function(e){
			var service = window._current_drag;
			if(!service) return ;
			if($(e.target).hasClass("_success")){
				var id = _self.guid();
				_self.createService(id,service,$(e.target));
			}
		};
	};
	this.createTempService=function(service,$target){
		$("#_temp_service").remove();
		var $temp = $('<div id="_temp_service" class="_temp col-sm">' + service.name + '</div>');
		$temp.appendTo($target);
	};
	this.createService=function(id,service,$target){
		var $temp = $('<div id="'+id+'" class="_service col-sm" serviceId="'
				+ service.id + '">' + service.name + '</div>');
		$temp.appendTo($target);
	};
	this.initPortal=function(portalDom){
		portalDom.ondragenter=function(e){
			console.log("enter");
			var portal = window._current_drag;
			if(!portal) return ;
			var success = false;
			if(
				portal.type=='portal'
				&&
				(
					$(e.target).hasClass("_portals")
					||
					$(e.target).hasClass("_portal")
				)
			){
				success = true;
			}
			if(success){
				$(e.target).addClass("_success");
				_self.createTempPortal(portal,$(e.target));
			}
		};
		portalDom.ondragleave=function(e){
		};
		portalDom.ondragover=function(e){
			console.log("over");
			e.preventDefault();
		};
		portalDom.ondrop=function(e){
			console.log("drop");
			var portal = window._current_drag;
			if(!portal) return ;
			if($(e.target).hasClass("_success")){
				var id = _self.guid();
				_self.createPortal(id,portal,$(e.target));
			}
		};
	};
	this.createTempPortal=function(portal,$target){
		$("#_temp_portal").remove();
		var $temp = null;
		if($target.hasClass("_portals")){
			$temp = $('<div id="_temp_portal" class="_temp row"><div class="col-sm">' + portal.name + '</div></div>');
			$temp.appendTo($target);
		}else if($target.hasClass("_portal")){
			$temp = $('<div id="_temp_portal" class="_temp col-sm">' + portal.name + '</div>');
			$temp.appendTo($target.parent());
		}
	};
	this.createPortal=function(id,portal,$target){
		var $temp = null;
		if($target.hasClass("_portals")){
			$temp = $(
				 '<div class="_portals row">'
				+'	<div id="'+id+'" class="_portal col-sm" portalId="'+ portal.id + '">' + portal.name + '</div>'
				+'</div>');
			$temp.appendTo($target);
		}else if($target.hasClass("_portal")){
			$temp = $('<div id="'+id+'" class="_portal col-sm" portalId="'
					+ portal.id + '">' + portal.name + '</div>');
			$temp.appendTo($target.parent());
		}
	};
}