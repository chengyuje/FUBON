/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('JSB100_LOGController', function($rootScope, $scope, $controller) {
    $controller('BaseController', {$scope: $scope});
    $scope.controllerName = "JSB100_LOGController";
    
    $scope.init = function() {
    	$scope.inputVO.seq = $scope.row.SEQ;
		$scope.inputVO.acceptid = $scope.row.ACCEPTID;
		$scope.inputVO.policy_nbr = $scope.row.POLICY_NBR;
		$scope.inputVO.policy_simp_name = $scope.row.POLICY_SIMP_NAME;
		
		$scope.sendRecv("JSB100", "getEditLog", "com.systex.jbranch.app.server.fps.jsb100.JSB100InputVO", $scope.inputVO,
		function(tota, isError) {
			if (!isError) {
				$scope.resultList = [];
				if (tota[0].body.resultList.length > 0) {
					$scope.resultList = tota[0].body.resultList;
				} else {
					$scope.showMsg("ehl_01_common_009");	//查無資料
					return;
				}
			}
		});
	}
    $scope.init();
});