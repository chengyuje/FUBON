'use strict';
eSoafApp.controller('PMS427UController', function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService, getParameter, $compile) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "PMS427UController";
	
	// 繼承
	$controller('PMS427Controller', {$scope: $scope});
});