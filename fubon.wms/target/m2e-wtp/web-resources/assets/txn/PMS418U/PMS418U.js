/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS418UController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS418UController";
		
		// 繼承
		$controller('PMS418Controller', {$scope: $scope});
		
		$scope.initPMS418U = function() {
			$scope.inputVO.uhrmRC = '031';
			$scope.inputVO.uhrmOP = '031A';
			$scope.inputVO.person_role = 'UHRM';
			
			$scope.isMainten = false;
			$scope.sendRecv("PMS401U", "isMainten", "com.systex.jbranch.app.server.fps.pms401u.PMS401UInputVO", {'itemID': 'PMS418U'},
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.resultList[0].MAIN_YN == 'Y') {
							$scope.isMainten = true;
						}
					}
			});
		};
		
		$scope.initPMS418U();
		
});
