'use strict';
eSoafApp.controller('PRD270_SN_UPLOAD_TEMPController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD270_SN_UPLOAD_TEMPController";
		
		$scope.inputVO = {};
		
		$scope.downloadSimple = function() {
        	$scope.sendRecv("PRD270", "downloadSimpleTemp", "com.systex.jbranch.app.server.fps.prd270.PRD270InputVO", {},
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
        	$scope.sendRecv("PRD270", "uploadTemp", "com.systex.jbranch.app.server.fps.prd270.PRD270InputVO", $scope.inputVO,
					function(tota, isError) {
		        		if (isError) {
		            		$scope.showErrorMsg(tota[0].body.msgData);
		            	}
		            	if (tota.length > 0) {
		            		if(tota[0].body.errorList3 && tota[0].body.errorList3.length > 0)
		            			$scope.showErrorMsg('ehl_01_prd210_004',[tota[0].body.errorList3.toString()]);
		            		if(tota[0].body.errorList4 && tota[0].body.errorList4.length > 0)
		            			$scope.showErrorMsg('ehl_01_prd210_005',[tota[0].body.errorList4.toString()]);
		            		if(tota[0].body.errorList5 && tota[0].body.errorList5.length > 0)
		            			$scope.showErrorMsg('ehl_01_prd210_007',[tota[0].body.errorList5.toString()]);
		            		$scope.showSuccessMsg('ehl_01_common_004');
		            		$scope.closeThisDialog('successful');
		            	};
			});
        };
		
		
});