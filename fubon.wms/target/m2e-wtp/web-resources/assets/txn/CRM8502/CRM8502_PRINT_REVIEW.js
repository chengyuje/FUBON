/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM8502_REVIEWController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM8502_REVIEWController";
				
		$scope.init = function() {
			    $scope.inputVO = {
			    	custID: $scope.custID,
        			seq: $scope.seq
        		};
        		
        		$scope.sendRecv("CRM8502", "printReview","com.systex.jbranch.app.server.fps.crm8502.CRM8502InputVO", $scope.inputVO,
    				function(tota, isError) {
    					if (isError) {
    						$scope.showErrorMsg(tota[0].body.msgData);
    						return;
    					};
    					debugger;
    					$scope.result = tota[0].body.result[0];
    			});
		};
		$scope.init();
});