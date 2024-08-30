/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SOT113Controller',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, $interval, getParameter, $timeout) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SOT113Controller";
		
		$controller('SOT111Controller', {$scope: $scope});
		$scope.inputVO.trustTS = 'M'; //M:金錢信託  S:特金交易--預設特金如果契約編號有值則為SOT112交易為金錢信託
});