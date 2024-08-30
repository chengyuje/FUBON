'use strict';
eSoafApp.controller('PRD100_BOSSController', function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "PRD100_BOSSController";
	
	$scope.init = function() {
		$scope.inputVO = {};
		$scope.inputVO.invalidMsgList = $scope.invalidMsgList;
		$scope.inputVO.matchDate = $scope.matchDate;
		$scope.inputVO.prodType = $scope.prodType;
		$scope.inputVO.tradeType = $scope.tradeType;
		$scope.inputVO.seniorAuthType = $scope.seniorAuthType;
		$scope.inputVO.custID = $scope.custID;
		$scope.inputVO.trustTS = $scope.trustTS;
		$scope.inputVO.invalidMsgC = $scope.invalidMsgC;
		$scope.inputVO.invalidMsgF = $scope.invalidMsgF;
	};
    $scope.init();
    
    $scope.checkBossAdmin = function () {
		$scope.sendRecv("PRD100", "checkBossAdmin", "com.systex.jbranch.app.server.fps.prd100.PRD100InputVO", $scope.inputVO, 
			function(tota, isError) {
				if (isError) {
	                return;
	            } else {
	            	$scope.closeThisDialog('successful');
	            }
		});
    }
});
