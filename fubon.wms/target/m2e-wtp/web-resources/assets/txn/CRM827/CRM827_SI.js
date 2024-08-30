/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM827_SIController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM827_SIController";
		
		
		$scope.init = function(){
			//$scope.inputVO.cust_id =  $scope.custVO.CUST_ID;
			$scope.inputVO.cust_id = $scope.row.DA_ARR01.trim();
			$scope.inputVO.contract_no = $scope.row.DA_ARR03.trim();
			$scope.SIList = [];
			$scope.SUMIVAMT2 = 0;
			$scope.a = 0;
			$scope.Return = 0;
			$scope.int_pri = 0;
			$scope.Currency = [];
		};
		$scope.init();
		
		
		$scope.inquireSI = function(){
			//最新幣值查詢
			$scope.sendRecv("CRM821", "inquire_Currency", "com.systex.jbranch.app.server.fps.crm821.CRM821InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.Currency = tota[0].body.resultList;
						}
				
			});
			
			$scope.sendRecv("CRM827", "inquireSI", "com.systex.jbranch.app.server.fps.crm827.CRM827InputVO", $scope.inputVO ,
				function(tota, isError){
					if(!isError){
						if(tota[0].body.SIList == 0) {
							$scope.showMsg("ehl_01_common_009");
	                		return;
						}
						$scope.SIList = tota[0].body.SIList;  
						$scope.outputVO = tota[0].body;	

						angular.forEach($scope.SIList, function(row){
							$scope.cod = 1;   //台幣不轉換
							if(row.CRCY_TYPE != 'TWD'){
								//幣值轉換
								for(var j = 0; j < $scope.Currency.length; j++) {
									if(row.CRCY_TYPE == $scope.Currency[j].CUR_COD){
										$scope.cod = $scope.Currency[j].BUY_RATE;
									}
								}
							}
							
							$scope.SUMIVAMT2 += (row.INV_AMT * $scope.cod);   //庫存總面額
							$scope.a += (row.RATE_OF_RETURN * row.INV_AMT * $scope.cod);
						});
						
						$scope.Return = $scope.a / $scope.SUMIVAMT2;
					}else{
						$scope.showErrorMsg("fail");
					}
			}
		)};
		$scope.inquireSI();
		
});
