/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRMEANK_INSERTController',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter,sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRMEANK_INSERTController";
		
		$scope.init = function(){
			$scope.inputVO = {
					PARAM_CODE:'',
					PARAM_DESC:'',
					PARAM_NAME:''
			};
			
		};
		$scope.init();
		
		$scope.btnSubmit = function(){
			if($scope.parameterTypeEditForm.$invalid) {
		          $scope.showErrorMsgInDialog('欄位檢核錯誤:請輸入必要欄位');
		          return;
		    }
			$scope.sendRecv("CRMEBANK", "insert", "com.systex.jbranch.app.server.fps.crmebank.CRMEBANKInputVO", $scope.inputVO , function(tota, isError) {
				if (isError) {
            		$scope.inputVO.PARAM_CODE='';
            		return;
            	}
            	if (tota.length > 0) {
            		$scope.showSuccessMsg('ehl_01_common_001');
            		$scope.closeThisDialog('successful');
            	}
			});
		};
		
});
