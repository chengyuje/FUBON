/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('MGM114_pointDetailController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter, sysInfoService, $q, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "MGM114_pointDetailController";
		
		$scope.init = function() {
			$scope.resultList = [];
			$scope.outputVO = [];
			
			$scope.inputVO.act_seq = $scope.row.ACT_SEQ;
			$scope.inputVO.cust_id = $scope.row.CUST_ID;
			$scope.sendRecv("MGM114", "getPointDetail", "com.systex.jbranch.app.server.fps.mgm110.MGM110InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.resultList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;
							return;
						}
			});
			
		}
		$scope.init();
});
