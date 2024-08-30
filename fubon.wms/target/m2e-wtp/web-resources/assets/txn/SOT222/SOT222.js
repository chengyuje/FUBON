/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SOT222Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter, getParameter, $q) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SOT222Controller";
		
		$scope.init = function(){
			$scope.etfList = [];
			$scope.stockList = [];
			$scope.onTradeList = []
			$scope.outputVO = null;
			
			$scope.inputVO = {
					custID: $scope.custID,
					isOBU: ($scope.isOBU == "Y") ? true : false
        	};
		};
        $scope.init();
        
        $scope.sendRecv("SOT222", "getCustAssetETFData", "com.systex.jbranch.app.server.fps.sot222.SOT222InputVO", $scope.inputVO, 
        	function(tota, isError) {
				if (!isError) {
					if (tota[0].body.errorMsg != "" && tota[0].body.errorMsg != null) {
						$scope.showErrorMsg(tota[0].body.errorMsg);
					} else {
						$scope.etfList = tota[0].body.etfList;
//						angular.forEach($scope.etfList, function(row){
//							row.ForCurBal = Math.floor(row.ForCurBal)//取小於這個數的最大整數(無條件捨去)
//						});
						$scope.stockList = tota[0].body.stockList;
//						angular.forEach($scope.stockList, function(row){
//							row.ForCurBal = Math.floor(row.ForCurBal)//取小於這個數的最大整數(無條件捨去)
//						});
						
						//委託賣出商品
						$scope.onTradeList = []
						angular.forEach(tota[0].body.etfList, function(row, index, objs){
							if(row.assetType == '0003' || (row.assetType == '0004')) {
								$scope.onTradeList.push(row);
							}
						});
						angular.forEach(tota[0].body.stockList, function(row, index, objs){
							if(row.assetType == '0003' || (row.assetType == '0004')) {
								$scope.onTradeList.push(row);
							}
						});
						$scope.outputVO = tota[0].body;
					}

					return;
				}
			}
        );
        
        $scope.choose = function(row,type){
			$scope.returnData = row;
			$scope.returnData.PTYPE=type;
			$scope.connector('set', 'SOTProd', $scope.returnData);
			$scope.closeThisDialog('successful');
		}
        
		
});