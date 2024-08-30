'use strict';
eSoafApp.controller('IOT111Controller', function($rootScope, $scope, $controller, getParameter, ngDialog) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "IOT111Controller";
  	
	//XML參數
	getParameter.XML(["IOT.TRADE_TYPE", "IOT.PREMATCH_STATUS", "CALLOUT_STATUS"],function(totas){
		if(totas){
			$scope.mappingSet['IOT.TRADE_TYPE'] = totas.data[totas.key.indexOf('IOT.TRADE_TYPE')];				// 交易類型
			$scope.mappingSet['IOT.PREMATCH_STATUS'] = totas.data[totas.key.indexOf('IOT.PREMATCH_STATUS')];	// 狀態
			$scope.mappingSet['CALLOUT_STATUS'] = totas.data[totas.key.indexOf('CALLOUT_STATUS')];				// 電訪狀態
		}
	});
    
	// 有效起始日期
	$scope.bgn_sDateOptions = {
		maxDate: $scope.maxDate,
		minDate: $scope.minDate
	};
	
	$scope.bgn_eDateOptions = {
		maxDate: $scope.maxDate,
		minDate: $scope.minDate
	};
	
	// config
	$scope.model = {};
	$scope.open = function($event, elementOpened) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.model[elementOpened] = !$scope.model[elementOpened];
	};
	
	$scope.limitDate = function() {
		$scope.bgn_sDateOptions.maxDate = $scope.inputVO.APPLY_DATE_E || $scope.maxDate;
		$scope.bgn_eDateOptions.minDate = $scope.inputVO.APPLY_DATE_F || $scope.minDate;
	};
	
	$scope.inquireInit = function() {
		$scope.data = [];
		$scope.prematchList = [];
		$scope.outputVO = [];
		$scope.showExport = false;
	};
	$scope.inquireInit();	
	
	$scope.query = function() {
		$scope.prematchList = [];
		$scope.outputVO = [];
		$scope.showExport = false;
		
		if(($scope.inputVO.REG_TYPE == null || $scope.inputVO.REG_TYPE == undefined || $scope.inputVO.REG_TYPE == "") && 
				($scope.inputVO.CUST_ID == null || $scope.inputVO.CUST_ID == undefined || $scope.inputVO.CUST_ID == "") && 
				($scope.inputVO.STATUS == null || $scope.inputVO.STATUS == undefined || $scope.inputVO.STATUS == "") && 
				($scope.inputVO.PREMATCH_SEQ == null || $scope.inputVO.PREMATCH_SEQ == undefined || $scope.inputVO.PREMATCH_SEQ == "")) {
			$scope.showMsg("請至少輸入一項必要查詢條件");
			return;
		} else {
			$scope.connector('set','IOT111_querytemp',$scope.inputVO);
			$scope.sendRecv("IOT111", "queryData", "com.systex.jbranch.app.server.fps.iot111.IOT111InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.prematchList.length == 0) {
							$scope.showMsg("ehl_01_common_009");
	            			return;
	            		}
						$scope.prematchList = tota[0].body.prematchList;
						$scope.outputVO = tota[0].body;
						if ($scope.inputVO.REG_TYPE == '1') {
							$scope.showExport = true;
						}
						return;
					}
				});
		}
	}
	
	$scope.export = function() {
		$scope.sendRecv("IOT111", "export", "com.systex.jbranch.app.server.fps.iot111.IOT111InputVO", {'prematchList': $scope.prematchList},
		function(tota, isError) {
			
		});
	}
	
	$scope.upload = function() {
		var dialog = ngDialog.open({
			template: 'assets/txn/IOT111/IOT111_UPLOAD.html',
			className: 'IOT111',
			showClose: false,
			 controller: ['$scope', function($scope) {
//				 $scope.inputVO = inputVO;
//				 $scope.resultList = resultList;
             }]
		});
//		dialog.closePromise.then(function (data) {
//			if(data.value === 'successful' || data.value === 'cancel'){
//				
//			}
//		});
	}
		
	$scope.goIOT110 = function(row) {
		$scope.connector('set',"IOT_PREMATCH_SEQ", row.PREMATCH_SEQ);
		$rootScope.menuItemInfo.url = "assets/txn/IOT110/IOT110.html";
	}
	
	$scope.printReport = function(row) {
		$scope.sendRecv("IOT110", "printCheckList", "com.systex.jbranch.app.server.fps.iot110.IOT110InputVO", {PREMATCH_SEQ: row.PREMATCH_SEQ},
				function(tota, isError) {
			return;
		});
	}
	
	Date.prototype.addDays = function(days) {
		  this.setDate(this.getDate() + days);
		  return this;
		}
	
	var today = new Date();
	$scope.init = function(){
    	$scope.update_return = $scope.connector('get','IOT111_updateSubmit');
    	$scope.showExport = false;
		$scope.inputVO = {
			REG_TYPE: '',
			CUST_ID: '',
			STATUS: '',
			PREMATCH_SEQ: '',
			RECRUIT_ID: '',
			APPLY_DATE_F: today.addDays(-1),
			APPLY_DATE_E: new Date()
    	};	
		
		if($scope.update_return){
        	$scope.connector('set','IOT111_updateSubmit','');
        	$scope.inputVO = $scope.connector('get','IOT111_querytemp');
        	if($scope.inputVO){
        		$scope.connector('set','IOT111_querytemp','');
        	}
        	$scope.query();
    	}
	};
	
	/**
	 * IOT400 電訪預約/取消申請作業
	 * 點選時檢核「電訪作業檔」，若有相同案件編號已存在：
	 * 符合電訪狀態=2.電訪預約中、3.電訪處理中、5.電訪未成功、6.電訪疑義、8.退件處理-契撤時，
	 * 產生小視窗提醒：「相同案件編號案件已有電訪記錄，請確認!」，無法執行電訪預約/取消作業
	 * 若案件電訪狀態=1.未申請、7.取消電訪，可執行電訪預約/取消作業。
	 * **/
	$scope.openIOT400 = function(row) {
		if (row.CASE_ID != undefined) {
			$scope.sendRecv("IOT400","checkCaseID","com.systex.jbranch.app.server.fps.iot400.IOT400InputVO", 
			{'CASE_ID': row.CASE_ID, 'PREMATCH_SEQ': row.PREMATCH_SEQ},
			function(tota, isError) {
				if (!isError) {
					if (tota[0].body.resultList.length != 0) {
						$scope.showErrorMsg('相同案件編號案件已有電訪記錄，請確認!');
						return;
					} else {
						var inputVO = row;
						var dialog = ngDialog.open({
							template: 'assets/txn/IOT400/IOT400.html',
							className: 'IOT400',
							controller:['$scope',function($scope){
								$scope.inputVO = inputVO;
							}]
						});
						dialog.closePromise.then(function(data){
							if (data.value === 'successful') {
								$scope.query();
							}
						});
					}
				}
			});
		} else {
			var inputVO = row;
			var dialog = ngDialog.open({
				template: 'assets/txn/IOT400/IOT400.html',
				className: 'IOT400',
				controller:['$scope',function($scope){
					$scope.inputVO = inputVO;
				}]
			});
			dialog.closePromise.then(function(data){
				if (data.value === 'successful') {
					$scope.query();
				}
			});
		}
	}
	
	// IOT410 電訪狀態暨查詢
	$scope.openIOT410 = function(row) {
		var inputVO = row;
		
		var dialog = ngDialog.open({
			template: 'assets/txn/IOT410/IOT410.html',
			className: 'IOT410',
			controller:['$scope',function($scope){
				$scope.inputVO = inputVO;
				$scope.fromIOT111 = true;
			}]
		});
		dialog.closePromise.then(function(data){
			
		});
	}
	
	// 可查詢所有同一保險文件編號之電訪記錄，若無電訪記錄，則頁面顯示：「無電訪記錄」
//	$scope.getCallOut = function(row) {
//		if (row.INS_ID != undefined) {
//			$scope.sendRecv("IOT111", "getCallOut", "com.systex.jbranch.app.server.fps.iot111.IOT111InputVO", {'INS_ID': row.INS_ID},
//			function(tota, isError) {
//				if (tota[0].body.resultList.length == 0) {
//					$scope.showMsg("無電訪記錄");
//					return;
//  	            } else {
//  	            	var resultList = tota[0].body.resultList;
//  	            	var dialog = ngDialog.open({
//	  	  				template: 'assets/txn/IOT111/IOT111_CALLOUT.html',
//	  	  				className: 'IOT111',
//	  	  				controller:['$scope',function($scope){
//	  	  					$scope.resultList = resultList
//	  	  				}]
//	  	  			});
//	  	  			dialog.closePromise.then(function(data){
//	  	  				
//	  	  			});
//  	            }
//			});
//		} else {
//			$scope.showMsg("無保險文件編號");
//		}
//	}
	
	$scope.init();
		
});
