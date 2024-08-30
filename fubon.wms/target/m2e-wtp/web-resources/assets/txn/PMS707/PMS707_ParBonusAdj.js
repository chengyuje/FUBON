'use strict';
eSoafApp.controller('PMS707_ParBonusAdjController', function($rootScope, $scope,
		$controller, $confirm, socketService, ngDialog, projInfoService) {
	$controller('BaseController', {
		$scope : $scope
	});
	$scope.controllerName = "PMS707_ParBonusAdjController";

	$scope.init = function() {
		$scope.inputVO = {
				yearMon :$scope.yearMon,
				userId  :projInfoService.getUserID()
		};
		$scope.resultList = [];
	};
	$scope.init();
	
	/** 查詢 * */
	$scope.queryParBonusAdj = function() {
		$scope.sendRecv("PMS707", "queryParBonusAdj",
				"com.systex.jbranch.app.server.fps.pms707.PMS707InputVO",
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
	$scope.queryParBonusAdj();
	
	$scope.uploadParBonusAdj = function(yearMon) {
		var dialog = ngDialog.open({
			template: 'assets/txn/PMS707/PMS707_UPLOAD.html',
			className: 'PMS707_UPLOAD',
			showClose : true,
    		controller: ['$scope', function($scope) {
				$scope.yearMon = yearMon;
				$scope.workFlow = 'addParBonusAdj';
            }]
		});
		dialog.closePromise.then(function(data) {
    		if(data.value == 'cancel'){
    			 $scope.queryParBonusAdj();
				}
    	});
	}
	
	
});