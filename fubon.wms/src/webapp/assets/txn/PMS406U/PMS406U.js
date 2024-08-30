/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS406UController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService,getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS406UController";
		
		// 繼承
		$controller('PMS406Controller', {$scope: $scope});
		
		$scope.initPMS406U = function() {
			$scope.sendRecv("PMS401U", "isMainten", "com.systex.jbranch.app.server.fps.pms401u.PMS401UInputVO", {'itemID': 'PMS406U'}, function(tota, isError) {
				if (!isError) {
					$scope.uhrmRCList = [];
					$scope.uhrmOPList = [];

					if (null != tota[0].body.uhrmORGList) {
						angular.forEach(tota[0].body.uhrmORGList, function(row) {
							$scope.uhrmRCList.push({LABEL: row.REGION_CENTER_NAME, DATA: row.REGION_CENTER_ID});
						});	
						
						$scope.inputVO.uhrmRC = tota[0].body.uhrmORGList[0].REGION_CENTER_ID;
						
						angular.forEach(tota[0].body.uhrmORGList, function(row) {
							$scope.uhrmOPList.push({LABEL: row.BRANCH_AREA_NAME, DATA: row.BRANCH_AREA_ID});
						});
						
						$scope.inputVO.uhrmOP = tota[0].body.uhrmORGList[0].BRANCH_AREA_ID;
			        }
				}
			});
		};
		
		$scope.initPMS406U();
});
