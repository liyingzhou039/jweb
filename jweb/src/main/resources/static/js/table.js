function LchTable(settings){
	if(settings.createUrl) this.createUrl = settings.createUrl;
	if(settings.removeUrl) this.removeUrl = settings.removeUrl;
	if(settings.updateUrl) this.updateUrl = settings.updateUrl;
	
	var defaultSetting={
			table:'#tableId',
		    url: null,
		    method: 'get',
		    toolbar: '#toolbarId',
		    striped: true,
		    cache: false,
		    pagination: true,
		    sortable: true,
		    sortOrder:'id',
		    queryParams:function (params) {
	            var pa=this.params();
	        	if (params){
	        		pa.limit = params.limit;
	        		pa.offset = params.offset;
	            }
	            return pa;
	        },
	        params:function () {
	    		return {};
	    	},
		    sidePagination: "server", 
		    pageNumber: 1, 
		    pageSize: 10, 
		    pageList: [10, 25, 50, 100], 
		    search: false, 
		    strictSearch: false,
		    showColumns: false, 
		    showRefresh: false, 
		    minimumCountColumns: 2, 
		    clickToSelect: true, 
		    uniqueId:'id', 
		    showToggle: false, 
		    cardView: false, 
		    detailView: false, 
		    columns:[]
		}; 
		$.extend(defaultSetting,settings);
	    this.view = $(defaultSetting.table).bootstrapTable(defaultSetting);
};

LchTable.prototype={
	constructor:LchTable,
	search:function(params){
		this.view.bootstrapTable('refresh');
	},
	getRowById(id){
		return this.view.bootstrapTable('getRowByUniqueId',id);
	},
	create:function(bean,callback,settings){
		var self = this;
		var postSettings={
			type:'POST',
			dataType : "json"
		}
		$.extend(postSettings,settings);
		//将路径参数替换
		var url = self.createUrl;
		if(!url){
			util.info('请配置创建对象的URL：createUrl');
			return ;
		}
		for(var p in bean){
			url = url.replace('{'+p+'}',bean[p]);
		}
		var self = this;
		$.ajax({
			type : postSettings.type,
			url : url,
			dataType : postSettings.dataType,
			contentType : postSettings.contentType,
			data : bean,
			success : function(data) {
				callback(data);
			},
			error : function() {
				util.error("系统错误");
			}
		});
	},
	update:function(bean,callback,settings){
		var self = this;
		var postSettings={
			type:'PUT',
			dataType : "json"
		}
		$.extend(postSettings,settings);
		//将路径参数替换
		var url = self.updateUrl;
		if(!url){
			util.info('请配置更新对象的URL：updateUrl');
			return ;
		}
		for(var p in bean){
			url = url.replace('{'+p+'}',bean[p]);
		}
		var self = this;
		$.ajax({
			type : postSettings.type,
			url : url,
			dataType : postSettings.dataType,
			contentType : postSettings.contentType,
			data : bean,
			success : function(data) {
				callback(data);
			},
			error : function() {
				util.error("系统错误");
			}
		});
	},
	remove:function(bean,callback,settings){
		var self = this;
		var postSettings={
			type:'DELETE',
			dataType : "json"
		}
		$.extend(postSettings,settings);
		//将路径参数替换
		var url = self.removeUrl;
		if(!url){
			util.info('请配置删除对象的URL：removeUrl');
			return ;
		}
		for(var p in bean){
			url = url.replace('{'+p+'}',bean[p]);
		}
		var self = this;
		$.ajax({
			type : postSettings.type,
			url : url,
			dataType : postSettings.dataType,
			contentType : postSettings.contentType,
			data : bean,
			success : function(data) {
				callback(data);
			},
			error : function() {
				util.error("系统错误");
			}
		});
	}
}

