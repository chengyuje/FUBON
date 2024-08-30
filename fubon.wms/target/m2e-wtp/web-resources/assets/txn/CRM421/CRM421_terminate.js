/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM421_terminateController',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM421_terminateController";
		
		$scope.init = function(){
			$scope.inputVO = {
					seq: $scope.row.APPLY_SEQ,
					cust_id: $scope.row.CUST_ID, 
					apply_cat: $scope.row.APPLY_CAT, 
					terminateReason: ''
        	};
		};
        $scope.init();
		
        $scope.terminate = function() {
	    	$confirm({text: '是否終止此筆資料!!'}, {size: 'sm'}).then(function() {
		    	$scope.sendRecv("CRM421", "terminate", "com.systex.jbranch.app.server.fps.crm421.CRM421InputVO", $scope.inputVO,
	    				function(totas, isError) {
	                    	if (isError) {
//	                    		$scope.showErrorMsg(totas[0].body.msgData);
	            				return;
	                    	}
	                    	if (totas.length > 0) {
	                    		$scope.showSuccessMsg("ehl_01_CRM421_007");
	                    		$scope.closeThisDialog('successful');
	                    	};
	    				}
	    		);
	    	});
        }
});
