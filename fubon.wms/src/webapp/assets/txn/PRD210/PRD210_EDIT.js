'use strict';
eSoafApp.controller('PRD210_EDITController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD210_EDITController";
		
		$scope.checkID = function() {
			var edit = $scope.isUpdate ? 'Y' : 'N';
			if($scope.inputVO.prd_id) {
				$scope.sendRecv("PRD210", "checkID", "com.systex.jbranch.app.server.fps.prd210.PRD210InputVO", {'prd_id':$scope.inputVO.prd_id,'status':edit},
					function(tota, isError) {
						if (!isError) {
							$scope.inputVO.ins_name = tota[0].body.ins_name;
							$scope.canEdit = tota[0].body.canEdit;
							$scope.errMsg = tota[0].body.errorMsg;
						}
				});
			}
		};
		
		$scope.init = function() {
			if($scope.row)
				$scope.isUpdate = true
			$scope.row = $scope.row || {};
			$scope.inputVO = {
				prd_id: $scope.row.PRD_ID,
				ins_type: $scope.row.INS_TYPE,
				retired: $scope.row.IS_RETIRED,
				education: $scope.row.IS_EDUCATION,
				purpose: $scope.row.IS_PURPOSE,
				life: $scope.row.IS_LIFE_INS,
				accident: $scope.row.IS_ACCIDENT,
				medical: $scope.row.IS_MEDICAL,
				diseases: $scope.row.IS_DISEASES,
				is_inv: $scope.row.IS_INV,
				base_amt_of_purchase: $scope.row.BASE_AMT_OF_PURCHASE
            };
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
				$scope.sendRecv("PRD210", "editData", "com.systex.jbranch.app.server.fps.prd210.PRD210InputVO", $scope.inputVO,
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
				$scope.sendRecv("PRD210", "addData", "com.systex.jbranch.app.server.fps.prd210.PRD210InputVO", $scope.inputVO,
					function(tota, isError) {
						if (isError) {
	                		$scope.showErrorMsg(tota[0].body.msgData);
	                	}
	                	if (tota.length > 0) {
	                		$scope.showSuccessMsg('ehl_01_common_004');
	                		$scope.closeThisDialog('successful');
	                	};
				});
			}
		};
		
		
		
});