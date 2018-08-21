function LchTable(settings){
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
	        		pa.pageSize = params.limit;
	        		pa.pageNumber = (params.offset/this.pageSize)+1;
	            }else{
	            	pa.pageSize =this.pageSize;
	            	pa.pageNumber = this.pageNumber;
	            }
	            return pa;
	        },
	        params:function () {
	    		return {};
	    	},
		    sidePagination: "server", 
		    pageNumber: 1, 
		    pageSize: 8, 
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
	getRowById:function(id){
		return this.view.bootstrapTable('getRowByUniqueId',id);
	}
}

