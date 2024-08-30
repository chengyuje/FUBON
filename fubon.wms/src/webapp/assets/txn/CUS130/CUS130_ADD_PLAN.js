/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CUS130_ADD_PLANController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CUS130_ADD_PLANController";
		
		$scope.init = function(){
			$scope.row = $scope.row || {};
			$scope.inputVO = {
					label: $scope.row.FIELD_LABEL,
					type: $scope.row.FIELD_TYPE,
					content: $scope.row.DROPDOWN_CONTENT
            };
		};
        $scope.init();
		
		$scope.save = function () {
			if($scope.parameterTypeEditForm.$invalid){
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
			if($scope.inputVO.type == '5' && !$scope.inputVO.content){
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
			$scope.connector('set','CUS130_PLAN', $scope.inputVO);
    		$scope.closeThisDialog('successful');
		};
		
});
