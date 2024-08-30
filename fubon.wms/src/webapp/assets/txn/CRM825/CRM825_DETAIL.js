/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM825_DETAILController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM825_DETAILController";

		$scope.txnType = [];
		$scope.txnType.push({LABEL : '買入',DATA : 'B'},{LABEL : '賣出',DATA : 'S'},{LABEL : '繼入',DATA : 'Y'},{LABEL : '繼出',DATA : 'Z'});
		$scope.inputVO.prod_id = $scope.row.BondNo;
//		$scope.inputVO.cust_id = $scope.connector('get','CRM110_CUST_ID');
		$scope.inputVO.cust_id = $scope.cust_id;
		$scope.inputVO.cert_id = $scope.row.TrustNo;
		
		//查SQL
		$scope.sendRecv("CRM825", "inquire", "com.systex.jbranch.app.server.fps.crm825.CRM825InputVO",$scope.inputVO,
			function(tota, isError) {
				if (!isError) {
//					if(tota[0].body.resultList.length == 0) {
//            			return;
//            		}
//					$scope.row.table = tota[0].body.resultList;
//					
//					$scope.connect_list = [];
//					if ($scope.row.table[0].INVESTMENT_TARGETS != null) {
//						$scope.connect_list = $scope.comma_split($scope.row.table[0].INVESTMENT_TARGETS);
//					}
					
					$scope.txnList = tota[0].body.txnList;
//					$scope.divList = tota[0].body.divList;
//					var sum = 0;
//					angular.forEach($scope.divList, function(row) {
//						sum += row.CURR_INT;
//						row.TOTCURR_INT = sum;
//					});
//					$scope.priceList = tota[0].body.priceList;
					return;
				}
		});
		
		//字串分成[]
	    $scope.comma_split = function(value) {
	    	return value.split(',');
	    }
	    
});