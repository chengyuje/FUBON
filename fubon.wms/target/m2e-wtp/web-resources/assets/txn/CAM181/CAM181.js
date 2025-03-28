'use strict';
eSoafApp.controller('CAM181Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, getParameter, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$controller('RegionController', {$scope: $scope});
		$scope.controllerName = "CAM181Controller";
		
		// 繼承
		$controller('CAM180Controller', {$scope: $scope});
		
		$scope.priID = String(sysInfoService.getPriID());
		/*
		 * 取得UHRM人員清單(由員工檔+角色檔)
		 */
		$scope.getUHRMList = function() {
			$scope.sendRecv("ORG260", "getUHRMList", "com.systex.jbranch.app.server.fps.org260.ORG260InputVO", $scope.inputVO, function(tota, isError) {
				if (isError) {
					return;
				}
				if (tota.length > 0) {
					$scope.mappingSet['UHRM_LIST'] = tota[0].body.uhrmList;
					$scope.inputVO.pCode = tota[0].body.uEmpID;
				}
			});
		};
		$scope.getUHRMList();
});