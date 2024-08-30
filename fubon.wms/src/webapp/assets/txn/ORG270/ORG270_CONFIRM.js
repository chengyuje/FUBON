/*
 * #0662: WMS-CR-20210624-01_配合DiamondTeam專案調整系統模組功能_組織+客管
 */
'use strict';
eSoafApp.controller('ORG270_CONFIRMController', function($scope, $controller, $confirm, $compile, socketService, ngDialog, projInfoService) {
	$controller('BaseController', {
		$scope : $scope
	});
	$scope.controllerName = "ORG270_CONFIRMController";
	
	$scope.add = function () {
		$scope.sendRecv("ORG270", "add", "com.systex.jbranch.app.server.fps.org270.ORG270InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(tota.body.msgData);
				return;
			}
			if (tota.length > 0) {
				$scope.showMsg("ehl_01_common_010");
				$scope.closeThisDialog("success");
				
				return;
			} else {
				$scope.closeThisDialog("cancel");
			}
		});
	};
	
	$scope.closeORG270CONFIRM = function () {
		$scope.closeThisDialog("cancel");
	};
});
