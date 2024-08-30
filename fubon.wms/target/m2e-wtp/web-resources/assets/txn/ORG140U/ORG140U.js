'use strict';
eSoafApp.controller('ORG140UController', function(sysInfoService, $rootScope, $scope, $controller, socketService, ngDialog, projInfoService, getParameter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "ORG140UController";
	
	// 繼承
	$controller('ORG140Controller', {$scope: $scope});
	
	//組織連動繼承
	$controller('RegionController', {$scope: $scope});
	
	//初始化
	$scope.initORG140U = function(){
		$scope.inputVO.uhrmRC = '031';
		$scope.inputVO.uhrmOP = '031A';
		$scope.inputVO.person_role = 'UHRM';
	};
	
	$scope.initORG140U();
});
