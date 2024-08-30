/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CAM130_FAILController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CAM130_FAILController";
		
		$scope.inputVO = {};
		$scope.inputVO.seq = $scope.row.SEQNO;
		
		$scope.getFailData = function() {
			$scope.sendRecv("CAM130", "getFailData", "com.systex.jbranch.app.server.fps.cam130.CAM130InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.resultList.length == 0) {
							$scope.showMsg("ehl_01_common_009");
	            			return;
	            		}
						$scope.paramList = tota[0].body.resultList;
						$scope.outputVO = tota[0].body;
					}
			});
		};
		$scope.getFailData();
		
		
});