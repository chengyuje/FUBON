/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SQM410ListController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $q ,sysInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SQM410ListController";
		
		$scope.init = function() {	
//			debugger;
			$scope.inputVO = $scope.connector("get", "SQM410_inputVO");
			$scope.connector("set", "SQM410_inputVO", null);
			// 測試用
//			$scope.inputVO = { 
//				loginEmpID 	: '631329',	//督導'671169'//總行'229881'//處長'298522''161799'//副處長'641588'
//				privilegeID	: '012'		//012: 營運督導, 013: 業務處處長, 064:總行
//			};
		};
		$scope.init();
		
		$scope.viewDetail = function() {
			$scope.sendRecv("SQM410", "inquire", "com.systex.jbranch.app.server.fps.sqm410.SQM410InputVO", $scope.inputVO,
				function(totas, isError) {
					if ((totas[0].body.resultList == null || totas[0].body.resultList.length == 0) && (totas[0].body.agentList == null || totas[0].body.agentList.length == 0)) {
						$scope.showMsg("ehl_01_common_009");
            			return;
            		}
					debugger;					
					$scope.resultList = totas[0].body.resultList;	
					$scope.inputVO.jobTitleName = totas[0].body.jobTitleName;
					$scope.QTN_LIST = totas[0].body.qtnTypeList;
					$scope.agentList = totas[0].body.agentList; //代理人的資料清單
			});
		};
		$scope.viewDetail();
		
		$scope.detail = function(row,type) {
//			alert(JSON.stringify(row));
			$scope.inputVO = {
				loginEmpID	: $scope.inputVO.loginEmpID, 
				privilegeID	: $scope.inputVO.privilegeID,
				jobTitleName: $scope.inputVO.jobTitleName,
				branchID  	: row.BRANCH_NBR, 
				branchName	: row.BRANCH_NAME,
				QTN_LIST    : $scope.QTN_LIST,
				agent_type   : type
			};
			$scope.connector("set", "SQM410_inputVO", $scope.inputVO);
			$scope.connector("set", "SQM410_routeURL", 'assets/txn/SQM410/SQM410_CASELIST.html');
		};
});