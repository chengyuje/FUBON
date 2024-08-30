///======================
/// 客戶關係圖檔上傳模式
///======================
eSoafApp.directive('fileModel', ['$parse', function ($parse) {
    return {
        restrict: 'A',
        link: function(scope, element, attrs) {
            var model = $parse(attrs.fileModel);
            var modelSetter = model.assign;
            
            element.bind('change', function(){
                scope.$apply(function(){
                    modelSetter(scope, element[0].files[0]);
                });
            });
        }
    };
}]);

eSoafApp.service('imgFileUploadService', ['$http', 
	function($http) {
    	this.uploadFile = function(oFile, oCallbackFunc) {
	        var fd = new FormData();
	        fd.append('file', oFile);
	        $http.post("./Service/MsgService.svc/rest/Image2Base64", fd, {
	            transformRequest: angular.identity,
	            headers: {'Content-Type': undefined}
	        })
	        .success(function(oResp) {
	        	if (oCallbackFunc) {
	        		//console.log(oResp.d);
	        		oCallbackFunc(oResp.d);
	        	}
	        })
	        .error(function(oErr) {
				console.log(oErr);
	        });
    	};
	}]
);