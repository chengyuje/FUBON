'use strict';
eSoafApp.controller('CAM210_GETCNAMEController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CAM210_GETCNAMEController";
		
		$scope.inputVO = $scope.input;
		$scope.inputVO.tabSheet = $scope.tabSheet;
		
		$scope.getCampaignName = function() {
			$scope.sendRecv("CAM210", "getCampaignName", "com.systex.jbranch.app.server.fps.cam210.CAM210InputVO", $scope.inputVO,
    				function(totas, isError) {
						if (isError) {
							$scope.showErrorMsgInDialog(tota.body.msgData);
							return;
						}
						$scope.cnameList = totas[0].body.cnameList;
						$scope.outputVO = totas[0].body;
    				}
    		);
		};
		
		$scope.getCampaignName();
		
		$scope.returnName = function (row) {
			$scope.closeThisDialog(row.CAMPAIGN_NAME)
		};
		
		
});