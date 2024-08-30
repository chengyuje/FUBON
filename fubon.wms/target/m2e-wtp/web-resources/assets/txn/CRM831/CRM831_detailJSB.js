/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM831_detailJSBController',
	function($rootScope, $scope, $controller, getParameter, $confirm, socketService, ngDialog, projInfoService, $q) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM831_detailJSBController";
		
		//xml參數初始化
		getParameter.XML(["PMS.PAY_YQD"], function(totas) {
			if (totas) {
				$scope.payTypeList = totas.data[totas.key.indexOf('PMS.PAY_YQD')];
			}
		});
		
});