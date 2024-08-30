/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS711_met02Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS711_met02Controller";
		$scope.select = function() {
			$scope.inputVO.date_year = $scope.date_year;
			$scope.inputVO.personType = $scope.personType;
			$scope.inputVO.flag = $scope.flag;
			$scope.inputVO.EDate1 = $scope.endDate;
			$scope.showRowList = new Object();
			var i = 0;
			for(var key in $scope.showMonthList){
				i++;	
				$scope.showRowList[$scope.showMonthList[key]] = key; 
			}
			$scope.showLength = i;
			$scope.inputVO.subProjectSeqId = Number($scope.subId);
			$scope.sendRecv("PMS711","queryMet02","com.systex.jbranch.app.server.fps.pms711.PMS711InputVO",$scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.showList.length>0){
								$scope.showList = tota[0].body.showList;
								$scope.outputVO = tota[0].body;
								$scope.totalChange();
							}
						}else{
							$scope.showMsg("ehl_01_common_024");		//執行失敗
						}
					});
		}
		$scope.select();
		//計算統計值
		$scope.totalChange = function(){
			for(var i=0; i < $scope.showList.length; i++){
				var sum = 0;
				for(var key in $scope.showMonthList){
					sum += Number($scope.showList[i][key]);
				}
				$scope.showList[i].TOTAL = sum;
			}
		}
		//將表單數據插入或更新到數據庫
		/*$scope.update = function() {
			$scope.inputVO.showList = $scope.showList;
			$scope.inputVO.date_year = $scope.date_year;
			$scope.inputVO.personType = $scope.personType;
			$scope.inputVO.branchs = $scope.branchs;
			$scope.inputVO.subProjectSeqId = $scope.subId;
			for(var i=0;i<$scope.inputVO.showList.length;i++){
				if($scope.inputVO.showList[i].D_GOAL==''){
					$scope.showMsg("ehl_01_common_022");
					return;
				}
				if($scope.inputVO.showList[i].D_GOAL==0){
					$scope.showErrorMsg("欄位檢驗錯誤：輸入不能為0");
					return;
				}
			}
			$scope.sendRecv("PMS711","saveMet02","com.systex.jbranch.app.server.fps.pms711.PMS711InputVO",$scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.showMsg("ehl_01_common_002"); //更新成功
						$scope.select();
						return;
					}else{
						$scope.showErrorMsg("ehl_01_common_007");//更新失敗
					}
				});
		}*/
});