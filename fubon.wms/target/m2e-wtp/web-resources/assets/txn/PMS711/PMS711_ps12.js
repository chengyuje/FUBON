/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS711_ps12Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS711_ps12Controller";
		
		$scope.mappingSet['myType'] = [];
		$scope.mappingSet['myType'].push(
				{LABEL: '中長期購屋', DATA: '0'},
				{LABEL: '非購屋', DATA: '1'}
				);
		$scope.inputVO.tsType = '0';
		$scope.select = function() {
			/*$scope.inputVO.date_year = $scope.date_year;
			$scope.inputVO.personType = $scope.personType;
			$scope.inputVO.tsType = $scope.inputVO.myType;
			$scope.sendRecv("PMS711","queryPs12","com.systex.jbranch.app.server.fps.pms711.PMS711InputVO",$scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.showList = tota[0].body.showList;
						return;
					}
				});*/
			$scope.inputVO.date_year = $scope.date_year;
			$scope.inputVO.personType = $scope.personType;
			$scope.showRowList = new Object();
			for(var key in $scope.showMonthList){
				$scope.showRowList[$scope.showMonthList[key]] = key; 
			}
			$scope.inputVO.subProjectSeqId = Number($scope.subId);
			$scope.sendRecv("PMS711","queryPs12","com.systex.jbranch.app.server.fps.pms711.PMS711InputVO",$scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.showList = tota[0].body.showList;
							return;
						}
					});
		}
		$scope.select();
		$scope.myTypeChange = function() {
			$scope.showList = [];
			$scope.select();
		}
		/*//將表單數據插入或更新到數據庫
		$scope.update = function() {
			$scope.inputVO.showList = $scope.showList;
			$scope.inputVO.tsType = $scope.inputVO.myType;
			$scope.inputVO.date_year = $scope.date_year;
			$scope.inputVO.personType = $scope.personType;
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
			$scope.sendRecv("PMS711","savePs12","com.systex.jbranch.app.server.fps.pms711.PMS711InputVO",$scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.showMsg("ehl_01_common_002"); //更新成功
						$scope.select();
						return;
					}else{
						$scope.showMsg("ehl_01_common_007");//更新失敗
					}
				});
		}*/
});