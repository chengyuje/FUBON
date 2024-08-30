'use strict';
eSoafApp.controller('PMS364_DETAILController', function($scope, $controller, $confirm, $compile, socketService, ngDialog, projInfoService) {
	$controller('BaseController', {
		$scope : $scope
	});
	$scope.controllerName = "PMS364_DETAILController";
	
	$scope.inputVO = {};
	
	$scope.sendRecv("PMS364", "getDTL", "com.systex.jbranch.app.server.fps.pms364.PMS364InputVO", {'yyyymm': $scope.yyyymm, 
																								   'branchNbr': $scope.branchNbr, 
																								   'custAO': $scope.custAO, 
																								   'type': $scope.type}, function(tota, isError) {
		if (!isError) {
			$scope.dtlList = tota[0].body.dtlList;
			$scope.outputVO = tota[0].body;
		}
	});
	
	$scope.exportDtlRPT = function(){
		
		$scope.inputVO.exportDtlList = $scope.dtlList;
		$scope.inputVO.yyyymm = $scope.yyyymm;
		$scope.inputVO.custAO = $scope.custAO;
		$scope.inputVO.type = $scope.type;
		
		$scope.sendRecv("PMS364", "exportDtl", "com.systex.jbranch.app.server.fps.pms364.PMS364InputVO", $scope.inputVO, function(tota, isError) {						
			if (isError) {
        		$scope.showErrorMsgInDialog(tota[0].body.msgData);		            		
        	}
		});
	};
	
});
