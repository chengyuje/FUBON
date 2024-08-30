'use strict';
eSoafApp.controller('FPS910_UPLOADController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "FPS910_UPLOADController";
		
		$scope.inputVO = {
			setting_type: $scope.home_type
		};
		
		$scope.downloadSimple = function() {
        	$scope.sendRecv("FPS910", "downloadSimple", "com.systex.jbranch.app.server.fps.fps910.FPS910InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						
					}
			});
		};
		
		$scope.uploadFinshed = function(name, rname) {
        	$scope.inputVO.fileName = name;
        	$scope.inputVO.fileRealName = rname;
        };
		
        $scope.save = function() {
        	$scope.sendRecv("FPS910", "upload", "com.systex.jbranch.app.server.fps.fps910.FPS910InputVO", $scope.inputVO,
				function(tota, isError) {
	        		if (!isError) {
	            		$scope.showSuccessMsg('ehl_01_common_004');
	            		$scope.closeThisDialog('successful');
	            	};
			});
        };
		
		
});