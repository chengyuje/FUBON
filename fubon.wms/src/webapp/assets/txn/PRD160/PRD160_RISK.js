/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD160_RISKController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD160_RISKController";
		
		$scope.inputVO = {};
		
		$scope.sendRecv("PRD160", "getInsRisk", "com.systex.jbranch.app.server.fps.prd160.PRD160InputVO", {'ins_id':$scope.INSPRD_ID},
			function(tota, isError) {
				if (!isError) {
					$scope.risk = tota[0].body.resultList;
					$scope.outputVO = tota[0].body;
				}
		});
		
		
});