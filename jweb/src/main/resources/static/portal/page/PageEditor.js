function portal_page_PageEditor(){
	var h='';
	h+='<div id="'+this.id+'" class="'+this.clazz+'"></div>';
	$(this.parent).append(h);
	
	this.render=function(page){
		this.page = page;
		var h = '';
		h+='<div class="row" style="min-height:100%;">';
		h+='	<div class="services col-sm-2">services</div>';
		h+='	<div class="portals col-sm">portals</div>';
		h+='</div>';
		
		var $this = $("#"+this.id);
		$this.empty().html(h);
	};
}