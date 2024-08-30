'use strict';
eSoafApp.controller('FPS920_UPLOADController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "FPS920_UPLOADController";
		
		$scope.inputVO = {};
		
		$scope.downloadSimple = function() {
        	$scope.sendRecv("FPS920", "downloadSimple", "com.systex.jbranch.app.server.fps.fps920.FPS920InputVO", {},
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
        	$scope.sendRecv("FPS920", "upload", "com.systex.jbranch.app.server.fps.fps920.FPS920InputVO", $scope.inputVO,
				function(tota, isError) {
	        		if (!isError) {
	            		if(tota[0].body.errorList && tota[0].body.errorList.length > 0)
	            			$scope.showErrorMsg('ehl_01_fps920_001',[tota[0].body.errorList.toString()]);
	            		if(tota[0].body.errorList2 && tota[0].body.errorList2.length > 0)
	            			$scope.showErrorMsg('ehl_01_fps920_002',[tota[0].body.errorList2.toString()]);
	            		$scope.showSuccessMsg('ehl_01_common_004');
	            		$scope.closeThisDialog('successful');
	            	};
			});
        };
		
		
});