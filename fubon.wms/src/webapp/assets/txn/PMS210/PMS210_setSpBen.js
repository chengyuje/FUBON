/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS210_setSpBenController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS210_setSpBenController";

		$scope.select = function() {
			$scope.inputVO.yearMon = $scope.resultList.YEARMON;
			$scope.inputVO.userID = projInfoService.getUserID();
			$scope.sendRecv("PMS210", "querySpBen",
					"com.systex.jbranch.app.server.fps.pms210.PMS210InputVO",
					$scope.inputVO, function(tota, isError) {
						if (!isError) {
							$scope.spBenList = tota[0].body.spBenList;
							return;
						}
					});
		}
		$scope.select();
		
		// 將表單數據插入或更新到數據庫
	$scope.save = function() {
		$scope.inputVO.spBenList = $scope.spBenList;
		for (var i = 0; i < $scope.inputVO.spBenList.length; i++) {
			if ("" == String($scope.inputVO.spBenList[i].MULTIPLE1)) {
				$scope.showMsg("ehl_01_common_022");  //欄位檢核錯誤：必要輸入欄位
				return;
			} else if ("" == String($scope.inputVO.spBenList[i].MULTIPLE2)) {
				$scope.showMsg("ehl_01_common_022");  //欄位檢核錯誤：必要輸入欄位
				return;
			} else if ("" == String($scope.inputVO.spBenList[i].MULTIPLE3)) {
				$scope.showMsg("ehl_01_common_022");  //欄位檢核錯誤：必要輸入欄位
				return;
			}
		}
		if (!myForm.$valid) {
		} 
			$scope.sendRecv("PMS210", "updateSpBen",
					"com.systex.jbranch.app.server.fps.pms210.PMS210InputVO",
					$scope.inputVO, function(tota, isError) {
						if (!isError) {
							$scope.showMsg("ehl_01_common_002");  //修改成功
							return;
						}
						
					});
		

		
		
	}
		
});