/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('SOT230_DtController',
    function($scope, $controller, socketService, alerts, projInfoService,getParameter) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "SOT230_DtController";
        getParameter
		.XML(
				[ "CAM.PRD_TYPE" ],
				function(totas) {
					if (totas) {
						$scope.mappingSet['CAM.PRD_TYPE'] = totas.data[totas.key
								.indexOf('CAM.PRD_TYPE')];
					}
				});
		getParameter
		.XML(
				[ "CRM.ETF_TRUST_CURR_TYPE" ],
				function(totas) {
					if (totas) {
						$scope.mappingSet['CRM.ETF_TRUST_CURR_TYPE'] = totas.data[totas.key
								.indexOf('CRM.ETF_TRUST_CURR_TYPE')];
					}
				});
		//INSERT INTO TBSYSPARAMETER VALUES('SOT.ETF_CHANNEL_TYPE','M',0,4,'行銀','行銀',NULL,0,SYSDATE,'SYSTEM','SYSTEM',SYSDATE,NULL);
		getParameter
		.XML(
				[ "SOT.ETF_CHANNEL_TYPE" ],
				function(totas) {
					if (totas) {
						$scope.mappingSet['SOT.ETF_CHANNEL_TYPE'] = totas.data[totas.key
								.indexOf('SOT.ETF_CHANNEL_TYPE')];
					}
				});
		getParameter
		.XML(
				[ "ETF.PROD_TYPE" ],
				function(totas) {
					if (totas) {
						$scope.mappingSet['ETF.PROD_TYPE'] = totas.data[totas.key
								.indexOf('ETF.PROD_TYPE')];
					}
				});
		getParameter
		.XML(
				[ "CRM.CRM822_BUY_RESULT" ],
				function(totas) {
					if (totas) {
						$scope.mappingSet['CRM.CRM822_BUY_RESULT'] = totas.data[totas.key
								.indexOf('CRM.CRM822_BUY_RESULT')];
					}
				});
		getParameter
		.XML(
				[ "FPS.CURRENCY" ],
				function(totas) {
					if (totas) {
						$scope.mappingSet['FPS.CURRENCY'] = totas.data[totas.key
								.indexOf('FPS.CURRENCY')];
					}
				});
        $scope.init = function(){
        	if($scope.row){
        		$scope.isUpdate = true
        	}
            $scope.row = $scope.row || {};
            
        	
        };

		
        $scope.init();
        
        

        
    }
);
