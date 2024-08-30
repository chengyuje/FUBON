/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM824_DETAILController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM824_DETAILController";
		
		$scope.inputVO.cust_id = $scope.cust_id;
		$scope.inputVO.prod_id = $scope.row.SDPRD;
		$scope.inputVO.cert_id = $scope.row.IVRNO;
		//查SQL
		$scope.sendRecv("CRM824", "inquire", "com.systex.jbranch.app.server.fps.crm824.CRM824InputVO",$scope.inputVO,
			function(tota, isError) {
				if (!isError) {
//					if(tota[0].body.resultList.length == 0) {
//            			return;
//            		}
//					$scope.row.table = tota[0].body.resultList;
					$scope.txnList = tota[0].body.txnList;
//					$scope.priceList = tota[0].body.priceList;
					return;
				}
		});
		
		
		
});
