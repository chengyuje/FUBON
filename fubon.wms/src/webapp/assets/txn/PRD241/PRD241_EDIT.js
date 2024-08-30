/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD241_EDITController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD241_EDITController";
		
		// date picker
		$scope.multi_sDateOptions = {};
		$scope.multi_eDateOptions = {};
		// config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.multi_sDateOptions.maxDate = $scope.inputVO.multi_eDate;
			$scope.multi_eDateOptions.minDate = $scope.inputVO.multi_sDate;
		};
		// date picker end
		
		$scope.checkID = function() {
			var edit = $scope.isUpdate ? 'Y' : 'N';
			if($scope.inputVO.prd_id) {
				$scope.sendRecv("PRD241", "checkID", "com.systex.jbranch.app.server.fps.prd241.PRD241InputVO", {'prd_id':$scope.inputVO.prd_id,'status':edit},
						function(tota, isError) {
							if (!isError) {
								$scope.inputVO.stock_name = tota[0].body.stock_name;
								$scope.canEdit = tota[0].body.canEdit;
								$scope.errMsg = tota[0].body.errorMsg;
								return;
							}
				});
			}
		};
		
		$scope.init = function() {
			if($scope.row){
        		$scope.isUpdate = true
        	}
			$scope.row = $scope.row || {};
			$scope.inputVO = {
				prd_id: $scope.row.PRD_ID,
				stock_name: $scope.row.STOCK_CNAME,
				yield: $scope.row.CNR_YIELD,
				cnr_mult: $scope.row.CNR_MULTIPLE
            };
			if($scope.row.MULTIPLE_SDATE)
            	$scope.inputVO.multi_sDate = $scope.toJsDate($scope.row.MULTIPLE_SDATE);
            if($scope.row.MULTIPLE_EDATE)
            	$scope.inputVO.multi_eDate = $scope.toJsDate($scope.row.MULTIPLE_EDATE);
            if($scope.isUpdate)
            	$scope.checkID();
		};
		$scope.init();
		
		$scope.save = function() {
			if($scope.parameterTypeEditForm.$invalid) {
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
			if(!$scope.canEdit) {
				$scope.showErrorMsg($scope.errMsg, [' ']);
        		return;
			}
			if($scope.isUpdate) {
				$scope.sendRecv("PRD241", "editData", "com.systex.jbranch.app.server.fps.prd241.PRD241InputVO", $scope.inputVO,
						function(tota, isError) {
							if (isError) {
		                		$scope.showErrorMsg(tota[0].body.msgData);
		                	}
		                	if (tota.length > 0) {
		                		$scope.showSuccessMsg('ehl_01_common_004');
		                		$scope.closeThisDialog('successful');
		                	};
				});
			} else {
				
			}
		};
		
		
		
});