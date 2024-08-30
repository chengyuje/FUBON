/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM823_DETAILController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM823_DETAILController";
		
		$scope.inputVO.prod_id = $scope.row.BondNo;
		$scope.inputVO.cust_id = $scope.cust_id;
		$scope.inputVO.cert_id = $scope.row.TrustNo;
		
		//查SQL
		$scope.sendRecv("CRM823", "inquire", "com.systex.jbranch.app.server.fps.crm823.CRM823InputVO",$scope.inputVO,
			function(tota, isError) {
				if (!isError) {
					$scope.txnList = tota[0].body.txnList;
					return;
				}
		});
		
		
});