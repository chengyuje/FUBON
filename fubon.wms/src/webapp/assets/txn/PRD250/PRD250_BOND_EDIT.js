/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD250_BOND_EDITController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD250_BOND_EDITController";

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
				$scope.sendRecv("PRD250", "checkID", "com.systex.jbranch.app.server.fps.prd250.PRD250InputVO", {'prd_id':$scope.inputVO.prd_id,'status':edit},
						function(tota, isError) {
							if (!isError) {
								$scope.inputVO.bond_name = tota[0].body.bond_name;
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
					isin_code: $scope.row.ISIN_CODE,
					bond_pri: $scope.row.BOND_PRIORITY,
					rating_sp: $scope.row.CREDIT_RATING_SP,
					rating_moddy: $scope.row.CREDIT_RATING_MODDY,
					rating_fitch: $scope.row.CREDIT_RATING_FITCH,
					bond_rating_sp: $scope.row.BOND_CREDIT_RATING_SP,
					bond_rating_moddy: $scope.row.BOND_CREDIT_RATING_MODDY,
					bond_rating_fitch: $scope.row.BOND_CREDIT_RATING_FITCH,
					buyback: $scope.row.ISSUER_BUYBACK,
					checklist: $scope.row.RISK_CHECKLIST,
					stock_bond_type: $scope.row.STOCK_BOND_TYPE,
					bondProject: $scope.row.PROJECT,
					bondCustLevel: $scope.row.CUSTOMER_LEVEL
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
			if($scope.parameterTypeEditForm.$invalid ||
					$scope.inputVO.stock_bond_type=='' ||
					$scope.inputVO.stock_bond_type==undefined) {
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
			if(!$scope.canEdit) {
				$scope.showErrorMsg($scope.errMsg, [' ']);
        		return;
			}
			if($scope.isUpdate) {
				$scope.sendRecv("PRD250", "editData", "com.systex.jbranch.app.server.fps.prd250.PRD250InputVO", $scope.inputVO,
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
				$scope.sendRecv("PRD250", "addData", "com.systex.jbranch.app.server.fps.prd250.PRD250InputVO", $scope.inputVO,
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
