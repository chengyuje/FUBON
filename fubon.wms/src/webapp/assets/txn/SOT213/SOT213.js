/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SOT213Controller',
	function($rootScope, $scope, $controller, socketService,sysInfoService, ngDialog, projInfoService, $q, $confirm, $filter, $interval, getParameter, $timeout) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SOT213Controller";
		
		// 特金ETF繼承
		$controller('SOT211Controller', {$scope: $scope});
		$scope.inputVO.trustTS = 'M'; //M:金錢信託  S:特金交易
});