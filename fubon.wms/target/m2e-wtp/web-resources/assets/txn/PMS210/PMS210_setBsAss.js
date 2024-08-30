/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS210_setBsAssController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS210_setBsAssController";
		
		//初始化頁面，先查詢
		$scope.select = function(){
        	$scope.inputVO.yearMon = $scope.resultList.YEARMON;
			$scope.sendRecv("PMS210", "queryBsAss", "com.systex.jbranch.app.server.fps.pms210.PMS210InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.bsAssList = tota[0].body.bsAssList;
						}
			});
		}
		$scope.select();
		
		//校驗輸入數據不能為空
		$scope.checkNull = function(){
			for(var i=0; i<$scope.bsAssList.length; i++){
				for(var j in $scope.bsAssList[i]){
					if(String($scope.bsAssList[i][j]) == undefined || String($scope.bsAssList[i][j]) == ''){
						$scope.showMsg("ehl_01_common_022");
						return;
					}
				}
			}
			$scope.save();
		}
		
		//储存页面输入数据
		$scope.save = function(){
			$scope.inputVO.bsAssList = $scope.bsAssList;			
			$scope.inputVO.userID = sysInfoService.getUserID();
			$scope.inputVO.yearMon = $scope.resultList.YEARMON;
			
			$scope.sendRecv("PMS210", "saveBsAss", "com.systex.jbranch.app.server.fps.pms210.PMS210InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.backResult==4){
								$scope.showMsg("ehl_01_common_002"); // 修改成功
							}else{
								$scope.showMsg("ehl_01_common_007");//更新失敗
							}
						}
			});
		}
});