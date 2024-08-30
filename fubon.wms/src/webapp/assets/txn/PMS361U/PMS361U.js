'use strict';
eSoafApp.controller('PMS361UController', function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "PMS361UController";
	
	// 繼承
	$controller('PMS361Controller', {$scope: $scope});
});