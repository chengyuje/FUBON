'use strict';
eSoafApp.controller('CRM3102_ERRLSTController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM3102_ERRLSTController";
		
		getParameter.XML(["CRM.TRS_PRJ_ROT_IMP_STATUS"], function(totas) {
			if (totas) {
				$scope.TRS_PRJ_ROT_IMP_STATUS = totas.data[totas.key.indexOf('CRM.TRS_PRJ_ROT_IMP_STATUS')];
			}
		});
		
		$scope.inputVO = {};
		$scope.inputVO.PRJ_ID = $scope.row.PRJ_ID;
		if($scope.act!=null)
			$scope.inputVO.acttype = $scope.act;
		$scope.FailList = [];
		$scope.outputVO = {};
		
		// old code
		$scope.fail = function() {
			$scope.sendRecv("CRM3102", "doFunction", "com.systex.jbranch.app.server.fps.crm3102.CRM3102InputVO",$scope.inputVO,
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
		};
		$scope.fail();
		
});