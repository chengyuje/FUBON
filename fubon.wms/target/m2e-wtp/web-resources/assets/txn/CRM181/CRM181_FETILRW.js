/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM181_FETILRWController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM181_FETILRWController";
		
		$scope.inputVO = {};
		$scope.paramList = $scope.fetilrListW;
		$scope.outputVO = {};
		
		// 連至客戶首頁
        $scope.custDTL = function(row) {
        	$scope.custVO = {
    				CUST_ID :  row.CUST_ID,
    				CUST_NAME :row.CUST_NAME	
    		}
    		$scope.connector('set','CRM_CUSTVO',$scope.custVO);
        	
        	var path = "assets/txn/CRM610/CRM610_MAIN.html";
			var set = $scope.connector("set","CRM610URL",path);
			var dialog = ngDialog.open({
				template: 'assets/txn/CRM610/CRM610.html',
				className: 'CRM610',
				showClose: false
			});
		}
});