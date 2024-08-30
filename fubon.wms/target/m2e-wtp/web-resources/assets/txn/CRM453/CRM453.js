/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM453ListController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM453ListController";
		
		$scope.init = function() {
			$scope.inputVO = $scope.connector("get", "CRM453_inputVO");
			$scope.connector("set", "CRM453_inputVO", null);
			
			// 測試用
//			$scope.inputVO = { 
//				empID : '630730'
//			};
		}
		$scope.init();
		
		$scope.viewDetail = function() {
			$scope.sendRecv("CRM453", "inquire", "com.systex.jbranch.app.server.fps.crm453.CRM453InputVO", $scope.inputVO,
				function(totas, isError) {
					if (totas[0].body.resultList.length == 0) {
						$scope.showMsg("ehl_01_common_009");
            			return;
            		}
					if (totas[0].body.resultList.length > 0) {
						$scope.resultList = totas[0].body.resultList;
					}		
			});
		};
		$scope.viewDetail();
		
		$scope.detail = function(row) {
//			alert(JSON.stringify(row));
			$scope.empID = $scope.inputVO.empID;
			$scope.inputVO = {
				empID : $scope.empID, 
				type  : row.APPLY_TYPE == '1' ? 'period' : 'single', 
				custID: row.CUST_ID,
				seqNum: row.MPLUS_BATCH,
			};
			$scope.connector("set", "CRM453_inputVO", $scope.inputVO);
			$scope.connector("set", "CRM453_routeURL", 'assets/txn/CRM452/CRM452.html');
		}
});