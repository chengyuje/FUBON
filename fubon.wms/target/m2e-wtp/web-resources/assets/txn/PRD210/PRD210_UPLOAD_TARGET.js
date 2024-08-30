/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD210_UPLOAD_TARGETController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD210_UPLOAD_TARGETController";
		
		$scope.inputVO = {};
		
		$scope.downloadSimple = function() {
        	$scope.sendRecv("PRD210", "downloadTargetSimple", "com.systex.jbranch.app.server.fps.prd210.PRD210InputVO", {},
					function(tota, isError) {
						if (!isError) {
							return;
						}
			});
		};
		
		$scope.uploadFinshed = function(name, rname) {
        	$scope.inputVO.fileName = name;
        	$scope.inputVO.fileRealName = rname;
        };
		
        $scope.save = function() {
        	$scope.sendRecv("PRD210", "uploadTarget", "com.systex.jbranch.app.server.fps.prd210.PRD210InputVO", $scope.inputVO,
					function(tota, isError) {
		        		if (isError) {
		            		$scope.showErrorMsg(tota[0].body.msgData);
		            	}
		            	if (tota.length > 0) {
		            		if(tota[0].body.errorList && tota[0].body.errorList.length > 0)
		            			$scope.showErrorMsg('ehl_01_prd210_001',[tota[0].body.errorList.toString()]);
		            		if(tota[0].body.errorList2 && tota[0].body.errorList2.length > 0)
		            			$scope.showErrorMsg('ehl_01_prd210_003',[tota[0].body.errorList2.toString()]);
		            		if(tota[0].body.errorList3 && tota[0].body.errorList3.length > 0)
		            			$scope.showErrorMsg('ehl_01_prd210_004',[tota[0].body.errorList3.toString()]);
		            		if(tota[0].body.errorList4 && tota[0].body.errorList4.length > 0)
		            			$scope.showErrorMsg('ehl_01_prd210_005',[tota[0].body.errorList4.toString()]);
		            		$scope.showSuccessMsg('ehl_01_common_004');
		            		$scope.closeThisDialog('successful');
		            	};
			});
        };
		
		
});