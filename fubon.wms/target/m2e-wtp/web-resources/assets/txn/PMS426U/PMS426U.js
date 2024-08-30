'use strict';
eSoafApp.controller('PMS426UController', function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService, getParameter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "PMS426UController";;
	
	// 繼承
	$controller('PMS426Controller', {$scope: $scope});
	
	$scope.initPMS426U = function() {
		$scope.inputVO.uhrmRC = '031';
		$scope.inputVO.uhrmOP = '031A';
		
	};
	
	$scope.initPMS426U();
	
});