/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('MPL110_DEBTController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "MPL110_DEBTController";
		
		$scope.init = function() {
			$scope.inputVO = $scope.connector("get", "MPL110_inputVO");
			$scope.connector("set", "MPL110_inputVO", null);
			// 測試用
//			$scope.inputVO = {
//				empID 	  : '199850', 
//				branchID  : '330', 
//				branchName: '玉成分行'
//			};
		}
		$scope.init();
		
		$scope.viewDetail = function() {
//			alert(JSON.stringify($scope.inputVO));
			$scope.sendRecv("MPL110", "getDebt", "com.systex.jbranch.app.server.fps.mpl110.MPL110InputVO", $scope.inputVO,
				function(totas, isError) {
					if (totas[0].body.resultList.length == 0) {
						$scope.showMsg("ehl_01_common_009");
            			return;
            		}
					if (totas[0].body.resultList.length > 0) {
						angular.forEach(totas[0].body.resultList, function(row){
							row.AO_WO_Y = parseInt(row.AO_WO_Y);
							row.AO_WO_N = parseInt(row.AO_WO_N);
						});
						$scope.resultList = totas[0].body.resultList;
						$scope.dataDate = $scope.resultList[0].DATA_DATE;
					}		
			});
		};
		$scope.viewDetail();
		
		$scope.backMainList = function() {
			$scope.connector("set", "MPL110_inputVO", $scope.inputVO);
			$scope.connector("set", "MPL110_routeURL", 'assets/txn/MPL110/MPL110.html');
		}
		
		$scope.backMenuList = function() {
			$scope.connector("set", "MPL110_inputVO", $scope.inputVO);
			$scope.connector("set", "MPL110_routeURL", 'assets/txn/MPL110/MPL110_MENU.html');
		}
});