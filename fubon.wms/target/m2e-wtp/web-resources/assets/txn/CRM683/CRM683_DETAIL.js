/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM683_DETAILController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM683_DETAILController";
		
		//=== filter
		getParameter.XML(["CRM.CRM827_V0023", "FPS.CURRENCY", "CRM.NMVP3A_T0101", "CRM.NMVP3A_T0102"], function(totas) {
			if (totas) {
				$scope.mappingSet['CRM827_V0023'] = totas.data[totas.key.indexOf('CRM.CRM827_V0023')];
				$scope.mappingSet['FPS.CURRENCY'] = totas.data[totas.key.indexOf('FPS.CURRENCY')];
				$scope.mappingSet['CRM.NMVP3A_T0101'] = totas.data[totas.key.indexOf('CRM.NMVP3A_T0101')];
				$scope.mappingSet['CRM.NMVP3A_T0102'] = totas.data[totas.key.indexOf('CRM.NMVP3A_T0102')];
			}
		});

});
