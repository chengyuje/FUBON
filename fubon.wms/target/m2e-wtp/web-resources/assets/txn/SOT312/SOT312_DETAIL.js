/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SOT312_DETAILController',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SOT312_DETAILController";
				
		$scope.query = function() {
			$scope.gtcDetailList = [];
			$scope.outputVO = [];
			debugger
			$scope.inputVO.GtcNo = $scope.GtcNo;
			$scope.inputVO.custID = $scope.CustId.toUpperCase();
			console.log("$scope.inputVO.custID length", $scope.inputVO.custID.length);
			$scope.sendRecv("SOT312", "queryDetails", "com.systex.jbranch.app.server.fps.sot312.SOT312InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {		
						debugger
						$scope.gtcDetailList = tota[0].body.gtcDetailList;
						console.log($scope.gtcDetailList);
						$scope.outputVO = tota[0].body;
						
						return;
					}
			});
		}
		$scope.query();
		
});
