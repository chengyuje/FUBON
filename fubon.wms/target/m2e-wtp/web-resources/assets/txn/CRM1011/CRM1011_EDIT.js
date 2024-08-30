/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM1011_EDITController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM1011_EDITController";
		
		// combobox
		$scope.mappingSet['msg'] = [];
		$scope.mappingSet['msg'].push({LABEL : '重要訊息',DATA : '1'},{LABEL : '一般訊息',DATA : '2'});
		// date picker
		// 起迄日
		$scope.sDateOptions = {};
		$scope.eDateOptions = {};
		// config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.sDateOptions.maxDate = $scope.inputVO.eDate || $scope.maxDate;
			$scope.eDateOptions.minDate = $scope.inputVO.sDate || $scope.minDate;
		};
		// date picker end
		
		$scope.init = function(){
        	if($scope.row){
        		$scope.isUpdate = true
        	}
            $scope.row = $scope.row || {};
            $scope.inputVO = {
            		id: $scope.row.SEQ,
            		title: $scope.row.TITLE,
            		creator: $scope.row.MODIFIER || projInfoService.getUserID(),
            		content: $scope.row.CONTENT,
            		msg_level: $scope.row.MSG_LEVEL,
            		display: $scope.row.DISPLAY || 'Y'
            };
            $scope.creatorName = $scope.row.EMP_NAME || projInfoService.getUserName();
            if($scope.row.START_DATE)
            	$scope.inputVO.sDate = $scope.toJsDate($scope.row.START_DATE);
            if($scope.row.END_DATE)
            	$scope.inputVO.eDate = $scope.toJsDate($scope.row.END_DATE);
            // date picker
            $scope.sDateOptions.maxDate = $scope.inputVO.eDate || $scope.maxDate;
			$scope.eDateOptions.minDate = $scope.inputVO.sDate || $scope.minDate;
        };
        $scope.init();
        
        $scope.save = function(){
        	if($scope.parameterTypeEditForm.$invalid){
        		$scope.showErrorMsgInDialog('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
        	if($scope.isUpdate) {
        		$scope.sendRecv("CRM1011", "clickModifyCRM1011List", "com.systex.jbranch.app.server.fps.crm1011.CRM1011InputVO", $scope.inputVO,
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
        	} else {
        		$scope.sendRecv("CRM1011", "clickInsertCRM1011List", "com.systex.jbranch.app.server.fps.crm1011.CRM1011InputVO", $scope.inputVO,
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
        };
		
});
