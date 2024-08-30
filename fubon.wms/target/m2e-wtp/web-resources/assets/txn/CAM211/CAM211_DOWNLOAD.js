'use strict';
eSoafApp.controller('CAM211_DOWNLOADController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CAM211_DOWNLOADController";
		
		$scope.exportReport1 = function() {
			$scope.sendRecv("CAM210", "exportReport1", "com.systex.jbranch.app.server.fps.cam210.CAM210InputVO", $scope.inputVO,
    				function(totas, isError) {
						if (isError) {
							$scope.showErrorMsgInDialog(tota.body.msgData);
							return;
						}
						if (totas.length > 0) {
						}
    				}
    		);
		};
		
		$scope.exportReport2 = function() {
			$scope.sendRecv("CAM210", "exportReport2", "com.systex.jbranch.app.server.fps.cam210.CAM210InputVO", $scope.inputVO,
    				function(totas, isError) {
						if (isError) {
							$scope.showErrorMsgInDialog(tota.body.msgData);
							return;
						}
						if (totas.length > 0) {
						}
    				}
    		);
		};
		
		$scope.exportReport3 = function() {
			$scope.sendRecv("CAM210", "exportReport3", "com.systex.jbranch.app.server.fps.cam210.CAM210InputVO", $scope.inputVO,
    				function(totas, isError) {
						if (isError) {
							$scope.showErrorMsgInDialog(tota.body.msgData);
							return;
						}
						if (totas.length > 0) {
						}
    				}
    		);
		};
		
});