'use strict';
eSoafApp.controller("PMS997UController", function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService, getParameter, $compile) {
	
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "PMS997Controller";

	//組織連動繼承
	$controller('RegionController', {$scope: $scope});

	// 繼承
	$controller('PMS997Controller', {$scope: $scope});
	
	$scope.initPMS997U = function() {
		$scope.inputVO.uhrmRC = '031';
		$scope.inputVO.uhrmOP = '031A';
		
        $scope.sendRecv("PMS401U", "isMainten", "com.systex.jbranch.app.server.fps.pms401u.PMS401UInputVO", {'itemID': 'PMS997U'}, function(tota, isError) {
			if (!isError) {
				$scope.chkMaintenance = tota[0].body.isMaintenancePRI == 'Y' ? true : false;

				return;
			}						
		});
	};
	
	$scope.initPMS997U();
});
