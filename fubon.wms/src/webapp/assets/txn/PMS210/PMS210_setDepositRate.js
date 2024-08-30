/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS210_setDepositRateController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS210_setDepositRateController";
		
		//初始化頁面，先查詢
		$scope.select = function(){
        	$scope.inputVO.yearMon = $scope.resultList.YEARMON;
			$scope.sendRecv("PMS210", "queryDepositRate", "com.systex.jbranch.app.server.fps.pms210.PMS210InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.depositRateList = tota[0].body.depositRateList;
						}
			});
		}
		$scope.select();
		
		//校驗輸入數據不能為空
		$scope.checkNull = function(){
			$scope.inputVO.depositRateList = $scope.depositRateList;
			for (var i = 0; i < $scope.inputVO.depositRateList.length; i++) {
				if (String($scope.inputVO.depositRateList[i].DEPOSIT_RATE) == "") {
					$scope.showMsg("ehl_01_common_022");	//欄位檢核錯誤：必要輸入欄位
					return;
				}
			}
			$scope.save();
		}
		
		//储存页面输入数据
		$scope.save = function(){
			$scope.inputVO.userID = sysInfoService.getUserID();
			$scope.inputVO.yearMon = $scope.resultList.YEARMON;
			
			$scope.sendRecv("PMS210", "saveDepositRate", "com.systex.jbranch.app.server.fps.pms210.PMS210InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.backResult>0){
								$scope.showMsg("ehl_01_common_002");
							}else{
								$scope.showMsg("ehl_01_common_007");
							}
						}
			});
		}
});