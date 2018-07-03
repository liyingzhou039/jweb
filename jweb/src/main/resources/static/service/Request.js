window.service_Request={};
window.service_Request.process=function(service,data){
        if(!data) data ={};
        if(!service.data) service.data={};
        if(!service.data.contentType){
            service.data.contentType='application/x-www-form-urlencoded';
        }
	    if(!service.data.method){
		    service.data.method='GET';
	    }
	    var url = service.data.uri;
	    //数据变量
	    for(var p in data){
		    url = url.replace('{'+p+'}',data[p]);
	    }
	    //page变量
	    for(var p in page){
		    url = url.replace('{page.'+p+'}',page[p]);
	    }

        $.ajax({
            type :service.data.method,
            url : page.prefix+url,
            contentType:service.data.contentType,
            dataType : 'json',
            data : data,
            success : function(result) {
                _message_sender(service.id+'.success',result);
            },
            error : function(e) {
                console.log(e);
                _message_sender(service.id+'.error',{msg:'系统错误'});
            }
        });
};