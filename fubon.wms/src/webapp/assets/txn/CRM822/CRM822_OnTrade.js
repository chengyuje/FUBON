/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM822_OnTradeController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM822_OnTradeController";
		
		getParameter.XML(["CRM.CRM822_BUY_RESULT", "CRM.CRM822_SELL_RESULT"], function(totas) {
			if (totas) {
				$scope.BUY_RESULT = totas.data[totas.key.indexOf('CRM.CRM822_BUY_RESULT')];
				$scope.SELL_RESULT = totas.data[totas.key.indexOf('CRM.CRM822_SELL_RESULT')];
			}
		});
		
		//在途查詢
		$scope.QueryOnTrade = function () {
			$scope.inputVO.custId = $scope.cust_id;
			$scope.inputVO.isOnlyInTran = true;
			$scope.sendRecv("SOT705", "getCustAssetETFDataCheckObu", "com.systex.jbranch.app.server.fps.sot705.SOT705InputVO", $scope.inputVO,
					function(tota, isError) {
				if (!isError) {
					if((tota[0].body.custAssetETFList == null || tota[0].body.custAssetETFList.length == 0) && 
							(tota[0].body.custAssetStockList == null || tota[0].body.custAssetStockList.length == 0)) {
						$scope.showMsg("ehl_01_common_009");
						return;
					}
					
					$scope.buyList = [];
					$scope.sellList = [];
					$scope.buyListOutputVO = {};
					$scope.sellListOutputVO = {};
					
//					If (&Range [1].NodeValue = "0002") Then /*買入在途*/
//					Result = 1 or 3 then 成交金額=處理中
//
//					If (&Range [1].NodeValue = "0004") Then /*賣出委託交易*/
//					State = 1 then 成交股數=處理中，成交報價=處理中，成交金額=處理中
					
					// EFT
					$scope.eList = tota[0].body.custAssetETFList;
					angular.forEach($scope.eList, function(row, index, objs){
						if(row.assetType == '0002'){
//							Result(委託結果)：	1= 交易已成交未扣款、2= 交易已成交已扣款未入庫、
//												3= 交易已取消(部分成交)未扣款、4= 交易已取消(部分成交)已扣款未入庫
							if(row.Result == '1' || row.Result == '3'){
								row.TradeCost = '處理中';		//成交金額
							}
							$scope.buyList.push(row);
						}else if(row.assetType == '0003'){
							$scope.sellList.push(row);
						}
					});
					
					// 股票
					$scope.sList = tota[0].body.custAssetStockList;
					angular.forEach($scope.sList, function(row, index, objs){
						if(row.assetType == '0002'){
							$scope.buyList.push(row);
						}else if(row.assetType == '0003'){
							$scope.sellList.push(row);
						}
					});
					
					//分頁用
					$scope.buyListOutputVO = {'data':$scope.buyList};
					$scope.sellListOutputVO = {'data':$scope.sellList};
					
					return;
								
				}
			});
		}
		$scope.QueryOnTrade();
		
		//明細
		$scope.detail = function (row) {
			var dialog = ngDialog.open({
				template: 'assets/txn/CRM822/CRM822_OnTrade_DETAIL.html',
				className: 'CRM822_OnTrade_DETAIL',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.row = row;
                }]
			});
		};
        
});
