'use strict';
eSoafApp.controller('PRD313_DETAILController', function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "PRD313_DETAILController";
	
	// INFO
	$scope.sendRecv("PRD313", "getPrdDtl", "com.systex.jbranch.app.server.fps.prd313.PRD313InputVO", {'STOCK_CODE': $scope.row.STOCK_CODE}, function(tota, isError) {
		if (!isError) {
			$scope.insInfo = tota[0].body.resultList[0];
		}
	});
	
	//for App
	if (typeof(webViewParamObj) != 'undefined') {
		$scope.fromApp = true;
	}
});