/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CAM160_LEADSController',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CAM160_LEADSController";
		
		$scope.init = function(){
			$scope.inputVO = {
				campID: $scope.campID,
				stepID: $scope.stepID, 
				campName: $scope.campName
        	};
		};
        $scope.init();
        
        $scope.sendRecv("CAM160", "getLeadsList", "com.systex.jbranch.app.server.fps.cam160.CAM160InputVO", {his_stepID: $scope.stepID, his_campID: $scope.campID},
        		function(tota, isError) {
					if (!isError) {
						if(tota[0].body.leadsList.length == 0) {
							$scope.showMsg("ehl_01_common_009");
                			return;
                		}
						$scope.leadsList = tota[0].body.leadsList;
						$scope.outputVO = tota[0].body;

						return;
					}
		});
});
