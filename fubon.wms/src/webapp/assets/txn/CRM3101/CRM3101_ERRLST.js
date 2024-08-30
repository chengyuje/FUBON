'use strict';
eSoafApp.controller('CRM3101_ERRLSTController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM3101_ERRLSTController";
		
		getParameter.XML(["CRM.TRS_PRJ_DTL_IMP_STATUS"], function(totas) {
			if (totas) {
				$scope.TRS_PRJ_DTL_IMP_STATUS = totas.data[totas.key.indexOf('CRM.TRS_PRJ_DTL_IMP_STATUS')];
			}
		});
		
		$scope.inputVO = {};
		$scope.inputVO.PRJ_ID = $scope.row.PRJ_ID;
		$scope.inputVO.IMP_FILE_NAME = $scope.row.IMP_FILE_NAME;
		if($scope.act!=null)
			$scope.inputVO.acttype = $scope.act;
		$scope.FailList = [];
		$scope.outputVO = {};
		
		// old code
		$scope.fail = function() {
			if($scope.inputVO.acttype!='3') {
				$scope.sendRecv("CRM3101", "getFailCount", "com.systex.jbranch.app.server.fps.crm3101.CRM3101InputVO",$scope.inputVO,
					function(tota, isError) {
					   if(!isError) {
						   if(tota[0].body.FailList.length == 0) {
							   $scope.showMsg("ehl_01_common_009");
							   return;
						   }
						   $scope.FailList = tota[0].body.FailList;
						   $scope.outputVO = tota[0].body;
					   }
				});
			} else {
				$scope.sendRecv("CRM3101", "doFunction", "com.systex.jbranch.app.server.fps.crm3101.CRM3101InputVO",$scope.inputVO,
					function(tota, isError) {
					   if(!isError) {
						   if(tota[0].body.AOFailLst.length == 0) {
							   $scope.showMsg("ehl_01_common_009");
							   return;
						   }
						   $scope.FailList = tota[0].body.AOFailLst;
						   $scope.outputVO = tota[0].body;
					   }
				});
			}
		};
		$scope.fail();
		
		//匯出
		$scope.exportFailRPT = function() {
			$scope.sendRecv("CRM3101", "exportFail","com.systex.jbranch.app.server.fps.crm3101.CRM3101InputVO", 
					{'list':$scope.FailList || $scope.AOFailLst , 'acttype': $scope.inputVO.acttype},
						function(tota, isError) {
							if (!isError) {
						
							}
			});
		};
		
});