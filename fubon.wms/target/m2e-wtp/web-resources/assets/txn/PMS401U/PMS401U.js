'use strict';
eSoafApp.controller('PMS401UController', function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService, getParameter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "PMS401UController";
	// 繼承共用的組織連動選單

	// 繼承
	$controller('PMS401Controller', {$scope: $scope});
	
	$scope.initPMS401U = function() {
		$scope.inputVO.uhrmRC = '031';
		$scope.inputVO.uhrmOP = '031A';
		$scope.inputVO.person_role = 'UHRM';
		
		$scope.isMainten = false;
		$scope.sendRecv("PMS401U", "isMainten", "com.systex.jbranch.app.server.fps.pms401u.PMS401UInputVO", {'itemID': 'PMS401U'},
			function(tota, isError) {
				if (!isError) {
					if(tota[0].body.resultList[0].MAIN_YN == 'Y') {
						$scope.isMainten = true;
					}
				}
		});
	};
	
	$scope.initPMS401U();
});