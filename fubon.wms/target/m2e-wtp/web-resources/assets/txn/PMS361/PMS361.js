'use strict';
eSoafApp.controller('PMS361Controller', function($rootScope, $scope, $controller, sysInfoService, $confirm, socketService, ngDialog, projInfoService, getParameter) {
	$controller('BaseController', {$scope: $scope});
	$controller('RegionController', {$scope: $scope});
	$scope.controllerName = "PMS361Controller";
	
	// filter
	getParameter.XML(["PMS.CHECK_TYPE", "PMS.CUST_BASE"], function(totas) {
		if (totas) {
			$scope.mappingSet['PMS.CHECK_TYPE'] = totas.data[totas.key.indexOf('PMS.CHECK_TYPE')];
			$scope.mappingSet['PMS.CUST_BASE'] = totas.data[totas.key.indexOf('PMS.CUST_BASE')];
		}
	});
    //
	
	// date picker
	$scope.sDateOptions = {};
	$scope.eDateOptions = {};
	// config
	$scope.model = {};
	$scope.open = function($event, elementOpened) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.model[elementOpened] = !$scope.model[elementOpened];
	};
	$scope.limitDate = function() {
		$scope.sDateOptions.maxDate = $scope.inputVO.eDate;
		$scope.eDateOptions.minDate = $scope.inputVO.sDate;
	};
	// date picker end
	
	// init
	$scope.init = function() {
		$scope.inputVO = {
			memLoginFlag: String(sysInfoService.getMemLoginFlag())
		};
		
		var min_mon = new Date();
		min_mon.setMonth(min_mon.getMonth() - 1);
		min_mon.setHours(0, 0, 0, 0);
		$scope.inputVO.sDate = min_mon;
		
		var min_mon2 = new Date();
		min_mon2.setHours(0, 0, 0, 0);
		$scope.inputVO.eDate = min_mon2;
		$scope.limitDate();
		
		// 連動
		$scope.regionOBJ = ['N', $scope.inputVO, "region_center_id", "REGION_LIST", "branch_area_id", "AREA_LIST", "branch_nbr", "BRANCH_LIST", "ao_code", "AO_LIST", "emp_id", "EMP_LIST"];
        $scope.RegionController_setName($scope.regionOBJ);
        
        $scope.sendRecv("PMS401U", "isMainten", "com.systex.jbranch.app.server.fps.pms401u.PMS401UInputVO", {'itemID': 'PMS361'}, function(tota, isError) {
			if (!isError) {
				$scope.chkMaintenance = tota[0].body.isMaintenancePRI == 'Y' ? true : false;

				return;
			}						
		});
	};
	$scope.init();
	$scope.inquireInit = function() {
		$scope.totalList = [];
		$scope.outputVO = {};
	}
	$scope.inquireInit();
	
	$scope.inquire = function() {
		$scope.inputVO.showNoData = "Y";
		
		$scope.sendRecv("PMS361", "inquire", "com.systex.jbranch.app.server.fps.pms361.PMS361InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				if(tota[0].body.resultList.length == 0) {
					$scope.showMsg("ehl_01_common_009");
        			return;
        		}
				$scope.totalList = tota[0].body.resultList;
				$scope.outputVO = tota[0].body;

				angular.forEach($scope.totalList, function(row) {
					row.NOTE  = row.NOTE  || '';
					row.NOTE2 = row.NOTE2 || '';
					row.NOTE3 = row.NOTE3 || '';
					row.OLD_NOTE =  angular.copy(row.NOTE)  || '';
					row.OLD_NOTE2 = angular.copy(row.NOTE2) || '';
					row.OLD_NOTE3 = angular.copy(row.NOTE3) || '';
					row.OLD_WARNING_YN = angular.copy(row.WARNING_YN);
					
					if (row.CUST_AGE < 18) {
						row.set = [];
						row.set.push({LABEL: "行員未成年子女", DATA: "U"});
						row.set.push({LABEL: "其它", DATA: "O"})
					} else {
						row.set = [];
						row.set.push({LABEL: "其它", DATA: "O"})
					}
				});
			}
		});
	};

	$scope.updateFlag = function (row){
		row.UPDATE_FLAG = 'Y';			
	}
	
	$scope.setDefaultVal = function (row){
		if (!row.NOTE3 && row.CUST_BASE == 'U') {
			row.NOTE3 = "行員未成年子女";
			row.WARNING_YN = 'N';
		}
	}
	
	$scope.exportData = function() {
		$scope.sendRecv("PMS361", "export", "com.systex.jbranch.app.server.fps.pms361.PMS361InputVO", {'totalList': $scope.totalList}, function(totas, isError) {
    		if (!isError) {
    			
			}
		});
	};
	
	$scope.save = function() {
		$scope.updateList = [];
		var checkData = true;

		angular.forEach($scope.paramList, function(row, index, objs){
			if(row.UPDATE_FLAG == 'Y') {
				if ((!row.CUST_BASE || // 客戶背景或關係需填寫
					 (row.CUST_BASE == 'O' && !row.NOTE2)) || // 客戶背景或關係 若為其他，需補充說明
					!row.NOTE3 || 
					!row.WARNING_YN ||
					(!row.NOTE_TYPE || // 查證方式需填寫
					 (row.NOTE_TYPE == 'O' && !row.NOTE) || // 查證方式若為其它，請補充查證方式
					 ((row.NOTE_TYPE == 'I' || row.NOTE_TYPE == 'A') && !row.RECORD_SEQ && row.RECORD_YN == 'Y') || // 查證方式若為電訪/系統查詢及電訪客戶，請輸入電訪錄音編號
					 ((row.NOTE_TYPE == 'I' || row.NOTE_TYPE == 'A') && row.RECORD_SEQ && row.RECORD_SEQ.length != 12 && row.RECORD_YN == 'Y') // 查證方式若為電訪/系統查詢及電訪客戶，電訪錄音編號需滿12碼
					)) {
					checkData = false; 
					return;
				};
				$scope.updateList.push(row);
			};
		});
		
		if(!checkData){
			$scope.showErrorMsg("已勾選的查證方式、資金來源/帳戶關係、具體原因/用途、初判異常轉法遵部調查，都必須輸入。");
    		return;
		};
		
		$scope.sendRecv("PMS361", "update", "com.systex.jbranch.app.server.fps.pms361.PMS361InputVO", {'totalList': $scope.updateList}, function(totas, isError) {
			if (!isError) {
				$scope.showSuccessMsg('ehl_01_common_004');
				$scope.inquireInit();
				$scope.inquire();
			}
		});
	};
	
});