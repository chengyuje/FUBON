/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM711Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM711Controller";

		// 消金的客戶首頁繼承 CRM610_MAIN
		$controller('CRM610_MAINController', {$scope: $scope});

		$scope.loadingCustHomeData();
});