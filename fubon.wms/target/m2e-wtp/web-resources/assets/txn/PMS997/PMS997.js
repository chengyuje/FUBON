'use strict';
eSoafApp.controller("PMS997Controller", function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService, getParameter, $compile) {
	
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "PMS997Controller";

	//組織連動繼承
	$controller('RegionController', {$scope: $scope});

	getParameter.XML(["PMS.CHECK_TYPE"], function(totas) {
		if (totas) {
			$scope.mappingSet['PMS.CHECK_TYPE'] = totas.data[totas.key.indexOf('PMS.CHECK_TYPE')];
		}
	});
	
	$scope.model = {};
	$scope.open = function($event, elementOpened) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.model[elementOpened] = !$scope.model[elementOpened];
	};
	
    $scope.importStartDateOptions = {
    	minMode: 'month', 
		maxDate: $scope.inputVO.importEDate || $scope.maxDate,
		minDate: $scope.minDate
	};
    
	$scope.importEndDateOptions = {
		minMode: 'month',
		maxDate: $scope.maxDate,
		minDate: $scope.inputVO.importSDate || $scope.minDate
	};
	
	$scope.limitDate = function() {
		$scope.importStartDateOptions.maxDate = $scope.inputVO.importEDate || $scope.maxDate;
		$scope.importEndDateOptions.minDate = $scope.inputVO.importSDate || $scope.minDate;
	};

	 //選取月份下拉選單 --> 重新設定可視範圍
	$scope.dateChange = function(){
		if ($scope.inputVO.importSDate != '') {
			$scope.inputVO.reportDate = $scope.inputVO.importSDate;
		} else {   
			$scope.inputVO.importSDate = '201701';
			$scope.inputVO.reportDate = $scope.inputVO.importSDate;
			$scope.inputVO.importSDate = '';
		} 
		
		$scope.inputVO.dataMonth = $scope.inputVO.importSDate; 
	}; 
   
	$scope.init = function() {
		$scope.priID = String(sysInfoService.getPriID());
		
		$scope.inputVO = {					
			importSDate      : undefined,	
			importEDate      : undefined,
			region_center_id : '',
			branch_area_id   : '',
			branch_nbr       : '',
			custID           : '',
			empID            : '',
			careType         : '',
			noteStatus       : '',
			exportList       : [], 
			memLoginFlag     : String(sysInfoService.getMemLoginFlag())
    	};
		
		$scope.resultList = [];
		$scope.outputVO = [];
		$scope.curDate = new Date();
		$scope.dateChange();
	    
        $scope.test = ['N', $scope.inputVO, "region_center_id", "REGION_LIST", "branch_area_id", "AREA_LIST", "branch_nbr", "BRANCH_LIST", "aoCode", "AO_LIST", "emp_id", "EMP_LIST"];
        $scope.RegionController_setName($scope.test);
        
        $scope.sendRecv("PMS401U", "isMainten", "com.systex.jbranch.app.server.fps.pms401u.PMS401UInputVO", {'itemID': 'PMS997'}, function(tota, isError) {
			if (!isError) {
				$scope.chkMaintenance = tota[0].body.isMaintenancePRI == 'Y' ? true : false;

				return;
			}						
		});
	};
	
	$scope.init();
	
	$scope.inquireInit = function(){
		$scope.initLimit();
		$scope.resultList = [];
		$scope.outputVO = [];
		$scope.originalList = [];
	}
	$scope.inquireInit();
	
	$scope.query = function(){
		if($scope.inputVO.importSDate == '' || $scope.inputVO.importSDate == undefined || $scope.inputVO.importSDate == null) {
    		$scope.showErrorMsgInDialog('欄位檢核錯誤:日期必要輸入欄位');
    		return;
    	}
		
		$scope.sendRecv("PMS997", "query", "com.systex.jbranch.app.server.fps.pms997.PMS997InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				if(tota[0].body.resultList.length == 0) {
					$scope.showMsg("ehl_01_common_009");
        			return;
        		}
				
				$scope.resultList = tota[0].body.resultList;
				$scope.exportList =	tota[0].body.resultList;
				$scope.outputVO = tota[0].body;
			}						
		});
	};
	
	$scope.exportRPT = function(){			
		angular.copy($scope.exportList,$scope.inputVO.exportList);
		$scope.sendRecv("PMS997", "export", "com.systex.jbranch.app.server.fps.pms997.PMS997InputVO", $scope.inputVO, function(tota, isError) {						
			if (isError) {
        		$scope.showErrorMsgInDialog(tota[0].body.msgData);		            		
        	}
			if (tota.length > 0) {
        		if(tota[0].body.resultList && tota[0].body.resultList.length == 0) {
        			$scope.showMsg("ehl_01_common_009");
        			return;
        		}
        	};
		});
	};
	
	$scope.updateFlag = function (row) {
		row.UPDATE_FLAG = 'Y';			
	}
	
	$scope.showCareDrcr = function (row) {
		var msg = '';
		if (row.CARE_DRCR != undefined || row.CARE_DRCR != '' || row.CARE_DRCR != null) {
			msg += row.CARE_DRCR;
		} else {
			msg += '無說明';
		}
		
		if (msg != '') {
			$confirm({
					text : msg,
					title: '關懷說明', hideCancel: true
				}, 
				{size: '120px'}).then(function() {}
			);
		}
	}
	
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
			if(row.UPDATE_FLAG == 'Y') {
				if (!row.HR_ATTR || 
					!row.NOTE2 ||
					(!row.NOTE_TYPE || // 查證方式需填寫
					 (row.NOTE_TYPE == 'O' && !row.NOTE || // 查證方式若為其它，請補充查證方式
					 ((row.NOTE_TYPE == 'I' || row.NOTE_TYPE == 'A') && !row.RECORD_SEQ) || // 查證方式若為電訪/系統查詢及電訪客戶，請輸入電訪錄音編號
					 ((row.NOTE_TYPE == 'I' || row.NOTE_TYPE == 'A') && row.RECORD_SEQ && row.RECORD_SEQ.length != 12)) // // 查證方式若為電訪/系統查詢及電訪客戶，電訪錄音編號需滿12碼
					)) {
					checkData = false; 
					return;
				};
				
				$scope.updateList.push(row);
			};
		});
		
		if(!checkData){
			$scope.showErrorMsg("查證方式(若為其它，請補充查證方式；若為電訪，請填寫12碼電訪錄音編號)、關懷結果、初判異常，都必須輸入。");
    		return;
		};
		
		$scope.sendRecv("PMS997", "save", 'com.systex.jbranch.app.server.fps.pms997.PMS997InputVO', {list: $scope.updateList}, (_, isError) => {
            if (!isError) {
                $scope.showSuccessMsg('ehl_01_common_025');
            }
        });
    };
    
	$scope.getCareCaseExp = function() {
		$scope.sendRecv("PMS997", "getCareCaseExp", "com.systex.jbranch.app.server.fps.pms997.PMS997InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(tota.body.msgData);
				return;
			}
			
			if (tota.length > 0) {
			}
		});
	};
	
	$scope.getCareTypeExp = function() {
		$scope.sendRecv("PMS997", "getCareTypeExp", "com.systex.jbranch.app.server.fps.pms997.PMS997InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(tota.body.msgData);
				return;
			}
			
			if (tota.length > 0) {
			}
		});
	};
	
	$scope.updCareCase = function(name, rname) {
		$scope.inputVO.FILE_NAME = name;
		$scope.inputVO.ACTUAL_FILE_NAME = rname;
		
		$confirm({text: '是否上傳：' + rname + '?'}, {size: '200px'}).then(function() {
			$scope.sendRecv("PMS997", "updCareCase", "com.systex.jbranch.app.server.fps.pms997.PMS997InputVO", $scope.inputVO, function(tota, isError) {
				if (isError) {
				} else if (tota.length > 0) {
					$scope.showMsg("ehl_01_common_010");
				}
			});
		});
		
		angular.element($("#updCareCas")).remove();
		angular.element($("#careCaseBox")).append("<e-upload id='updCareCas' success='updCareCase(name, rname)' text='上傳' accept='.csv'></e-upload>");
		var content = angular.element($("#careCaseBox"));
		var scope = content.scope();
		$compile(content.contents())(scope);
	};
	
	$scope.updCareType = function(name, rname) {
		$scope.inputVO.FILE_NAME = name;
		$scope.inputVO.ACTUAL_FILE_NAME = rname;
		
		$confirm({text: '是否上傳：' + rname + '?'}, {size: '200px'}).then(function() {
			$scope.sendRecv("PMS997", "updCareType", "com.systex.jbranch.app.server.fps.pms997.PMS997InputVO", $scope.inputVO, function(tota, isError) {
				if (isError) {
				} else if (tota.length > 0) {
					$scope.showMsg("ehl_01_common_010");
				}
			});
		});
		
		angular.element($("#updCareType")).remove();
		angular.element($("#careTypeBox")).append("<e-upload id='updCareType' success='updCareType(name, rname)' text='上傳' accept='.csv'></e-upload>");
		var content = angular.element($("#careTypeBox"));
		var scope = content.scope();
		$compile(content.contents())(scope);
	};
});
