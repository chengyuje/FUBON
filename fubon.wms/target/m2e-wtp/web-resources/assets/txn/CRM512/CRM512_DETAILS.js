'use strict';
eSoafApp.controller('CRM512_DETAILSController', function ($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService, getParameter, $timeout) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "CRM512_DETAILSController";
	//CRM512繼承
	$controller('CRM512Controller', {$scope: $scope});
});