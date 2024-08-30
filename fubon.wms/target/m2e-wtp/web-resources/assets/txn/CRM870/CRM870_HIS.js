/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM870_HISController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM870_HISController";
		
		// date picker
		$scope.startDateOptions = {
			maxDate: $scope.inputVO.endDate || $scope.maxDate,
			minDate: $scope.minDate
		};
		
		$scope.endDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.inputVO.startDate || $scope.minDate
		};
		
		// config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		
		$scope.limitDate = function() {
			// 起日最小值(一年)
			var startDate_Min = new Date($scope.inputVO.endDate);
			startDate_Min.setMonth(startDate_Min.getMonth() - 12);
			startDate_Min.setHours(0, 0, 0, 0);
			
			$scope.startDateOptions.minDate = startDate_Min || $scope.minDate;	
			
			// 起日最大值 (不限)
			$scope.startDateOptions.maxDate = $scope.inputVO.endDate || $scope.maxDate;

			// 迄日最小值
			$scope.endDateOptions.minDate = $scope.inputVO.startDate || $scope.maxDate;
			
			// 迄日最大值(一年)
			var endDate_Max = new Date($scope.inputVO.startDate);
			endDate_Max.setMonth(endDate_Max.getMonth() + 12);
			endDate_Max.setHours(0, 0, 0, 0);
			
			$scope.endDateOptions.maxDate = endDate_Max || $scope.maxDate;
		};
		// date picker end
		
		$scope.getTXNList = function(tabSheet){
			$scope.tabSheet = tabSheet;
			
			$scope.txnData_all   = [];  //查詢用
			$scope.txnList_all   = [];  //查詢用
			
			$scope.txnData_stock = [];  //查詢用
			$scope.txnList_stock = [];  //查詢用
			
			$scope.txnData_bond  = [];  //查詢用
			$scope.txnList_bond  = [];  //查詢用
			
			$scope.txnData_sn    = [];  //查詢用
			$scope.txnList_sn    = [];  //查詢用
			
			$scope.txnData_dsn   = [];  //查詢用
			$scope.txnList_dsn   = [];  //查詢用
			
			$scope.txnData_dsn_e = [];  //查詢用
			$scope.txnList_dsn_e = [];  //查詢用
			
        	$scope.sendRecv("CRM870", "getTxnList", "com.systex.jbranch.app.server.fps.crm870.CRM870InputVO", {"custID"    : $scope.inputVO.custID, 
    																										   "tabSheet"  : $scope.tabSheet, 
    																										   "startDate" : $scope.inputVO.startDate, 
    																										   "endDate"   : $scope.inputVO.endDate, 
    																										   "prdID"     : $scope.inputVO.prdID, 
    																										   "rptType"   : $scope.inputVO.RPT_TYPE}, function(tota, isError) {
        		if (!isError) {
        			switch (tabSheet) {
	        			case 1:
	        				$scope.outputVO_t_all = tota[0].body.txnList;
	        				$scope.txnList_all = tota[0].body.txnList;
	        				break;
	        			case 2:
	        				$scope.outputVO_t_stock = tota[0].body.txnList;
	        				$scope.txnList_stock = tota[0].body.txnList;
	        				break;
	        			case 3:
	        				$scope.outputVO_t_bond = tota[0].body.txnList;
	        				$scope.txnList_bond = tota[0].body.txnList;
	        				break;
	        			case 4:
	        				$scope.outputVO_t_sn = tota[0].body.txnList;
	        				$scope.txnList_sn = tota[0].body.txnList;
	        				break;
	        			case 5:
	        				$scope.outputVO_t_dsn = tota[0].body.txnList;
	        				$scope.txnList_dsn = tota[0].body.txnList;
	        				
	        				$scope.outputVO_t_dsn_e = tota[0].body.maturityList;
	        				$scope.txnList_dsn_e = tota[0].body.maturityList;
	        				break;
        			}
        			
        			return;       			
        		}      		
        	});
		}
		
		$scope.getDIVList = function(tabSheet){
			$scope.tabSheet = tabSheet;

			$scope.divData_all   = [];  //查詢用
			$scope.divList_all   = [];  //查詢用
			
			$scope.divData_stock = [];  //查詢用
			$scope.divList_stock = [];  //查詢用
			
			$scope.divData_bond  = [];  //查詢用
			$scope.divList_bond  = [];  //查詢用
			
			$scope.divData_sn    = [];  //查詢用
			$scope.divList_sn    = [];  //查詢用
			
			$scope.divData_dsn   = [];  //查詢用
			$scope.divList_dsn   = [];  //查詢用
			
        	$scope.sendRecv("CRM870", "getDivList", "com.systex.jbranch.app.server.fps.crm870.CRM870InputVO", {"custID"    : $scope.inputVO.custID, 
    																										   "tabSheet"  : $scope.tabSheet, 
    																										   "startDate" : $scope.inputVO.startDate, 
    																										   "endDate"   : $scope.inputVO.endDate, 
    																										   "prdID"     : $scope.inputVO.prdID, 
    																										   "rptType"   : $scope.inputVO.RPT_TYPE}, function(tota, isError) {
        		if (!isError) {
        			switch (tabSheet) {
	        			case 1:
	        				$scope.outputVO_d_all = tota[0].body;
	        				$scope.divList_all = tota[0].body.divList;
	        				break;
	        			case 2:
	        				$scope.outputVO_d_stock = tota[0].body;
	        				$scope.divList_stock = tota[0].body.divList;
	        				break;
	        			case 3:
	        				$scope.outputVO_d_bond = tota[0].body;
	        				$scope.divList_bond = tota[0].body.divList;
	        				break;
	        			case 4:
	        				$scope.outputVO_d_sn = tota[0].body;
	        				$scope.divList_sn = tota[0].body.divList;
	        				break;
	        			case 5:
	        				$scope.outputVO_d_dsn = tota[0].body;
	        				$scope.divList_dsn = tota[0].body.divList;
	        				break;
        			}
        			
        			return;       			
        		}      		
        	});
		}
		
		$scope.init = function() {
			$scope.inputVO = {};

			$scope.inputVO.custID = $scope.custVO.CUST_ID;
			$scope.inputVO.RPT_TYPE = 'DT_SEARCH';
			
			// TXN
			$scope.txnData_all   = [];  //查詢用
			$scope.txnList_all   = [];  //查詢用
			
			$scope.txnData_stock = [];  //查詢用
			$scope.txnList_stock = [];  //查詢用
			
			$scope.txnData_bond  = [];  //查詢用
			$scope.txnList_bond  = [];  //查詢用
			
			$scope.txnData_sn    = [];  //查詢用
			$scope.txnList_sn    = [];  //查詢用
			
			$scope.txnData_dsn   = [];  //查詢用
			$scope.txnList_dsn   = [];  //查詢用
			
			$scope.txnData_dsn_e = [];  //查詢用
			$scope.txnList_dsn_e = [];  //查詢用
			
			// DIV
			$scope.divData_all   = [];  //查詢用
			$scope.divList_all   = [];  //查詢用
			
			$scope.divData_stock = [];  //查詢用
			$scope.divList_stock = [];  //查詢用
			
			$scope.divData_bond  = [];  //查詢用
			$scope.divList_bond  = [];  //查詢用
			
			$scope.divData_sn    = [];  //查詢用
			$scope.divList_sn    = [];  //查詢用
			
			$scope.divData_dsn   = [];  //查詢用
			$scope.divList_dsn   = [];  //查詢用
			
			// DATE
			var startDate = new Date();
			startDate.setMonth(startDate.getMonth() - 1);
			startDate.setHours(0, 0, 0, 0);
			$scope.inputVO.startDate = startDate;
			
			var endDate_Max = new Date();
			endDate_Max.setDate(endDate_Max.getDate() - 1);
			endDate_Max.setHours(0, 0, 0, 0);
			$scope.inputVO.endDate = endDate_Max;
			
			$scope.limitDate();
			
			$scope.getTXNList($scope.tabSheet);
			$scope.getDIVList($scope.tabSheet);
		};
		
		$scope.init();
		
		$scope.detail = function(tabSheet, row) {
			switch (tabSheet) {
				case 2: // 海外股票
				case 3:	// 海外債券
					break;
				case 4: // 境外結構型商品
					var dialog = ngDialog.open({
						template: 'assets/txn/PRD312/PRD312_DETAIL.html',
						className: 'PRD312_DETAIL',
						showClose: false,
		                controller: ['$scope', function($scope) {
		                	$scope.row = row;
		                }]
					});
					break;
				case 5: // 境內結構型商品
					var dialog = ngDialog.open({
						template: 'assets/txn/PRD313/PRD313_DETAIL.html',
						className: 'PRD313_DETAIL',
						showClose: false,
		                controller: ['$scope', function($scope) {
		                	$scope.row = row;
		                }]
					});
					break;
			}
		};
});