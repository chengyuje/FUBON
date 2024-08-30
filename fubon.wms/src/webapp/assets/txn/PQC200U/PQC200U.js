'use strict';
eSoafApp.controller('PQC200UController', function(sysInfoService, $rootScope, $scope, $controller, socketService, ngDialog, projInfoService, getParameter, $confirm) {
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "PQC200UController";
	
	//組織連動繼承
	$controller('RegionController', {$scope: $scope});
	
	// 繼承
	$controller('PQC200Controller', {$scope: $scope});
	
	
	$scope.initBy200U = function() {
		$scope.sendRecv("PMS401U", "isMainten", "com.systex.jbranch.app.server.fps.pms401u.PMS401UInputVO", {'itemID': 'IOT190'}, function(tota, isError) {
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
		
		$scope.inputVO.u_emp_Id = '';
	};
	
	$scope.initBy200U();	
});
