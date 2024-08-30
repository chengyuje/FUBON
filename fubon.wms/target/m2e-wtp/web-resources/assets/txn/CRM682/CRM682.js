/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM682Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService,sysInfoService, $q, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM682Controller";
		$scope.login = String(sysInfoService.getPriID());
		
		$scope.init = function(){
			$scope.inputVO = {};
			$scope.inputVO.cust_id = $scope.custVO.CUST_ID;
		}
		$scope.init();
		
		getParameter.XML('COMMON.YES_NO', function(totas) {
			if (totas) {
				$scope.mappingSet['COMMON.YES_NO'] = totas.data[0];
			}
		});
		
		$scope.inquire = function(){
			$scope.sendRecv("CRM682", "inquire", "com.systex.jbranch.app.server.fps.crm682.CRM682InputVO", $scope.inputVO,
				function(tota, isError) {
					if (isError) {
						$scope.showErrorMsg(totas[0].body.msgData);
						return;
					}
					if(tota[0].body.resultList.length == 0 && tota[0].body.resultList2.length == 0 && tota[0].body.resultList3.length == 0 && tota[0].body.resultList4.length == 0) {
						$scope.showMsg("ehl_01_common_009");
                		return;
					}
					$scope.resultList = tota[0].body.resultList;
					$scope.resultList2 = tota[0].body.resultList2;
					$scope.resultList3 = tota[0].body.resultList3;
					
					$scope.show = false;
					angular.forEach($scope.resultList, function(row) {
		        		if(row.COLLATERAL_AMT3 != null) { 
		        			$scope.show = true;
		        		}
					});
					
					if(tota[0].body.resultList.length != 0){
						$scope.OTH_ADD_RATE1 = tota[0].body.resultList[0].OTH_ADD_RATE1;
						$scope.OTH_ADD_RATE2 = tota[0].body.resultList[0].OTH_ADD_RATE2;
						$scope.OTH_ADD_RATE3 = tota[0].body.resultList[0].OTH_ADD_RATE3;
						$scope.OTH_ADD_RATE4 = tota[0].body.resultList[0].OTH_ADD_RATE4;
						$scope.OTH_ADD_LIMIT1 = tota[0].body.resultList[0].OTH_ADD_LIMIT1;
						$scope.OTH_ADD_LIMIT2 = tota[0].body.resultList[0].OTH_ADD_LIMIT2;
					}
					
					if(tota[0].body.resultList2.length != 0){
						$scope.oldlib = tota[0].body.resultList2.length + 1;		
					}
					
					if(tota[0].body.resultList3.length != 0){
						$scope.newlib = tota[0].body.resultList3.length + 1;
					}
					if(tota[0].body.resultList4.length != 0){
						$scope.CREDIT_CARD_AMT = tota[0].body.resultList4[0].CREDIT_CARD_AMT;
						$scope.CREDIT_CARD_INC_AMT = tota[0].body.resultList4[0].CREDIT_CARD_INC_AMT;
					}
				
					$scope.outputVO = tota[0].body;
					return;
				}
		)};
		$scope.inquire();
});