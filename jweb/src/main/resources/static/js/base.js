$(function(){
	page.portalsMap={};
	if(page.portals){
		for(var i=0;i<page.portals.length;i++){
			_initPortal(page.portals[i],null);
		}
	}
	//服务消息字典
	page.messagesMap={}
	//收集组件的消息依赖信息
	for(var portalId in page.portalsMap){
		var portal = page.portalsMap[portalId];
		if(portal&&portal.input&&portal.input.length){
			for(var i=0;i<portal.input.length;i++){
				if(
						!page.messagesMap[portal.input[i].id]
						||
						!page.messagesMap[portal.input[i].id].length
				){
					page.messagesMap[portal.input[i].id]=[];
				}
				portal.targetType='portal';
				page.messagesMap[portal.input[i].id].push(portal);
			}
		}
	}
	//收集服务的消息依赖信息
	for(var serviceId in page.services){
		var service = page.services[serviceId];
		if(service&&service.input&&service.input.length){
			for(var i=0;i<service.input.length;i++){
				if(
						!page.messagesMap[service.input[i].id]
						||
						!page.messagesMap[service.input[i].id].length
				){
					page.messagesMap[service.input[i].id]=[];
				}
				service.targetType='service';
				page.messagesMap[service.input[i].id].push(service);
			}
		}
	}
	////////////////////////初始消息////////////////////////////
	_message_sender('page._start');
});
function _initPortal(portal,parent){
	if(!parent) parent="BODY";
	if(portal){
		portal.parent=parent;
		page.portalsMap[portal.id] = portal;
		portal.instance = window[portal.type.replace(/\./g,'_')].bind(portal);
		portal.instance();
		if(portal.children){
			for(var i=0;i<portal.children.length;i++){
				_initPortal(portal.children[i],"#"+portal.id);
			}
		}
	}
}

function _message_sender(msg,data){
	var targets = page.messagesMap[msg];
	if(targets&&targets.length){
		for(var i=0;i<targets.length;i++){
			var target = targets[i];
			window['_message_'+target.targetType+'_handler'](target,data);
		}
	}
}
function _message_service_handler(service,data){
	if(!data) data ={};
	if(!service.contentType){
		service.contentType='application/x-www-form-urlencoded';
	}
	if(!service.method){
		service.method='GET';
	}
	var url = service.uri;
	//数据变量
	for(var p in data){
		url = url.replace('{'+p+'}',data[p]);
	}
	//page变量
	for(var p in page){
		url = url.replace('{page.'+p+'}',page[p]);
	}
	
	$.ajax({
		type :service.method,
		url : page.prefix+url,
		contentType:service.contentType,
		dataType : 'json',
		data : data,
		success : function(result) {
			_message_sender(service.id+'.success',result);
		},
		error : function(e) {
			console.log(e);
			_message_sender(service.id+'.error',{msg:'系统错误'});
		}
	});
}
function _message_portal_handler(portal,data){
	portal.render(data);
}
