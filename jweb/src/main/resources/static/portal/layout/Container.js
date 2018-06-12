function portal_layout_Container(){
	var h='';
	h+='<div id="'+this.id+'" class="'+this.clazz+'">';
	h+='</div>';
	$(this.parent).append(h);
};