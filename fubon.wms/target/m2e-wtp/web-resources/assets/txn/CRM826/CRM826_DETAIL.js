/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM826_DETAILController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM826_DETAILController";
	
		// filter
		getParameter.XML(["CRM.DCI_CRCY_CHGE_STATUS", "CRM.DCI_TRADE_STATUS"], function(totas) {
			if (totas) {
				$scope.mappingSet['CRM.DCI_CRCY_CHGE_STATUS'] = totas.data[totas.key.indexOf('CRM.DCI_CRCY_CHGE_STATUS')];
				$scope.mappingSet['CRM.DCI_TRADE_STATUS'] = totas.data[totas.key.indexOf('CRM.DCI_TRADE_STATUS')];
			}
		});
        
});
