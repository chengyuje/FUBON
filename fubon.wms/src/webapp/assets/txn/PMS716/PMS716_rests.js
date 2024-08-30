'use strict';
eSoafApp.controller('PMS716_restsController', function($rootScope, $scope,
		$controller, $confirm, socketService, ngDialog, projInfoService) {
	$controller('BaseController', {
		$scope : $scope
	});
	$scope.controllerName = "PMS716_restsController";

	
	/** 查詢 * */
	$scope.queryRests = function() {
		$scope.inputVO.sTime = $scope.sTime;
		$scope.sendRecv("PMS716", "queryRests",
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
	$scope.queryRests();
	
	$scope.uploadRests = function(sTime) {
		var dialog = ngDialog.open({
			template: 'assets/txn/PMS716/PMS716_UPLOAD.html',
			className: 'PMS716_UPLOAD',
			showClose : true,
    		controller: ['$scope', function($scope) {
				$scope.sTime = sTime,
				$scope.workFlow = 'addRests';
            }]
		});
		dialog.closePromise.then(function(data) {
    		if(data.value == 'cancel'){
    			 $scope.queryRests();
				}
    	});
	}
	
	/**download sample files**/
	$scope.downloadSample = function() {	
    	$scope.sendRecv("PMS716", "downloadSample", "com.systex.jbranch.app.server.fps.pms716.PMS716InputVO", {"downloadSample":"rests"},
				function(tota, isError) {
					if (!isError) {
						return;
					}
		});
	};
	
});