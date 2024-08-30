'use strict';
eSoafApp.controller('CUS121Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CUS121Controller";
		
		$scope.inputVO = {};
		
		//顯示新增訊息內容
		if ($scope.openView == 'content')
			$scope.content = true;
		else
			$scope.content = false;
		//顯示選擇上傳檔案
		if ($scope.openView == 'attachment')
			$scope.attachment = true;
		else
			$scope.attachment = false;
		
		$scope.upload = function(name,rname) {
			$scope.inputVO.dataName = name;
			$scope.inputVO.realDataName = rname;
		};
		
		$scope.btnContent = function(row) {
			if($scope.parameterTypeEditForm.$invalid){
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
			$scope.sendRecv("CUS120", "addMessageReview", "com.systex.jbranch.app.server.fps.cus120.CUS120ContentInputVO", $scope.inputVO,
					function(totas, isError) {
	                	if (isError) {
	                		$scope.showErrorMsg(totas[0].body.msgData);
	                	}
	                	if (totas.length > 0) {
	                		$scope.showSuccessMsg('ehl_01_common_004');
                    		$scope.closeThisDialog('successful');
	                	};
					}
			);
		}
		
		$scope.btnAttachment = function(row) {
			if($scope.parameterTypeEditForm.$invalid){
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
			$scope.sendRecv("CUS120", "addDataReview", "com.systex.jbranch.app.server.fps.cus120.CUS120AttachmentInputVO", $scope.inputVO,
					function(totas, isError) {
	                	if (isError) {
	                		$scope.showErrorMsg(totas[0].body.msgData);
	                	}
	                	if (totas.length > 0) {
	                		$scope.showSuccessMsg('ehl_01_common_004');
                    		$scope.closeThisDialog('successful');
	                	};
					}
			);
		}
		
		
});
