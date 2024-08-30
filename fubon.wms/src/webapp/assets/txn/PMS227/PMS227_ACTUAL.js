'use strict';
eSoafApp.controller('PMS227_ACTUALController', function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $timeout, $filter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "PMS227_ACTUALController";

	$scope.init = function() {
		$scope.inputVO = {
			sTime      : $scope.yearMon,
			uploadName : $scope.uploadName
		};
		$scope.largeAgrList = [];
	};
	$scope.init();
	    
	// 查詢
	$scope.query = function() {	
		$scope.sendRecv("PMS227", "countNoCust", "com.systex.jbranch.app.server.fps.pms227.PMS227InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {								
				if(tota[0].body.custList.length == 0) {
					$scope.custList = [];
					$scope.showMsg("ehl_01_common_009");							
					return;
				}
				$scope.custList = tota[0].body.custList;
			}					
		});
	};
	
	$scope.query();
	    
    // 上傳
	$scope.custfile = function() {
		var uploadName = $scope.uploadName;
		var sTime = $scope.inputVO.sTime
		var dialog = ngDialog.open({
			template: 'assets/txn/PMS227/PMS227_CUST_UPLOAD.html',
			className: 'PMS227_CUST_UPLOAD',
			showClose: false,
			controller: ['$scope', function($scope) {
				$scope.yearMon = sTime;
				$scope.uploadName = uploadName;
            }]
		}).closePromise.then(function(data) {
    		if(data.value == 'cancel'){
    		//	 $scope.query();
    		}
    	});
		
	}
	
	// 下載 
	$scope.downLoad = function() {
		$scope.sendRecv("PMS227", "downLoad", "com.systex.jbranch.app.server.fps.pms227.PMS227InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(tota.body.msgData);
				return;
			}
			if (tota.length > 0) {
			}
		});
	}
});