/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM827_ETFController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM827_ETFController";
		
		$scope.Currency = [];
		$scope.pri = projInfoService.getPriID()[0];
        $scope.IsMobile = projInfoService.getLoginSourceToken() == 'mobile';
        
		//初始化
		$scope.init = function(){
			$scope.inputVO.cust_id = $scope.row.DA_ARR01.trim();
			$scope.inputVO.contract_no = $scope.row.DA_ARR03.trim();
			$scope.ETFList = [];
			$scope.TotalAssets = 0;
			$scope.Return = 0;
			$scope.SUMCurBalNT = 0;
			$scope.SUMCurBalCost = 0;
			$scope.SUMProfitAndLoss = 0;
			
		};
		$scope.init();
		
		
		$scope.inquireETF = function(){
			//最新幣值查詢
			$scope.sendRecv("CRM821", "inquire_Currency", "com.systex.jbranch.app.server.fps.crm821.CRM821InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.Currency = tota[0].body.resultList;
						}
				
			});
			$scope.sendRecv("CRM827", "inquireETF", "com.systex.jbranch.app.server.fps.crm827.CRM827InputVO", $scope.inputVO ,
				function(tota, isError){
					if(!isError){
						if(tota[0].body.ETFList == 0) {
							$scope.showMsg("ehl_01_common_009");
	                		return;
						}
						$scope.ETFList = tota[0].body.ETFList;  
						$scope.outputVO = tota[0].body.ETFList;
						

						angular.forEach($scope.ETFList, function(row){
							
							$scope.cod = 1;   //台幣不轉換
							if(row.CRCY_TYPE != 'TWD'){
								//幣值轉換
								for(var j = 0; j < $scope.Currency.length; j++) {
									if(row.CRCY_TYPE == $scope.Currency[j].CUR_COD){
										$scope.cod = $scope.Currency[j].BUY_RATE;
									}
								}
							}
							
							$scope.TotalAssets += row.REF_MKT_VAL * $scope.cod;
							$scope.SUMCurBalNT += row.REF_MKT_VAL * $scope.cod;
							$scope.SUMCurBalCost += row.INV_AMT * $scope.cod;
						});
						$scope.SUMProfitAndLoss = $scope.SUMCurBalNT - $scope.SUMCurBalCost;
						$scope.Return = $scope.SUMProfitAndLoss * 100 / $scope.SUMCurBalCost;
						
					}
			}
		)};
		$scope.inquireETF();
		
		
		
		//歷史查詢
		$scope.QueryHist = function () {
			var cust_id = $scope.inputVO.cust_id;
			var dialog = ngDialog.open({
				template: 'assets/txn/CRM822/CRM822_QueryHist.html',
				className: 'CRM822_QueryHist',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.cust_id = cust_id;
                }]
			});
		};
		
		//帳戶每日入扣帳明細查詢
		$scope.AccDetails = function () {
			var cust_id = $scope.inputVO.cust_id;
			var dialog = ngDialog.open({
				template: 'assets/txn/CRM822/CRM822_AccDetails.html',
				className: 'CRM822_AccDetails',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.cust_id = cust_id;
                }]
			});
		}
				
		
		$scope.detail = function (row) {
			var dialog = ngDialog.open({
				template: 'assets/txn/CRM822/CRM822_DETAIL.html',
				className: 'CRM822_DETAIL',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.row = row;
                }]
			});
		};
		
		$scope.getSUM = function(E,S) {
			$scope.AcctBal_E = 0 ;			//ETF
			$scope.AcctBalCost_E = 0 ;		//ETF台幣成本
			$scope.AcctBal_S = 0 ;			//海外股票
			$scope.AcctBalCost_S = 0 ;		//海外股票台幣成本
			
			$scope.SUMCurBalNT = 0 ;		//參考總市值(約當台幣)
			$scope.SUMCurBalCost = 0 ;		//參考總成本
			$scope.SUMProfitAndLoss = 0 ;	//參考總損益金額
			$scope.Return = 0 ;				//參考總報酬率(不含配息)
						
			//ETF
			for(var i = 0; i < E.length; i++) {
				$scope.AcctBal_E += E[i].AcctBal;
				$scope.AcctBalCost_E += E[i].AcctBalCost;
			}
			
			//股票
			for(var i = 0; i < S.length; i++) {
				$scope.AcctBal_S += S[i].AcctBal;
				$scope.AcctBalCost_S += S[i].AcctBalCost;
			}
			
			//庫存總市值
			$scope.SUMCurBalNT = $scope.AcctBal_E + $scope.AcctBal_S;				//計算庫存總市值(庫存--台幣現值加總)
			//庫存投資金額
			$scope.SUMCurBalCost = $scope.AcctBalCost_E + $scope.AcctBalCost_S;		//計算庫存投資金額(庫存--台幣成本加總)
			//庫存投資損益金額
			$scope.SUMProfitAndLoss = $scope.SUMCurBalNT - $scope.SUMCurBalCost;	//庫存總市值 - 庫存投資金額
			//庫存投資報酬率(不含配息) = (庫存總市值 - 庫存投資金額) / 庫存投資金額  * 100
			$scope.Return = Number(($scope.SUMProfitAndLoss * 100/$scope.SUMCurBalCost).toFixed(2));
			console.log($scope.Return + '  ' + $scope.Return.size);
		}
		
});
