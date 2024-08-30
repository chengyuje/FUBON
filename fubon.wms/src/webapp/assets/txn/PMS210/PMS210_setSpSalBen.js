/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS210_setSpSalBenController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS210_setSpSalBenController";
		
		//初始化頁面，先查詢
		$scope.select = function(){
        	$scope.inputVO.yearMon = $scope.resultList.YEARMON;
			$scope.sendRecv("PMS210", "querySpSalBen", "com.systex.jbranch.app.server.fps.pms210.PMS210InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.spSalBenList.length>0){
								$scope.spSalBenList = tota[0].body.spSalBenList;
								$scope.outputVO = tota[0].body;
							}
							
						}
			});
		}
		$scope.select();
		
		$scope.getData = function(flag){
			$scope.inputVO.flag = flag;
			$scope.sendRecv("PMS210", "getData", "com.systex.jbranch.app.server.fps.pms210.PMS210InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.select();
						}
			});
        };
		
		//导出数据(csv格式)
		$scope.exportData = function(){
        	$scope.inputVO.yearMon = $scope.resultList.YEARMON;
			$scope.sendRecv("PMS210", "exportSpSalBen", "com.systex.jbranch.app.server.fps.pms210.PMS210InputVO", $scope.inputVO,
			function(tota, isError) {
				if (!isError) {
					$scope.outputVO = tota[0].body;
					return;
				}
			});
		}
        
});