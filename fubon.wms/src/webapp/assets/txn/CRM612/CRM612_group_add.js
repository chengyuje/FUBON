/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM612_group_addController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService, $filter, $timeout, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM612_group_addController";

		$scope.inputVO.ao_code = '';
		$scope.inputVO.ao_code = $scope.ao_code;
		$scope.group_addConfirm = function(){
		   	$scope.sendRecv("CRM612", "group_addConfirm", "com.systex.jbranch.app.server.fps.crm612.CRM612InputVO", $scope.inputVO,
    			function(totas, isError) {
        			if (isError) {
        				$scope.showErrorMsgInDialog(totas.body.msgData);
        				return;
	                }
	                if (totas.length > 0) {
	                	$scope.showMsg('ehl_01_common_001');
	                	$scope.closeThisDialog('successful');
	                };
	            }
        	);
        }
		
		
		
		
	}
);