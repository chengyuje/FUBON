'use strict';
eSoafApp.controller('PMS399_QUERYController', function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "PMS399_QUERYController";

	$scope.paramList = [];
	$scope.totalList = [];
	$scope.inputVO = {};
	$scope.inputVO.sCreDate = $scope.sCreDate;
	$scope.inputVO.eCreDate = $scope.eCreDate;
	$scope.inputVO.cust_id = $scope.cust_id;
	
	$scope.queryDetail = function(){ 
		$scope.sendRecv("PMS399", "queryDetail", "com.systex.jbranch.app.server.fps.pms399.PMS399InputdetailVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				if (tota[0].body.resultList.length == 0) {
					$scope.showMsg("ehl_01_common_009");
					return;
				}
				$scope.totalList = tota[0].body.resultList;
				$scope.outputVO = tota[0].body;
			}
		});
	}
	
	$scope.queryDetail();
});