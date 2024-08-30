/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS711_finGoalController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS711_finGoalController";
		$scope.select = function() {
			$scope.inputVO.date_year = $scope.date_year;
			$scope.inputVO.personType = $scope.personType;
			$scope.sendRecv("PMS711","queryFinGoal","com.systex.jbranch.app.server.fps.pms711.PMS711InputVO",$scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.showList = tota[0].body.showList;
						return;
					}
				});
		}
		$scope.select();
		
		 //將輸入添加到showList并顯示
		$scope.add = function(){
			var obj = new Object();
			obj.START_RATE = $scope.START_RATE;
			obj.END_RATE = $scope.END_RATE;
			obj.M_GOAL = $scope.M_GOAL;
			if (null == obj.START_RATE || "" == obj.START_RATE
				|| null == obj.END_RATE || "" == obj.END_RATE
				|| null == obj.M_GOAL || "" == obj.M_GOAL) {
				$scope.showMsg("ehl_01_common_022");	//欄位檢核錯誤：必要輸入欄位
				return;
			}
			
			//校驗撥款額度級距
			if(Number($scope.START_RATE) > Number($scope.END_RATE) ) {
				$scope.showMsg("基期滲透率(起) > 基期滲透率(迄)");
				return;	//基期滲透率(起) >= 基期滲透率(迄)
			}
			
			for(var j = 0; j < $scope.showList.length ; j++){
					var nextSTART_RATE = Number($scope.showList[j].START_RATE);
					var nextEND_RATE = Number($scope.showList[j].END_RATE);
					if(($scope.START_RATE < nextEND_RATE) && ($scope.END_RATE >= nextEND_RATE)){
						$scope.showMsg("基期滲透率區間重複");
						return;		//有重複數據
					}
					if(($scope.START_RATE > nextSTART_RATE) && ($scope.END_RATE <= nextEND_RATE)){
						$scope.showMsg("基期滲透率區間重複");
						return;		//有重複數據
					}
			}
			$scope.showList.push(obj);
			$scope.START_RATE = null;
			$scope.END_RATE = null;
			$scope.M_GOAL = null;
		}
		
		//將相應行的數據移出showList
		$scope.del = function(delIndex){
			$scope.showList.splice(delIndex,1);
		}
		
		//將表單數據插入或更新到數據庫
		$scope.update = function() {
			$scope.inputVO.showList = $scope.showList;
			$scope.inputVO.date_year = $scope.date_year;
			$scope.inputVO.personType = $scope.personType;
			$scope.inputVO.subProjectSeqId = $scope.subId;
			$scope.inputVO.isSpecial = $scope.isSpecial;
			
			for(var i=0;i<$scope.inputVO.showList.length;i++){
				if($scope.inputVO.showList[i].M_GOAL==''){
					$scope.showMsg("ehl_01_common_022");
					return;
				}
			}
			$scope.sendRecv("PMS711","saveFinGoal","com.systex.jbranch.app.server.fps.pms711.PMS711InputVO",$scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.showMsg("ehl_01_common_002"); //更新成功
						$scope.select();
						return;
					}else{
						$scope.showMsg("ehl_01_common_007");//更新失敗
					}
				});
		}
});