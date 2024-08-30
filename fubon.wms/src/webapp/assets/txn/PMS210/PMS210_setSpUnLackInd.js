/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS210_setSpUnLackIndController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS210_setSpUnLackIndController";
		
		//初始化頁面，先查詢
		$scope.select = function(){
        	$scope.inputVO.yearMon = $scope.resultList.YEARMON;
			$scope.sendRecv("PMS210", "querySpUnLackInd", "com.systex.jbranch.app.server.fps.pms210.PMS210InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.spUnLackIndList = tota[0].body.spUnLackIndList;
						}
			});
		}
		$scope.select();
		
		//校驗輸入數據不能為空
		$scope.checkNull = function(){
			for(var i=0; i<6; i++){
				for(var j in $scope.spUnLackIndList[i]){
					if(String($scope.spUnLackIndList[i][j]) == undefined || String($scope.spUnLackIndList[i][j]) == ''){
						$scope.showMsg("ehl_01_common_022");  //欄位檢核錯誤：必要輸入欄位
						return;
					}
				}
			}
			$scope.save();
		}
		
		//储存页面输入数据
		$scope.save = function(){
			$scope.inputVO.spUnLackIndList = $scope.spUnLackIndList;
			$scope.inputVO.userID = sysInfoService.getUserID();
			$scope.inputVO.yearMon = $scope.resultList.YEARMON;
			
			$scope.sendRecv("PMS210", "saveSpUnLackInd", "com.systex.jbranch.app.server.fps.pms210.PMS210InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.backResult>0){
								$scope.showMsg("ehl_01_common_002");  //修改成功
							}else{
								$scope.showMsg("ehl_01_common_007");  //更新失敗
							}
						}
			});
		}
});