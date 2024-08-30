'use strict';
eSoafApp.controller('PMS399UController', function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService, getParameter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "PMS399UController";
	// 繼承共用的組織連動選單

	// 繼承
	$controller('PMS399Controller', {$scope: $scope});
	
	$scope.initPMS399U = function() {
		$scope.inputVO.uhrmRC = '031';
		$scope.inputVO.uhrmOP = '031A';
		$scope.inputVO.person_role = 'UHRM';
		
		$scope.isMainten = false;
		$scope.sendRecv("PMS399U", "isMainten", "com.systex.jbranch.app.server.fps.pms399u.PMS399UInputVO", {'itemID': 'PMS399U'}, function(tota, isError) {
			if (!isError) {
				if(tota[0].body.resultList[0].MAIN_YN == 'Y') {
					$scope.isMainten = true;
				}
			}
		});
	};
	
	$scope.initPMS399U();
});