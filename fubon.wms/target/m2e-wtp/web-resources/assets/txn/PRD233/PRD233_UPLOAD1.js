/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD233_UPLOAD1Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD233_UPLOAD1Controller";
		
		// date picker
		$scope.doc_sDateOptions = {};
		$scope.doc_eDateOptions = {};
		// config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.doc_sDateOptions.maxDate = $scope.inputVO.doc_eDate || $scope.maxDate;
			$scope.doc_eDateOptions.minDate = $scope.inputVO.doc_sDate || $scope.minDate;
		};
		// date picker end
		
		$scope.init = function() {
			$scope.inputVO = {
				idList: $scope.list
			};
		};
		$scope.init();
		
		$scope.uploadFinshed = function(name, rname) {
			$scope.inputVO.fileName = name;
			$scope.inputVO.realfileName = rname;
        };
        $scope.ClearUpload = function() {
        	if($scope.inputVO.type == 1)
        		$scope.inputVO.web = "";
        	else if($scope.inputVO.type == 2) {
        		$scope.inputVO.fileName = "";
    			$scope.inputVO.realfileName = "";
        	}
        };
		
        $scope.save = function() {
        	if($scope.parameterTypeEditForm.$invalid) {
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
			if(($scope.inputVO.type == 1 && !$scope.inputVO.realfileName) || ($scope.inputVO.type == 2 && !$scope.inputVO.web)) {
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
			$scope.sendRecv("PRD233", "upload", "com.systex.jbranch.app.server.fps.prd233.PRD233InputVO", $scope.inputVO,
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