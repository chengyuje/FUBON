'use strict';
eSoafApp.controller('CRM3102_ADDEMPCUSTController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM3102_ADDEMPCUSTController";
		
		$scope.inputVO = {};
		$scope.inputVO.PRJ_ID = $scope.PRJ_ID;
		$scope.inputVO.BRANCH_NBR = $scope.BRANCH_NBR;
		$scope.inputVO.BRANCH_NAME = $scope.BRANCH_NAME;
		$scope.inputVO.EMP_ID = $scope.EMP_ID;
		$scope.inputVO.EMP_NAME = $scope.EMP_NAME;
		$scope.inputVO.CUST_ID = "";
		
		//檢核理專客戶名單
		$scope.chkEmpCust5YR = function() {
			$scope.inputVO.acttype = "7";			
			$scope.sendRecv("CRM3102", "doFunction", "com.systex.jbranch.app.server.fps.crm3102.CRM3102InputVO", $scope.inputVO,
				function(tota, isError) {
					if(!isError) {
						$scope.inputVO.CUST_NAME = tota[0].body.CUST_NAME;
					} else {
						$scope.inputVO.CUST_ID = "";
					}
			});		
		}
		
		//新增理專客戶名單
		$scope.addEmpCust5YR = function() {
			if($scope.inputVO.CUST_ID == "") return;
			
			$scope.inputVO.acttype = "8";			
			$scope.sendRecv("CRM3102", "doFunction", "com.systex.jbranch.app.server.fps.crm3102.CRM3102InputVO", $scope.inputVO,
				function(tota, isError) {
					if(!isError) {
						$scope.closeThisDialog('successful');
					}
			});		
		}
		
});