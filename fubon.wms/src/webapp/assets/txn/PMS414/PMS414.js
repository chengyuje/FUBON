'use strict';
eSoafApp.controller('PMS414Controller', function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService, getParameter) {
	
	$controller('BaseController', {$scope: $scope});
	$controller('PMSRegionController', {$scope: $scope});
	
	$scope.controllerName = "PMS414Controller";
	
	// filter
	getParameter.XML(["PMS.CHECK_TYPE"], function(totas) {
		if (totas) {
			$scope.mappingSet['PMS.CHECK_TYPE'] = totas.data[totas.key.indexOf('PMS.CHECK_TYPE')];
		}
	});
    //
	
	/* 處理登入者雙重角色情況 */
	var rcID = sysInfoService.getRegionID();  //登入者區域中心ID
	var opID = sysInfoService.getAreaID();    //登入者營運區ID
	var brID = sysInfoService.getBranchID();  //登入者分行ID	
	var roleID = sysInfoService.getRoleID();  //登入者角色ID
	
	var roleL = '';
	
	if("null" != rcID){	 //非總行人員
		if("null" != opID){ 
			if("000" != brID)
				roleL = 'br';				
			else   //營運區督導
				roleL = 'op';									
		} else{	//區域中心主管
			roleL = 'rc';
		}				
	}			
	//END
	
	$scope.test = function(){
		$scope.inputVO[ 'reportDate' ] = $filter('date')($scope.inputVO.eCreDate, 'yyyyMM');
		$scope.RegionController_getORG($scope.inputVO);
	}
    
    //選取月份下拉選單 --> 重新設定可視範圍
    $scope.dateChange = function(){
    	$scope.inputVO.region_center_id = '';
    	$scope.inputVO.branch_area_id = '';
    	$scope.inputVO.branch_nbr = '';
    	$scope.inputVO.emp_id = '';
    };
    
    //20170929:問題單3791:IF(「本次交易是否有行員在場」=「是」AND (「是否為行員代登入網銀帳號密碼」=「是」OR「客戶登入網銀帳號密碼時，行員是否迴避」=「否」OR「行員協助操作時，客戶是否全程在旁」=「否」) 則 「是否違規」 => 「是」
    $scope.oboChange = function(row){		
    	if (row.STAFF_THERE_FLAG == 'Y') {
    		if(row.OBO_CUST_FLAG == 'Y' || row.AVOID_FLAG == 'N' || row.NEARBY_CUST_FLAG == 'N'){        			
    			row.VIOLATION_FLAG = 'Y';   
    		}else{
    			row.VIOLATION_FLAG = 'N';   
    		}
    	} else if (row.STAFF_THERE_FLAG == 'N') {
    		row.VIOLATION_FLAG = 'N';   
    	}        	
    }        
    
	$scope.open = function($event, index) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope['opened' + index] = true;
	};
	
	/*
	 * 預設日期為系統日前一日
	 * 2022.12.22 #1383 改為系統日
	 */
	var iniDt = new Date();
	//iniDt.setDate(iniDt.getDate() - 1);
	
	$scope.init = function(){
		$scope.inputVO = {
			sCreDate        : undefined,
			eCreDate        : iniDt,
			region_center_id: '',
			branch_area_id  : '',
			branch_nbr      : '', 
			reason          : ''
    	};			
		$scope.curDate = iniDt;
		$scope.paramList =[];
		$scope.paramList1 = [];
		$scope.outputVO = {totalList:[]};
		$scope.dets = 0;
		
		$scope.sendRecv("PMS401U", "isMainten", "com.systex.jbranch.app.server.fps.pms401u.PMS401UInputVO", {'itemID': 'PMS414'}, function(tota, isError) {
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
		$scope.originalList = [];
	}
	
	$scope.inquireInit();	
	
	// date picker
	$scope.bgn_sDateOptions = {
		maxDate: $scope.maxDate,
		minDate: $scope.minDate
	};
	
	$scope.bgn_eDateOptions = {
		maxDate: $scope.maxDate,
		minDate: $scope.minDate
	};

	$scope.model = {};
	$scope.open = function($event, elementOpened) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.model[elementOpened] = !$scope.model[elementOpened];
	};
	
	$scope.limitDate = function() {			
		$scope.bgn_sDateOptions.maxDate = $scope.inputVO.eCreDate || $scope.maxDate;
		$scope.bgn_eDateOptions.minDate = $scope.inputVO.sCreDate || $scope.minDate;
	};
	// date picker end
    
	$scope.mappingSet['flagYN'] = [];
    $scope.mappingSet['flagYN'].push({LABEL:'請選擇', DATA:null}, {LABEL:'是', DATA:'Y'}, {LABEL:'否', DATA:'N'}); 
    
	$scope.query = function(){
		$scope.sendRecv("PMS414", "queryData", "com.systex.jbranch.app.server.fps.pms414.PMS414InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				if(tota[0].body.resultList.length == 0) {
					$scope.paramList = [];
					$scope.totalData = [];
					$scope.showMsg("ehl_01_common_009");
        			return;
        		}		
				
				$scope.originalList = angular.copy(tota[0].body.resultList);
				$scope.paramList = tota[0].body.resultList;
				
				$scope.reasonFlag = false;
				
				angular.forEach($scope.paramList, function(row, index, objs){
					if (Number(row.MEETING) > 0) {
						row.MEETING = new Date(Number(row.MEETING));
					} else {
						row.MEETING = $scope.toJsDate(row.MEETING);
					}
				});
				
				$scope.totalData = tota[0].body.totalList;
				$scope.outputVO = tota[0].body;		
				
				return;
			}						
		});
	};
	
	angular.forEach($scope.paramList, function(row, index, objs){
		row.MEETING_HOUR = 59;
	});
	
	/****儲存****/
	$scope.save = function () {
		$scope.reasonFlag = false;
		
		angular.forEach($scope.paramList, function(row, index, objs) {
//			if (row.MEETING_HOUR > 23) {
//				$scope.showErrorMsg("欄位檢查錯誤:[小時]欄位不可以超過23小時!");
//			 	$scope.dets = 1;
//				return;
//			}
//			
//			if (row.MEETING_MIN > 59) {
//				$scope.showErrorMsg("欄位檢查錯誤:[分鐘]欄位不可以超過59分鐘!");
//				$scope.dets = 1;
//				return;
//			}
			
			//2017-10-11 willis 增加檢核邏輯 問題單0003818與0003826
			if (!row.OBO_CUST_FLAG && !row.AVOID_FLAG && !row.NEARBY_CUST_FLAG) {
				
			} else {
				if (row.STAFF_THERE_FLAG == 'Y') {
					if ((row.OBO_CUST_FLAG == null || row.OBO_CUST_FLAG == '') || 
					    (row.AVOID_FLAG == null || row.AVOID_FLAG == '') || 
					    (row.NEARBY_CUST_FLAG == null || row.NEARBY_CUST_FLAG == '')) { 
						$scope.showErrorMsg("第" + (index+1) + " 筆，本次交易有行員在場，請依序填寫其他問題。");
						$scope.dets = 1;
						return;
	        		}
				}
				
				if (row.STAFF_THERE_FLAG == undefined || row.STAFF_THERE_FLAG == null || row.STAFF_THERE_FLAG == '') {
					$scope.showErrorMsg("欄位檢查錯誤:[本次交易是否有行員在場]欄位為必填!");
					$scope.dets = 1;
					return;
				}
			}

			if (!row.NOTE_TYPE || // 查證方式需填寫
				(row.NOTE_TYPE == 'O' && !row.NOTE) || // 查證方式若為其它，請補充查證方式
				((row.NOTE_TYPE == 'I' || row.NOTE_TYPE == 'A') && !row.RECORD_SEQ && row.RECORD_YN == 'Y') || // 查證方式若為電訪/系統查詢及電訪客戶，請輸入電訪錄音編號
				((row.NOTE_TYPE == 'I' || row.NOTE_TYPE == 'A') && row.RECORD_SEQ && row.RECORD_SEQ.length != 12 && row.RECORD_YN == 'Y') // 查證方式若為電訪/系統查詢及電訪客戶，電訪錄音編號需滿12碼
			   ) {
				$scope.showErrorMsg("查詢方式為必填；若為其它，請補充查詢方式；若為電訪客戶/系統查詢及電訪客戶，請輸入電訪錄音編號(12碼)。");
				$scope.dets = 1;
				return;
			};
			
			angular.forEach($scope.originalList, function (originalRow, originalIndex, originalObjs) {
				if (row.SEQ == originalRow.SEQ && row.VIOLATION_FLAG != originalRow.VIOLATION_FLAG && originalRow.FIRSTUPDATE != null) {
					$scope.reasonFlag = true;
				}
			});
		});
		
		if ($scope.dets == 1) {
			$scope.dets = 0;
			return;
		}

		if ($scope.reasonFlag && ($scope.inputVO.reason == undefined || $scope.inputVO.reason == null || $scope.inputVO.reason == "")) {
			var dialog = ngDialog.open({
				template: 'assets/txn/PMS414/PMS414_REASON.html',
				className: 'PMS414',
				showClose: false
			}).closePromise.then(function(data) {
				if(data.value.status == 'success'){
					
					angular.forEach($scope.paramList, function(row, index, objs) {
						if(row.MEETING != null ){
							var iniDt = new Date(row.MEETING);
							row.MEETING_SAVE = iniDt.getTime() + "";
						}
					});
					
					$scope.inputVO.reason = data.value.reason;

					$scope.saveFunc();
					
					$scope.inputVO.reason = "";
				}
			});
		} else {
			angular.forEach($scope.paramList, function(row, index, objs) {
				if(row.MEETING != null ){
					var iniDt = new Date(row.MEETING);
					row.MEETING_SAVE = iniDt.getTime() + "";
				}
			});

			
			$scope.saveFunc();
		}
	};
	
	$scope.saveFunc = function () {
		$scope.sendRecv("PMS414", "save", "com.systex.jbranch.app.server.fps.pms414.PMS414InputVO", {'list':$scope.paramList, 'list2':$scope.originalList, 'reason':$scope.inputVO.reason}, function(tota, isError) {	
			if (isError) {
        		$scope.showErrorMsgInDialog(tota[0].body.msgData);
        		return;
        	}
        	if (tota.length > 0) {
        		$scope.showMsg('儲存成功');
        	};		
		});
	}
	
	$scope.exportRPT = function(){
		angular.forEach($scope.outputVO.totalList, function(row, index, objs){
			if (row.MEETING != null ) {
				var iniDt = new Date(row.MEETING);
				row.MEETING = iniDt.getTime() + "";
				row.MEETING = $filter('date')(row.MEETING, 'yyyy/MM/dd') + "";
			}
			
			if (row.MEETING_HOUR == null)
				row.MEETING_HOUR = "00";
			else
				row.MEETING_HOUR = row.MEETING_HOUR + "";
			
			if(row.MEETING_MIN == null)
				row.MEETING_MIN = "00";
			else
				row.MEETING_MIN=row.MEETING_MIN + "";
			
		});
		
		$scope.sendRecv("PMS414", "export", "com.systex.jbranch.app.server.fps.pms414.PMS414OutputVO", $scope.outputVO, function(tota, isError) {						
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
	
	$scope.setDefaultVal = function (row) {
		if (row.NOTE_TYPE != 'O') {
			row.NOTE = '';
		}
		
		if (row.NOTE_TYPE != 'I' && row.NOTE_TYPE != 'A') {
			row.RECORD_SEQ = '';
		}
	}
	
    $scope.oldRPT = function(page) {
    	$rootScope.menuItemInfo.url = "assets/txn/PMS" + page + "/PMS" + page + ".html";
    };
});
