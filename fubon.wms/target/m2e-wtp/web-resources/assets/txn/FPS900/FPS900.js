'use strict';
eSoafApp.controller('FPS900Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "FPS900Controller";
		
		$scope.init = function(tag) {
			switch (tag) {
				case '1':
					$scope.$broadcast("FPS900_Controller.query1");
					break;
				case '2':
					$scope.$broadcast("FPS900_Controller.query2");
					break;
				case '3':
					$scope.$broadcast("FPS900_Controller.query3");
					break;
				case '4':
					$scope.$broadcast("FPS900_Controller.query4");
					break;
				default:
					break;
			}
		};
		
});