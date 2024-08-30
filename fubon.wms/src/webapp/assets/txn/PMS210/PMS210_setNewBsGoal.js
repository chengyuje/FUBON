/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS210_setNewBsGoalController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS210_setNewBsGoalController";
		$scope.select = function(){
			$scope.inputVO.yearMon = $scope.resultList.YEARMON;
			$scope.inputVO.userID = projInfoService.getUserID();
			$scope.sendRecv("PMS210", "queryNewBsGoal", "com.systex.jbranch.app.server.fps.pms210.PMS210InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.newBsGoalList = tota[0].body.newBsGoalList;
							return;
						}
			});
		}
		$scope.select();
		
		//將表單數據插入或更新到數據庫
		$scope.save = function(){
			$scope.inputVO.newBsGoalList = $scope.newBsGoalList;
			for(var i = 0; i < $scope.inputVO.newBsGoalList.length; i++){
				if(""==String($scope.inputVO.newBsGoalList[i].MONTH1) || null==String($scope.inputVO.newBsGoalList[i].MONTH1)){
					$scope.showMsg("ehl_01_common_022");   //欄位檢核錯誤：必要輸入欄位
					return;
				}
				else if("" == String($scope.inputVO.newBsGoalList[i].MONTH2) || null==String($scope.inputVO.newBsGoalList[i].MONTH2)){
					$scope.showMsg("ehl_01_common_022");   //欄位檢核錯誤：必要輸入欄位
					return;
				}
				else if("" == String($scope.inputVO.newBsGoalList[i].MONTH3) || null==String($scope.inputVO.newBsGoalList[i].MONTH3)){
					$scope.showMsg("ehl_01_common_022");   //欄位檢核錯誤：必要輸入欄位
					return;
				}
				else if("" == String($scope.inputVO.newBsGoalList[i].MONTH4) || null==String($scope.inputVO.newBsGoalList[i].MONTH4)){
					$scope.showMsg("ehl_01_common_022");   //欄位檢核錯誤：必要輸入欄位
					return;
				}
				else if("" == String($scope.inputVO.newBsGoalList[i].MONTH5) || null==String($scope.inputVO.newBsGoalList[i].MONTH5)){
					$scope.showMsg("ehl_01_common_022");   //欄位檢核錯誤：必要輸入欄位
					return;
				}
				else if("" == String($scope.inputVO.newBsGoalList[i].MONTH6) || null==String($scope.inputVO.newBsGoalList[i].MONTH6)){
					$scope.showMsg("ehl_01_common_022");   //欄位檢核錯誤：必要輸入欄位
					return;
				}
			}
			
			$scope.sendRecv("PMS210","updateNewBsGoal", "com.systex.jbranch.app.server.fps.pms210.PMS210InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.showMsg("ehl_01_common_002");	//  修改成功
						return;
					}else{
						$scope.showErrorMsg("ehl_01_common_024");  //執行失敗
					}
				});
		}
		
});