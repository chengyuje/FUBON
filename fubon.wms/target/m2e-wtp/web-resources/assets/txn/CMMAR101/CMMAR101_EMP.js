/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('CMMAR101_EmpController',
    function($rootScope, $scope, $controller, socketService, alerts, projInfoService, $q) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "CMMAR101_EmpController";
        
        $scope.sendRecv("CMMAR101", "getEmpData", "com.systex.jbranch.app.server.fps.cmmar101.CMMAR101EMPVO", {'info': $scope.row.INFO_NO},
				function(totas, isError) {
                	if (isError) {
                		$scope.showErrorMsgInDialog(totas[0].body.msgData);
                	}
                	if (totas.length > 0) {
                		$scope.adgINFOEmpData = totas[0].body.empLS;
                		$rootScope.escroll();
                	};
				}
		);
        
    }
);
