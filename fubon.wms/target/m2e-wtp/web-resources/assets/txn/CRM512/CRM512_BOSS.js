'use strict';
eSoafApp.controller('CRM512_BOSSController', function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "CRM512_BOSSController";
	
	$scope.init = function(){
		$scope.inputVO = {
			qusBankListBefore: $scope.qusBankListBefore,
			qusBankListAfter: $scope.qusBankListAfter,
			custID: $scope.custID, 
			checkBossFlag: 'Y'
    	};
	};
    $scope.init();
    
    $scope.bossCallSaveFunc = function () {
    	
    	if (sysInfoService.getUserID() == $scope.inputVO.bossEmpID) {
    		$scope.showMsg('維護人員與授權人員不得為同一人。');
    	} else {
    		$scope.sendRecv("CRM512", "save", "com.systex.jbranch.app.server.fps.crm512.CRM512InputVO", $scope.inputVO, function(tota, isError) {
    			if (isError) {
                	$scope.showErrorMsgInDialog(tota.body.msgData);
                    return;
                } else {
                	$scope.closeThisDialog('successful');
                }
    		});
    	}
    }
});
