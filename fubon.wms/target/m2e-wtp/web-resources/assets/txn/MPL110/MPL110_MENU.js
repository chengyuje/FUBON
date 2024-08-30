/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('MPL110_MENUController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "MPL110_MENUController";
		
		$scope.resultList = [];
		$scope.resultList.push(
			{LABEL: '客戶資產分佈', DATA: 'aum'},
			{LABEL: '客戶負債分佈', DATA: 'debt'},
			{LABEL: '客戶數分佈', DATA: 'count'}
		);
		
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
		
		$scope.detail = function(row) {
//			alert(JSON.stringify(row));
			$scope.inputVO.infoType = row.DATA;
			$scope.inputVO.infoName = row.LABEL;
			$scope.connector("set", "MPL110_inputVO", $scope.inputVO);

			switch (row.DATA) {
				case 'aum':
					$scope.connector("set", "MPL110_routeURL", 'assets/txn/MPL110/MPL110_AUM.html');
					break;
				case 'debt':
					$scope.connector("set", "MPL110_routeURL", 'assets/txn/MPL110/MPL110_DEBT.html');
					break;
				case 'count':
					$scope.connector("set", "MPL110_routeURL", 'assets/txn/MPL110/MPL110_COUNT.html');
					break;
				default:
					$scope.connector("set", "MPL110_routeURL", 'assets/txn/MPL110/MPL110_MENU.html');
					break;
			}
		}
});