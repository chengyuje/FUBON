'use strict';
eSoafApp.controller('PMS365_DETAILController', function($scope, $controller, $confirm, $compile, socketService, ngDialog, projInfoService) {
	$controller('BaseController', {$scope : $scope});
	
	$scope.controllerName = "PMS365_DETAILController";
	
	$scope.mappingSet['AO_TYPE'] = [];
	$scope.mappingSet['AO_TYPE'].push({LABEL:'(計績)', DATA:'1'},{LABEL:'(兼職)', DATA:'2'},{LABEL:'(維護)', DATA:'3'},{LABEL:'(UHRM計績)', DATA:'5'},{LABEL:'(UHRM維護)', DATA:'6'});
	
	$scope.mappingSet['AUM_TYPE'] = [];
	$scope.mappingSet['AUM_TYPE'].push({LABEL:'存款AUM(萬)', DATA:'SAV'},{LABEL:'投資AUM(萬)', DATA:'INV'},{LABEL:'保險AUM(萬)', DATA:'INS'});
		
	
	$scope.inputVO = {};
	
	$scope.sendRecv("PMS365", "getDTL", "com.systex.jbranch.app.server.fps.pms365.PMS365InputVO", {'yyyymm'   : $scope.yyyymm,
																								   'branchNbr': $scope.branchNbr,
																								   'aoCode'   : $scope.aoCode, 
																								   'aumTime'  : $scope.aumTime, 
																								   'aumType'  : $scope.aumType}, function(tota, isError) {
		if (!isError) {
			$scope.dtlList = tota[0].body.dtlList;
			$scope.outputVO = tota[0].body;
		}
	});
	
});
