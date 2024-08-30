'use strict';
eSoafApp.controller('PMS401Controller', function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService, getParameter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "PMS401Controller";

	$controller('PMSRegionController', {$scope: $scope});

    /*** 可視範圍  JACKY共用版  START ***/
    //選取月份下拉選單 --> 重新設定可視範圍
    $scope.dateChange = function(){
    	//設定回傳時間
    	$scope.inputVO.reportDate = $filter('date')($scope.inputVO.eCreDate, 'yyyyMMdd');
    	//可視範圍  觸發 
    	$scope.RegionController_getORG($scope.inputVO);
    };
    
    // filter
	getParameter.XML(["PMS.CHECK_TYPE"], function(totas) {
		if (totas) {
			$scope.mappingSet['PMS.CHECK_TYPE'] = totas.data[totas.key.indexOf('PMS.CHECK_TYPE')];
		}
	});
    //

	var rp = "RC";
	if(sysInfoService.getRoleName().substring(0, 2) == 'FC')
		rp = "AO";
	if(sysInfoService.getRoleID() == 'A161')
		rp = "BR";
	
	/*** 可視範圍  JACKY共用版  END***/
    
	// date picker start
	$scope.open = function($event, index) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope['opened' + index] = true;
	};	
	
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
	
	$scope.role_id = projInfoService.getRoleID();
	
	$scope.init = function(){
		$scope.inputVO = {
			aoFlag           :'Y',
			psFlag           :'N',
			sTime            : '',
			sCreDate         : new Date(),
			eCreDate         : new Date(),
			region_center_id : '',  	//區域中心
			branch_area_id   : '' ,   	//營運區
			branch_nbr       : '',		//分行			
			memLoginFlag     : String(sysInfoService.getMemLoginFlag())
    	};
		
		$scope.rptDate = '';
		$scope.totalData = [];
		$scope.paramList = [];
		$scope.outputVO = {totalList:[]};
		$scope.curDate = new Date();	
		$scope.dateChange();
	
		$scope.sumFlag = false;
		$scope.disableRegionCombo = false;
		$scope.disableAreaCombo = false;
		$scope.disableBranchCombo = false;
		$scope.disableAoCombo = false;
		
		var vo = {'param_type': 'FUBONSYS.FC_ROLE', 'desc': false};
    	$scope.requestComboBox(vo, function(totas) {      	
    		if (totas[totas.length - 1].body.result === 'success') {        		
    			projInfoService.mappingSet['FUBONSYS.FC_ROLE'] = totas[0].body.result;
    			for(var key in projInfoService.mappingSet['FUBONSYS.FC_ROLE']){
    	    		if(projInfoService.mappingSet['FUBONSYS.FC_ROLE'][key].DATA == projInfoService.getRoleID()){
    	    			$scope.inputVO.empHistFlag = 'Y';
    	    		}
    	    	}
    		}
    	});
    	
    	$scope.sendRecv("PMS401U", "isMainten", "com.systex.jbranch.app.server.fps.pms401u.PMS401UInputVO", {'itemID': 'PMS401'}, function(tota, isError) {
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
		$scope.updateList = [];
		$scope.outputVO={totalList:[]};
	}
	
	$scope.inquireInit();	
	
	
	var CRM181toPMS401 = "NO";
	
	//資料查詢
	$scope.query = function() {			
//		if (CRM181toPMS401 != "Yes") {				
//			if ($scope.REGION_LIST.length == 0) {				
//				$scope.inputVO.ao_code = sysInfoService.getUserID();				
//			}
//		}
		CRM181toPMS401 = "NO";
		$scope.rptDate = $filter('date')($scope.inputVO.sCreDate, 'yyyy/MM/dd')+ '~' +$filter('date')($scope.inputVO.eCreDate, 'yyyy/MM/dd'); 			
		$scope.sendRecv("PMS401", "queryData", "com.systex.jbranch.app.server.fps.pms401.PMS401InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				if (tota[0].body.resultList.length == 0) {
					$scope.paramList = [];
					$scope.totalData = [];
					$scope.outputVO = {};
					$scope.showMsg("ehl_01_common_009");
        			return;
        		}
				
				$scope.paramList = tota[0].body.totalList;
						
				angular.forEach($scope.paramList, function (row, index, objs) {
					row.UPDATE_FLAG = 'N';
					row.ROWNUM = index + 1;
					//資料最後修改日小於20170825，則不能修改"主管確認"以及"主管備註欄"
					if (row.LASTUPDATE != null) { 
						row.DISABLE_FLAG = 'N';
					} else {
						row.DISABLE_FLAG = 'N';
					}
					
					if ($scope.role_id =='A150' || $scope.role_id == 'ABRF' || $scope.role_id == 'A157' ||
						$scope.role_id =='A161' || $scope.role_id == 'A149' || $scope.role_id =='ABRU' ||
						$scope.role_id =='A308' || $scope.role_id =='A147') {
						// 不能修改自己的資料
						if (row.EMP_ID == projInfoService.getUserID()) {
							row.DISABLE_ROLE_FLAG = 'Y';
						}
					}
				});	
				$scope.totalData = tota[0].body.totalList;
				$scope.outputVO = tota[0].body;		
				return;
			}						
		});
	};
	
	$scope.updateFlag = function (row) {
		row.UPDATE_FLAG = 'Y';			
	}
	
	$scope.save = function () {
		$scope.updateList = [];
		var checkData = true;
		var rowIndex;
		angular.forEach($scope.paramList, function (row, index, objs) {
			if (row.UPDATE_FLAG == 'Y') {
				if (!row.HR_ATTR || 	//初判異常轉法遵部調查
					!row.NOTE2 ||		//資金來源或帳戶關係
					!row.NOTE3 ||		//具體原因或用途
					(!row.NOTE_TYPE || 	// 查證方式需填寫
					 (row.NOTE_TYPE == 'O' && !row.NOTE || // 查證方式若為其它，請補充查證方式
					 ((row.NOTE_TYPE == 'I' || row.NOTE_TYPE == 'A') && !row.RECORD_SEQ && row.RECORD_YN == 'Y') || // 查證方式若為電訪/系統查詢及電訪客戶，請輸入電訪錄音編號
					 ((row.NOTE_TYPE == 'I' || row.NOTE_TYPE == 'A') && row.RECORD_SEQ && row.RECORD_SEQ.length != 12 && row.RECORD_YN == 'Y')) // // 查證方式若為電訪/系統查詢及電訪客戶，電訪錄音編號需滿12碼
					)) {
					rowIndex = row.ROWNUM;
					checkData = false; 
					return;
				};
				
				$scope.updateList.push(row);
			};
		});
		
		if (!checkData) {
			$scope.showErrorMsg("第" + rowIndex + "筆的查證方式、資金來源/帳戶關係、具體原因/用途、初判異常轉法遵部調查，都必須輸入。");
    		return;
		};
		
		$scope.sendRecv("PMS401", "save", "com.systex.jbranch.app.server.fps.pms401.PMS401InputVO", {'list':$scope.updateList}, function(tota, isError) {						
			if (isError) {
        		$scope.showErrorMsgInDialog(tota[0].body.msgData);
        		return;
        	}
			
        	if (tota.length > 0) {
        		$scope.showMsg('儲存成功');
        		$scope.inputVO.currentPageIndex=$scope.outputVO.currentPageIndex || 0;
        		$scope.query();
        	};		
		});
	};
	
	$scope.exportRPT = function() {
		$scope.sendRecv("PMS401", "export", "com.systex.jbranch.app.server.fps.pms401.PMS401OutputVO", $scope.outputVO, function(tota, isError) {						
			if (isError) {
        		$scope.showErrorMsgInDialog(tota[0].body.msgData);		            		
        	}
			if (tota.length > 0) {
        		if (tota[0].body.resultList && tota[0].body.resultList.length == 0) {
        			$scope.showMsg("ehl_01_common_009");
        			return;
        		}
        	};
		});
	};
	
	$scope.passParams = $scope.connector('get','passParams');
	
	$scope.fromCRM181 = function() {
		CRM181toPMS401 = "Yes";
		if($scope.passParams != null){
			$scope.query();
		}
	}
	
	$scope.fromCRM181();
	
	$scope.openDetail = function(row, type) {
    	var txDate = row.TX_DATE;
    	var empID = row.EMP_ID;
    	var dialog = ngDialog.open({
    	    template:'assets/txn/PMS401/PMS401_detail.html',
    	    className:'PMS401',     
    	    controller:['$scope',function($scope) {
    	    	$scope.txDate = txDate;
    	    	$scope.empID = empID;
    	    }]        	 
    	});
    };
    
    $scope.oldRPT = function(page) {
    	$rootScope.menuItemInfo.url = "assets/txn/PMS" + page + "/PMS" + page + ".html";
    };
});