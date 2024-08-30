	/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM827_SNController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM827_SNController";
		
		$scope.init = function(){
			//$scope.inputVO.cust_id =  $scope.custVO.CUST_ID;
			$scope.inputVO.cust_id = $scope.row.DA_ARR01.trim();
			$scope.inputVO.contract_no = $scope.row.DA_ARR03.trim();
			$scope.SNList = [];
			$scope.SUMTrustVal = 0;
			$scope.ReturnRate = 0;
			$scope.Currency = [];
		};
		$scope.init();
		
		
		$scope.inquireSN = function(){
			//最新幣值查詢
			$scope.sendRecv("CRM821", "inquire_Currency", "com.systex.jbranch.app.server.fps.crm821.CRM821InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.Currency = tota[0].body.resultList;
						}
				
			});
			$scope.sendRecv("CRM827", "inquireSN", "com.systex.jbranch.app.server.fps.crm827.CRM827InputVO", $scope.inputVO ,
				function(tota, isError){
					if(!isError){
						if(tota[0].body.SNList == 0) {
							$scope.showMsg("ehl_01_common_009");
	                		return;
						}
						$scope.SNList = tota[0].body.SNList;  
						$scope.outputVO = tota[0].body.SNList;
						
						$scope.a = 0;
						$scope.b = 0;
						angular.forEach($scope.SNList, function(row){
							
							$scope.cod = 1;   //台幣不轉換
							if(row.CUR_TYPE != 'TWD'){
								//幣值轉換
								for(var j = 0; j < $scope.Currency.length; j++) {
									if(row.CUR_TYPE == $scope.Currency[j].CUR_COD){
										$scope.cod = $scope.Currency[j].BUY_RATE;
									}
								}
							}
							
							$scope.SUMTrustVal += row.DENOM * $scope.cod;
							$scope.a += (row.DENOM * row.LATEST_PRICE/100 + row.ACC_CASH_DIV - row.DENOM);
                            $scope.b += row.TRUST_AMT/100;

						});
						 $scope.ReturnRate = $scope.a / $scope.b;
						
					}else{
						$scope.showErrorMsg("fail");
					}
			}
		)};
		$scope.inquireSN();
		
		
		$scope.detail = function (row) {
			var cust_id = $scope.inputVO.cust_id;
			var dialog = ngDialog.open({
				template: 'assets/txn/CRM825/CRM825_DETAIL.html',
				className: 'CRM825_DETAIL',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.row = row;
                	$scope.cust_id = cust_id;
                }]
			});
		}
		
		$scope.goPrdDetail = function(data) {
			var row = {};
			row.PRD_ID = data.PROD_ID;
			row.SN_CNAME = data.PROD_NAME;
			var dialog = ngDialog.open({
				template: 'assets/txn/PRD140/PRD140_DETAIL.html',
				className: 'PRD140_DETAIL',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.row = row;
                }]
			});
		};
		
		
});