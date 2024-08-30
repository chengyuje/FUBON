'use strict';
eSoafApp.controller('PMS414_REASONController', function($rootScope, $scope, $controller, $confirm, $filter, socketService, ngDialog, projInfoService,sysInfoService) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "PMS414_REASONController";

	$scope.inputVO = {
		reason: ''
	};
	
	$scope.enterReason = function(type) {
		if (type == 'cancel') {
			var data = {
				reason : $scope.inputVO.reason,
				status : 'cancel'
			}

	        $scope.closeThisDialog(data);
		} else {
			if ($scope.inputVO.reason == undefined || $scope.inputVO.reason == null || $scope.inputVO.reason == '') {
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
	    		return;
	    	}
			
			var data = {
				reason : $scope.inputVO.reason,
				status : 'success'
			}

	        $scope.closeThisDialog(data);
		}
		
	};
});