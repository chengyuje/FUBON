'use strict';
eSoafApp.controller('PMS716_minEmpController', function($rootScope, $scope,
		$controller, $confirm, socketService, ngDialog, projInfoService) {
	$controller('BaseController', {
		$scope : $scope
	});
	$scope.controllerName = "PMS716_minEmpController";

	
	/** 查詢 * */
	$scope.queryMinEmp = function() {
		$scope.inputVO.sTime = $scope.sTime;
		$scope.sendRecv("PMS716", "queryMinEmp",
				"com.systex.jbranch.app.server.fps.pms716.PMS716InputVO",
				$scope.inputVO, function(tota, isError) {
					if (!isError) {
						if (tota[0].body.resultList == null
								|| tota[0].body.resultList.length == 0) {
							$scope.resultList = [];
							//$scope.showMsg("ehl_01_common_009");
							return;
						}
						$scope.resultList = tota[0].body.resultList;
						$scope.outputVO = tota[0].body;
					}
				});
	}
	$scope.queryMinEmp();
	
	$scope.uploadMinEmp = function(sTime) {
		var dialog = ngDialog.open({
			template: 'assets/txn/PMS716/PMS716_UPLOAD.html',
			className: 'PMS716_UPLOAD',
			showClose : true,
    		controller: ['$scope', function($scope) {
				$scope.sTime = sTime,
				$scope.workFlow = 'addMinEmp';
            }]
		});
		dialog.closePromise.then(function(data) {
    		if(data.value == 'cancel'){
    			 $scope.queryMinEmp();
				}
    	});
	}

	/**download sample files**/
	$scope.downloadSample = function() {
    	$scope.sendRecv("PMS716", "downloadSample", "com.systex.jbranch.app.server.fps.pms716.PMS716InputVO", {"downloadSample":"minEmp"},
				function(tota, isError) {
					if (!isError) {
						return;
					}
		});
	};
	
});