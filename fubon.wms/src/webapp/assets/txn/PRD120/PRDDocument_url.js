'use strict';
eSoafApp.controller('PRDDocument_urlController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRDDocument_urlController";
		
		if($scope.isPDF) {
			$scope.pdfUrl = angular.copy($scope.url);
		}
		
		
		
});