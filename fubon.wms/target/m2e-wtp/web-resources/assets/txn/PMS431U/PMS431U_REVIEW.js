'use strict';
eSoafApp.controller('PMS431U_REVIEWController', function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
	
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "PMS431U_REVIEWController";
	
	// 繼承
	$controller('PMS431_REVIEWController', {$scope: $scope});
});