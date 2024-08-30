/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM3101_UPLOADController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM3101_UPLOADController";
		
		$scope.inputVO = {};
		$scope.uploadFinshed = function(name, rname) {
			$scope.inputVO.fileName = name;
        	$scope.inputVO.realfileName = rname;
		};
		$scope.uploadFinshed2 = function(name, rname) {
			$scope.inputVO.fileName2 = name;
        	$scope.inputVO.realfileName2 = rname;
		};
        
		$scope.save = function() {
			if($scope.parameterTypeEditForm.$invalid){
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
			if(!$scope.inputVO.realfileName || !$scope.inputVO.realfileName2) {
				$scope.showErrorMsg('欄位檢核錯誤:必要上傳欄位');
        		return;
			}
			$scope.sendRecv("CRM3101", "saveUpload", "com.systex.jbranch.app.server.fps.crm3101.CRM3101InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.showSuccessMsg('ehl_01_common_004');
                    		$scope.closeThisDialog('successful');
						}
			});
		};
		
		
		
});