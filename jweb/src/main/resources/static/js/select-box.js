function SelectBox(settings){
	var self = this;
	$.extend(self.defaultSetting,settings);
	self.$el=$(self.defaultSetting.content);
	self.render();
	return self;
};
SelectBox.prototype={
	constructor:SelectBox,
	$el:null,
	defaultSetting:{
		content:'.select-box',
		id:'dname',
		cols:[
			{name:'dname',title:'d名称'},
			{name:'ddes',title:'d描述'}
		],
		src:[],
		des:[]
	},
	getSelected:function(){
		return this.defaultSetting.des;
	},
	getSelectedIds:function(){
		var ids = [];
		var des = this.defaultSetting.des;
		for(var i=0;i<des.length;i++){
			ids.push(des[i][this.defaultSetting.id]);
		}
		return ids;
	},
	render(){
		var h='';
		h+='   <div class="left-box">                                                     ';
		h+='       <div class="select-top">                                               ';
		h+='           <label>待选</label>                                                 ';
		h+='       </div>                                                                 ';
		h+='       <div class="search">                                                   ';
		h+='           <input type="text" placeholder="输入关键字搜索" />                      ';
		h+='           <i class="fa fa-search"></i>                                       ';
		h+='       </div>                                                                 ';
		h+='       <div class="select-content">                                           ';
		h+='           <table class="table">                                              ';
		h+='                                                                              ';
		h+='           </table>                                                           ';
		h+='       </div>                                                                 ';
		h+='   </div>                                                                     ';
		h+='   <div class="right-box">                                                    ';
		h+='       <div class="select-top">                                               ';
		h+='           <label>已选</label>                                                 ';
		h+='       </div>                                                                 ';
		h+='       <div class="search">                                                   ';
		h+='           <input type="text" placeholder="输入关键字搜索" />                      ';
		h+='           <i class="fa fa-search"></i>                                       ';
		h+='       </div>                                                                 ';
		h+='       <div class="select-content">                                           ';
		h+='           <table class="table table-hover">                                  ';
		h+='                                                                              ';
		h+='           </table>                                                           ';
		h+='       </div>                                                                 ';
		h+='   </div>                                                                     ';
		
		this.$el.empty().html(h);
		var cols = this.defaultSetting.cols;
		var th='';
		th+='<thead>';
        th+='<tr>';
        for(var i=0;i<cols.length;i++){
        	th+='<th>'+cols[i].title+'</th>';
        }
        th+='   <th class="control-btn"></th>';
        th+='</tr>';
        th+='</thead>';
        th+='<tbody></tbody>';
        var $leftTh=$(th);
        var $rightTh=$(th);
        this.$el.find(".left-box").find(".table").append($leftTh);
        this.$el.find(".right-box").find(".table").append($rightTh);
        
        $leftTh.find(".control-btn").text("全选").click(function(){
        	this.defaultSetting.des = [].concat(this.defaultSetting.src);
        	this.renderData();
        }.bind(this));
        $rightTh.find(".control-btn").text("全清").click(function(){
        	this.defaultSetting.des = [];
        	this.renderData();
        }.bind(this));
        //搜索事件
        this.$el.find(".left-box").find(".search").find("INPUT").unbind().keyup(function(){
        	this.renderData();
        }.bind(this));
        this.$el.find(".right-box").find(".search").find("INPUT").unbind().keyup(function(){
        	this.renderData();
        }.bind(this));
        this.renderData();
	},
	renderData:function(){
		var self = this;
		var src = this.defaultSetting.src;
        var des = this.defaultSetting.des;
        var id = this.defaultSetting.id;
        var cols = this.defaultSetting.cols;
        var srcKey = this.$el.find(".left-box").find(".search").find("INPUT").val();
        var desKey = this.$el.find(".right-box").find(".search").find("INPUT").val();
        
        for(var i=0;i<src.length;i++){
        	src[i]._selected = false;
        	for(var j=0;j<des.length;j++){
        		if(src[i][id]==des[j][id]){
        			src[i]._selected = true;
        			break;
        		}
        	}
        }
        
        var trs='';
        for(var i=0;i<src.length;i++){
        	if(src[i]._selected) continue;
        	if(srcKey&&src[i][id].indexOf(srcKey)==-1) continue;
        	trs+='<tr>';
        	for(var j=0;j<cols.length;j++){
        		trs+='<td>'+src[i][cols[j].field]+'</td>';
            }
        	trs+='<td class="control-btn" rowid="'+src[i][id]+'"><i class="fa fa-plus"></i></td>';
        	trs+='</tr>';
        }
        var $leftBody=this.$el.find(".left-box").find(".table").find("TBODY");
        $leftBody.empty().html(trs);
        $leftBody.find(".control-btn").click(function(){
        	for(var i=0;i<self.defaultSetting.src.length;i++){
        		if(self.defaultSetting.src[i][self.defaultSetting.id]==$(this).attr("rowid")){
        			self.defaultSetting.des.push(self.defaultSetting.src[i]);
        		}
        	}
        	self.renderData();
        });
        
        trs='';
        for(var i=0;i<des.length;i++){
        	if(desKey&&des[i][id].indexOf(desKey)==-1) continue;
        	trs+='<tr>';
        	for(var j=0;j<cols.length;j++){
        		trs+='<td>'+des[i][cols[j].field]+'</td>';
            }
        	trs+='<td class="control-btn" rowid="'+des[i][id]+'"><i class="fa fa-minus"></i></td>';
        	trs+='</tr>';
        }
        var $rightBody=this.$el.find(".right-box").find(".table").find("TBODY");
        $rightBody.empty().html(trs);
        
        $rightBody.find(".control-btn").click(function(){
        	for(var i=0;i<self.defaultSetting.des.length;i++){
        		if(self.defaultSetting.des[i][self.defaultSetting.id]==$(this).attr("rowid")){
        			self.defaultSetting.des.splice(i,1);
        		}
        	}
        	self.renderData();
        });
	}
}
/* demo
var sbox = new SelectBox({
	content:'.select-box',
	id:'name',
	cols:[
		{field:'name',title:'名称'},
		{field:'des',title:'描述'}
	],
	src:[
		{name:'name1',des:'description1'},
		{name:'name2',des:'description2'},
		{name:'name3',des:'description3'},
		{name:'name4',des:'description4'},
		{name:'name5',des:'description5'},
		{name:'name6',des:'description6'},
		{name:'name7',des:'description7'},
		{name:'name8',des:'description8'},
		{name:'name9',des:'description9'},
		{name:'name10',des:'description10'},
		{name:'name11',des:'description11'},
		{name:'name12',des:'description12'}
	],
	des:[
		{name:'name2',des:'description2'}
	]
});*/