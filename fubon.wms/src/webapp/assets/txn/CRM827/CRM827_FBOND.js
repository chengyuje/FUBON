/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM827_FBONDController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM827_FBONDController";
		

		$scope.init = function(){
			$scope.inputVO.cust_id = $scope.row.DA_ARR01.trim();
			$scope.inputVO.contract_no = $scope.row.DA_ARR03.trim();
			$scope.SUMTrustAmt = 0;
			$scope.Return = 0;
			$scope.a = 0; 
			$scope.b = 0; 
			$scope.Currency = [];

		};
		$scope.init();
		
		$scope.inquireFBOND = function(){
			//最新幣值查詢
			$scope.sendRecv("CRM821", "inquire_Currency", "com.systex.jbranch.app.server.fps.crm821.CRM821InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.Currency = tota[0].body.resultList;
						}
			});
			$scope.sendRecv("CRM827", "inquireFbond", "com.systex.jbranch.app.server.fps.crm827.CRM827InputVO", $scope.inputVO ,
				function(tota, isError){
					if(!isError){
						debugger;
						if(tota[0].body.fbondList == 0) {
							$scope.showMsg("ehl_01_common_009");
	                		return;
						}
						$scope.fbondList = tota[0].body.fbondList;  
						$scope.outputVO = tota[0].body.fbondList;		

						angular.forEach($scope.fbondList, function(row){
							
							$scope.cod = 1;   //台幣不轉換
							if(row.CRCY_TYPE != 'TWD'){
								//幣值轉換
								for(var j = 0; j < $scope.Currency.length; j++) {
									if(row.CRCY_TYPE == $scope.Currency[j].CUR_COD){
										$scope.cod = $scope.Currency[j].BUY_RATE;
									}
								}
							}
							$scope.SUMTrustAmt += row.TRUST_AMT * $scope.cod;
							$scope.a += (row.DENOM * (row.REF_REDEEM / 100) + row.CASH_DIV + row.PRE_INT - row.PAID_SERV_FEE); 
							$scope.b += row.TRUST_AMT;
						});
						
						$scope.Return = (($scope.a / $scope.b) - 1) * 100;
						
					}else{
						$scope.showErrorMsg("fail");
					}
			}
		)};
		$scope.inquireFBOND();
		
		$scope.detail = function (row) {
			var custID =  $scope.custVO.CUST_ID;
			var dialog = ngDialog.open({
				template: 'assets/txn/CRM823/CRM823_DETAIL.html',
				className: 'CRM823_DETAIL',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.row = row;
                	$scope.cust_id = custID;
                }]
			});
		}
		
		$scope.goPrdDetail = function(data) {
			debugger;
			var row = {};
			row.PRD_ID = data.PROD_ID;
			row.BOND_CNAME = data.PROD_NAME;
			var dialog = ngDialog.open({
				template: 'assets/txn/PRD130/PRD130_DETAIL.html',
				className: 'PRD130_DETAIL',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.row = row;
                }]
			});
		};
		
		
		
});
