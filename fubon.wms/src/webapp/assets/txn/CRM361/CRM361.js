/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM361Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM361Controller";

		//如果CRM181是連動來的，跳至專案頁面。
	    $scope.LinkedData = $scope.connector('get','LinkFlag_CRM361');
	    if ($scope.LinkedData != undefined && $scope.LinkedData.linked == "Y") {
	    	$scope.activeJustified = 1;
		}
		
});