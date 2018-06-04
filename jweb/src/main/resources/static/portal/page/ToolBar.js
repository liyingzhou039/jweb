function portal_page_ToolBar(){
	var _self = this;
	_self.nodes={};
	
	var h='';
	h+='<div id="'+this.id+'" class="'+this.clazz+'"></div>';
	$(this.parent).append(h);
	
	this.render=function(data){
		var h = '';
		h+='<ul class="nav nav-tabs">';
		for(var i=0;i<data.length;i++){
			h+='  <li class="nav-item">';
			h+='    <a class="nav-link" data-toggle="tab">'+data[i].name+'</a>';
			h+='  </li>';
		}
		h+='</ul>';
		h+='<div class="tab-content">';
		for(var i=0;i<data.length;i++){
			h+='<div class="tab-pane">';
			h+='<ul>'
			for(var j=0;j<data[i].data.length;j++){
				_self.nodes[data[i].data[j].id]=data[i].data[j];
				h+='<li  nodeId="'+data[i].data[j].id+'"  draggable="true">'+data[i].data[j].name+'</li>';
			}
			h+='</ul>';
			h+='</div>';
		}
		h+='</div>';
		var $this = $("#"+this.id);
		$this.empty().html(h);
		$this.find(".nav-item").click(function(){
			var index = $(this).index();
			$this.find(".nav-link").removeClass("active");
			$this.find(".nav-link").eq(index).addClass("active");
			$this.find(".tab-pane").removeClass("active");
			$this.find(".tab-pane").eq(index).addClass("active");
		});
		var $nodes=$this.find("LI[draggable='true']");
		for(var i=0;i<$nodes.length;i++){
			$nodes[i].ondragstart=function(e){
				var nodeId = $(e.target).attr("nodeId");
				var node = _self.nodes[nodeId];
				window['_current_drag']=node;
			};
			$nodes[i].ondragend=function(e){
				$("#_temp_service").remove();
				$("#_temp_portal").remove();
				$("._success").removeClass("_success");
			};
		}
		$this.find(".nav-item").eq(0).trigger("click");
	};
};