/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM811Controller', function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
	$controller('BaseController', {
		$scope : $scope
	});
	$scope.controllerName = "CRM811Controller";

	// 初始化
	$scope.init = function() {
		// $scope.inputVO.cust_id = 'A201262543'; //test
		// $scope.inputVO.cust_id = $scope.connector('get','CRM110_CUST_ID');
		$scope.inputVO.cust_id = $scope.custVO.CUST_ID;
	};
	$scope.init();

	// 初始化
	$scope.inquireInit = function() {
		$scope.resultList = [];
		$scope.resultList2 = [];
	}
	$scope.inquireInit();

	// 查詢台幣活存
	$scope.inquire = function() {
		$scope.sendRecv("CRM811", "inquire", "com.systex.jbranch.app.server.fps.crm811.CRM811InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsg(totas[0].body.msgData);
			}
			if (tota[0].body.resultList == null || tota[0].body.resultList.length == 0) {
//				$scope.showMsg("ehl_01_common_009");
				return;
			}
			$scope.resultList = tota[0].body.resultList;
			$scope.outputVO = tota[0].body;
			return;
		})
	};
	$scope.inquire();
	// 查詢外幣活存
	$scope.inquire2 = function() {
		$scope.sendRecv("CRM811", "inquire2", "com.systex.jbranch.app.server.fps.crm811.CRM811InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsg(totas[0].body.msgData);
			}
			if (tota[0].body.resultList == null || tota[0].body.resultList.length == 0) {
//				$scope.showMsg("ehl_01_common_009");
				return;
			}
			$scope.resultList2 = tota[0].body.resultList;
			$scope.outputVO2 = tota[0].body;
			return;
		})
	};
	$scope.inquire2();
	
});
