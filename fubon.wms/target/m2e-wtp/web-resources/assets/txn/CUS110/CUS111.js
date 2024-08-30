/*
 */
'use strict';                                
eSoafApp.controller('CUS111Controller', function($scope, $rootScope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "CUS111Controller";
	
	// combobox
	getParameter.XML(["SYS.CATEGORY_CONTENT", "SYS.CATEGORY_PRODUCT"], function(totas) {
		if (totas) {
			$scope.CATEGORY_CONTENT = totas.data[totas.key.indexOf('SYS.CATEGORY_CONTENT')];
			$scope.CATEGORY_PRODUCT = totas.data[totas.key.indexOf('SYS.CATEGORY_PRODUCT')];
		}
	});
	
	$scope.inputVO = {};
	$scope.sendRecv("CUS110", "queryAddData", "com.systex.jbranch.app.server.fps.cus110.CUS110InputVO", {},
			function(tota, isError) {
				if (!isError) {
					$scope.dataList = tota[0].body.resultList;
				}
	});
	
	$scope.btnContent = function() {
		var ans = $scope.dataList.filter(function(obj) {
    		return (obj.getData == true);
    	});
		if(ans.length == 0) {
    		return;
    	}
		$scope.closeThisDialog(ans);
	};
	
	
});