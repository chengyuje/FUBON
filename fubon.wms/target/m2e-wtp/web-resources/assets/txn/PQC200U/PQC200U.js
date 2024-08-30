'use strict';
eSoafApp.controller('PQC200UController', function(sysInfoService, $rootScope, $scope, $controller, socketService, ngDialog, projInfoService, getParameter, $confirm) {
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "PQC200UController";
	
	//組織連動繼承
	$controller('RegionController', {$scope: $scope});
	
	// 繼承
	$controller('PQC200Controller', {$scope: $scope});
	
	
	$scope.initBy200U = function() {
		$scope.inputVO.uhrmRC = '031';
		$scope.inputVO.uhrmOP = '031A';
		$scope.inputVO.u_emp_Id = '';
	};
	
	$scope.initBy200U();	
});
