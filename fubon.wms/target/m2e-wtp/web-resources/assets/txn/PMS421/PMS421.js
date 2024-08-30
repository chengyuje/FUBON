
'use strict';
eSoafApp.controller('PMS421Controller', function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService) {
	$controller('BaseController', {$scope: $scope});
	$controller('PMSRegionController', {$scope: $scope});
	$scope.controllerName = "PMS421Controller";
    
    /*** 可示範圍  JACKY共用版  START ***/
    //選取月份下拉選單 --> 重新設定可視範圍
    $scope.dateChange = function(){
    	//設定回傳時間
    	if($scope.inputVO.sCreDate != ''){
    		$scope.inputVO.reportDate = $scope.inputVO.sCreDate;
    		$scope.RegionController_getORG($scope.inputVO);
    	}
    };
    
    $scope.EBILL_COMFIRM_SOU  = [{'LABEL':'每季', 'DATA': '1'}, {'LABEL':'輪調加強', 'DATA': '2'}];
    $scope.EBILL_CONTENT_FLAG = [{'LABEL':'相符', 'DATA': 'Y'}, {'LABEL':'不相符', 'DATA': 'N'}];
    
	var rp = "RC";
	if(sysInfoService.getRoleName().substring(0, 2) == 'FC')
		rp = "AO";
	if(sysInfoService.getRoleID() == 'A161')
		rp = "BR";
	
	$scope.open = function($event, index) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope['opened'+index] = true;
	};		
	
	// date picker
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
	// date picker end
	
	/*** 可示範圍  JACKY共用版  END***/			
	
	$scope.init = function(){
		/** InputVO 初始，預設日期為系統日 **/
        let defaultDate = new Date();
        $scope.inputVO = {
        		sCreDate 	: defaultDate,
        		endDate		: defaultDate,
				dataMonth	: '',					
				rc_id		: '',
				op_id		: '',
				br_id		: '',
				emp_id		: '',
				memLoginFlag: String(sysInfoService.getMemLoginFlag()),
                /** 連動組織會用到的參數 **/
                region_center_id	: undefined,
                branch_area_id		: undefined,
                branch_nbr			: undefined,
                reportDate			: $filter('date')(defaultDate, 'yyyyMM')
        };
        $scope.dateChange();
		
		$scope.curDate = new Date();
		$scope.outputVO={};
		$scope.paramList = [];
		$scope.inputVO.loginRole = sysInfoService.getRoleID();
		//把鎖定清掉
		$scope.disableRegionCombo = false;
		$scope.disableAreaCombo = false;
		$scope.disableBranchCombo = false;
		$scope.disableAoCombo = false;
	};
	$scope.init();
	
	$scope.inquireInit = function(){
		$scope.paramList = [];
		$scope.outputVO = {};
	}
	$scope.inquireInit();
	
	$scope.query = function(){
		if ($scope.inputVO.sCreDate == '' || 
			$scope.inputVO.sCreDate == undefined ||
			$scope.inputVO.endDate == '' || 
			$scope.inputVO.endDate == undefined ){
			$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位(資料統計月份)');
    		return;
    	}
		$scope.sendRecv("PMS421", "queryData", "com.systex.jbranch.app.server.fps.pms421.PMS421InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.resultList.length == 0) {
							$scope.showMsg("ehl_01_common_009");
							$scope.paramList=[];
							return;
                		}
						$scope.paramList = tota[0].body.resultList;
						$scope.outputVO = tota[0].body;
						return;
					}						
		});
	};
	
	$scope.updateFlag = function (row){
		row.UPDATE_FLAG = 'Y';			
	}

	$scope.save = function(){
		$scope.updateList = [];
		var checkData = true;
		angular.forEach($scope.paramList, function(row, index, objs){
			if (row.UPDATE_FLAG == 'Y') {
				if (!row.UPDATE_REMARK || 
					!row.EBILL_CONTENT_FLAG || 
					(row.RECORD_SEQ && row.RECORD_SEQ.length != 12 && row.RECORD_YN == 'Y')
					){
					checkData = false; 
					return;
				};
				$scope.updateList.push(row);
			};
		});
		
		if(!checkData){
			$scope.showErrorMsg("聯繫內容及對帳單內容是否符合欄位，都必須輸入。");
    		return;
		};
		
		$scope.sendRecv("PMS421", "save", "com.systex.jbranch.app.server.fps.pms421.PMS421InputVO",{list: $scope.updateList},
			function(tota, isError) {
            if (!isError) {
                $scope.showSuccessMsg('ehl_01_common_025');
                $scope.query();
            }
        });
    };
    
	// 連至客戶首頁
    $scope.custDTL = function(row) {
    	$scope.custVO = {
				CUST_ID   : row.CUST_ID,
				CUST_NAME : row.CUST_NAME	
		}
		$scope.connector('set','CRM_CUSTVO',$scope.custVO);
    	
    	var path = "assets/txn/CRM610/CRM610_MAIN.html";
		var set = $scope.connector("set","CRM610URL",path);
		var dialog = ngDialog.open({
			template: 'assets/txn/CRM610/CRM610.html',
			className: 'CRM610',
			showClose: false
		});
	};
	
	//確認如果是連動來的，請自動查詢待處理事項。
	if('Y' == $scope.connector('get','LinkFlag_PMS421')){
		var curdate = new Date();
		var edate = new Date();
		var sDate = new Date(curdate.setMonth(curdate.getMonth()-2));
		
		$scope.inputVO.from181 = "Y";
		$scope.inputVO.sCreDate = sDate;
		$scope.inputVO.endDate = edate;
		$scope.inputVO.loginRole = sysInfoService.getRoleID();
		$scope.dateChange();

		$scope.query();
		
    }
	
	$scope.upload = function(){
		var dialog = ngDialog.open({
			template: 'assets/txn/PMS421/PMS421_UPLOAD.html',
			className: 'PMS421_UPLOAD',
			showClose: false
		});
		dialog.closePromise.then(function(data) {
			if(data.value.success == 'success'){
			}
		});
	}
	
	//匯出
	$scope.exportData = function(){
		$scope.sendRecv("PMS421","export","com.systex.jbranch.app.server.fps.pms421.PMS421InputVO", {'list': $scope.paramList}, function(tota, isError) {
			if (isError) {							
				return;            		
        	}
		});			
	};
});
