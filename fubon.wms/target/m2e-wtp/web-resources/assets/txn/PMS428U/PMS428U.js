'use strict';
eSoafApp.controller('PMS428UController', function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService, getParameter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "PMS428Controller";;
	
	// 繼承
	$controller('PMS428Controller', {$scope: $scope});
	
	$scope.initPMS428U = function() {
		$scope.inputVO.uhrmRC = '031';
		$scope.inputVO.uhrmOP = '031A';
		
	};
	
	$scope.initPMS428U();
});