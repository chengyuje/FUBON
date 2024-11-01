/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('ELN100_BBGController',
	function($rootScope, $scope, $controller) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "ELN100_BBGController";
		
		$scope.getBBG = function() {
			$scope.sendRecv("ELN100", "getBBG", "com.systex.jbranch.app.server.fps.eln100.ELN100InputVO", $scope.inputVO,
			function(tota, isError) {
				if (!isError) {
					$scope.resultList = [];
					$scope.outputVO = [];
					if (tota[0].body.resultList.length > 0) {
						$scope.resultList = tota[0].body.resultList;
						$scope.outputVO = tota[0].body;
					} else {
						$scope.showMsg("ehl_01_common_009");	//查無資料
						return;
					}
				}
			});
		}
		
		//初始化
		$scope.init = function(){
			$scope.inputVO = {};
			$scope.resultList = [];
			$scope.outputVO = {};
			$scope.getBBG();
		}
		$scope.init();
		
		$scope.get = function(row) {
			$scope.closeThisDialog(row);
		};
});