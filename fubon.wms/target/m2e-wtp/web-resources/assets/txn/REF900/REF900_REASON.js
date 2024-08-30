'use strict';
eSoafApp.controller('REF900_REASONController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "REF900_REASONController";
		
		$scope.inputVO = {};
		$scope.save = function() {
			if(!$scope.inputVO.reason) {
				$scope.showErrorMsg("請填寫不接受原因！");
				return;
			}
			$scope.closeThisDialog($scope.inputVO.reason);
		};
		
		
		
});