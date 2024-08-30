/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM822Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM822Controller";
		
		$scope.Currency = [];
		$scope.pri = projInfoService.getPriID()[0];
        $scope.IsMobile = projInfoService.getLoginSourceToken() == 'mobile';
        
		//初始化
		$scope.init = function(){
			$scope.inputVO.cust_id =  $scope.custVO.CUST_ID;
		};
		$scope.init();
		
		//最新幣值查詢
		$scope.sendRecv("CRM821", "inquire_Currency", "com.systex.jbranch.app.server.fps.crm821.CRM821InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.Currency = tota[0].body.resultList;
					}
			
		});
		
		//#1913_海外ETF/海外股票總資產
		$scope.sendRecv("CRM822", "getETFStockDeposit", "com.systex.jbranch.app.server.fps.crm822.CRM822InputVO", { cust_id: $scope.custVO.CUST_ID },
			function(tota, isError) {
				if (!isError) {
					$scope.TotalAssets = tota[0].body.etfStockAmount;
					$scope.sendRecv("SOT705", "getCustAssetETFDataCheckObu", "com.systex.jbranch.app.server.fps.sot705.SOT705InputVO",
						{ "custId": $scope.inputVO.cust_id, "isInTran": true }, function(tota, isError) {
							if (!isError) {
								// EFT
								$scope.eList = [];
								$scope.eList = tota[0].body.eList;

								angular.forEach($scope.eList, function(row, index, objs) {
									row.set = [];
									row.set.push({ LABEL: "買進", DATA: "B" });
									row.set.push({ LABEL: "賣出", DATA: "S" });
//									if(row.assetType != '0001'){
//										$scope.eList.splice(index, 1);
//									}
								});
								// 股票
								$scope.sList = [];
								$scope.sList = tota[0].body.sList;
								angular.forEach($scope.sList, function(row, index, objs) {
									row.set = [];
									row.set.push({ LABEL: "買進", DATA: "B" });
									row.set.push({ LABEL: "賣出", DATA: "S" });
								});

								//委託賣出商品
								$scope.onTradeList = []
								angular.forEach(tota[0].body.custAssetETFList, function(row, index, objs) {
									if (row.assetType == '0003' || (row.assetType == '0004')) {
										$scope.onTradeList.push(row);
									}
								});
								angular.forEach(tota[0].body.custAssetStockList, function(row, index, objs) {
									if (row.assetType == '0003' || (row.assetType == '0004')) {
										$scope.onTradeList.push(row);
									}
								});

								$scope.outputVOe = [];
								$.extend($scope.outputVOe, tota[0].body);
								$scope.outputVOs = [];
								$.extend($scope.outputVOs, tota[0].body);
								$scope.outputVOonTrade = [];
								$.extend($scope.outputVOonTrade, tota[0].body);

								$scope.getSUM($scope.eList, $scope.sList);
							}
						});
				}
			});
		
		
		
		
		
		//在途查詢
//		$scope.OnTrade = function () {
//			var cust_id = $scope.inputVO.cust_id;
//			var dialog = ngDialog.open({
//				template: 'assets/txn/CRM822/CRM822_OnTrade.html',
//				className: 'CRM822_OnTrade',
//				showClose: false,
//                controller: ['$scope', function($scope) {
//                	$scope.cust_id = cust_id;
//                }]
//			});
//		};
		
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
				
		$scope.action = function(row) {
			if(row.cmbAction) {
				var str='';
				if(row.cmbAction == "B") {
					str='SOT210';
				} else{
					str='SOT220';
				}
				$scope.connector('set','SOTCustID',$scope.inputVO.cust_id);
				$scope.connector('set','SOTProd',row);
				$rootScope.menuItemInfo.url = "assets/txn/"+str+"/"+str+".html";
				$scope.closeThisDialog('cancel');
				row.cmbAction = "";
			}
		};
		
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
		
		$scope.getSum = function(list, key) {
			if(list == null) 
				return;
			
			var sum = 0;
			for (var i = 0; i < list.length; i++ ) {
				sum += Number(list[i][key]);
			}
			return Number(sum);
		}
		
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
			
		}
		
});
