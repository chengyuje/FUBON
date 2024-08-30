'use strict';
eSoafApp.controller('CRM3102_EMPCUSTController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM3102_EMPCUSTController";
		
		$scope.inputVO = {};
		$scope.inputVO = $scope.row;
		
		getParameter.XML(["COMMON.YES_NO"], function(totas) {
			if (totas) {
		    	//YES;NO
		        $scope.mappingSet['COMMON.YES_NO'] = totas.data[totas.key.indexOf('COMMON.YES_NO')];
			}
		});
		 
		//取得換手理專轄下經營滿5年客戶名單
		$scope.getEmpCust5YR = function() {
			$scope.inputVO.acttype = "5";
			$scope.sendRecv("CRM3102", "doFunction", "com.systex.jbranch.app.server.fps.crm3102.CRM3102InputVO",$scope.inputVO,
				function(tota, isError) {
					  if(!isError) {
						  if(tota[0].body.resultList.length == 0) {
							  $scope.showMsg("ehl_01_common_009");	 
							  return;
						  }
						  debugger
						  $scope.cust5YRList = tota[0].body.resultList;
						  $scope.outputVO = tota[0].body;
						  $scope.inputVO.EDIT_YN = tota[0].body.resultList[0].EDIT_YN; //若已匯入帳務報表則不可再新增/刪除
					   }
			});			
		};
		$scope.getEmpCust5YR();
		
		//新增理專客戶名單
		$scope.showEmpCust5YR = function() {
			debugger
			var prjId = $scope.inputVO.PRJ_ID;
			var branchNbr = $scope.inputVO.BRANCH_NBR;
			var branchName = $scope.inputVO.BRANCH_NAME;
			var empId = $scope.inputVO.EMP_ID;
			var empName = $scope.inputVO.EMP_NAME;
			
			var dialog = ngDialog.open({
				template: 'assets/txn/CRM3102/CRM3102_ADDEMPCUST.html',
				className: 'CRM3103_ADDEMPCUST',
				showClose: false,
				controller: ['$scope', function($scope) {
					$scope.PRJ_ID = prjId;
					$scope.BRANCH_NBR = branchNbr;
					$scope.BRANCH_NAME = branchName;
					$scope.EMP_ID = empId;
					$scope.EMP_NAME = empName;
				}]
			});
			dialog.closePromise.then(function(data) {
				$scope.getEmpCust5YR();
			});
		}
		
		//刪除理專客戶名單
		$scope.deleteCust = function(row) {
			$scope.inputVO.acttype = "6";
			$scope.inputVO.CUST_ID = row.CUST_ID;
			
			$scope.sendRecv("CRM3102", "doFunction", "com.systex.jbranch.app.server.fps.crm3102.CRM3102InputVO", $scope.inputVO,
				function(tota, isError) {
					$scope.getEmpCust5YR();
			});		
		}
		
});