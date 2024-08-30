/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS210_setBsGoalTarController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS210_setBsGoalTarController";
		
		$scope.select = function() {
			$scope.inputVO.yearMon = $scope.resultList.YEARMON;
			$scope.inputVO.userID = projInfoService.getUserID();
			$scope.bsGoalTarList = [];
			$scope.sendRecv(
				"PMS210",
				"queryBsGoalTar",
				"com.systex.jbranch.app.server.fps.pms210.PMS210InputVO",
				$scope.inputVO,
				function(tota, isError) {
					if (!isError) {
							$scope.bsGoalTarList = tota[0].body.bsGoalTarList;
							return;
						}
					});
		}
		$scope.select();
		
		//將輸入框中的內容加入bsGoalTarList中
		$scope.add = function(){
			var GOAL_RATE_START = $scope.GOAL_RATE_START;
			var GOAL_RATE_END = $scope.GOAL_RATE_END;
			var BONUS_DISC = $scope.BONUS_DISC;
			if (null == GOAL_RATE_START || "" == GOAL_RATE_START
					|| null == GOAL_RATE_END || "" == GOAL_RATE_END
					|| null == BONUS_DISC || "" == BONUS_DISC) {
				$scope.showMsg("ehl_01_common_022");	//欄位檢核錯誤：必要輸入欄位
				return;
			}
			
			for (var i = 0; i < $scope.bsGoalTarList.length; i++) {
				if ($scope.bsGoalTarList[i].GOAL_RATE_START == GOAL_RATE_START
						&& $scope.bsGoalTarList[i].GOAL_RATE_END == GOAL_RATE_END) {
					$scope.showMsg("ehl_01_common_016");	//資料已存在
					return;
				}
			}
			var a = new Object();
			a.GOAL_RATE_END = GOAL_RATE_END;
			a.BONUS_DISC = BONUS_DISC;
			a.GOAL_RATE_START = GOAL_RATE_START;
			$scope.bsGoalTarList.push(a);
			$scope.GOAL_RATE_START = null;
			$scope.GOAL_RATE_END = null;
			$scope.BONUS_DISC = null;
		}
		//刪除bsGoalTarList中對應的項
		$scope.del = function(delIndex){
			$scope.bsGoalTarList.splice(delIndex,1);
		}
		//將表單數據插入或更新到數據庫
		$scope.save = function() {
			$scope.inputVO.bsGoalTarList = $scope.bsGoalTarList;
			if(isCheckError()){
				$scope.showMsg("欄位檢核錯誤：數據有重複");
				return;
			}
			$scope.sendRecv(
				"PMS210",
				"updateBsGoalTar",
				"com.systex.jbranch.app.server.fps.pms210.PMS210InputVO",
				$scope.inputVO,
				function(tota, isError) {
					if(!isError){
						$scope.showMsg("ehl_01_common_002"); // 修改成功
						return;						
					}else{
						$scope.showErrorMsg("ehl_01_common_024");	//執行失敗
					}
				});
		}

		/*檢驗輸入，若輸入的數據有重複*/
		function isCheckError(){
			for (var i = 0; i < $scope.inputVO.bsGoalTarList.length ; i++) {
				var startNum = Number($scope.inputVO.bsGoalTarList[i].GOAL_RATE_START);
				var endNum = Number($scope.inputVO.bsGoalTarList[i].GOAL_RATE_END);
				if(startNum > endNum ) {
						return true;	//若startNum > endNum 
				}
				for(var j = i+1; j < $scope.inputVO.bsGoalTarList.length ; j++){
					var nextStartNum = Number($scope.inputVO.bsGoalTarList[j].GOAL_RATE_START);
					var nextEndNum = Number($scope.inputVO.bsGoalTarList[j].GOAL_RATE_END);
					if((startNum < nextStartNum) && (endNum >= nextStartNum)){
						return true;		//有重複數據，返回false
					}
					if((startNum > nextStartNum) && (startNum <= nextEndNum)){
						return true;		//有重複數據，返回false
					}
					if(startNum == nextStartNum){
						return true;		//有重複數據，返回false
					}
				}
			}
			return false;
		}
		
});