/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('ORG241_CONFIRMController', function($scope, $controller, $confirm, $compile, socketService, ngDialog, projInfoService) {
	$controller('BaseController', {
		$scope : $scope
	});
	$scope.controllerName = "ORG241_CONFIRMController";
	
	$scope.add = function () {
		$scope.sendRecv("ORG241", "add", "com.systex.jbranch.app.server.fps.org241.ORG241InputVO", $scope.inputVO, function(tota, isError) {
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
	
	$scope.closeORG241CONFIRM = function () {
		$scope.closeThisDialog("cancel");
	};
});
