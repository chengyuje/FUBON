/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CAM150_SPRController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CAM150_SPRController";
		
		$scope.init = function(){
			$scope.inputVO = {
					totalNum: '',
					successNum: '', 
					failureNum: ''
        	};
		};
        $scope.init();
        
        // edit data
		$scope.sendRecv("CAM150", "getSpreadsheets", "com.systex.jbranch.app.server.fps.cam150.CAM150InputVO", {seqNo: $scope.seqNo},
				function(tota, isError) {
					if (!isError) {
						$scope.inputVO.totalNum = tota[0].body.totalNum;
						$scope.inputVO.successNum = tota[0].body.successNum;
						$scope.inputVO.failureNum = tota[0].body.failureNum;
						return;
					}
		});

});
