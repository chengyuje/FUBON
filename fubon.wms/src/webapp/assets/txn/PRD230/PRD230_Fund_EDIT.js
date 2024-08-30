/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD230_Fund_EDITController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD230_Fund_EDITController";
		
		// date picker
		// 衛星
		$scope.main_sDateOptions = {};
		$scope.main_eDateOptions = {};
		// 核心
		$scope.raise_sDateOptions = {};
		$scope.raise_eDateOptions = {};
		// IPO/專案
		$scope.ipo_sDateOptions = {};
		$scope.ipo_eDateOptions = {};
		// 資料起迄日
		$scope.sDateOptions = {};
		$scope.eDateOptions = {};
		// 核心區間
		$scope.multi_sDateOptions = {};
		$scope.multi_eDateOptions = {};
		// 基金管理費標的計績追溯
		$scope.cnrtar_sDateOptions = {};
		$scope.cnrtar_eDateOptions = {};
		// config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.main_sDateOptions.maxDate = $scope.inputVO.main_eDate;
			$scope.main_eDateOptions.minDate = $scope.inputVO.main_sDate;
		};
		$scope.limitDate2 = function() {
			$scope.raise_sDateOptions.maxDate = $scope.inputVO.raise_eDate;
			$scope.raise_eDateOptions.minDate = $scope.inputVO.raise_sDate;
		};
		$scope.limitDate3 = function() {
			$scope.ipo_sDateOptions.maxDate = $scope.inputVO.ipo_eDate;
			$scope.ipo_eDateOptions.minDate = $scope.inputVO.ipo_sDate;
		};
		$scope.limitDate4 = function() {
			$scope.sDateOptions.maxDate = $scope.inputVO.eDate;
			$scope.eDateOptions.minDate = $scope.inputVO.sDate;
		};
		$scope.limitDate5 = function() {
			$scope.multi_sDateOptions.maxDate = $scope.inputVO.multi_eDate;
			$scope.multi_eDateOptions.minDate = $scope.inputVO.multi_sDate;
		};
		$scope.limitDate6 = function() {
			$scope.cnrtar_sDateOptions.maxDate = $scope.inputVO.cnrtar_eDate;
			$scope.cnrtar_eDateOptions.minDate = $scope.inputVO.cnrtar_sDate;
		};
		// date picker end
		
		$scope.checkID = function() {
			var edit = $scope.isUpdate ? 'Y' : 'N';
			if($scope.inputVO.prd_id) {
				$scope.sendRecv("PRD230", "checkID", "com.systex.jbranch.app.server.fps.prd230.PRD230InputVO", {'prd_id':$scope.inputVO.prd_id,'status':edit},
						function(tota, isError) {
							if (!isError) {
								$scope.inputVO.cname = tota[0].body.cname;
								$scope.inputVO.lipper_id = tota[0].body.lipper;
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
					allot: $scope.row.ALLOTMENT_RATIO,
					ipo: $scope.row.IPO,
					yield: $scope.row.CNR_YIELD,
					plus: $scope.row.CNR_MULTIPLE,
					cnr_discount: $scope.row.CNR_DISCOUNT,
					rate_discount: $scope.row.RATE_DISCOUNT,
					cnr_target: $scope.row.IS_CNR_TARGET,
					fee: $scope.row.CNR_FEE,
					fus40: $scope.row.FUS40,
					purchase: $scope.row.NO_E_PURCHASE,
					out: $scope.row.NO_E_OUT,
					ein: $scope.row.NO_E_IN,
					buyback: $scope.row.NO_E_BUYBACK,
					control: $scope.row.QUOTA_CONTROL,
					yreturn: $scope.row.Y_RETURN,
					std: $scope.row.Y_STD,
					vigilant: $scope.row.VIGILANT,
					lipper_rank: $scope.row.LIPPER_RANK,
					lipper_ben_id: $scope.row.LIPPER_BENCHMARK_ID,
					stock_bond_type: $scope.row.STOCK_BOND_TYPE,
					ori_warning: $scope.row.WARNING,
					warning: $scope.row.WARNING,
					fund_subject1: $scope.row.SUBJECT1,
					fund_subject2: $scope.row.SUBJECT2,
					fund_subject3: $scope.row.SUBJECT3,
					fund_project1: $scope.row.PROJECT1,
					fund_project2: $scope.row.PROJECT2,
					customer_level: $scope.row.CUSTOMER_LEVEL
            };
			if($scope.row.MAIN_PRD_SDATE)
            	$scope.inputVO.main_sDate = $scope.toJsDate($scope.row.MAIN_PRD_SDATE);
            if($scope.row.MAIN_PRD_EDATE)
            	$scope.inputVO.main_eDate = $scope.toJsDate($scope.row.MAIN_PRD_EDATE);
            if($scope.row.RAISE_FUND_SDATE)
            	$scope.inputVO.raise_sDate = $scope.toJsDate($scope.row.RAISE_FUND_SDATE);
            if($scope.row.RAISE_FUND_EDATE)
            	$scope.inputVO.raise_eDate = $scope.toJsDate($scope.row.RAISE_FUND_EDATE);
            if($scope.row.IPO_SDATE)
            	$scope.inputVO.ipo_sDate = $scope.toJsDate($scope.row.IPO_SDATE);
            if($scope.row.IPO_EDATE)
            	$scope.inputVO.ipo_eDate = $scope.toJsDate($scope.row.IPO_EDATE);
            if($scope.row.SDATE)
            	$scope.inputVO.sDate = $scope.toJsDate($scope.row.SDATE);
            if($scope.row.EDATE)
            	$scope.inputVO.eDate = $scope.toJsDate($scope.row.EDATE);
            if($scope.row.MULTIPLE_SDATE)
            	$scope.inputVO.multi_sDate = $scope.toJsDate($scope.row.MULTIPLE_SDATE);
            if($scope.row.MULTIPLE_EDATE)
            	$scope.inputVO.multi_eDate = $scope.toJsDate($scope.row.MULTIPLE_EDATE);
            if($scope.row.CNR_TARGET_SDATE)
            	$scope.inputVO.cnrtar_sDate = $scope.toJsDate($scope.row.CNR_TARGET_SDATE);
            if($scope.row.CNR_TARGET_EDATE)
            	$scope.inputVO.cnrtar_eDate = $scope.toJsDate($scope.row.CNR_TARGET_EDATE);
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
				$scope.showErrorMsg($scope.errMsg);
        		return;
			}
			if($scope.isUpdate) {
				$scope.sendRecv("PRD230", "editData", "com.systex.jbranch.app.server.fps.prd230.PRD230InputVO", $scope.inputVO,
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
				$scope.sendRecv("PRD230", "addData", "com.systex.jbranch.app.server.fps.prd230.PRD230InputVO", $scope.inputVO,
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
		
		$scope.editTBPRD_FUND = function() {
			if(!$scope.inputVO.prd_id){
				$scope.showErrorMsg("尚未輸入基金代碼");
				return;
			}
			
			$confirm({
	            title:"提醒視窗",
	            text:"是否更新紅字標籤?",
	            ok: "確定更新",
	            cancel: "取消"
			})
			.then(function(){
				$scope.sendRecv("PRD230", "updateTBPRD_FUND", "com.systex.jbranch.app.server.fps.prd230.PRD230InputVO", $scope.inputVO,
				function(tota, isError) {
					if (isError) {
						$scope.showErrorMsg("更新警語&主題代碼失敗:");
                		$scope.showErrorMsg(tota[0].body.msgData);
                	} else {
                		$scope.showSuccessMsg("更新警語&主題代碼成功");
                		$scope.closeThisDialog('successful');
                	}
					
		});
			}); 
			
		};
		
		
		
});