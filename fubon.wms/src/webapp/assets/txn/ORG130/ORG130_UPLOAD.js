/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('ORG130_UPLOAD_Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "ORG130_UPLOAD_Controller";
		
		$scope.inputVO = {};
		$scope.inputVO.fileData = $scope.row.DATA;
				
		$scope.uploadFinshed = function(name, rname) {
        	$scope.inputVO.fileName = name;
        	$scope.inputVO.fileRealName = rname;
        };
		
        $scope.save = function() {
        	$scope.sendRecv("ORG130", "uploadSimple", "com.systex.jbranch.app.server.fps.org130.ORG130InputVO", $scope.inputVO,
					function(tota, isError) {
		        		if (isError) {
		            		$scope.showErrorMsg(tota[0].body.msgData);
		            	}
		            	if (tota.length > 0) {
		            		$scope.showSuccessMsg('ehl_01_common_004');
		            		$scope.closeThisDialog('successful');
		            	};
			});
        };
        
        
});