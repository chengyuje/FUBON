/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CAM160_DELController',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CAM160_DELController";
		
		$scope.init = function(){
			$scope.inputVO = {
					type: $scope.type,
					campID: $scope.campID,
					stepID: $scope.stepID,
					rvReason: $scope.reason
        	};
		};
        $scope.init();
		
        $scope.removeLead = function() {
        	if ("M1" == $scope.inputVO.type) {
				$confirm({text: '是否執行一般刪除!!'}, {size: 'sm'}).then(function() {
					$scope.sendRecv("CAM160", "removeLead", "com.systex.jbranch.app.server.fps.cam160.CAM160InputVO", $scope.inputVO,
	        				function(totas, isError) {
	                        	if (isError) {
	                        		$scope.showErrorMsg(totas[0].body.msgData);
	                        	}
	                        	if (totas.length > 0) {
	                        		$scope.showSuccessMsg("ehl_01_common_003");
	                        		$scope.closeThisDialog('successful');
	                        	};
	        				}
	        		);
				});
        	} else {
				$confirm({text: '是否執行強制移除!!'}, {size: 'sm'}).then(function() {
					$scope.sendRecv("CAM160", "removeLead", "com.systex.jbranch.app.server.fps.cam160.CAM160InputVO", $scope.inputVO,
	        				function(totas, isError) {
	                        	if (isError) {
	                        		$scope.showErrorMsg(totas[0].body.msgData);
	                        	}
	                        	if (totas.length > 0) {
	                        		$scope.showSuccessMsg("ehl_01_common_003");
	                        		$scope.closeThisDialog('successful');
	                        	};
	        				}
	        		);
				});
        	}
        }
});
