'use strict';
eSoafApp.controller('PMS428Controller', function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService, getParameter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "PMS428Controller";;
	
	// 繼承共用的組織連動選單
	$controller('PMSRegionController', {$scope: $scope});
	
	// filter
	getParameter.XML(["PMS.CHECK_TYPE", "PMS.PMS428_DATA_SOURCE"], function(totas) {
		if (totas) {
			$scope.mappingSet['PMS.CHECK_TYPE'] = totas.data[totas.key.indexOf('PMS.CHECK_TYPE')];
			$scope.mappingSet['PMS.PMS428_DATA_SOURCE'] = totas.data[totas.key.indexOf('PMS.PMS428_DATA_SOURCE')];
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

    //選取月份下拉選單 --> 重新設定可視範圍
    $scope.dateChange = function(){
    	if(!$scope.inputVO.eDate){
    		return;
    	}
    	//設定回傳時間
    	$scope.inputVO.reportDate = $scope.inputVO.eDate;
    	//可視範圍觸發 
    	$scope.RegionController_getORG($scope.inputVO);
    };
    
    // 查詢初始化
	$scope.queryInit = function(){
		$scope.resultList = [];
		$scope.outputVO = [];
	}
    
	// 初始化
	$scope.init = function(){
		$scope.inputVO = {
			sDate				: undefined,
			eDate				: undefined,
			region_center_id	: '',   // 區域中心
			branch_area_id		: '', 	// 營運區
			branch_nbr			: '',	// 分行	
			exportList          : [], 
			memLoginFlag        : String(sysInfoService.getMemLoginFlag())
    	};
		
		var min_mon2 = new Date();
		min_mon2.setHours(0, 0, 0, 0);
		$scope.inputVO.eDate = min_mon2;
		$scope.limitDate();
		
		$scope.dateChange();
		$scope.queryInit();
		
		$scope.sendRecv("PMS401U", "isMainten", "com.systex.jbranch.app.server.fps.pms401u.PMS401UInputVO", {'itemID': 'PMS428'}, function(tota, isError) {
			if (!isError) {
				$scope.chkMaintenance = tota[0].body.isMaintenancePRI == 'Y' ? true : false;

				$scope.uhrmRCList = [];
				$scope.uhrmOPList = [];

				if (null != tota[0].body.uhrmORGList) {
					angular.forEach(tota[0].body.uhrmORGList, function(row) {
						$scope.uhrmRCList.push({LABEL: row.REGION_CENTER_NAME, DATA: row.REGION_CENTER_ID});
					});	
					
					$scope.inputVO.uhrmRC = tota[0].body.uhrmORGList[0].REGION_CENTER_ID;
					
					angular.forEach(tota[0].body.uhrmORGList, function(row) {
						$scope.uhrmOPList.push({LABEL: row.BRANCH_AREA_NAME, DATA: row.BRANCH_AREA_ID});
					});
					
					$scope.inputVO.uhrmOP = tota[0].body.uhrmORGList[0].BRANCH_AREA_ID;
		        }
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
		
		$scope.sendRecv("PMS428", "query", "com.systex.jbranch.app.server.fps.pms428.PMS428InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				if(tota[0].body.resultList.length == 0) {
					$scope.showMsg("ehl_01_common_009");
        			return;
        		}
				
				$scope.resultList = tota[0].body.resultList;
				$scope.outputVO = tota[0].body;		
			}				
		});
	};
	
	$scope.exportRPT = function(){
		$scope.inputVO.exportList = $scope.resultList;
		
		$scope.sendRecv("PMS428", "export", "com.systex.jbranch.app.server.fps.pms428.PMS428InputVO", $scope.inputVO, function(tota, isError) {						
			if (isError) {
        		$scope.showErrorMsgInDialog(tota[0].body.msgData);		            		
        	}
		});
	};
	
	$scope.updateFlag = function (row){
		row.UPDATE_FLAG = 'Y';			
	}
    
    /** 添加 User 可編輯的 Input 欄位，進而判斷與原欄位差異者，更新資料 **/
    $scope.addInputCol = each => {
        each.HR_ATTR_INPUT = each.HR_ATTR;
        each.NOTE_INPUT = each.NOTE;
        each.NOTE2_INPUT = each.NOTE2;
        each.NOTE3_INPUT = each.NOTE3;
    };
    
    /**
     * 儲存使用者編輯的資料
     */
    $scope.save = () => {
		$scope.updateList = [];
		var checkData = true;
		
		angular.forEach($scope.paramList, function(row, index, objs){
			if(row.UPDATE_FLAG == 'Y') {
				if (!row.HR_ATTR || 
					!row.NOTE2 || 
					!row.NOTE3 ||
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
			$scope.showErrorMsg("已勾選的查證方式、資金來源/帳戶關係、具體原因或用途、初判異常轉法遵部調查，都必須輸入。");
    		return;
		};
		
		$scope.sendRecv("PMS428", "save", 'com.systex.jbranch.app.server.fps.pms428.PMS428InputVO', {list: $scope.updateList}, (_, isError) => {
            if (!isError) {
                $scope.showSuccessMsg('ehl_01_common_025');
//                $scope.query();
            }
        });
    };
	
});