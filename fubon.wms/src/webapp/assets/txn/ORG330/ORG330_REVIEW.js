/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('ORG330_REVIEWController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "ORG330_REVIEWController";
		
		// filter
		getParameter.XML(["COMMON.YES_NO", "COMMON.REVIEW_STATUS", "COMMON.ACT_TYPE", "ORG.SYS_ROLE"], function(totas) {
			if (totas) {
				$scope.mappingSet['COMMON.YES_NO'] = totas.data[totas.key.indexOf('COMMON.YES_NO')];
				$scope.mappingSet['COMMON.REVIEW_STATUS'] = totas.data[totas.key.indexOf('COMMON.REVIEW_STATUS')];
				$scope.mappingSet['COMMON.ACT_TYPE'] = totas.data[totas.key.indexOf('COMMON.ACT_TYPE')];
				$scope.mappingSet['ORG.SYS_ROLE'] = totas.data[totas.key.indexOf('ORG.SYS_ROLE')];
			}
		});
        //
		
		$scope.init = function(){
			$scope.inputVO = {
					ROLE_ID: $scope.ROLE_ID
        	};
		};
        $scope.init();

        $scope.sendRecv("ORG330", "getReviewList", "com.systex.jbranch.app.server.fps.org330.ORG330InputVO", $scope.inputVO, 
        	function(tota, isError) {
				if (!isError) {
					$scope.reviewList = tota[0].body.reviewList;
					$scope.outputVO = tota[0].body;
					
					return;
				}
			}
        );
});
