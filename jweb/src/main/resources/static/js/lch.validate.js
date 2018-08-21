var validator={
	required:function(zhField,value){
		if(!value){
			return zhField+"不能为空";
		}else{
			return null;
		}
	},
	length:function(zhField,value,params){
		if(value){
			var len=value.length;
			if(params&&params.length==2){
				var min=params[0];
				var max=params[1];
				if(len<min||len>max){
					return zhField+"长度必须介于["+min+","+max+"]之间";
				}
			}else{
				return zhField+"检验参数不正确";
			}
		}
		return null;
	},
	exists:function(zhField,value,params){
		var data = {};
		data[params[1]]=value;
		var r=null;
		if(value){
			$.ajax({
    			async:false,
    			type : "GET",
    			url : params[0],
    			dataType : "json",
    			data : data,
    			success : function(data) {
    				if(data&&data.ok)
    				{	
    					r = zhField+"已存在";
    				}
    			}
    		});
		}
		return r;
	},
	same:function(zhField,value,params){
		var otherValue = $(params[0]).val();
		if(value!=otherValue){
			return params[1];
		}
		return null;
	},
	remote:function(zhField,value,params){
		var data = {};
		data[params[1]]=value;
		var r=null;
		if(value){
			$.ajax({
    			async:false,
    			type : "POST",
    			url : params[0],
    			dataType : "json",
    			data : data,
    			success : function(data) {
    				if(!data||!data.ok)
    				{	
    					r = zhField+data.msg;
    				}
    			}
    		});
		}
		return r;
	},
	complexValid:function(zhField,value,params){
		if(value){
			if(params&&params.length==2){
				var reg=new RegExp(params[0]);
				var msg=params[1];
				if(!reg.test(value)){
					return zhField+msg;
				}
			}else{
				return zhField+"检验参数不正确";
			}
		}
		return null;
	}
};
var ValidatorUtil = {
		init:function($p){
			if(!$p) $p = $(".box-dialog").find(".content-body").children();
			$p.each(function(){
				$(this).find("LABEL").each(function(index){
					var $labelFor = $(this).parent().find("[name='"+$(this).attr("for")+"']");
					var text = $(this).text();
					$(this).empty().append('<span>'+text+'</span>');
					//必填项初始化
					if($labelFor.is('[required]')){
						$(this).append('<span style="color: red;">&nbsp;*</span>');
					}
					//文本改变校验初始化
					$labelFor.change(function(){
						ValidatorUtil.checkField($(this));
					});
					//帮助信息初始化
					var style="width:200px;";
					if(index>2){
						style+="top:auto;bottom:0;";
					}
					//初始化帮助信息
					if($labelFor.is('[tips]')){
						var tips=$labelFor.attr("tips");
						var $tips=$('<span class="box-help">'+
							     	'	<i class="fa fa-question-circle" style="cursor:pointer;"></i>'+
							        '	<span class="content-help" style="'+style+'">'+
							        tips+
							        '</span>'+
							    	'</span>');
						$(this).append($tips);
					}
				});
			});
		},
		render:function($field,msg){
			var $error = $field.next(".has-error");
			$field.parent().removeClass("has-error");
			$error.remove();
			if(msg){
				$field.after('<span class="has-error" style="color:red;line-height:18px;font-size:12px;">'+msg+'</span>');
				//如果是选项卡则获取选项卡下标
				var $pTab = $field.parents(".dialog-tab");
				if($pTab.length){
					var index =  $pTab.index();
					$pTab.parents(".box-dialog-step").find(".box-step").find("LI").eq(index).trigger("click");
				}
				$field.parent().addClass("has-error");
				$field.focus();
			}
		},
		checkField:function($field){
			this.check($field.parent());
		},
		check:function($form){
			if($form==null) return true;
			var fs =$form.find("*[name]");
			for(var i=0;i<fs.length;i++){
				var $field = $(fs[i]);
				//隐藏的输入域不做校验
				if($field.is(":disabled")){
					continue;
				}
					
				var name = $field.attr("name");
				var value = $field.val();
				var zhName = $form.find("label[for='"+name+"']").find("SPAN").eq(0).text();
				var required = $field.is('[required]');
				var validType =$.trim($field.attr("validType"));
				if (required) {
					var msg = validator.required(zhName, value);
					this.render($field,msg);
					if(msg){
						//添加验证不通过的逻辑
						return false;
					}
				}
				//validType
				//方法名称1('','',...);方法名称2('','',...);...;方法名n;...
				if(validType&&validType.length>0){
					validType = validType.split(";");
					for(var j=0;j<validType.length;j++){
						var vt = validType[j];//方法名称('','',...) or 方法名称
						var msg = this.valid(zhName,value,vt);
						this.render($field,msg);
						if(msg){
							//添加验证不通过的逻辑
							return false;
						}
					}
				}
			}
			return true;
		},
		valid:function(zhFieldName,fieldValue,validType){
			validType=$.trim(validType);
			if(validType.indexOf("(")==-1){
				if(validator[validType]){
					return validator[validType](zhFieldName,fieldValue);
				}else{
					return "验证方法"+validType+"不存在";
				}
			}else if(validType.indexOf("(")!=-1&&validType.indexOf(")")!=-1){
				var method = validType.substring(0,validType.indexOf("("));
				var params = validType.substring(validType.indexOf("(")+1,validType.indexOf(")")).split(",");
				return validator[method](zhFieldName,fieldValue,params);
			}
		}	
}
$(function(){
	ValidatorUtil.init();
});
//自定义
validator.cidr=function(zhField,value,params){
	var reg = /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\/([1-9]|[1-2][0-9]|3[0-1])$/;
	if(value){
		if(params&&params.length==1){
			var msg=params[0];
			if(!reg.test(value)){
				return zhField+msg;
			}
		}else{
			return zhField+"检验参数不正确";
		}
	}
	return null;
};
validator.ip=function(zhField,value,params){
	var reg = /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/;
	if(value){
		if(params&&params.length==1){
			var msg=params[0];
			if(!reg.test(value)){
				return zhField+msg;
			}
		}else{
			return zhField+"检验参数不正确";
		}
	}
	return null;
};
validator.ips=function(zhField,value,params){
	var reg = /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/;
	if(value){
		if(params&&params.length==1){
			var msg=params[0];
			var vs = value.split("\n");
			for(var i=0;i<vs.length;i++){
				if(!$.trim(vs[i])) continue;
				if(!reg.test($.trim(vs[i]))){
					return zhField+":"+vs[i]+msg;
				}
			}
		}else{
			return zhField+"检验参数不正确";
		}
	}
	return null;
};
validator.ipPool=function(zhField,value,params){
	var reg = /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5]),(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/;
	if(value){
		if(params&&params.length==1){
			var msg=params[0];
			var vs = value.split("\n");
			for(var i=0;i<vs.length;i++){
				if(!$.trim(vs[i])) continue;
				if(!reg.test($.trim(vs[i]))){
					return zhField+":"+vs[i]+msg;
				}
			}
		}else{
			return zhField+"检验参数不正确";
		}
	}
	return null;
};
validator.routers=function(zhField,value,params){
	var reg = /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\/([1-9]|[1-2][0-9]|3[0-1]),(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/;
	if(value){
		if(params&&params.length==1){
			var msg=params[0];
			var vs = value.split("\n");
			for(var i=0;i<vs.length;i++){
				if(!$.trim(vs[i])) continue;
				if(!reg.test($.trim(vs[i]))){
					return zhField+":"+vs[i]+msg;
				}
			}
		}else{
			return zhField+"检验参数不正确";
		}
	}
	return null;
};