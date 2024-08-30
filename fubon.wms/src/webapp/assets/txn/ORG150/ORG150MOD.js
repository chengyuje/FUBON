/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('ORG150_MODController', function($scope, $controller, socketService, ngDialog, projInfoService) {
	$controller('BaseController', {
		$scope : $scope
	});
	$scope.controllerName = "ORG150_MODController";
	
	$scope.modInputVO = {
		REGION_CENTER_NAME : '',
		BRANCH_AREA_NAME   : '',
		BRANCH_NAME        : '',
		EMP_ID             : '',
		EMP_NAME           : '',
		RESIGN_REASON      : '',
		RESIGN_DESTINATION : '',
		DESTINATION_BANK_ID: ''
	};
	
	$scope.init = function() {
		$scope.modInputVO.REGION_CENTER_NAME  = $scope.selectedRow.REGION_CENTER_NAME ;
		$scope.modInputVO.BRANCH_AREA_NAME    = $scope.selectedRow.BRANCH_AREA_NAME   ;
		$scope.modInputVO.BRANCH_NAME         = $scope.selectedRow.BRANCH_NAME        ;
		$scope.modInputVO.EMP_ID              = $scope.selectedRow.EMP_ID             ;
		$scope.modInputVO.EMP_NAME            = $scope.selectedRow.EMP_NAME           ;
		$scope.modInputVO.RESIGN_REASON       = $scope.selectedRow.RESIGN_REASON      ;
		$scope.modInputVO.RESIGN_DESTINATION  = $scope.selectedRow.RESIGN_DESTINATION ;
		$scope.modInputVO.DESTINATION_BANK_ID = $scope.selectedRow.DESTINATION_BANK_ID;
	};
	$scope.init();
	
	$scope.closeORG150MOD = function() {
		//$scope.org150mod_dialog.close();
		$scope.closeThisDialog('successful');
	};
	
	$scope.updateResignMemo = function() {
		$scope.sendRecv("ORG150", "updateResignMemo", "com.systex.jbranch.app.server.fps.org150.ORG150InputVO", $scope.modInputVO, function(totas, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(totas.body.msgData);
				return;
			}
			if (totas.length > 0) {
			}
			//$scope.org150mod_dialog.close();
			$scope.closeThisDialog('successful');
		});
	};
});
