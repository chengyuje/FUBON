/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM372_ERRORController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter, sysInfoService ) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM372_ERRORController";
        
});