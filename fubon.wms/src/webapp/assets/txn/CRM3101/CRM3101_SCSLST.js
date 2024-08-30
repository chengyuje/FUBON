'use strict';
eSoafApp.controller('CRM3101_SCSLSTController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM3101_SCSLSTController";
		
		$scope.inputVO = {};
		$scope.inputVO.PRJ_ID = $scope.row.PRJ_ID;
		$scope.inputVO.IMP_FILE_NAME = $scope.row.IMP_FILE_NAME;
		if($scope.act!=null)
			$scope.inputVO.acttype = $scope.act;
		
		// old code
		$scope.scss = function() {
			$scope.sendRecv("CRM3101", "doFunction", "com.systex.jbranch.app.server.fps.crm3101.CRM3101InputVO",$scope.inputVO,
				function(tota, isError) {
					  if(!isError) {
						  if(tota[0].body.AOScssLst.length == 0) {
							  $scope.showMsg("ehl_01_common_009");	 
						  }
						  $scope.AOScssLst = tota[0].body.AOScssLst;
						  $scope.outputVO2 = tota[0].body;
					   }
				});
			
		};
		$scope.scss();
		
		//匯出
		$scope.exportScssRPT = function() {
			$scope.sendRecv("CRM3101", "exportScss","com.systex.jbranch.app.server.fps.crm3101.CRM3101InputVO", 
					{'list':$scope.AOScssLst , 'acttype': $scope.inputVO.acttype},
				function(tota, isError) {
					if (!isError) {
						
					}
			});
		};
});