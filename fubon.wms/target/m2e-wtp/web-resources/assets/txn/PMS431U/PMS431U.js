'use strict';
eSoafApp.controller('PMS431UController', function ($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService, getParameter) {
        
	$controller('BaseController', {$scope: $scope});
    $controller('PMSRegionController', {$scope: $scope});
    
	$scope.controllerName = "PMS431UController";

	// 繼承
	$controller('PMS431Controller', {$scope: $scope});
	
	$scope.initPMS431U = function() {
		$scope.inputVO.uhrmRC = '031';
		$scope.inputVO.uhrmOP = '031A';
		
	};
	
	$scope.initPMS431U();
});
