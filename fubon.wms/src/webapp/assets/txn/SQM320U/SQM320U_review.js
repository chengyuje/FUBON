/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SQM320U_reviewController', 
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope : $scope});
		$scope.controllerName = "SQM320U_reviewController";
	
		// 繼承
		$controller('SQM320_reviewController', {$scope: $scope});
	
});