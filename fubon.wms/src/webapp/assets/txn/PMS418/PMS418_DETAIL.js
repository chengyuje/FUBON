'use strict';
eSoafApp.controller('PMS418_DETAILController', function($rootScope, $scope, $controller) {
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "PMS418_DETAILController";

	/** 查詢明細 **/
    $scope.queryDetail = function() {
    	$scope.sendRecv("PMS418", "queryDetail", "com.systex.jbranch.app.server.fps.pms418.PMS418InputVO", $scope.inputVO, function(tota, isError) {
    		if (!isError) {
				$scope.details = tota[0].body.detailList;
				$scope.outputVO = tota[0].body;
				
				console.log(JSON.stringify($scope.details))

				if (!$scope.details.length)
					$scope.showMsg("ehl_01_common_009");
			}						
    	});
    }; 
    
	$scope.queryDetail();

});