function LchDialog(){
};
LchDialog.prototype={
	constructor:LchDialog,
	$dialog:null,
	defaultSetting:{
		width:'500px',
		title:'信息',
		content:'#dialog',
		buttons:[]
	},
	render:function(){
		var self = this;
		this.$dialog = $(this.defaultSetting.content);
		this.$dialog.addClass('show');
		var h='';
		h+='	<div class="content-title">                         					';
		h+='	    <div class="bg">                                					';
		h+='	        <span class="text">'+this.defaultSetting.title+'</span>         ';
		h+='	    </div>                                          					';
		h+='	</div>                                              					';
		
		this.$dialog.find(".content-title").remove();
		this.$dialog.find(".box-dialog").prepend(h);
		this.$dialog.find(".box-dialog").css('width',this.defaultSetting.width);
		this.$dialog.find(".box-dialog").unbind().click(function(event){
			event.stopPropagation();
		});
		
		//按钮
		var $buttons = $('<div class="dialog-buttons text-center"></div>');
		this.$dialog.find(".dialog-buttons").remove();
		this.$dialog.find(".box-dialog").append($buttons);
		var btns = this.defaultSetting.buttons;
		//如果包含导航层则加入两个按钮上一步、下一步
		if(this.$dialog.find(".box-dialog").hasClass("box-dialog-step")){
			self.$dialog.find(".box-step").find('LI').unbind().click(function(){
				var index = $(this).index();
				self.$dialog.find(".box-step").find('LI').removeClass("active");
				$(this).addClass("active");
				self.$dialog.find(".box-dialog .content-body").children(".dialog-tab").hide();
				self.$dialog.find(".box-dialog .content-body").children(".dialog-tab:eq("+index+")").show();
				
				self.$dialog.find("#next-box-btn").removeAttr("disabled");
				self.$dialog.find("#prev-box-btn").removeAttr("disabled");
				//如果后面已经没有可见元素，隐藏下一步
				var next = false,prev = false;
				self.$dialog.find(".box-step").find('LI:gt('+index+')').each(function(){
					if($(this).is(":visible")){
						next = true;
					}
				});
				self.$dialog.find(".box-step").find('LI:lt('+index+')').each(function(){
					if($(this).is(":visible")){
						prev = true;
					}
				});
				if(!next){
					self.$dialog.find("#next-box-btn").attr("disabled","disabled");
				}
				//如果前面已经没有可见的，影藏上一步
				if(!prev){
					self.$dialog.find("#prev-box-btn").attr("disabled","disabled");
				}
			});
			
			btns = [
				{
					id:'prev-box-btn',
					text:'上一步',
					click:function(){
						var $step = self.$dialog.find(".box-step");
						var $cur = $step.find('.active');
						var index = $cur.index();
						for(var i=index-1;i>=0;i--){
							var $li = $step.find("LI:eq("+i+")");
							if($li.is(":visible")){
								$li.trigger("click");
								break;
							}
						}
					}
				},
				{
					id:'next-box-btn',
					text:'下一步',
					click:function(){
						var $step = self.$dialog.find(".box-step");
						var $cur = $step.find('.active');
						var index = $cur.index();
						for(var i=index+1;i<$step.find("LI").length;i++){
							var $li = $step.find("LI:eq("+i+")");
							if($li.is(":visible")){
								$li.trigger("click");
								break;
							}
						}
					}
				}
			].concat(btns);
		}
		
		for(var i=0;i<btns.length;i++){
			var $btn=$('<button id="'+btns[i].id+'" type="button" class="btn btn-primary">'+btns[i].text+'</button>');
			$buttons.append($btn);
			$btn.click(btns[i].click);
		}
		//如果包含导航层则加入两个按钮上一步、下一步
		if(this.$dialog.find(".box-dialog").hasClass("box-dialog-step")){
			var $step = self.$dialog.find(".box-step");
			for(var i=0;i<$step.find("LI").length;i++){
				var $li = $step.find("LI:eq("+i+")");
				if($li.is(":visible")){
					$li.trigger("click");
					break;
				}
			}
		}
		
	},
	open:function(settings){
		var self = this;
		$.extend(this.defaultSetting,settings);
		this.render();
		$(this.defaultSetting.content).unbind().click(function(){
			if(!this.$dialog){
				return;
			}
			util.confirm("是否放弃本次操作?",function(){
				this.close();
			}.bind(this));
		}.bind(this));
		
		$(window).resize(function(){
			if(!self.$dialog) return ;
			var $body=self.$dialog.find(".box-dialog .content-body");
			var top = $body.scrollTop(); 
			$body.scrollTop(top+1);
			//有滚动条
		    if($body.scrollTop()>0 ){
		    	 $body.addClass("overflow");
		    }else{
		    	 $body.removeClass("overflow");
		    }
		    $body.scrollTop(top-1);
		}).resize();
		
		return this;
	},
	close:function(){
		if(!this.$dialog) return;
		$(this.defaultSetting.content).unbind();
		this.$dialog.removeClass('show').addClass('leaving');
        setTimeout(function(){
        	this.$dialog.removeClass('leaving')
        	this.$dialog=null;
        }.bind(this), 300)
	}
}
var Dialog = new LchDialog();
