'use strict';
eSoafApp.controller('CRM3102_SCSLSTController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM3102_SCSLSTController";
		
		$scope.inputVO = {};
		$scope.inputVO.PRJ_ID = $scope.row.PRJ_ID;
		if($scope.act!=null)
			$scope.inputVO.acttype = $scope.act;
		
		// old code
		$scope.scss = function() {
			$scope.sendRecv("CRM3102", "doFunction", "com.systex.jbranch.app.server.fps.crm3102.CRM3102InputVO",$scope.inputVO,
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
		
		//檢視理專客戶名單
		$scope.goEmpCust = function(row) {
			var dialog = ngDialog.open({
				template: 'assets/txn/CRM3102/CRM3102_EMPCUST.html',
				className: 'CRM3103_EMPCUST',
				showClose: false,
				controller: ['$scope', function($scope) {
					$scope.row = row;
				}]
			});
			dialog.closePromise.then(function(data) {
				
			});
		}
});