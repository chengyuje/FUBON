'use strict';
eSoafApp.controller('PMS418U_DETAILController', function($rootScope, $scope, $controller) {
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "PMS418U_DETAILController";

	// 繼承
	$controller('PMS418_DETAILController', {$scope: $scope});
});