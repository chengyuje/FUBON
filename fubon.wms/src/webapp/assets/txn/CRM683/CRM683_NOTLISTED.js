/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM683_NOTLISTEDController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM683_NOTLISTEDController";
		

		$scope.init = function(){
			$scope.inputVO.cust_id = $scope.row.DA_ARR01.trim();
			$scope.inputVO.contract_no = $scope.row.DA_ARR03.trim();
//			$scope.SUMTrustAmt = 0;
//			$scope.Return = 0;
//			$scope.a = 0; 
//			$scope.b = 0; 
//			$scope.Currency = [];

		};
		$scope.init();
		
		$scope.inquireNotListed = function(){
//			//最新幣值查詢
//			$scope.sendRecv("CRM821", "inquire_Currency", "com.systex.jbranch.app.server.fps.crm821.CRM821InputVO", $scope.inputVO,
//					function(tota, isError) {
//						if (!isError) {
//							$scope.Currency = tota[0].body.resultList;
//						}
//			});
			$scope.sendRecv("CRM683", "inquireNotListed", "com.systex.jbranch.app.server.fps.crm683.CRM683InputVO", $scope.inputVO ,
				function(tota, isError){
					if(!isError){
						debugger;
						if(tota[0].body.fbondList == 0) {
							$scope.showMsg("ehl_01_common_009");
	                		return;
						}
						$scope.stockList = tota[0].body.resultList;  
						$scope.outputVO = tota[0].body.resultList;		

//						angular.forEach($scope.fbondList, function(row){
//							
//							$scope.cod = 1;   //台幣不轉換
//							if(row.CRCY_TYPE != 'TWD'){
//								//幣值轉換
//								for(var j = 0; j < $scope.Currency.length; j++) {
//									if(row.CRCY_TYPE == $scope.Currency[j].CUR_COD){
//										$scope.cod = $scope.Currency[j].BUY_RATE;
//									}
//								}
//							}
//							$scope.SUMTrustAmt += row.TRUST_AMT * $scope.cod;
//							$scope.a += (row.DENOM * (row.REF_REDEEM / 100) + row.CASH_DIV + row.PRE_INT - row.PAID_SERV_FEE); 
//							$scope.b += row.TRUST_AMT;
//						});
//						
//						$scope.Return = (($scope.a / $scope.b) - 1) * 100;
//						
					}else{
						$scope.showErrorMsg("fail");
					}
			}
		)};
		$scope.inquireNotListed();
		
		
});
