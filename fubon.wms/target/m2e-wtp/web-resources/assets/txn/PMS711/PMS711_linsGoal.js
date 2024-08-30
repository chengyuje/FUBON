/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS711_linsGoalController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS711_linsGoalController";
		$scope.select = function() {
			$scope.inputVO.date_year = $scope.date_year;
			$scope.inputVO.personType = $scope.personType;
			$scope.sendRecv("PMS711","queryLinsGoal","com.systex.jbranch.app.server.fps.pms711.PMS711InputVO",$scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.showList = tota[0].body.showList;
						return;
					}
				});
		}
		$scope.select();
		
		//將表單數據插入或更新到數據庫
		$scope.update = function() {
			$scope.inputVO.showList = $scope.showList;
			$scope.inputVO.date_year = $scope.date_year;
			$scope.inputVO.personType = $scope.personType;
			$scope.inputVO.subProjectSeqId = $scope.subId;
			$scope.inputVO.isSpecial = $scope.isSpecial;
			
			for(var i=0;i<$scope.inputVO.showList.length;i++){
				if(String($scope.inputVO.showList[i].D_GOAL)==''){
					$scope.showMsg("ehl_01_common_022");
					return;
				}
			}
			$scope.sendRecv("PMS711","saveLinsGoal","com.systex.jbranch.app.server.fps.pms711.PMS711InputVO",$scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.showMsg("ehl_01_common_002"); //更新成功
						$scope.select();
						return;
					}else{
						$scope.showErrorMsg("ehl_01_common_007");//更新失敗
					}
				});
		}
});