/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS210_setSpAssController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS210_setSpAssController";
		
		//初始化頁面，先查詢
		$scope.select = function(){
        	$scope.inputVO.yearMon = $scope.resultList.YEARMON;
			$scope.sendRecv("PMS210", "querySpAss", "com.systex.jbranch.app.server.fps.pms210.PMS210InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.spAssList = tota[0].body.spAssList;
						}
			});
		}
		$scope.select();
		
		
		//校驗輸入數據不能為空
		$scope.checkNull = function(){
			for(var i=0; i<6; i++){
				for(var j in $scope.spAssList[i]){
					if(String($scope.spAssList[i][j]) == undefined || String($scope.spAssList[i][j]) == ''){
						$scope.showMsg("ehl_01_common_022");  //欄位檢核錯誤：必要輸入欄位
						return;
					}
				}
			}
			$scope.save();
		}
		
		//储存页面输入数据
		$scope.save = function(){
			$scope.inputVO.spAssList = $scope.spAssList;
			$scope.inputVO.userID = sysInfoService.getUserID();
			
			$scope.sendRecv("PMS210", "saveSpAss", "com.systex.jbranch.app.server.fps.pms210.PMS210InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.backResult==6){
								$scope.showMsg("ehl_01_common_002"); //修改成功
								$scope.changeSet();
							}else{
								$scope.showMsg("ehl_01_common_007");  //更新失敗
							}
						}
			});
		}
		
		//将未设定改为已设定
		$scope.changeSet = function(){
			$scope.inputVO.yearMon = $scope.resultList.YEARMON;
			$scope.sendRecv("PMS210", "changeSet", "com.systex.jbranch.app.server.fps.pms210.PMS210InputVO", $scope.inputVO,
					function(tota, isError) {
						if(!isError){
							
						}
			});
		}
});