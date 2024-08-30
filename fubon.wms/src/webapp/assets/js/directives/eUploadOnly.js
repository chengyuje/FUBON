/**================================================================================================
@program: eUploadOnly.js
@description: 
@version: 1.0.20170313
=================================================================================================*/
eSoafApp.directive('eUploadOnly', ['sysInfoService', '$rootScope', '$q', function(sysInfoService, $rootScope, $q) {
    return {
        restrict: 'E',
        transclude: true,
        replace: true,
        scope:{
        	model: '=?',	//control file input text to show.
        	allow: '@?',	//limit file type with more than two.
        	accept: '@?',	//default file type of file browser.
        	success: '&',	//running file success function with binding and catch response file info parameters.
        	fail: '&',      //running file fail function with binding and catch response parameters.
        	process : '=?', //return file loading processing with integer.
        	processStr : '@?process', //get controller variable with binding.
        	eDisabled: '=?' //setting eUpload disabled on/off.
        },
        template: function(element, attrs) {
            var htmlText = 
 			   '<div>'+
 			   '	<table><tr>'+
 			   '		<td><span id="filebtn" class="btn btn-info fileinput-button">' +
 			   '				<input id="fileup" type="file" ng-model="eUpload_input">'+
 			   '    			<span>{{text}}</span>'+
 			   '		</span></td>'+
 			   '	</tr></table>'+
 			   '</div>'
 			   ;
            return htmlText;
        },
        link: function (scope, element, attrs, ctlModel) {
        	/** Initialize **/
        	scope.showBar = false;
        	var inputBtn = $(element).find('#filebtn');
        	if(scope.eDisabled)scope.eDisabled===true?inputBtn.attr("disabled", true):inputBtn.attr("disabled", false);
        	var input = $(element).find('#fileup'),
        		$limit = {'str':false,'size':false},
        		state = [
        		function(){
        			var deferred = $q.defer();
        			$limit.str = $.map(sysInfoService.getFileParameter(), function(row) {
        				if(row.DATA == 'DEFAULT') return row.LABEL.toString().trim();
	        		})[0];
        			deferred.resolve($limit.str);
        			return deferred.promise;
        		},
        		function(){
        			var deferred = $q.defer();
        			$limit.size = $.map(sysInfoService.getFileParameter(), function(row) {
			   			if(row.DATA == 'DEFAULT') return parseInt(row.LABEL.toString().trim())*1024*1024;
        			})[0];
        			deferred.resolve($limit.size);
        			return deferred.promise;
        		}];
    		$q.all([state[0](),state[1]()]).then(function(){
    			var chkCustom = $.map(sysInfoService.getFileParameter(), function(row) {
                	if(row.DATA == scope.$parent["authority_txnName"]) return parseInt(row.LABEL.toString().trim());
                })[0];
        		if(chkCustom){
        			$limit.str = chkCustom;
    				$limit.size = chkCustom*1024*1024;
        		}	
    		}).then(function(){    			
	        	scope.text = '瀏覽';
		    	/** calls **/
	        	$(input).fileupload({
	                url: 'FileUpload', //'//jquery-file-upload.appspot.com/'
	                dataType: 'json',  //'HEAD'
	//              maxFileSize: $limit.size, 
	//              acceptFileTypes: /(\.|\/)(gif|jpe?g|png)$/i,
	                replaceFileInput: false,
	                change: function (e, data) {
	                	/**	                	 
	                	 * parse file
	                	 * 解析檔案
	                	 * 
	                	 * **/
	                	//settle file variables 設定檔案變數
	                	var type = data.files[0].name.split('.')[data.files[0].name.split('.').length-1];
	                	if(scope.allow){
	                		if(scope.allow.indexOf('.') !== -1)scope.allow = scope.allow.replace('.','');
	                		var allowList = scope.allow.split(',');
	                		//resolve file type 判斷檔案格式
	                		if(allowList.indexOf(type) === -1){
	                			scope.value = '';
	                			$rootScope.showWarningMsg("上傳檔案類型錯誤，檔案格式限定"+scope.allow+"。");
	                			return false;
	                		}
	                	}
	                	if(scope.accept){
	                		if(scope.accept.indexOf('.') !== -1)scope.accept = scope.accept.replace('.','');
	                		var acceptList = scope.accept.split(',');
	                		//resolve file type 判斷檔案格式
	                		if(acceptList.indexOf(type) === -1){
	                			scope.value = '';
	                			$rootScope.showWarningMsg("上傳檔案類型錯誤，檔案格式限定"+scope.accept+"。");
	                			return false;
	                		}
	                	}          	
	                	//resolve file size 判斷檔案大小
	                	if(data.files[0].size > $limit.size){
	                		scope.value = '';
	                		$rootScope.showWarningMsg("檔案大小不能超過"+$limit.str+"Mb。");
	        	    		return false;
	        	    	}
	                	//disable with file upload progressing 上傳檔案中限制使用
	                	scope.showBar = true;
	                	scope.$parent[scope.processStr] = 0;
	                	inputBtn.attr("disabled", true);
	                	/**	                	 
	                	 * file uploading
	                	 * 檔案上傳
	                	 * 
	                	 * **/
	                	var fileTag = this;
	                	var re = /(?:\.([^.]+))?$/;
	                    $.each(data.files, function (index, file) {
	                    	var uploadFileName = uuid();
	                    	var extName = re.exec(file.name)[1];
	                    	var FileName = file.name;
	                    	var FileSize = file.size;
	                    	if(extName != undefined){
	                    		uploadFileName += '.' + extName;
	                    	}
	                    	var FilePath = 'temp/' + uploadFileName;
	                    	// fullPath
	                    	$(input).attr('fullPath', FilePath);
	                    	// Random name(UUID)
	                    	$(input).attr('name', uploadFileName);
	                    	// Real name(FILE)
	                    	$(input).attr('rname', FileName);
	                    	// size
	                    	$(input).attr('size', FileSize);
	                    	fileTag.name = uploadFileName;
	                    	scope.value = FileName;
	                    });
	                },
	                /** 
	                 * 
	                 * progress response
	                 * 進程回傳
	                 * 
	                 *  **/
	                progressall: function (e, data) {
	                    if (e.isDefaultPrevented()) {
	                        return false;
	                    }
	                    var $this = $(this),
	                       counts = Math.floor(data.loaded / data.total * 100);
	                    scope.w = counts;
	                    scope.wStr = counts+'%';
	                },
	                /** 
	                 * 
	                 * successful & fail response
	                 * 成功與失敗回傳
	                 * 
	                 *  **/
	                fail: function (jqXHR, textStatus, errorThrown) {//only check is success
	                	if(textStatus._response.jqXHR.status == 200){
	                		scope.success({'tempPath': $(input).attr('tempPath'),
	                					   'fullPath': $(input).attr('fullPath'),
	                			               'name': $(input).attr('name'),
	                			              'rname': $(input).attr('rname'),
	                			               'size': $(input).attr('size')});
	                		$(input).attr('value', '');
	                		scope.$parent[scope.processStr] = 100;
	                		scope.showBar = false;
	                		scope.w = 0;
		                    scope.wStr = 0+'%';
	                		inputBtn.attr("disabled", false);
	                		return;
	                	}
	                	if($(input).attr('error') == undefined){
	                		alert('上傳失敗, status[' + textStatus._response.jqXHR.status + ']');
	                		scope.$parent[scope.processStr] = false;
	                		scope.showBar = false;
	                		scope.w = 0;
		                    scope.wStr = 0+'%';
	                		return;
	                	}
	              		scope.error({status: textStatus._response.jqXHR.status});
	              		scope.$parent[scope.processStr] = false;
	              		scope.showBar = false;
	              		inputBtn.attr("disabled", false);
	                }
	            });
        	});
    		
    		/**	                	 
        	 * watching file
        	 * 監視檔案
        	 * 
        	 * **/
    		if(angular.isDefined(scope.model)){
    			scope.$watch("model",function(newVal,oldVal){
            		if(!newVal || newVal == ''){
            			return scope.value = '';
            		}
            		return scope.value = newVal;
            	});
    		}
        }
    };
    
}]);