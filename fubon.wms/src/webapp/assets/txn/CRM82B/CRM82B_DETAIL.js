/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM82B_DETAILController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM82B_DETAILController";

		$scope.txnType = [];
		$scope.txnType.push({LABEL : '買斷',DATA : '1'},{LABEL : '配息',DATA : '2'},{LABEL : '到期',DATA : '3'},{LABEL : '贖回',DATA : '4'});
		$scope.inputVO.prod_id = $scope.row.PROD_ID;
		$scope.inputVO.cert_id = $scope.row.CERT_ID;
		$scope.inputVO.cust_id = $scope.cust_id;
		
		//查SQL
		$scope.sendRecv("CRM82B", "inquire", "com.systex.jbranch.app.server.fps.crm82B.CRM82BInputVO",$scope.inputVO,
			function(tota, isError) {
				if (!isError) {
					$scope.txnList = tota[0].body.txnList;
					return;
				}
		});
	
	    
});