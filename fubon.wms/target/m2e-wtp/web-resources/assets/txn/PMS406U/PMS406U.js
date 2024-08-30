/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS406UController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService,getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS406UController";
		
		// 繼承
		$controller('PMS406Controller', {$scope: $scope});
		
		$scope.initPMS406U = function() {
			$scope.inputVO.uhrmRC = '031';
			$scope.inputVO.uhrmOP = '031A';
			$scope.inputVO.person_role = 'UHRM';
		};
		
		$scope.initPMS406U();
});
