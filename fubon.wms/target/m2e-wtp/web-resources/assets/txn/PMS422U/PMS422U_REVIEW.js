/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS422U_REVIEWController', function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "PMS422U_REVIEWController";
	
	// 繼承
	$controller('PMS422_REVIEWController', {$scope: $scope});
	
});