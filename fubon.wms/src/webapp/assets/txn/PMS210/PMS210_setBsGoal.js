/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS210_setBsGoalController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS210_setBsGoalController";
	
		

		$scope.select = function() {
			$scope.inputVO.yearMon = $scope.resultList.YEARMON;
			$scope.inputVO.userID = projInfoService.getUserID();
			$scope.sendRecv("PMS210", "queryBsGoal",
					"com.systex.jbranch.app.server.fps.pms210.PMS210InputVO",
					$scope.inputVO, function(tota, isError) {
						if (!isError) {
							$scope.bsGoalList = tota[0].body.bsGoalList;
							return;
						}
					});
		}
		$scope.select();
		
		//將表單數據插入或更新到數據庫
		$scope.save = function() {
			$scope.inputVO.bsGoalList = $scope.bsGoalList;
			for (var i = 0; i < $scope.inputVO.bsGoalList.length; i++) {
				if (null==String($scope.inputVO.bsGoalList[i].GOAL_DISC) || "" == String($scope.inputVO.bsGoalList[i].GOAL_DISC)) {
					$scope.showMsg("ehl_01_common_022");	//欄位檢核錯誤：必要輸入欄位
					return;
				}
			}
			if(isCheckError()){	//檢驗輸入，若輸入的天數有重複或不合法
				$scope.showMsg("欄位檢核錯誤：有重複或不合法");
				return;
			}	
			$scope.sendRecv("PMS210", "updateBsGoal",
					"com.systex.jbranch.app.server.fps.pms210.PMS210InputVO",
					$scope.inputVO, function(tota, isError) {
						if (!isError) {
							$scope.showMsg("ehl_01_common_002");
							return;
						}
					});
		}
		
		/*檢驗輸入，若輸入的天數有重複或不合法（小于0 或 大于31）*/
		function isCheckError(){
			for (var i = 0; i < $scope.inputVO.bsGoalList.length ; i++) {
				var startNum = Number($scope.inputVO.bsGoalList[i].GOAL_DATE_START);
				var endNum = Number($scope.inputVO.bsGoalList[i].GOAL_DATE_END);
				if(startNum > endNum) {
						return true;	//若GOAL_DATE_START > GOAL_DATE_END，不合法返回false
				}
				for(var j = i+1; j < $scope.inputVO.bsGoalList.length ; j++){
					var nextStartNum = Number($scope.inputVO.bsGoalList[j].GOAL_DATE_START);
					var nextEndNum = Number($scope.inputVO.bsGoalList[j].GOAL_DATE_END);
					if((startNum < nextStartNum) && (endNum >= nextStartNum)){
						return true;		//有重複交集，返回false
					}
					if((startNum > nextStartNum) && (startNum <= nextEndNum)){
						return true;		//有重複交集，返回false
					}
					if(startNum == nextStartNum){
						return true;		//有重複交集，返回false
					}
				}
			}
			return false;
		}
		
});