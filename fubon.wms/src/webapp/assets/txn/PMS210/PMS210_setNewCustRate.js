/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS210_setNewCustRateController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS210_setNewCustRateController";
	
		

		$scope.select = function() {
			$scope.inputVO.yearMon = $scope.resultList.YEARMON;
			$scope.inputVO.userID = projInfoService.getUserID();
			$scope.newCustRateList = [];
			$scope.sendRecv(
				"PMS210",
				"queryNewCustRate",
				"com.systex.jbranch.app.server.fps.pms210.PMS210InputVO",
				$scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.newCustRateList = tota[0].body.newCustRateList;
						return;
					}
				});
		}
		$scope.select();
		
		//將輸入框中的內容加入newCustRateList中
		$scope.add = function() {
			var AUM_INC_START = $scope.AUM_INC_START;
			var AUM_INC_END = $scope.AUM_INC_END;
			var BONUS = $scope.BONUS;
			if (null == AUM_INC_START || "" == AUM_INC_START
					|| null == AUM_INC_END || "" == AUM_INC_END
					|| null == BONUS || "" == BONUS) {
				$scope.showMsg("ehl_01_common_022");  //欄位檢核錯誤：必要輸入欄位
				return;
			}
			for (var i = 0; i < $scope.newCustRateList.length; i++) {
				if ($scope.newCustRateList[i].AUM_INC_START == AUM_INC_START) {
					$scope.showMsg("ehl_01_common_016");  //資料已存在
					return;
				}
			}
			var a = new Object();
			a.BONUS = BONUS;
			a.AUM_INC_START = AUM_INC_START;
			a.AUM_INC_END = AUM_INC_END;
			$scope.newCustRateList.push(a);
			$scope.AUM_INC_START = null;
			$scope.AUM_INC_END = null;
			$scope.BONUS = null;
		}
		//刪除newCustRateList中對應的項
		$scope.del = function(delIndex){
			$scope.newCustRateList.splice(delIndex,1);
		}
		//將表單數據插入或更新到數據庫
		$scope.save = function() {
			$scope.inputVO.newCustRateList = $scope.newCustRateList;
			if(isCheckError()){
				$scope.showMsg("欄位檢核錯誤：額度有重複");
				return;
			}
			$scope.sendRecv(
				"PMS210",
				"updateNewCustRate",
				"com.systex.jbranch.app.server.fps.pms210.PMS210InputVO",
				$scope.inputVO,
				function(tota, isError) {
					if(!isError){
						$scope.showMsg("ehl_01_common_002"); //修改成功
						$scope.select();
					}else{
						$scope.showMsg("ehl_01_common_024"); //執行失敗
					}
					return;
				});
		}
		
		/*檢驗輸入，若輸入的數據有重複*/
		function isCheckError(){
			for (var i = 0; i < $scope.inputVO.newCustRateList.length ; i++) {
				var startNum = Number($scope.inputVO.newCustRateList[i].AUM_INC_START);
				var endNum = Number($scope.inputVO.newCustRateList[i].AUM_INC_END);
				if(startNum > endNum ) {
						return true;	//若startNum > endNum 
				}
				for(var j = i+1; j < $scope.inputVO.newCustRateList.length ; j++){
					var nextStartNum = Number($scope.inputVO.newCustRateList[j].AUM_INC_START);
					var nextEndNum = Number($scope.inputVO.newCustRateList[j].AUM_INC_END);
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