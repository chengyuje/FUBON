/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('IOT400_REJECTController', function($rootScope, $scope, $controller) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "IOT400_REJECTController";
	
	$scope.init = function() {
		$scope.inputVO.PREMATCH_SEQ = $scope.prematch_seq;
//		alert($scope.inputVO.PREMATCH_SEQ);
	}
	
	$scope.reject = function() {
		if ($scope.inputVO.REJECT_REASON == undefined) {
			$scope.showErrorMsg('請輸入退回原因');
    		return;
		}
		
		$scope.sendRecv("IOT400","reject","com.systex.jbranch.app.server.fps.iot400.IOT400InputVO", $scope.inputVO,
		function(tota, isError) {
			if (!isError) {
				$scope.showSuccessMsg('ehl_01_common_020');		// 退回成功
				$scope.closeThisDialog('successful');
			}
		});
	}
	
	$scope.init();	
});