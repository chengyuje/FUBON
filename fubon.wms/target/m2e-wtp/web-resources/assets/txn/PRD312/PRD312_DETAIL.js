'use strict';
eSoafApp.controller('PRD312_DETAILController', function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "PRD312_DETAILController";
	
	// INFO
	$scope.sendRecv("PRD312", "getPrdDtl", "com.systex.jbranch.app.server.fps.prd312.PRD312InputVO", {'STOCK_CODE':$scope.row.PROD_ID}, function(tota, isError) {
		if (!isError) {
			$scope.insInfo = tota[0].body.resultList[0];
		}
	});
	
	//for App
	if (typeof(webViewParamObj) != 'undefined') {
		$scope.fromApp = true;
	}
});