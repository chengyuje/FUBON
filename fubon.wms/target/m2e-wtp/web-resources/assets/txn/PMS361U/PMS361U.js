'use strict';
eSoafApp.controller('PMS361UController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS361UController";
		
		// 繼承
		$controller('PMS361Controller', {$scope: $scope});
		
		$scope.initPMS361U = function() {
			$scope.inputVO.uhrmRC = '031';
			$scope.inputVO.uhrmOP = '031A';
			$scope.inputVO.person_role = 'UHRM';
		};
		
		$scope.initPMS361U();
			
});