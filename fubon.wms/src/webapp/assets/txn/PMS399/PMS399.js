'use strict';
eSoafApp.controller('PMS399Controller', function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "PMS399Controller";

	$controller('PMSRegionController', {$scope: $scope});

    /*** 可視範圍  JACKY共用版  START ***/
    //選取月份下拉選單 --> 重新設定可視範圍
    $scope.dateChange = function(){
    	//設定回傳時間
    	$scope.inputVO.reportDate = $filter('date')($scope.inputVO.eCreDate, 'yyyyMMdd');
    	//可視範圍  觸發 
    	$scope.RegionController_getORG($scope.inputVO);
    };

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
			sCreDate         : new Date("2024-08-31"),
			eCreDate         : new Date("2024-08-31"),
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
	};
	
	$scope.init();
	
	$scope.inquireInit = function(){
		$scope.initLimit();
		$scope.paramList = [];
		$scope.updateList = [];
		$scope.outputVO={totalList:[]};
	}
	
	$scope.inquireInit();	
	
	
	var CRM181toPMS399 = "NO";
	
	//資料查詢
	$scope.query = function() {			
		if (CRM181toPMS399 != "Yes") {				
			if($scope.REGION_LIST.length == 0){				
				$scope.inputVO.ao_code = sysInfoService.getUserID();				
			}
		}
		CRM181toPMS399 = "NO";
		$scope.rptDate = $filter('date')($scope.inputVO.sCreDate, 'yyyy/MM/dd')+ '~' +$filter('date')($scope.inputVO.eCreDate, 'yyyy/MM/dd'); 			
		$scope.sendRecv("PMS399", "queryData", "com.systex.jbranch.app.server.fps.pms399.PMS399InputVO", $scope.inputVO, function(tota, isError) {
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
						$scope.dateLimit = row.LASTUPDATE.substring(0, 4) + row.LASTUPDATE.substring(5, 7) + row.LASTUPDATE.substring(8, 10);
						if ($scope.dateLimit < "20170825") {
							row.DISABLE_FLAG = 'Y';
						} else {
							row.DISABLE_FLAG = 'N';
						};
					} else {
						row.DISABLE_FLAG = 'N';
					}
					
					if (row.EMP_ID != null) {
						row.IDS = row.ID.substring(0, 4) + "****" + row.ID.substring(8, 10);    //隱藏身分證 四碼
					}
					
					if (row.NAME == '無資料') {
						row.IDS = '';    //無資料則不顯示
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
				if (!row.NOTE || !row.NOTE2 || !row.NOTE3 || !row.WARNING_YN) {
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
		
		$scope.sendRecv("PMS399", "save", "com.systex.jbranch.app.server.fps.pms399.PMS399InputVO", {'list':$scope.updateList}, function(tota, isError) {						
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
		angular.forEach($scope.outputVO.totalList, function (row, index, objs) {
			if (row.ID != ' ') {
				row.ID = row.ID.substring(0, 4) + "****" + row.ID.substring(8, 10);   //隱藏身分證 四碼
			}
		});	
		$scope.sendRecv("PMS399", "export", "com.systex.jbranch.app.server.fps.pms399.PMS399OutputVO", $scope.outputVO, function(tota, isError) {						
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
	
	$scope.bn = function(url) {
    	var sdate = $scope.inputVO.sCreDate;
    	var edate = $scope.inputVO.eCreDate;
    	var dialog = ngDialog.open({
    	    template:'assets/txn/PMS399/PMS399_detail.html',
    	    className:'PMS399',     
    	    controller:['$scope',function($scope) {
    	    	$scope.sCreDate = sdate;
    	    	$scope.eCreDate = edate;
    	    	$scope.cust_id = url;
    	    }]        	 
    	});
    };
    
	$scope.newRPT = function() {
		$rootScope.menuItemInfo.url = "assets/txn/PMS401/PMS401.html";
    };
});