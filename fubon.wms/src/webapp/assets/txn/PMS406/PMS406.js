'use strict';
eSoafApp.controller('PMS406Controller', function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService,getParameter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "PMS406Controller";
	
	// 繼承共用的組織連動選單
	$controller('PMSRegionController', {$scope: $scope});

	$scope.dateChange = function(){
    	$scope.inputVO.reportDate = $filter('date')($scope.inputVO.sCreDate,'yyyyMMdd');
    	$scope.RegionController_getORG($scope.inputVO);
	};
	
	getParameter.XML(["PMS.SHORT_TRADE_TRANS_SRC", "PMS.CHECK_TYPE"], function(totas) {
		if (totas) {
			$scope.TRADE_SRC = totas.data[totas.key.indexOf('PMS.SHORT_TRADE_TRANS_SRC')];
			$scope.mappingSet['PMS.CHECK_TYPE'] = totas.data[totas.key.indexOf('PMS.CHECK_TYPE')];
		}
	});

	$scope.open = function($event, index) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope['opened'+index] = true;
	};				
	
	var curDate = new Date();
	//預設日期改為T-2 by 20180122-Willis 問題單:4062 
	curDate = curDate.setDate(curDate.getDate()-2);
	
	$scope.init = function(){
		$scope.priID = String(sysInfoService.getPriID());
		
		$scope.inputVO = {					
			sCreDate         : curDate, //date.setDate(value)設定日期
			endDate          : curDate,
			freqType         : '',
			region_center_id : '',
			branch_area_id   : '' ,
			branch_nbr       : '',
			emp_id           : '',
			memLoginFlag     : String(sysInfoService.getMemLoginFlag())
    	};
		
		$scope.disableRegionCombo = false;
		$scope.disableAreaCombo = false;
		$scope.disableBranchCombo = false;
		$scope.disableAoCombo = false;
		$scope.dateChange();  //讓一開始就會自動讀取昨天
		$scope.paramList = [];
		
		$scope.sendRecv("PMS406", "chkMaintenance", "com.systex.jbranch.app.server.fps.pms406.PMS406InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				$scope.chkMaintenance = tota[0].body.isMaintenancePRI == 'Y' ? true : false;

				return;
			}						
		});
	};
	
	$scope.init();
	
	$scope.inquireInit = function(){
		$scope.initLimit();
		$scope.paramList = [];
		$scope.outputVO={totalList:[]};
	}
	$scope.inquireInit();
	
	// ===== date picker =====
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
		$scope.bgn_sDateOptions.maxDate = $scope.inputVO.endDate || $scope.maxDate;
		$scope.bgn_eDateOptions.minDate = $scope.inputVO.sCreDate || $scope.minDate;
	};
	// ===== date picker end =====
	
	$scope.query = function(){
		$scope.sendRecv("PMS406", "queryData", "com.systex.jbranch.app.server.fps.pms406.PMS406InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				if(tota[0].body.resultList.length == 0) {
					$scope.paramList = [];
					$scope.showMsg("ehl_01_common_009");
        			return;
        		}
				$scope.paramList = tota[0].body.resultList;
				$scope.totalData = tota[0].body.totalList;
				$scope.outputVO = tota[0].body;
				return;
			}						
		});
	};
	
	$scope.exportRPT = function(){			
		$scope.sendRecv("PMS406", "export", "com.systex.jbranch.app.server.fps.pms406.PMS406OutputVO", $scope.outputVO,
				function(tota, isError) {						
					if (isError) {
	            		$scope.showErrorMsgInDialog(tota[0].body.msgData);
	            		return;
	            	}						
		});
	};
	
	/** 客戶屬性代碼 --> 客戶屬性名稱 **/
	var vo = {'param_type': 'PMS.CUST_ATTR', 'desc': false};
    if(!projInfoService.mappingSet['PMS.CUST_ATTR']) {
    	$scope.requestComboBox(vo, function(totas) {      	
    		if (totas[totas.length - 1].body.result === 'success') {        		
    			projInfoService.mappingSet['PMS.CUST_ATTR'] = totas[0].body.result;
    			$scope.mappingSet['PMS.CUST_ATTR'] = projInfoService.mappingSet['PMS.CUST_ATTR'];        		
    		}
    	});
    } else
    	$scope.mappingSet['PMS.CUST_ATTR'] = projInfoService.mappingSet['PMS.CUST_ATTR'];
    
    /** 檢核客戶屬性 **/
    $scope.custCheck = function(val){
    	if(val=='1' || val=='2' || val=='3' || val=='4' || val=='5' || val=='6')
    		return true;
    	else
    		return false;
    }
    
    $scope.updateFlag = function (row){
		row.UPDATE_FLAG = 'Y';			
	}
    
    /** 添加 User 可編輯的 Input 欄位，進而判斷與原欄位差異者，更新資料 **/
    $scope.addInputCol = each => {
        each.HR_ATTR_INPUT = each.HR_ATTR;
        each.NOTE_INPUT = each.NOTE;
        each.NOTE2_INPUT = each.NOTE2;
    };
    
    /**
     * 儲存使用者編輯的資料
     *
     * * 主管確認欄位（Ａ）、專員代為操作（Ｂ）、備註欄（Ｃ）。
     *      1. Ａ、B、C 三者皆有或皆無才能執行儲存功能。有任何一項有值，其他無值將會出現警示訊息。
     *      2. A、B、C 三者有任一異動則執行更新邏輯。
     *
     */
    $scope.save = () => {
		$scope.updateList = [];
		var checkData = true;
		
		angular.forEach($scope.paramList, function(row, index, objs){
			if (row.UPDATE_FLAG == 'Y') {
				if (!row.HR_ATTR || 
					!row.NOTE2 || 
					(!row.NOTE_TYPE || // 查證方式需填寫
					 (row.NOTE_TYPE == 'O' && !row.NOTE || // 查證方式若為其它，請補充查證方式
					 ((row.NOTE_TYPE == 'I' || row.NOTE_TYPE == 'A') && !row.RECORD_SEQ && row.RECORD_YN == 'Y') || // 查證方式若為電訪/系統查詢及電訪客戶，請輸入電訪錄音編號
					 ((row.NOTE_TYPE == 'I' || row.NOTE_TYPE == 'A') && row.RECORD_SEQ && row.RECORD_SEQ.length != 12 && row.RECORD_YN == 'Y')) // // 查證方式若為電訪/系統查詢及電訪客戶，電訪錄音編號需滿12碼
					)) {
					checkData = false; 
					return;
				};
				
				$scope.updateList.push(row);
			};
		});
		
		if(!checkData){
			$scope.showErrorMsg("已勾選的專員未有勸誘客戶提高風險屬性、查證方式(若為其它，請補充查證方式；若為電訪，請填寫12碼電訪錄音編號)、具體原因/用途，都必須輸入。");
    		return;
		};
		
		$scope.sendRecv("PMS406", "save", 'com.systex.jbranch.app.server.fps.pms406.PMS406InputVO', {list: $scope.updateList}, (_, isError) => {
            if (!isError) {
                $scope.showSuccessMsg('ehl_01_common_025');
            }
        });
    };
});
