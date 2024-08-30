'use strict';
eSoafApp.controller('PMS366_DETAILController', function($scope, $controller, $confirm, $compile, socketService, ngDialog, projInfoService, getParameter) {
	$controller('BaseController', {$scope : $scope});
	
	$scope.controllerName = "PMS366_DETAILController";
	
	// filter
	getParameter.XML(["PMS.DEP_TYPE"], function(totas) {
		if (totas) {
			$scope.mappingSet['PMS.DEP_TYPE'] = totas.data[totas.key.indexOf('PMS.DEP_TYPE')];
		}
	});
	
	$scope.mappingSet['AO_TYPE'] = [];
	$scope.mappingSet['AO_TYPE'].push({LABEL:'(計績)', DATA:'1'},{LABEL:'(兼職)', DATA:'2'},{LABEL:'(維護)', DATA:'3'},{LABEL:'(UHRM計績)', DATA:'5'},{LABEL:'(UHRM維護)', DATA:'6'});
    //
	
	$scope.inputVO = {};
	
	$scope.sendRecv("PMS366", "getDTL", "com.systex.jbranch.app.server.fps.pms366.PMS366InputVO", {'YYYYMM'   : $scope.yyyymm,
																								   'AO_CODE'  : $scope.aoCode,
																								   'DEP_TYPE' : $scope.depType}, function(tota, isError) {
		if (!isError) {
			$scope.dtl_1_List = tota[0].body.dtl_1_List;
			$scope.dtl_2_List = tota[0].body.dtl_2_List;
			$scope.outputVO = tota[0].body;
		}
	});
	
});
