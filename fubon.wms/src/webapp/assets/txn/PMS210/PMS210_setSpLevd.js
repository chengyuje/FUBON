/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS210_setSpLevdController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS210_setSpLevdController";
	
		
	
		$scope.select = function() {
			$scope.inputVO.yearMon = $scope.resultList.YEARMON;
			$scope.inputVO.userID = projInfoService.getUserID();
			$scope.sendRecv("PMS210", "querySpLevd",
					"com.systex.jbranch.app.server.fps.pms210.PMS210InputVO",
					$scope.inputVO, function(tota, isError) {
						if (!isError) {
							$scope.spLevdList = tota[0].body.spLevdList;
							return;
						}
					});
		}
		$scope.select();
	
		//將表單數據插入或更新到數據庫
		$scope.save = function() {
			$scope.inputVO.spLevdList = $scope.spLevdList;
			for (var i = 0; i < $scope.inputVO.spLevdList.length; i++) {
				if ("" == String($scope.inputVO.spLevdList[i].START_DAY)) {
					$scope.showMsg("ehl_01_common_022");  //欄位檢核錯誤：必要輸入欄位
					return;
				} else if ("" == String($scope.inputVO.spLevdList[i].END_DAY)) {
					$scope.showMsg("ehl_01_common_022");  //欄位檢核錯誤：必要輸入欄位
					return;
				} else if ("" == String($scope.inputVO.spLevdList[i].TAR_DIS)) {
					$scope.showMsg("ehl_01_common_022");  //欄位檢核錯誤：必要輸入欄位
					return;
				}
			} 
			if(isCheckError()){	//檢驗輸入，若輸入的天數有重複或不合法（小于0 或 大于31）
				$scope.showMsg("欄位檢核錯誤：天數有重複或不合法");
				return;
			}
			$scope.sendRecv("PMS210", "updateSpLevd",
					"com.systex.jbranch.app.server.fps.pms210.PMS210InputVO",
					$scope.inputVO, function(tota, isError) {
						$scope.showMsg("ehl_01_common_002");  //修改成功
						$scope.select();
						return;
					});
		}
		
		/*檢驗輸入，若輸入的天數有重複或不合法（小于0 或 大于31）*/
		function isCheckError(){
			for (var i = 0; i < $scope.inputVO.spLevdList.length ; i++) {
				var startNum = Number($scope.inputVO.spLevdList[i].START_DAY);
				var endNum = Number($scope.inputVO.spLevdList[i].END_DAY);
				if(startNum > endNum ||　endNum　> 31 || startNum < 0) {
						return true;	//若START_DAY > END_DAY ，或者END_DAY > 31，不合法返回false
				}
				for(var j = i+1; j < $scope.inputVO.spLevdList.length ; j++){
					var nextStartNum = Number($scope.inputVO.spLevdList[j].START_DAY);
					var nextEndNum = Number($scope.inputVO.spLevdList[j].END_DAY);
					if((startNum < nextStartNum) && (endNum >= nextStartNum)){
						return true;		//有重複天數，返回false
					}
					if((startNum > nextStartNum) && (startNum <= nextEndNum)){
						return true;		//有重複天數，返回false
					}
					if(startNum == nextStartNum){
						return true;		//有重複天數，返回false
					}
				}
			}
			return false;
		}
});