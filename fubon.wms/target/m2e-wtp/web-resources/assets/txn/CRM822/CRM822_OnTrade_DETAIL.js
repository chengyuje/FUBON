/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM822_OnTrade_DETAILController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM822_OnTrade_DETAILController";
		
		getParameter.XML(["CRM.CRM822_BUY_RESULT", "CRM.CRM822_SELL_RESULT", "CRM.CRM822_PRD_TYPE", "CRM.CRM822_TRUST_TYPE", "SOT.ETF_CHANNEL_TYPE"], function(totas) {
			if (totas) {
				$scope.BUY_RESULT = totas.data[totas.key.indexOf('CRM.CRM822_BUY_RESULT')];
				$scope.SELL_RESULT = totas.data[totas.key.indexOf('CRM.CRM822_SELL_RESULT')];
				$scope.PRD_TYPE = totas.data[totas.key.indexOf('CRM.CRM822_PRD_TYPE')];
				$scope.TRUST_TYPE = totas.data[totas.key.indexOf('CRM.CRM822_TRUST_TYPE')];
				$scope.ETF_CHANNEL_TYPE = totas.data[totas.key.indexOf('SOT.ETF_CHANNEL_TYPE')];
			}
		});
        
});
