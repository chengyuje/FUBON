/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS210_setBsPointController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS210_setBsPointController";
		
		//初始化，先查詢
		$scope.select = function(){
        	$scope.inputVO.yearMon = $scope.resultList.YEARMON;
			$scope.sendRecv("PMS210", "queryBsPoint", "com.systex.jbranch.app.server.fps.pms210.PMS210InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.bsPointList = tota[0].body.bsPointList;
						}
			});
		}
		$scope.select();
		
		
		//校驗輸入數據不能為空
		$scope.checkNull = function(){
			for(var i=0; i<4; i++){
				for(var j in $scope.bsPointList[i]){
					if(String($scope.bsPointList[i][j]) == undefined || String($scope.bsPointList[i][j]) == ''){
						$scope.showMsg("ehl_01_common_022");	//欄位檢核錯誤：必要輸入欄位
						return;
					}
				}
			}
			$scope.save();
		}
		
		//储存页面输入数据
		$scope.save = function(){
			$scope.inputVO.bsPointList = $scope.bsPointList;
			$scope.inputVO.userID = sysInfoService.getUserID();
			$scope.inputVO.yearMon = $scope.resultList.YEARMON;
			
			$scope.sendRecv("PMS210", "saveBsPoint", "com.systex.jbranch.app.server.fps.pms210.PMS210InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.backResult==4){
								$scope.showMsg("ehl_01_common_002");
							}else{
								$scope.showMsg("ehl_01_common_007");
							}
						}
			});
		}
});