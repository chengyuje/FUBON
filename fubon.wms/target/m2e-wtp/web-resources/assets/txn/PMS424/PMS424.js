
'use strict';
eSoafApp.controller('PMS424Controller', function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService, getParameter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "PMS424Controller";
	
	// 繼承共用的組織連動選單
	$controller('PMSRegionController', {$scope: $scope});
	
	// filter
	getParameter.XML(["PMS.PMS424_SOURCE_OF_DEMAND", "PMS.CHECK_TYPE"], function(totas) {
		if (totas) {
			$scope.mappingSet['PMS.PMS424_SOURCE_OF_DEMAND'] = totas.data[totas.key.indexOf('PMS.PMS424_SOURCE_OF_DEMAND')];
			$scope.mappingSet['PMS.CHECK_TYPE'] = totas.data[totas.key.indexOf('PMS.CHECK_TYPE')];
		}
	});
	// ===
	
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

    //選取月份下拉選單 --> 重新設定可視範圍
    $scope.dateChange = function() {
    	if (!$scope.inputVO.eDate) {
    		return;
    	}
    	
    	//設定回傳時間
    	$scope.inputVO.reportDate = $scope.inputVO.eDate;
    	
    	//可視範圍觸發 
    	$scope.RegionController_getORG($scope.inputVO);
    };
    
    // 查詢初始化
	$scope.queryInit = function() {
		$scope.resultList = [];
		$scope.outputVO = [];
	}
    
	// 初始化
	$scope.init = function() {
		$scope.inputVO = {
			yyyymm				: '',
			region_center_id	: '',   // 區域中心
			branch_area_id		: '', 	// 營運區
			branch_nbr			: '',	// 分行	
			emp_id				: '',	// 員編
			memLoginFlag		: String(sysInfoService.getMemLoginFlag())
    	};
		
		var min_mon = new Date();
		min_mon.setMonth(min_mon.getMonth() - 1);
		min_mon.setHours(0, 0, 0, 0);
		$scope.inputVO.sDate = min_mon;
		
		var min_mon2 = new Date();
		min_mon2.setHours(0, 0, 0, 0);
		$scope.inputVO.eDate = min_mon2;
		$scope.limitDate();
		
		$scope.dateChange();
		$scope.queryInit();
		
		$scope.sendRecv("PMS401U", "isMainten", "com.systex.jbranch.app.server.fps.pms401u.PMS401UInputVO", {'itemID': 'PMS424'}, function(tota, isError) {
			if (!isError) {
				$scope.chkMaintenance = tota[0].body.isMaintenancePRI == 'Y' ? true : false;
			}
		});
	};
	$scope.init();
	
	//資料查詢
	$scope.query = function() {	
		if ($scope.inputVO.sDate == undefined || $scope.inputVO.sDate == null || $scope.inputVO.sDate == '' || 
			$scope.inputVO.eDate == undefined || $scope.inputVO.eDate == undefined || $scope.inputVO.eDate == undefined) {
    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位(資料統計月份)');
    		return;
    	}
		
		$scope.sendRecv("PMS424", "query", "com.systex.jbranch.app.server.fps.pms424.PMS424InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				if(tota[0].body.resultList.length == 0) {
					$scope.showMsg("ehl_01_common_009");
        			return;
        		}
				
				angular.forEach(tota[0].body.resultList, function(row, index, objs){
					if(row.ACCT_OUT_ID != null){
						row.ACCT_OUT_ID_MASK = row.ACCT_OUT_ID.substring(0, 4)+"****"+row.ACCT_OUT_ID.substring(8, 10);    //隱藏身分證四碼
					}
					if(row.ACCT_IN_ID != null){
						row.ACCT_IN_ID_MASK = row.ACCT_IN_ID.substring(0, 4)+"****"+row.ACCT_IN_ID.substring(8, 10);    //隱藏身分證四碼
					}
				});
				
				$scope.resultList = tota[0].body.resultList;
				$scope.outputVO = tota[0].body;		
			}				
		});
	};
	
	$scope.updateFlag = function (row){
		row.UPDATE_FLAG = 'Y';			
	}
	
	// 儲存
	$scope.save = function () {
		$scope.updateList = [];
		var checkData = true;
		var rowIndex;
		angular.forEach($scope.paramList, function(row, index, objs){
			if (row.UPDATE_FLAG == 'Y') {
				if (!row.NOTE2 || 
					!row.NOTE3 || 
					!row.WARNING_YN || 
					(!row.NOTE_TYPE || // 查證方式需填寫
					 (row.NOTE_TYPE == 'O' && !row.NOTE1) || // 查證方式若為其它，請補充查證方式
					 ((row.NOTE_TYPE == 'I' || row.NOTE_TYPE == 'A') && !row.RECORD_SEQ && row.RECORD_YN == 'Y') || // 查證方式若為電訪/系統查詢及電訪客戶，請輸入電訪錄音編號
					 ((row.NOTE_TYPE == 'I' || row.NOTE_TYPE == 'A') && row.RECORD_SEQ && row.RECORD_SEQ.length != 12 && row.RECORD_YN == 'Y') // 查證方式若為電訪/系統查詢及電訪客戶，電訪錄音編號需滿12碼
					)) {
					rowIndex = row.ROWNUM;
					checkData = false; 
					return;
				};
				$scope.updateList.push(row);
			};
		});
		
		if(!checkData){
			$scope.showErrorMsg("第" + rowIndex + "筆的查證方式、資金來源/帳戶關係、具體原因/用途、初判異常轉法遵部調查，都必須輸入。");
    		return;
		};
		
		$scope.sendRecv("PMS424", "save", "com.systex.jbranch.app.server.fps.pms424.PMS424InputVO", {'list': $scope.updateList}, function(tota, isError) {						
			if (isError) {
        		$scope.showErrorMsgInDialog(tota[0].body.msgData);
        		return;
        	}
        	if (tota.length > 0) {
        		$scope.showMsg('儲存成功');
//        		$scope.query();
        	};	
		});
	};
	
	// 匯出
	$scope.exportRPT = function(){
		$scope.sendRecv("PMS424", "export", "com.systex.jbranch.app.server.fps.pms424.PMS424InputVO", {'list' : $scope.resultList}, function(tota, isError) {
			if (isError) {
        		$scope.showErrorMsgInDialog(tota[0].body.msgData);
        		return;
        	}			
		});
	};
	
	$scope.getHistory = function() {
		$scope.sendRecv("PMS424", "getExample", "com.systex.jbranch.app.server.fps.pms424.PMS424InputVO", {}, function(tota, isError) {
			if (!isError) {
				
			}
		});
	};
});