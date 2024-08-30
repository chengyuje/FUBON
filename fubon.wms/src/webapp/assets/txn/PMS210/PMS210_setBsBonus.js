/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS210_setBsBonusController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS210_setBsBonusController";
		
		//初始化頁面，先查詢
		$scope.select = function(){
        	$scope.inputVO.yearMon = $scope.resultList.YEARMON;
			$scope.sendRecv("PMS210", "queryBsBonus", "com.systex.jbranch.app.server.fps.pms210.PMS210InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.bsBonusList = tota[0].body.bsBonusList;
						}
			});
		}
		$scope.select();
		
		//將輸入添加到bsBonusList并顯示
		$scope.add = function(){
			var obj = new Object();
			obj.N_MONTH = $scope.new_N_MONTH;
			obj.NEW_JURI = $scope.new_NEW_JURI;
			obj.NOW_JURI = $scope.new_NOW_JURI;
			if (null == obj.N_MONTH || "" == obj.N_MONTH
					|| null == obj.NEW_JURI || "" == obj.NEW_JURI
					|| null == obj.NOW_JURI || "" == obj.NOW_JURI) {
				$scope.showMsg("ehl_01_common_022");	//欄位檢核錯誤：必要輸入欄位
				return;
			}
			for(var i in $scope.bsBonusList){
				if($scope.new_N_MONTH == $scope.bsBonusList[i].N_MONTH){
					$scope.showMsg("ehl_01_common_016");	//資料已存在
					return;
				}
				
			}
			$scope.bsBonusList.push(obj);
			$scope.new_N_MONTH = null;
			$scope.new_NEW_JURI = null;
			$scope.new_NOW_JURI = null;
		}
		
		//將相應行的數據移出bsBonusList
		$scope.del = function(delIndex){
			$scope.bsBonusList.splice(delIndex,1);
		}
		
		
		//校驗輸入數據不能為空
	/*	$scope.checkNull = function(){
			for(var i in $scope.bsBonusList){
				for(var j in $scope.bsBonusList[i]){
					if($scope.bsBonusList[i][j] == undefined || $scope.bsBonusList[i][j] == ''){
						$scope.showMsg("所有輸入均不能為空！");
						return;
					}
				}
			}
			
			$scope.save();
		}
		*/
		//儲存數據
		$scope.save= function(){
			$scope.inputVO.bsBonusList = $scope.bsBonusList;
			$scope.inputVO.userID = sysInfoService.getUserID();
			$scope.inputVO.yearMon = $scope.resultList.YEARMON;
			$scope.sendRecv("PMS210", "saveBsBonus", "com.systex.jbranch.app.server.fps.pms210.PMS210InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.backResult > 0){
								$scope.showMsg("ehl_01_common_002");
							}else{
								$scope.showMsg("ehl_01_common_007");
							}
						}
			});
		}
		
});