'use strict';
eSoafApp.controller('PMS426Controller', function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService, getParameter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "PMS426Controller";;

	// 繼承共用的組織連動選單
	$controller('RegionController', {$scope: $scope});
	
	// filter
	getParameter.XML(["PMS.CHECK_TYPE", "PMS.ACCOUNT_REL"], function(totas) {
		if (totas) {
			$scope.mappingSet['PMS.CHECK_TYPE'] = totas.data[totas.key.indexOf('PMS.CHECK_TYPE')];
			$scope.mappingSet['PMS.ACCOUNT_REL'] = totas.data[totas.key.indexOf('PMS.ACCOUNT_REL')];
		}
	});
	// ===
	
	$scope.model = {};
	$scope.open = function($event, elementOpened) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.model[elementOpened] = !$scope.model[elementOpened];
	};
	
    $scope.importStartDateOptions = {
    	minMode: 'month', 
		maxDate: $scope.inputVO.eCreDate || $scope.maxDate,
		minDate: $scope.minDate
	};
    
	$scope.importEndDateOptions = {
		minMode: 'month',
		maxDate: $scope.maxDate,
		minDate: $scope.inputVO.sCreDate || $scope.minDate
	};
	
	$scope.limitDate = function() {
		$scope.importStartDateOptions.maxDate = $scope.inputVO.eCreDate || $scope.maxDate;
		$scope.importEndDateOptions.minDate = $scope.inputVO.sCreDate || $scope.minDate;
	};

	 //選取月份下拉選單 --> 重新設定可視範圍
	$scope.dateChange = function(){
		if ($scope.inputVO.sCreDate != '') {
			$scope.inputVO.reportDate = $scope.inputVO.sCreDate;
		} else {   
			$scope.inputVO.sCreDate = '201701';
			$scope.inputVO.reportDate = $scope.inputVO.sCreDate;
			$scope.inputVO.sCreDate = '';
		} 
		
		$scope.inputVO.dataMonth = $scope.inputVO.sCreDate; 
	}; 
    
	// 初始化
	$scope.init = function() {
		$scope.priID = String(sysInfoService.getPriID());

		$scope.inputVO = {
			sCreDate			: '',
			eCreDate            : '',
			region_center_id	: '',   // 區域中心
			branch_area_id		: '', 	// 營運區
			branch_nbr			: '',	// 分行	
			exportList          : [], 
			memLoginFlag        : String(sysInfoService.getMemLoginFlag())
    	};
		
		$scope.resultList = [];
		$scope.outputVO = [];
		
		$scope.dateChange();
		
		$scope.test = ['N', $scope.inputVO, "region_center_id", "REGION_LIST", "branch_area_id", "AREA_LIST", "branch_nbr", "BRANCH_LIST", "aoCode", "AO_LIST", "emp_id", "EMP_LIST"];
		$scope.RegionController_setName($scope.test);
	
		$scope.sendRecv("PMS401U", "isMainten", "com.systex.jbranch.app.server.fps.pms401u.PMS401UInputVO", {'itemID': 'PMS426'}, function(tota, isError) {
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
		if ($scope.inputVO.sCreDate == '') {
    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位(資料統計月份)');
    		return;
    	}
		
		$scope.paramList = [];
		$scope.resultList = [];
		$scope.sendRecv("PMS426", "query", "com.systex.jbranch.app.server.fps.pms426.PMS426InputVO", $scope.inputVO, function(tota, isError) {
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
		
		$scope.sendRecv("PMS426", "export", "com.systex.jbranch.app.server.fps.pms426.PMS426InputVO", $scope.inputVO, function(tota, isError) {						
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
			if (row.UPDATE_FLAG == 'Y') {
				if (!row.HR_ATTR || 
					(!row.CUST_BASE || // 客戶背景或關係需填寫
					 (row.CUST_BASE == 'O' && !row.NOTE2) // 客戶背景或關係若為其它，請補充查證方式
					) || 
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
			$scope.showErrorMsg("已勾選的查證方式、帳戶關係。如客戶彼此不認識請了解客戶留存原因是否合理、初判異常轉法遵部調查，都必須輸入。");
    		return;
		};
		
		$scope.sendRecv("PMS426", "save", 'com.systex.jbranch.app.server.fps.pms426.PMS426InputVO', {list: $scope.updateList}, (_, isError) => {
            if (!isError) {
                $scope.showSuccessMsg('ehl_01_common_025');
            }
        });
    };
    
    $scope.setDefaultVal = function (row) {
		switch(row.CUST_BASE) {
			case "M":
			case "PC":
			case "BS":
			case "G":
			case "B":
				row.HR_ATTR = 'N';
				break;
			case "O":
				break;
			default:
				break;
		}
	}
	
});