/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS711_batchUpload3Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS711_batchUpload3Controller";
		$scope.select = function() {
			$scope.inputVO.date_year = $scope.date_year;
			$scope.inputVO.personType = $scope.personType;
			$scope.sendRecv("PMS711","queryBatchUpload3","com.systex.jbranch.app.server.fps.pms711.PMS711InputVO",$scope.inputVO,
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
			$scope.sendRecv("PMS711","saveBatchUpload3","com.systex.jbranch.app.server.fps.pms711.PMS711InputVO",$scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.showMsg("更新成功"); //更新成功
						$scope.select();
						return;
					}else{
						$scope.showErrorMsg("更新失敗");//更新失敗
					}
				});
		}
		
		$scope.changeCheck1 = function(index){
			if($scope.showList[index].SET_FC=='1'){
				$scope.showList[index].SET_FC='0'
			}
			else {
				$scope.showList[index].SET_FC='1'
			}
		}
		
		$scope.changeCheck2 = function(index){
			if($scope.showList[index].SET_FCH=='1'){
				$scope.showList[index].SET_FCH='0'
			}
			else {
				$scope.showList[index].SET_FCH='1'
			}
		}
		$scope.changeCheck3 = function(index){
			if($scope.showList[index].SET_PS=='1'){
				$scope.showList[index].SET_PS='0'
			}
			else {
				$scope.showList[index].SET_PS='1'
			}
		}
		$scope.changeCheck4 = function(index){
			if($scope.showList[index].SET_PRO=='1'){
				$scope.showList[index].SET_PRO='0'
			}
			else {
				$scope.showList[index].SET_PRO='1'
			}
		}
		$scope.changeCheck5 = function(index){
			if($scope.showList[index].SET_MET=='1'){
				$scope.showList[index].SET_MET='0'
			}
			else {
				$scope.showList[index].SET_MET='1'
			}
		}
		$scope.changeCheck6 = function(index){
			if($scope.showList[index].SET_SER=='1'){
				$scope.showList[index].SET_SER='0'
			}
			else {
				$scope.showList[index].SET_SER='1'
			}
		}
		$scope.changeCheck7 = function(index){
			if($scope.showList[index].SET_AREA=='1'){
				$scope.showList[index].SET_AREA='0'
			}
			else {
				$scope.showList[index].SET_AREA='1'
			}
		}
			
});