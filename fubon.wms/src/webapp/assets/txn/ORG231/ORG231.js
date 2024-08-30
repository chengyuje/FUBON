'use strict';
eSoafApp.controller('ORG231Controller', function(sysInfoService, $scope, $controller, $confirm, $compile, socketService, ngDialog, projInfoService) {
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "ORG231Controller";
	
	$scope.init = function() {
		$scope.inputVO = {
			RM1_CNT    : 0,
			RM2_CNT    : 0,
			SRM_CNT    : 0,
			MODIFIER   : '',
			LASTUPDATE : ''
		};
		
		$scope.getList();
	};
	
	$scope.getList = function() {
		$scope.sendRecv("ORG231", "getList", "com.systex.jbranch.app.server.fps.org231.ORG231InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(tota.body.msgData);
				return;
			}
			
			if (tota.length > 0) {
				$scope.resultList = tota[0].body.resultList;
				$scope.outputVO = tota[0].body;
				
				$scope.inputVO.RM1_CNT    = $scope.resultList[0].RM1_CNT;
				$scope.inputVO.RM2_CNT    = $scope.resultList[0].RM2_CNT;
				$scope.inputVO.SRM_CNT    = $scope.resultList[0].SRM_CNT;
				$scope.inputVO.MODIFIER   = $scope.resultList[0].MODIFIER;
				$scope.inputVO.LASTUPDATE = $scope.resultList[0].LASTUPDATE;
			}
		});
	};
	
	$scope.save = function() {
		$scope.sendRecv("ORG231", "save", "com.systex.jbranch.app.server.fps.org231.ORG231InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(tota.body.msgData);
				return;
			}
			
			if (tota.length > 0) {
				$scope.showMsg("ehl_01_common_010");
			}
			
			$scope.getList();
		});
	};
	
	$scope.init();
});
