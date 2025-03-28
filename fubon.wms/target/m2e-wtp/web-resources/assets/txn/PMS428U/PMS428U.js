'use strict';
eSoafApp.controller('PMS428UController', function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService, getParameter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "PMS428Controller";;
	
	// 繼承
	$controller('PMS428Controller', {$scope: $scope});
	
});