/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('ORG120_REVIEWController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "ORG120_REVIEWController";
		
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

        $scope.sendRecv("ORG120", "getReviewList", "com.systex.jbranch.app.server.fps.org120.ORG120InputVO", $scope.inputVO, 
        	function(tota, isError) {
				if (!isError) {
					$scope.reviewList = tota[0].body.reviewList;
					$scope.outputVO = tota[0].body;
					return;
				}
			}
        );
});
