/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM3B1_EDITController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM3B1_EDITController";
		
		$scope.inputVO = {};
        
        $scope.init = function() {
        	
        	$scope.inputVO = {
        			CUST_ID : '',
        			CUST_NAME : '',
        			showFileName : '',
        			fileName : ''
        	};
        }
        $scope.init();
        
        $scope.save = function() {
        	$scope.inputVO.SAVE_TYPE = 'ADD';
        	$scope.sendRecv("CRM3B1", "edit", "com.systex.jbranch.app.server.fps.crm3b1.CRM3B1InputVO", $scope.inputVO,
				function(tota, isError) {
		        	if (isError) {
		           		$scope.showErrorMsg(tota[0].body.msgData);
		           	}
		           	
		        	$scope.showSuccessMsg('ehl_01_common_004');
		           	$scope.closeThisDialog('cancel');
			});
        };
        
        $scope.validateCust = function() {
        	if($scope.inputVO.CUST_ID == "") return;
        	
        	$scope.sendRecv("CRM3B1", "validateCust", "com.systex.jbranch.app.server.fps.crm3b1.CRM3B1InputVO", $scope.inputVO,
				function(tota, isError) {
	        		if (isError) {
	            		$scope.showErrorMsg(tota[0].body.msgData);
	            	}
	            	if (tota[0].body.errMsg) {
	            		$scope.showErrorMsg(tota[0].body.errMsg);
	            		$scope.clear();
	            	} else {
	            		$scope.inputVO.CUST_NAME = tota[0].body.CUST_NAME;
	            	}
			});
        };
        
        $scope.clear = function() {
        	$scope.inputVO.CUST_ID = '';
        	$scope.inputVO.CUST_NAME = '';
        	$scope.inputVO.fileName = '';
        	$scope.inputVO.showFileName = '';
        }
		
     	//上傳附件
	    $scope.uploadFinshed = function(name) {
	    	$scope.inputVO.fileName = name;
        };
		
});