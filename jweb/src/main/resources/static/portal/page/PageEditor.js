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
	
	this.render=function(pageBean){
		if(null==pageBean) pageBean={};
		this.pageBean = pageBean;
		if(!this.pageBean.uri){
			this.pageBean.uri=window.page.uri;
		}
		this.pageBean.page = JSON.parse(pageBean.page?pageBean.page:"{}");
		if(!_self.pageBean.page.services){
			_self.pageBean.page.services = [];
		}
		if(!this.pageBean.page.portals){
			this.pageBean.page.portals = [
				{
					id:_self.guid(),
					type:"portal.layout.Container",
					clazz:"container-fluid _jweb_grid",
					children:[
						{
							id:_self.guid(),
							type:"portal.layout.Container",
							clazz:"row _jweb_row",
							children:[
								
							]
						}
					]
				}];
		}
		
		var h = '';
		h+='<div class="row">';
		h+='	<div class="_portals col-sm" >';
		h+='		<div class="_edit_win"></div>';
		h+='		<iframe class="_view_win" frameborder="0"  src="'+window.page.prefix+window.page.uri+'"></iframe>';
		h+='	</div>';
		h+='</div>';
		
		var $this = $("#"+this.id);
		$this.empty().html(h);
		$this.children(".row").height($(window).height()-30);
		$this.find("._edit_win,._view_win").css({
			border:'dotted gray 1px',
			width:'100%',
			height:'50%'
		});
		
		this.initPage();
		var $outputs=$this.find("._edit_win").find('._jweb_output');
		for(var i=0;i<$outputs.length;i++){
			$outputs[i].ondragstart=function(e){
				var outputId = $(e.target).attr("outputId");
				var output={
						id:outputId,
						targetType:'output'
				};
				window['_current_drag']=output;
			};
			$outputs[i].ondragend=function(e){
				window['_current_drag']= null;
			};
		}
		$this.find("._edit_win").find('._jweb_service_delete').click(function(){
			var id= $(this).parent().attr("id");
			for(var i=0;i<_self.pageBean.page.services.length;i++){
				if(id==_self.pageBean.page.services[i].id){
					_self.pageBean.page.services.splice(i,1);
					//注意删除关系
					_self.savePageBean();
				}
			}
		});
		$this.find("._edit_win").find('._jweb_portal_delete').click(function(){
			var id= $(this).parent().attr("id");
			var pId= $(this).parent().parent().attr("id");
			var pPortal=_self.getPortal(_self.pageBean.page.portals,pId);
			for(var i=0;i<pPortal.children.length;i++){
				if(id==pPortal.children[i].id){
					pPortal.children.splice(i,1);
					//注意删除关系
					_self.savePageBean();
				}
			}
		});
		$this.find("._edit_win")[0].ondragover=function(e){
			e.preventDefault();
		};
		$this.find("._edit_win")[0].ondrop=function(e){
			var node = window['_current_drag'];
			if(node){
				_self['drop_'+node.targetType](node,e);
			}
		};
	};
	this.initPage=function(){
		var $this = $("#"+this.id);
		var $editor = $this.find("._edit_win");
		var page = this.pageBean.page;
		var h='';
		for(var i=0;i<page.portals.length;i++){
			h+=getPortalHTML(page.portals[i]);
		}
		h+='<div class="_jweb_services">';
		//页面初始化
		h+=getServiceHTML({
			id:"page",
			name:"页面初始化",
			input:[],
			output:[
				{id:"_start"}
			]
		});
		for(var i=0;i<page.services.length;i++){
			h+=getServiceHTML(page.services[i]);
		}
		h+='</div>';
		$editor.empty().html(h);
		function getPortalHTML(portal){
			if(!portal.output) portal.output=[];
			var h='';
			h+='<div id="'+portal.id+'" class="'+portal.clazz+'" >';
			h+='	<div class="_jweb_portal_title">'+portal.type+'</div>';
			h+='	<div class="_jweb_portal_delete">x</div>';
			for(var i=0;i<portal.output.length;i++){
				h+='<div class="_jweb_output" draggable="true" outputId="'
					+portal.id+'.'+portal.output[i].id
				+'">'+portal.output[i].id+'</div>';
			}
			if(portal.children&&portal.children.length){
				for(var i=0;i<portal.children.length;i++)
					h+=getPortalHTML(portal.children[i]);
			}
			h+='</div>';
			return h;
		}
		function getServiceHTML(service){
			var h='';
			h+='<div id="'+service.id+'" class="_jweb_service">';
			h+='	<div class="_jweb_service_title">'+service.name+'</div>';
			h+='	<div class="_jweb_service_delete">x</div>';
			for(var i=0;i<service.output.length;i++){
				h+='	<div class="_jweb_output" draggable="true" outputId="'
					+service.id+'.'+service.output[i].id
				+'">'+service.output[i].id+'</div>';
			}
			h+='</div>';
			return h;
		}
	};
	this.drop_output=function(output,e){
		var id=$(e.target).attr("id");
		if($(e.target).hasClass("_jweb_col")){
			var portal = _self.getPortal(_self.pageBean.page.portals,id);
			if(portal){
				if(!portal.input) portal.input=[];
				portal.input.push({
					id:output.id
				});
				_self.savePageBean();
			}
		}else if($(e.target).hasClass("_jweb_service")){
			for(var i=0;i<_self.pageBean.page.services.length;i++){
				if(id==_self.pageBean.page.services[i].id){
					var service = _self.pageBean.page.services[i];
					if(!service.input) service.input=[];
					service.input.push({
						id:output.id
					});
					_self.savePageBean();
				}
			}
		}
	};
	this.drop_service=function(srcService){
		var service={};
		$.extend(service,srcService);
		service.id=_self.guid();
		_self.pageBean.page.services.push(service);
		_self.savePageBean();
		
	};
	this.drop_portal=function(srcPortal,e){
		var x = e.offsetX;
		var y = e.offsetY;
		var cx = $(e.target).width();
		var cy = $(e.target).height();
		var isContainer = srcPortal.type && srcPortal.type=="portal.layout.Container";
		var isRow = $(e.target).hasClass("_jweb_row");
		var isCol = $(e.target).hasClass("_jweb_col");
		//获取方位，根据方位确定添加位置
		//如果源是portal，前后-col，上下-row-col
		//如果源是容器，前后-col-grid-row，上下-row
		var tmp={};
		var portal={};
		$.extend(portal,srcPortal);
		portal.id=_self.guid();
		if(isContainer){
			portal.clazz=portal.clazz+" _jweb_row";
			if(y<cy/4.0){//上
				tmp = portal;
			}else if(y>cy*3/4.0){//下
				tmp = portal;
			}else if(x>cx/2.0){//右
				tmp = {
					id:_self.guid(),
					type:"portal.layout.Container",
					clazz:"col-sm _jweb_col",
					children:[
						{
							id:_self.guid(),
							type:"portal.layout.Container",
							clazz:"container-fluid _jweb_grid",
							children:[
								portal
							]
						}
					]	
				};
			}else if(x<cx/2.0){//左
				tmp = {
					id:_self.guid(),
					type:"portal.layout.Container",
					clazz:"col-sm _jweb_col",
					children:[
						{
							id:_self.guid(),
							type:"portal.layout.Container",
							clazz:"container-fluid _jweb_grid",
							children:[
								portal
							]
						}
					]	
				};
			}
		}else{
			portal.clazz=portal.clazz+" _jweb_col";
			if(y<cy/4.0){//上
				tmp = {
					id:_self.guid(),
					type:"portal.layout.Container",
					clazz:"row _jweb_row",
					children:[
						portal
					]
				};
			}else if(y>cy*3/4.0){//下
				tmp = {
					id:_self.guid(),
					type:"portal.layout.Container",
					clazz:"row _jweb_row",
					children:[
						portal
					]
				};
			}else if(x>cx/2.0){//右
				tmp = portal;
			}else if(x<cx/2.0){//左
				tmp = portal;
			}
		}
		
		
		//如果目标是col：左边-往前插入，右边往后插入，上边缘-往前插入行，下边缘-往后插入行
		//如果目标是row：直接在目标插入
		var id = $(e.target).attr("id");
		if(isRow){
			console.log('row');
			var tPortal = _self.getPortal(_self.pageBean.page.portals,id);
			if(!tPortal.children) tPortal.children = [];
			tPortal.children.push(tmp);	
		}else if(isCol){
			console.log('col');
			if(y<cy/4.0){//上
				console.log('上');
				var gridId = $("#"+id).parent("._jweb_row").parent("._jweb_grid").attr("id");
				var tGrid = _self.getPortal(_self.pageBean.page.portals,gridId);
				if(!tGrid.children) tGrid.children = [];
				var index =0;
				for(var i=0;i<tGrid.children.length;i++){
					if(tGrid.children[i].id==id){
						index = i;
					}
				}
				tGrid.children.splice(index,0,tmp);
			}else if(y>cy*3/4.0){//下
				console.log('下');
				var gridId = $("#"+id).parent("._jweb_row").parent("._jweb_grid").attr("id");
				var tGrid = _self.getPortal(_self.pageBean.page.portals,gridId);
				if(!tGrid.children) tGrid.children = [];
				var index =0;
				for(var i=0;i<tGrid.children.length;i++){
					if(tGrid.children[i].id==id){
						index = i;
					}
				}
				tGrid.children.splice(index+1,0,tmp);
			}else if(x>cx/2.0){//右
				console.log('右');
				var rowId = $("#"+id).parent("._jweb_row").attr("id");
				var tRow = _self.getPortal(_self.pageBean.page.portals,rowId);
				if(!tRow.children) tRow.children = [];
				var index =0;
				for(var i=0;i<tRow.children.length;i++){
					if(tRow.children[i].id==id){
						index = i;
					}
				}
				tRow.children.splice(index+1,0,tmp);
			}else if(x<cx/2.0){//左
				console.log('左');
				var rowId = $("#"+id).parent("._jweb_row").attr("id");
				var tRow = _self.getPortal(_self.pageBean.page.portals,rowId);
				if(!tRow.children) tRow.children = [];
				var index =0;
				for(var i=0;i<tRow.children.length;i++){
					if(tRow.children[i].id==id){
						index = i;
					}
				}
				tRow.children.splice(index,0,tmp);
			}
		}
		_self.savePageBean();
		
	};
	this.getPortal=function(portals,id){
		if(portals&&portals.length){
			for(var i=0;i<portals.length;i++){
				if(portals[i].id==id){
					return portals[i];
				}
				if(portals[i].children){
					var portal = _self.getPortal(portals[i].children,id);
					if(portal) return portal;
				}
			}
		}
	};
	this.savePageBean=function(){
		var msg=_self.id;
		if(_self.pageBean.id){
			msg+=".updatePage";
		}else{
			msg+=".savePage";
		}
		_message_sender(msg,
			{
				beanName:'PageBean',
				beanJson:JSON.stringify({
					id:_self.pageBean.id,
					name:_self.pageBean.name,
					uri:_self.pageBean.uri,
					page:JSON.stringify(_self.pageBean.page)
				})
			}
		);
	}
}