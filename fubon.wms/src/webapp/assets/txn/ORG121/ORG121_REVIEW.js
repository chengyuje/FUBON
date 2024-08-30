'use strict';
eSoafApp.controller('ORG121_REVIEWController', function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "ORG121_REVIEWController";
	
	// filter
	getParameter.XML(["ORG.AOCODE_TYPE", "ORG.ATCH_REASON", "COMMON.ACT_TYPE", "COMMON.REVIEW_STATUS"], function(totas) {
		if (totas) {
			$scope.mappingSet['ORG.AOCODE_TYPE'] = totas.data[totas.key.indexOf('ORG.AOCODE_TYPE')];
			$scope.mappingSet['ORG.ATCH_REASON'] = totas.data[totas.key.indexOf('ORG.ATCH_REASON')];
			$scope.mappingSet['COMMON.ACT_TYPE'] = totas.data[totas.key.indexOf('COMMON.ACT_TYPE')];
			$scope.mappingSet['COMMON.REVIEW_STATUS'] = totas.data[totas.key.indexOf('COMMON.REVIEW_STATUS')];
		}
	});
	//===
	
	$scope.init = function(){
		$scope.inputVO.empID = $scope.row.EMP_ID

	};
    $scope.init();

    $scope.sendRecv("ORG121", "getReviewList", "com.systex.jbranch.app.server.fps.org121.ORG121InputVO", $scope.inputVO, 
    	function(tota, isError) {
			if (!isError) {
				$scope.reviewList = tota[0].body.reviewList;
				$scope.outputVO = tota[0].body;
				return;
			}
		}
    );
});
