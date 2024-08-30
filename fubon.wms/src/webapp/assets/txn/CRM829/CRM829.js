/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM829Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM829Controller";
		
		getParameter.XML(['CRM.NANO_TARGET','CRM.NANO_TYPE','CRM.NANO_EXCHANGE','CRM.NANO_DIV_TYPE','CRM.NANO_STRATEGY','CRM.NANO_CUM_SWITCH_ON','CRM.NANO_CUM_MULTIPLE', 'CRM.NANO2_RISKPREF'], function(totas) {
			if (totas) {
				$scope.mappingSet['CRM.NANO_TARGET'] = totas.data[totas.key.indexOf('CRM.NANO_TARGET')];
				$scope.mappingSet['CRM.NANO_TYPE'] = totas.data[totas.key.indexOf('CRM.NANO_TYPE')];
				$scope.mappingSet['CRM.NANO_EXCHANGE'] = totas.data[totas.key.indexOf('CRM.NANO_EXCHANGE')];
				$scope.mappingSet['CRM.NANO_DIV_TYPE'] = totas.data[totas.key.indexOf('CRM.NANO_DIV_TYPE')];
				$scope.mappingSet['CRM.NANO_STRATEGY'] = totas.data[totas.key.indexOf('CRM.NANO_STRATEGY')];
				$scope.mappingSet['CRM.NANO_CUM_SWITCH_ON'] = totas.data[totas.key.indexOf('CRM.NANO_CUM_SWITCH_ON')];
				$scope.mappingSet['CRM.NANO_CUM_MULTIPLE'] = totas.data[totas.key.indexOf('CRM.NANO_CUM_MULTIPLE')];
				$scope.mappingSet['CRM.NANO2_RISKPREF'] = totas.data[totas.key.indexOf('CRM.NANO2_RISKPREF')];
			}
		});
		
		//初始化
		$scope.init = function(){
			$scope.inputVO.cust_id = $scope.custVO.CUST_ID;
			$scope.inputVO.getSumYN = "N";
		};
		$scope.init();
		
		//奈米投庫存資料
		$scope.getCustAssetNano = function() {
			$scope.sendRecv("CRM829", "getNanoAsset", "com.systex.jbranch.app.server.fps.crm829.CRM829InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						debugger;
						if(tota[0].body.errorMsg && tota[0].body.errorMsg != "") {
							$scope.showErrorMsg(tota[0].body.errorMsg);
						}
						$scope.sellList = tota[0].body.sellList;
						$scope.outputVO2 = tota[0].body.sellList;
						
						if(tota[0].body.resultList == null || tota[0].body.resultList.length == 0) {
	            			return;
	            		}
						$scope.totalPlanNo = tota[0].body.totalPlanNo;
						$scope.totalInvestmentTwd = tota[0].body.totalInvestmentTwd;
						$scope.totalMarketValueTwd = tota[0].body.totalMarketValueTwd;
						$scope.resultList = tota[0].body.resultList;
						$scope.outputVO = tota[0].body;

					}
			});
		}
		$scope.getCustAssetNano();
		
});
