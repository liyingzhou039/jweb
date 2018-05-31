function portal_layout_Container(){
	var h='';
	h+='<div id="'+this.id+'" class="'+this.clazz+'"></div>';
	$(this.parent).append(h);
};