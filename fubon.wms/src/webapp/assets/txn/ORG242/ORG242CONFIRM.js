/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('ORG242_CONFIRMController', function($scope, $controller, $confirm, $compile, socketService, ngDialog, projInfoService) {
	$controller('BaseController', {
		$scope : $scope
	});
	$scope.controllerName = "ORG242_CONFIRMController";
	
	$scope.init = function() {
		$scope.isDataEmpty = false;
	};
	$scope.init();
	
	$scope.add = function() {
		$scope.sendRecv("ORG242", "add", "com.systex.jbranch.app.server.fps.org242.ORG242InputVO", $scope.inputVO, function(tota, isError) {
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

	$scope.closeORG242CONFIRM = function() {
		$scope.closeThisDialog("cancel");
	};

	if ($scope.emptyColumnMessage) {
		$scope.isDataEmpty = true;
		$scope.showErrorMsg($scope.emptyColumnMessage);
	}
});
