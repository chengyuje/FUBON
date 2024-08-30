/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SOT316Controller',
	function($rootScope, $scope, $controller, $confirm, sysInfoService, socketService, ngDialog, projInfoService, $filter, getParameter, $q) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SOT316Controller";
		
		// 特金海外債繼承
		$controller('SOT311Controller', {$scope: $scope});
		$scope.inputVO.trustTS = 'M'; //M:金錢信託  S:特金交易
});