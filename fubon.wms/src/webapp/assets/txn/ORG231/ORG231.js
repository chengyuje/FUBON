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
			}
		});
	};
	
	$scope.save = function(row) {
		$scope.inputVO.SRM1_CNT = row.SRM1_CNT;
		$scope.inputVO.SRM2_CNT = row.SRM2_CNT;
		$scope.inputVO.SRM3_CNT = row.SRM3_CNT;
		$scope.inputVO.OPH_CNT  = row.OPH_CNT;
		$scope.inputVO.OP1_CNT  = row.OP1_CNT;
		$scope.inputVO.OP2_CNT  = row.OP2_CNT;
		$scope.inputVO.DEPT_ID  = row.DEPT_ID;
		
		$scope.sendRecv("ORG231", "save", "com.systex.jbranch.app.server.fps.org231.ORG231InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(tota.body.msgData);
				return;
			}
			
			if (tota.length > 0) {
				$scope.showMsg("儲存成功");
			}
			
			$scope.getList();
		});
	};
	
	$scope.init();
});
