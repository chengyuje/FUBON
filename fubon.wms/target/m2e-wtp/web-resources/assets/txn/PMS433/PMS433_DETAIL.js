'use strict';
eSoafApp.controller('PMS433_DETAILController', function($scope, $controller, $confirm, $compile, socketService, ngDialog, projInfoService, getParameter) {
	$controller('BaseController', {
		$scope : $scope
	});
	$scope.controllerName = "PMS433_DETAILController";
	
	$scope.inputVO = {};
	
	getParameter.XML(["PMS.HIGH_SOT_TRADE_SOURCE", "PMS.HIGH_SOT_PROD_TYPE"], function(totas) {
		if (totas) {
			$scope.mappingSet['PMS.HIGH_SOT_TRADE_SOURCE'] = totas.data[totas.key.indexOf("PMS.HIGH_SOT_TRADE_SOURCE")];
			$scope.mappingSet['PMS.HIGH_SOT_PROD_TYPE'] = totas.data[totas.key.indexOf("PMS.HIGH_SOT_PROD_TYPE")];
		}
	});
	debugger;
	$scope.sendRecv("PMS433", "getDtl", "com.systex.jbranch.app.server.fps.pms433.PMS433InputVO", {'seq': $scope.seq}, function(tota, isError) {
		if (!isError) {
			$scope.dtlList = tota[0].body.detailList;
			$scope.outputVO = tota[0].body;
		}
	});
});
