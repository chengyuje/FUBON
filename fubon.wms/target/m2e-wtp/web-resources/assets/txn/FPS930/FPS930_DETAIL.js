'use strict';
eSoafApp.controller('FPS930_DETAILController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "FPS930_DETAILController";
		
		// combobox
		getParameter.XML(["COMMON.YES_NO"], function(totas) {
			if (totas) {
				$scope.COM_YN = totas.data[totas.key.indexOf('COMMON.YES_NO')];
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
				alert_type: $scope.Datarow.ALERT_TYPE,
				date: $scope.toJsDate($scope.Datarow.EFFECT_START_DATE)
			};
		};
		$scope.init();
		$scope.inquireInit = function() {
			$scope.inputVO.totalList = [];
		}
		$scope.inquireInit();
		
		$scope.init_detail = function() {
			$scope.sendRecv("FPS930", "init_detail", "com.systex.jbranch.app.server.fps.fps930.FPS930InputVO", {'param_no': $scope.inputVO.param_no},
				function(tota, isError) {
					if (!isError) {
						// 查無資料
						if(tota[0].body.resultList.length == 0) {
							$scope.inputVO.totalList = [];
							$scope.inputVO.totalList.push(
							 {'CUST_RISK_ATR': 'C1', 'VOL_TYPE': '1', 'VOLATILITY': 0, 'REINV_STOCK_VOL': 0}
							,{'CUST_RISK_ATR': 'C2', 'VOL_TYPE': '1', 'VOLATILITY': 0, 'REINV_STOCK_VOL': 0}
							,{'CUST_RISK_ATR': 'C3', 'VOL_TYPE': '1', 'VOLATILITY': 0, 'REINV_STOCK_VOL': 0}
							,{'CUST_RISK_ATR': 'C4', 'VOL_TYPE': '1', 'VOLATILITY': 0, 'REINV_STOCK_VOL': 0});
	            			return;
	            		}
						$scope.inputVO.totalList = tota[0].body.resultList;
					}
			});
		};
		$scope.init_detail();
		
		$scope.save = function () {
//			var c1_num = 0;var c2_num = 0;
//			var c3_num = 0;var c4_num = 0;
//			angular.forEach($scope.inputVO.totalList, function(row) {
//				row.VOLATILITY = row.VOLATILITY || 0;
//				if("C1" == row.CUST_RISK_ATR) c1_num = parseFloat(row.VOLATILITY);
//				if("C2" == row.CUST_RISK_ATR) c2_num = parseFloat(row.VOLATILITY);
//				if("C3" == row.CUST_RISK_ATR) c3_num = parseFloat(row.VOLATILITY);
//				if("C4" == row.CUST_RISK_ATR) c4_num = parseFloat(row.VOLATILITY);
//			});
//			if((c1_num > c2_num || c1_num > c3_num || c1_num > c4_num)
//					|| (c2_num > c3_num || c2_num > c3_num) || (c3_num > c4_num)) {
//				$scope.showErrorMsg('風險等級低的上界值不得高於風險等級高的');
//    			return;
//			}
			$scope.sendRecv("FPS930", "add", "com.systex.jbranch.app.server.fps.fps930.FPS930InputVO", $scope.inputVO,
				function(totas, isError) {
                	if (!isError) {
                		$scope.showSuccessMsg('ehl_01_common_004');
                		$scope.Datarow.STATUS = 'S';
                	};
				}
    		);
		};
		
		$scope.goReview = function () {
			var init_date = new Date();
			init_date.setHours(0, 0, 0, 0);
			if($scope.inputVO.date <= init_date) {
				$scope.showErrorMsg('生效日不可包含當天及更早的日期');
    			return;
			}
			
//			var c1_num = 0;var c2_num = 0;
//			var c3_num = 0;var c4_num = 0;
//			angular.forEach($scope.inputVO.totalList, function(row) {
//				row.VOLATILITY = row.VOLATILITY || 0;
//				if("C1" == row.CUST_RISK_ATR) c1_num = parseFloat(row.VOLATILITY);
//				if("C2" == row.CUST_RISK_ATR) c2_num = parseFloat(row.VOLATILITY);
//				if("C3" == row.CUST_RISK_ATR) c3_num = parseFloat(row.VOLATILITY);
//				if("C4" == row.CUST_RISK_ATR) c4_num = parseFloat(row.VOLATILITY);
//			});
//			if((c1_num > c2_num || c1_num > c3_num || c1_num > c4_num)
//					|| (c2_num > c3_num || c2_num > c3_num) || (c3_num > c4_num)) {
//				$scope.showErrorMsg('風險等級低的上界值不得高於風險等級高的');
//    			return;
//			}
			$confirm({text: '確定傳送主管覆核?'}, {size: 'sm'}).then(function() {
				$scope.sendRecv("FPS930", "goReview", "com.systex.jbranch.app.server.fps.fps930.FPS930InputVO", $scope.inputVO,
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
				$scope.sendRecv("FPS930", "review", "com.systex.jbranch.app.server.fps.fps930.FPS930InputVO", {'param_no': $scope.inputVO.param_no, 'status': status},
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