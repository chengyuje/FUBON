'use strict';
eSoafApp.controller('FPS910_DETAIL_2Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "FPS910_DETAIL_2Controller";
		
		// combobox
		getParameter.XML(["FPS.PORTFOLIO"], function(totas) {
			if (totas) {
				$scope.PORTFOLIO = totas.data[totas.key.indexOf('FPS.PORTFOLIO')];
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
				setting_type: $scope.Datarow.SETTING_TYPE,
				date: $scope.toJsDate($scope.Datarow.EFFECT_START_DATE),
				inv_amt_type: $scope.Datarow.INV_AMT_TYPE || '0',
				stockList: [],
				bondsList: []
			};
		};
		$scope.init();
		
		$scope.save = function () {
			$scope.$broadcast('getResultList');
			$scope.inputVO.stockList = $scope.connector('get', 'stockList');
			$scope.inputVO.bondsList = $scope.connector('get', 'bondsList');
//			alert(JSON.stringify($scope.inputVO.stockList));
//			alert(JSON.stringify($scope.inputVO.bondsList));
			$scope.connector('set', 'stockList', null);
			$scope.connector('set', 'bondsList', null);
			debugger;
			var check1 = false;
			angular.forEach($scope.inputVO.stockList, function(row) {
        		if(row.INV_PERCENT != 100)
        			check1 = true;
            });
			angular.forEach($scope.inputVO.bondsList, function(row) {
				if(row.INV_PERCENT != 100)
					check1 = true;
			});
        	if(check1) {
        		$scope.showErrorMsg('佔比必須為100%');
        		return;
        	}
			$scope.sendRecv("FPS910", "add", "com.systex.jbranch.app.server.fps.fps910.FPS910InputVO", $scope.inputVO,
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
			
			$scope.$broadcast('getResultList');
			$scope.inputVO.stockList = $scope.connector('get', 'stockList');
			$scope.inputVO.bondsList = $scope.connector('get', 'bondsList');
//			alert(JSON.stringify($scope.inputVO.stockList));
//			alert(JSON.stringify($scope.inputVO.bondsList));
			$scope.connector('set', 'stockList', null);
			$scope.connector('set', 'bondsList', null);
			
			var check1 = false;
			angular.forEach($scope.inputVO.stockList, function(row) {
        		if(row.INV_PERCENT != 100)
        			check1 = true;
            });
			angular.forEach($scope.inputVO.bondsList, function(row) {
				if(row.INV_PERCENT != 100)
					check1 = true;
			});
        	if(check1) {
        		$scope.showErrorMsg('佔比必須為100%');
        		return;
        	}
        	
			$confirm({text: '確定傳送主管覆核?'}, {size: 'sm'}).then(function() {
				$scope.sendRecv("FPS910", "goReview", "com.systex.jbranch.app.server.fps.fps910.FPS910InputVO", $scope.inputVO,
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
				$scope.sendRecv("FPS910", "review", "com.systex.jbranch.app.server.fps.fps910.FPS910InputVO", {'param_no': $scope.inputVO.param_no, 'status': status, 'setting_type': $scope.inputVO.setting_type, 'inv_amt_type': $scope.inputVO.inv_amt_type},
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