/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SQM410_CASELISTController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SQM410_CASELISTController";
		
//		getParameter.XML(["SQM.QTN_TYPE"], function(totas) {
//			if (totas) {
//				$scope.QTN_LIST = totas.data[totas.key.indexOf('SQM.QTN_TYPE')];
//			}
//		});
		
		$scope.init = function() {
			$scope.inputVO = $scope.connector("get", "SQM410_inputVO");
			$scope.connector("set", "SQM410_inputVO", null);
		};
		$scope.init();
		
		$scope.viewDetail = function() {
//			alert(JSON.stringify($scope.inputVO));
			$scope.sendRecv("SQM410", "getCaseList", "com.systex.jbranch.app.server.fps.sqm410.SQM410InputVO", $scope.inputVO,
				function(totas, isError) {
					if (totas[0].body.resultList == null || totas[0].body.resultList.length == 0) {
						$scope.showMsg("ehl_01_common_009");
            			return;
            		}
					
					$scope.resultList = totas[0].body.resultList;	
					$scope.QTN_LIST = $scope.inputVO.QTN_LIST;;
			});
		};
		$scope.viewDetail();
		
		$scope.detail = function(row) {
//			alert(JSON.stringify(row));
			//loginEmpID, branchID, branchName, jobTitleName由SQL410.js傳入
			$scope.inputVO.row = row;
			$scope.connector("set", "SQM410_inputVO", $scope.inputVO);
			$scope.connector("set", "SQM410_routeURL", 'assets/txn/SQM410/SQM410_DETAIL.html');
		};
		
		$scope.backMainList = function() {
			//loginEmpID, branchID, branchName, jobTitleName由SQL410.js傳入
			$scope.connector("set", "SQM410_inputVO", $scope.inputVO);
			$scope.connector("set", "SQM410_routeURL", 'assets/txn/SQM410/SQM410.html');
		}
});