/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SQM320U_sendController', function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $confirm) {
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "SQM320U_sendController";

	// 繼承
	$controller('SQM320_sendController', {$scope: $scope});
});