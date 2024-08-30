/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('ORG250_CONFIRMController', function($scope, $controller, $confirm, $compile, socketService, ngDialog, projInfoService) {
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "ORG250_CONFIRMController";
	
	$scope.confirm = function () {
		$scope.sendRecv("ORG250", "confirm", "com.systex.jbranch.app.server.fps.org250.ORG250InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(tota.body.msgData);
				return;
			}else{
				if (tota.length > 0) {
					$scope.showMsg("ehl_01_common_010");
					$scope.closeThisDialog('successful');
				}
			}
		});
	};
	
});
