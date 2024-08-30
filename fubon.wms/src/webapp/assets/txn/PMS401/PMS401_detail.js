'use strict';
eSoafApp.controller('PMS401_QUERYController', function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter, sysInfoService) {
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "PMS401_QUERYController";

	$scope.paramList = [];
	$scope.totalList = [];
	$scope.inputVO = {};
	$scope.inputVO.txDate = $scope.txDate;
	$scope.inputVO.empID = $scope.empID;
	
	$scope.pri = String(sysInfoService.getPriID());
	
	$scope.queryDetail = function(){ 
		$scope.sendRecv("PMS401", "queryDetail", "com.systex.jbranch.app.server.fps.pms401.PMS401InputdetailVO", $scope.inputVO, function(tota, isError) {
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
	
	$scope.exportRPT = function() {
		$scope.sendRecv("PMS401", "exportDtl", "com.systex.jbranch.app.server.fps.pms401.PMS401OutputVO", $scope.outputVO, function(tota, isError) {						
			if (isError) {
        		$scope.showErrorMsgInDialog(tota[0].body.msgData);		            		
        	}
			if (tota.length > 0) {
        		if (tota[0].body.resultList && tota[0].body.resultList.length == 0) {
        			$scope.showMsg("ehl_01_common_009");
        			return;
        		}
        	};
		});
	};
	
	$scope.queryDetail();
});