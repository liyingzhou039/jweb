function KeyValueBox(settings){
	this.defaultSetting={
			content:'.box-key-value',
			value:{
				key1:'value1',
				key2:'value2'
			}
	};
	$.extend(this.defaultSetting,settings);
	this.$el=$(this.defaultSetting.content);
	this.render();
	return this;
};
KeyValueBox.prototype={
	constructor:KeyValueBox,
	val:function(value){
		if(value){
			this.defaultSetting.value = value;
			this.renderData();
			return this.defaultSetting.value;
		}else{
			return this.defaultSetting.value;
		}
	},
	render:function(){
		var h='';
			h+='  <ul class="container-selected">		                                      ';
			h+='  </ul>		                                                                  ';
			h+='  <div class="container-input">		                                          ';
			h+='      <input name="key" type="text" placeholder="名称" />		              ';
			h+='      <input name="value" type="text" placeholder="值" />		              ';
			h+='      <span class="btn-add"><i class="fa fa-plus"></i></span>		          ';
			h+='  </div>		                                                              ';
		
		this.$el.empty().html(h);
		
		//事件
		var $key = this.$el.find(".container-input").find("INPUT[name='key']");
		var $value = this.$el.find(".container-input").find("INPUT[name='value']");
		var $plus = this.$el.find(".container-input").find(".btn-add");
		$plus.click(function(){
			if($key.val()){
				this.defaultSetting.value[$key.val()]=$value.val();
				this.renderData();
			}
		}.bind(this));
		this.renderData();
	},
	renderData:function(){
		var self = this;
		var h='';
		h+='      <li class="header">		                                              ';
		h+='          <span class="key">名称</span>		                                  ';
		h+='          <span class="value">值</span>		                              	  ';
		h+='          <span class="btn-remove">全清</span>		                          ';
		h+='      </li>		                                                              ';
		var value = this.defaultSetting.value;
		for(var key in value){
			h+='      <li class="record" key="'+key+'">		                                  ';
			h+='          <span class="key">'+key+'</span>		                              ';
			h+='          <span class="value">'+value[key]+'</span>		                      ';
			h+='          <span class="btn-remove"><i class="fa fa-minus"></i></span>		  ';
			h+='      </li>		                                                              ';
		}
		this.$el.find(".container-selected").empty().html(h);
		this.$el.find(".container-selected").find(".record").find(".btn-remove").click(function(){
			var key = $(this).parent().attr("key");
			delete self.defaultSetting.value[key];
			self.renderData();
		});
		this.$el.find(".container-selected").find(".header").find(".btn-remove").click(function(){
			self.defaultSetting.value={};
			self.renderData();
		});
	}
}
/* demo
var kvBox = new KeyValueBox({
	content:'.box-key-value',
	value:{
		key1:'value1'
	}
});*/