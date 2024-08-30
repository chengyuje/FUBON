/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD270_SN_EDITController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD270_SN_EDITController";

		// date picker
		$scope.buy_DateOptions = {};
		// config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		// date picker end

		$scope.checkID = function() {
			var edit = $scope.isUpdate ? 'Y' : 'N';
			if($scope.inputVO.prd_id) {
				$scope.sendRecv("PRD270", "checkID", "com.systex.jbranch.app.server.fps.prd270.PRD270InputVO", {'prd_id':$scope.inputVO.prd_id,'status':edit},
						function(tota, isError) {
							if (!isError) {
								$scope.inputVO.cname = tota[0].body.cname;
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
					rating_sp: $scope.row.CREDIT_RATING_SP,
					rating_moddy: $scope.row.CREDIT_RATING_MODDY,
					rating_fitch: $scope.row.CREDIT_RATING_FITCH,
					sn_rating_sp: $scope.row.AVOUCH_CREDIT_RATING_SP,
					sn_rating_moddy: $scope.row.AVOUCH_CREDIT_RATING_MODDY,
					sn_rating_fitch: $scope.row.AVOUCH_CREDIT_RATING_FITCH,
					fix_Date: $scope.row.FIXED_RATE_DURATION,
					exchange: $scope.row.CURRENCY_EXCHANGE,
					dividend: $scope.row.FLOATING_DIVIDEND_RATE,
					target: $scope.row.INVESTMENT_TARGETS,
					cnr_yield: $scope.row.CNR_YIELD,
					rate_channel: $scope.row.RATE_OF_CHANNEL,
					performance_review: $scope.row.PERFORMANCE_REVIEW,
					stock_bond_type: $scope.row.STOCK_BOND_TYPE,
					snProject: $scope.row.PROJECT,
					snCustLevel: $scope.row.CUSTOMER_LEVEL,
					bond_value: $scope.row.BOND_VALUE
            };
			if($scope.row.START_DATE_OF_BUYBACK)
            	$scope.inputVO.buy_Date = $scope.toJsDate($scope.row.START_DATE_OF_BUYBACK);
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
			if($scope.inputVO.target) {
				$scope.inputVO.target = $scope.inputVO.target.replace(/(\r\n|\n|\r)/gm,"");
				var temp = $scope.inputVO.target.split(";");
				if(temp.length > 20) {
					$scope.showErrorMsg('欄位檢核錯誤:連結標的');
	        		return;
				}
			}
			if($scope.isUpdate) {
				$scope.sendRecv("PRD270", "editData", "com.systex.jbranch.app.server.fps.prd270.PRD270InputVO", $scope.inputVO,
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
				$scope.sendRecv("PRD270", "addData", "com.systex.jbranch.app.server.fps.prd270.PRD270InputVO", $scope.inputVO,
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
