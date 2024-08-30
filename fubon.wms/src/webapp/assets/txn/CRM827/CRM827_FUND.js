/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM827_FUNDController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM827_FUNDController";
			
        $scope.pri = projInfoService.getPriID()[0];
        $scope.IsMobile = projInfoService.getLoginSourceToken() == 'mobile';
		
		//初始化
		$scope.init = function(){
			$scope.inputVO.cust_id = $scope.row.DA_ARR01.trim();
			$scope.inputVO.contract_no = $scope.row.DA_ARR03.trim();
			$scope.SUMCurBal = 0;
			$scope.FundType_1 = 0;
			$scope.SUMCurAmt = 0;
			$scope.FundType_4 = 0;
			$scope.SUMProfitAndLoss = 0;
			$scope.FundType_5 = 0;
			$scope.Return = 0;
			$scope.Return_int = 0;
			$scope.cash_int = 0;
			$scope.FundType_3 = 0;
			$scope.Currency = [];
		};
		$scope.init();
		
		
		
		$scope.inquireFund = function(){
			//最新幣值查詢
			$scope.sendRecv("CRM821", "inquire_Currency", "com.systex.jbranch.app.server.fps.crm821.CRM821InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.Currency = tota[0].body.resultList;
						}
				
			});
			$scope.sendRecv("CRM827", "inquireFund", "com.systex.jbranch.app.server.fps.crm827.CRM827InputVO", $scope.inputVO ,
				function(tota, isError){
					if(!isError){
						if(tota[0].body.fundList == 0) {
							$scope.showMsg("ehl_01_common_009");
	                		return;
						}
						$scope.fundList = tota[0].body.fundList;  
						$scope.outputVO = tota[0].body.fundList; 
						
						angular.forEach($scope.fundList, function(row){
							$scope.cod = 1;   //台幣不轉換
							if(row.CRCY_TYPE != 'TWD'){
								//幣值轉換
								for(var j = 0; j < $scope.Currency.length; j++) {
									if(row.CRCY_TYPE == $scope.Currency[j].CUR_COD){
										$scope.cod = $scope.Currency[j].BUY_RATE;
									}
								}
							}
							
							$scope.SUMCurBal += row.REF_MKT_VAL * $scope.cod;
							$scope.SUMCurAmt += row.INV_AMT * $scope.cod;
							$scope.SUMProfitAndLoss += row.INV_GAIN_LOSS * $scope.cod;
							$scope.cash_int += row.CASH_INT * $scope.cod;
							
							switch(row.FUND_TYPE){
								case '1':
									$scope.FundType_1 += row.REF_MKT_VAL * $scope.cod;    //股票
									break;
									
								case '3':
									$scope.FundType_3 += row.REF_MKT_VAL * $scope.cod;    //貨幣
									break;
									
								case '4':
									$scope.FundType_4 += row.REF_MKT_VAL * $scope.cod;    //債券
									break;
									
								case '5':
									$scope.FundType_5 += row.REF_MKT_VAL * $scope.cod;    //平衡
									break;
							}
						});
						$scope.Return = $scope.SUMProfitAndLoss * 100 / $scope.SUMCurAmt;
						$scope.Return_int = ($scope.SUMProfitAndLoss + $scope.cash_int)* 100 / $scope.SUMCurAmt;
					
					}else{
						$scope.showErrorMsg("fail");
					}
			}
		)};
		$scope.inquireFund();
		
		
		$scope.goDetail = function(row) {
			 var dialog = ngDialog.open({
	                template: 'assets/txn/CRM821/CRM821_DETAIL.html',
	                className: 'CRM821_DETAIL',
	                controller: ['$scope', function($scope) {
	              	  $scope.row = row;
	                }]
	            });
		};
		
		$scope.goPrdDetail = function(data) {
			var row = {};
			row.PRD_ID = data.FundNO;
			row.FUND_CNAME = data.FundName;
			var dialog = ngDialog.open({
				template: 'assets/txn/PRD110/PRD110_DETAIL.html',
				className: 'PRD110_DETAIL',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.row = row;
                }]
			});
		};
		


});
