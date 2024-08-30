'use strict';
eSoafApp.controller('FPS940_DETAILController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "FPS940_DETAILController";
		
		// combobox
		getParameter.XML(["FPS.DEPOSIT_CUR"], function(totas) {
			if (totas) {
				$scope.DEPOSIT_CUR = totas.data[totas.key.indexOf('FPS.DEPOSIT_CUR')];
			}
		});
		// date picker
		$scope.dateOptions = {
			minDate: $scope.nowDate
		};
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		// date picker end
		
		// init
		$scope.currUser = projInfoService.getUserID();
		$scope.init = function() {
			$scope.inputVO = {
				param_no: $scope.Datarow.PARAM_NO,
				date: $scope.toJsDate($scope.Datarow.EFFECT_START_DATE),
				plan_amt_1: 0,
				plan_amt_2: 0,
				gen_lead_para1: 0,
				gen_lead_para2: 0,
				fail_status: 0,
				efficient_limit: 0,
				efficient_points: 0,
				fund_aum: 0,
				deposit_aum: 0,
				sisn_base_purchase: 0,
				bond_base_purchase: 0,
				exchange_rate: 0,
				spp_achive_rate_1: 0,
				spp_achive_rate_2: 0,
				rf_rate: 0,
				available_amt: 0,
				university_fee_1: 0,
				university_fee_2: 0,
				university_fee_3: 0,
				university_cost_1: 0,
				university_cost_2: 0,
				university_cost_3: 0,
				graduated_fee_1: 0,
				graduated_fee_2: 0,
				graduated_fee_3: 0,
				graduated_cost_1: 0,
				graduated_cost_2: 0,
				graduated_cost_3: 0,
				doctoral_fee_1: 0,
				doctoral_fee_2: 0,
				doctoral_fee_3: 0,
				doctoral_cost_1: 0,
				doctoral_cost_2: 0,
				doctoral_cost_3: 0,
				feature_desc: ""
			};
//			$scope.chkCode = [];
		};
		$scope.init();
		
		$scope.init_detail = function() {
			$scope.sendRecv("FPS940", "init_detail", "com.systex.jbranch.app.server.fps.fps940.FPS940InputVO", {'param_no': $scope.inputVO.param_no},
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.resultList.length == 0)
							return;
						if(tota[0].body.resultList2 && tota[0].body.resultList2.length > 0)
							$scope.confirm_check_obj = tota[0].body.resultList2[0];
						// TBFPS_OTHER_PARA
						var para_data = tota[0].body.resultList[0];
						$scope.inputVO.plan_amt_1 = para_data.PLAN_AMT_1;
						$scope.inputVO.plan_amt_2 = para_data.PLAN_AMT_2;
						$scope.inputVO.gen_lead_para1 = para_data.GEN_LEAD_PARA1;
						$scope.inputVO.gen_lead_para2 = para_data.GEN_LEAD_PARA2;
						$scope.inputVO.fail_status = para_data.FAIL_STATUS;
						$scope.inputVO.efficient_limit = para_data.EFFICIENT_LIMIT;
						$scope.inputVO.efficient_points = para_data.EFFICIENT_POINTS;
						$scope.inputVO.fund_aum = para_data.FUND_AUM;
						$scope.inputVO.deposit_aum = para_data.DEPOSIT_AUM;
						$scope.inputVO.sisn_base_purchase = para_data.SISN_BASE_PURCHASE;
						$scope.inputVO.bond_base_purchase = para_data.BOND_BASE_PURCHASE;
						$scope.inputVO.exchange_rate = para_data.EXCHANGE_RATE;
						$scope.inputVO.spp_achive_rate_1 = para_data.SPP_ACHIVE_RATE_1;
						$scope.inputVO.spp_achive_rate_2 = para_data.SPP_ACHIVE_RATE_2;
						$scope.inputVO.rf_rate = para_data.RF_RATE;
						$scope.inputVO.available_amt = para_data.AVAILABLE_AMT;
						$scope.inputVO.university_fee_1 = para_data.UNIVERSITY_FEE_1;
						$scope.inputVO.university_fee_2 = para_data.UNIVERSITY_FEE_2;
						$scope.inputVO.university_fee_3 = para_data.UNIVERSITY_FEE_3;
						$scope.inputVO.university_cost_1 = para_data.UNIVERSITY_COST_1;
						$scope.inputVO.university_cost_2 = para_data.UNIVERSITY_COST_2;
						$scope.inputVO.university_cost_3 = para_data.UNIVERSITY_COST_3;
						$scope.inputVO.graduated_fee_1 = para_data.GRADUATED_FEE_1;
						$scope.inputVO.graduated_fee_2 = para_data.GRADUATED_FEE_2;
						$scope.inputVO.graduated_fee_3 = para_data.GRADUATED_FEE_3;
						$scope.inputVO.graduated_cost_1 = para_data.GRADUATED_COST_1;
						$scope.inputVO.graduated_cost_2 = para_data.GRADUATED_COST_2;
						$scope.inputVO.graduated_cost_3 = para_data.GRADUATED_COST_3;
						$scope.inputVO.doctoral_fee_1 = para_data.DOCTORAL_FEE_1;
						$scope.inputVO.doctoral_fee_2 = para_data.DOCTORAL_FEE_2;
						$scope.inputVO.doctoral_fee_3 = para_data.DOCTORAL_FEE_3;
						$scope.inputVO.doctoral_cost_1 = para_data.DOCTORAL_COST_1;
						$scope.inputVO.doctoral_cost_2 = para_data.DOCTORAL_COST_2;
						$scope.inputVO.doctoral_cost_3 = para_data.DOCTORAL_COST_3;
						
						$scope.inputVO.cash_prepare_age_1 = para_data.CASH_PREPARE_AGE_1;
						$scope.inputVO.cash_prepare_age_2 = para_data.CASH_PREPARE_AGE_2;
						$scope.inputVO.cash_prepare_age_3 = para_data.CASH_PREPARE_AGE_3;
						$scope.inputVO.cash_prepare_1 = para_data.CASH_PREPARE_1;
						$scope.inputVO.cash_prepare_2 = para_data.CASH_PREPARE_2;
						$scope.inputVO.cash_prepare_3 = para_data.CASH_PREPARE_3;
						$scope.inputVO.cash_prepare_4 = para_data.CASH_PREPARE_4;
						
						$scope.inputVO.feature_desc = para_data.FEATURE_DESC;
//						$scope.chkCode = para_data.DEPOSIT_CURR.split(",");
//						$scope.same_chkCode = true;
//						if($scope.confirm_check_obj) {
//							var temp_chk = $scope.confirm_check_obj.DEPOSIT_CURR.split(",");
//							$scope.same_chkCode = _.isEqual($scope.chkCode.sort(), temp_chk.sort());
//						}
					}
			});
		};
		$scope.init_detail();
		
		$scope.checkrow = function() {
//			$scope.chkCode = [];
			if($scope.clickAll) {
				angular.forEach($scope.DEPOSIT_CUR, function(row) {
					$scope.toggleSelection(row.DATA);
    			});
			}
		};
		
//		$scope.toggleSelection = function toggleSelection(data) {
//        	var idx = $scope.chkCode.indexOf(data);
//        	if (idx > -1) {
//        		$scope.chkCode.splice(idx, 1);
//        	} else {
//        		$scope.chkCode.push(data);
//        	}
//        };
        
		$scope.save = function () {
			if($scope.parameterTypeEditForm.$invalid) {
				debugger;
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
//			if($scope.chkCode.length == 0) {
//				$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
//        		return;
//			}
			var age1 = parseInt($scope.inputVO.cash_prepare_age_1);
			var age2 = parseInt($scope.inputVO.cash_prepare_age_2);
			var age3 = parseInt($scope.inputVO.cash_prepare_age_3);
			var cash1 = parseInt($scope.inputVO.cash_prepare_1);
			var cash2 = parseInt($scope.inputVO.cash_prepare_2);
			var cash3 = parseInt($scope.inputVO.cash_prepare_3);
			var cash4 = parseInt($scope.inputVO.cash_prepare_4);  
			
			if(age1 > age2 || age1 == age2 || age2 > age3 || age2 == age3 || 
					cash1 > cash2 || cash2 > cash3 || cash3 > cash4){
				$scope.showErrorMsg('年齡/比例須由小至大');
				return;
			}
			
//			$scope.inputVO.deposit_curr = $scope.chkCode.toString();
			$scope.sendRecv("FPS940", "add", "com.systex.jbranch.app.server.fps.fps940.FPS940InputVO", $scope.inputVO,
				function(totas, isError) {
                	if (!isError) {
                		$scope.showSuccessMsg('ehl_01_common_004');
                		$scope.Datarow.STATUS = 'S';
                	};
				}
    		);
		};
		
		$scope.goReview = function () {
			if($scope.parameterTypeEditForm.$invalid) {
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
			//
			var init_date = new Date();
			init_date.setHours(0, 0, 0, 0);
			if($scope.inputVO.date <= init_date) {
				$scope.showErrorMsg('生效日不可包含當天及更早的日期');
    			return;
			}
			//
//			if($scope.chkCode.length == 0) {
//				$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
//        		return;
//			}
			//
			var age1 = parseInt($scope.inputVO.cash_prepare_age_1);
			var age2 = parseInt($scope.inputVO.cash_prepare_age_2);
			var age3 = parseInt($scope.inputVO.cash_prepare_age_3);
			var cash1 = parseInt($scope.inputVO.cash_prepare_1);
			var cash2 = parseInt($scope.inputVO.cash_prepare_2);
			var cash3 = parseInt($scope.inputVO.cash_prepare_3);
			var cash4 = parseInt($scope.inputVO.cash_prepare_4);  
			
			if(age1 > age2 || age1 == age2 || age2 > age3 || age2 == age3 || 
					cash1 > cash2 || cash2 > cash3 || cash3 > cash4){
				$scope.showErrorMsg('年齡/比例須由小至大');
				return;
			}
			
//			$scope.inputVO.deposit_curr = $scope.chkCode.toString();
			$confirm({text: '確定傳送主管覆核?'}, {size: 'sm'}).then(function() {
				$scope.sendRecv("FPS940", "goReview", "com.systex.jbranch.app.server.fps.fps940.FPS940InputVO", $scope.inputVO,
					function(totas, isError) {
	                	if (!isError) {
	                		$scope.showSuccessMsg('ehl_01_common_004');
	                		$scope.closeThisDialog('successful');
	                	};
					}
	    		);
			});
		};
		
		$scope.review = function (status) {
			if(status == 'W') {
				var init_date = new Date();
				init_date.setHours(0, 0, 0, 0);
				if($scope.toJsDate($scope.Datarow.EFFECT_START_DATE) < init_date) {
					$scope.showErrorMsg('主管覆核日不可晚於生效日');
        			return;
				}	
			}
			$confirm({text: '是否' + (status == 'R' ? '退回' : status == 'W' ? '核可' : '失效')}, {size: 'sm'}).then(function() {
				$scope.sendRecv("FPS940", "review", "com.systex.jbranch.app.server.fps.fps940.FPS940InputVO", {'param_no': $scope.inputVO.param_no, 'status': status},
					function(totas, isError) {
	                	if (!isError) {
	                		$scope.showSuccessMsg((status == 'R') ? "ehl_01_common_020" : "ehl_01_common_021");
	                		$scope.closeThisDialog('successful');
	                	};
					}
	    		);
			});
		};
});