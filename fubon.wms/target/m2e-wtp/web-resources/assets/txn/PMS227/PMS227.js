'use strict';
eSoafApp.controller('PMS227Controller', function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
	$controller('BaseController', {$scope: $scope});
	$controller('PMSRegionController', {$scope: $scope});
	
	$scope.controllerName = "PMS227Controller";	
	
	$scope.init = function(){
		$scope.inputVO = {
			sCreDate  :'',    //初使年月   inputVO必要加  因可視範圍
			dataMonth : '',
			assignType: '1',
			role      : '',
			rptVersion : '',
			REF_ID    : '',
			TP_ID     : ''				
		};
		
		$scope.disableRegionCombo = false;
		$scope.disableAreaCombo = false;
		$scope.disableBranchCombo = false;
		$scope.disableAoCombo = false;
        $scope.paramList = [];
        
        //判斷理專
        var FCvo = {'param_type': 'FUBONSYS.FC_ROLE', 'desc': false};
    	$scope.requestComboBox(FCvo, function(totas) {      	
    		if (totas[totas.length - 1].body.result === 'success') {        		
    			projInfoService.mappingSet['FUBONSYS.FC_ROLE'] = totas[0].body.result;
    			for(var key in projInfoService.mappingSet['FUBONSYS.FC_ROLE']){
    	    		if(projInfoService.mappingSet['FUBONSYS.FC_ROLE'][key].DATA == projInfoService.getRoleID()){
    	    			$scope.FCFlag = 'Y';
    	    		}
    	    	}
    		}
    	})
    	
		var FCHvo = {'param_type': 'FUBONSYS.FCH_ROLE', 'desc': false};
    	$scope.requestComboBox(FCHvo, function(totas) {      	
    		if (totas[totas.length - 1].body.result === 'success') {        		
    			projInfoService.mappingSet['FUBONSYS.FCH_ROLE'] = totas[0].body.result;
    			for(var key in projInfoService.mappingSet['FUBONSYS.FCH_ROLE']){
    	    		if(projInfoService.mappingSet['FUBONSYS.FCH_ROLE'][key].DATA == projInfoService.getRoleID()){
    	    			$scope.FCHFlag = 'Y';
    	    		}
    	    	}
    		}
    	})
    	
    	if($scope.FCFlag == 'Y'||$scope.FCHFlag == 'Y'){
    		$scope.inputVO.empHistFlag = 'Y'
    	};
	};
	
	$scope.init();
	
	$scope.inquireInit = function(){
		$scope.AREA_LIST = [];
		$scope.REGION_LIST = [];
		$scope.BRANCH_LIST = [];
		$scope.AO_LIST = [];
	}
	
	$scope.inquireInit();
	
	$scope.isq = function(){
    	var NowDate = new Date();
		var yr = NowDate.getFullYear();
		var mm = NowDate.getMonth() + 2;
		var strmm = '';
		var xm = '';
		$scope.mappingSet['timeE'] = [];
		for (var i = 0; i < 12; i++) {
			mm = mm - 1;
			if (mm == 0) {
				mm = 12;
				yr = yr - 1;
			}
			if (mm < 10)
				strmm = '0' + mm;
			else
				strmm = mm;
			$scope.mappingSet['timeE'].push({
				LABEL : yr + '/' + strmm,
				DATA : yr + '' + strmm
			});
		} 
	};
	
	$scope.isq();
    
	$scope.dateChange = function(){
		$scope.inquireInit();
		
		if($scope.inputVO.sCreDate == ''){
			$scope.init();
			return;
		}
		
		$scope.inputVO.reportDate = $scope.inputVO.sCreDate;
		$scope.RegionController_getORG($scope.inputVO);
    };
	
    /** 整批上傳功能 **/
	$scope.upload = function(yearmon){
		$scope.inputVO.sTime  = $scope.inputVO.sCreDate;
		
		/*判斷計積年月是否為空*/
		if('' == $scope.inputVO.sTime || null == $scope.inputVO.sTime){
			$scope.showErrorMsg("尚未選擇報表年月");
			return;
		}
		
		var dialog = ngDialog.open({
			template: 'assets/txn/PMS227/PMS227_CUST_UPLOAD.html',
			className: 'PMS227_CUST_UPLOAD',
			showClose: false,
			controller: ['$scope', function($scope) {
				$scope.yearMon = yearmon;
				$scope.uploadName = 'importData';
            }]
		}).closePromise.then(function(data) {
    		if(data.value == 'cancel'){
    		//	 $scope.query();
			}
    	});
	}
	
    /** 無投保客戶上傳 **/
	$scope.uploadCust = function(yearmon){
		$scope.inputVO.sTime  = $scope.inputVO.sCreDate;
		
		/*判斷計積年月是否為空*/
		if('' == $scope.inputVO.sTime || null == $scope.inputVO.sTime){
			$scope.showErrorMsg("尚未選擇報表年月");
			return;
		}
		
		var dialog = ngDialog.open({
			template: 'assets/txn/PMS227/PMS227_CUST.html',
			className: 'PMS227_CUST',
			showClose: false,
			controller: ['$scope', function($scope) {
				$scope.yearMon = yearmon;
				$scope.uploadName = 'importCustData';
            }]
		}).closePromise.then(function(data) {
    		if(data.value == 'cancel'){
    		//	 $scope.query();
			}
    	});
	}
	
    /** 是否實動戶上傳 **/
	$scope.upActualCust = function(yearmon){
		$scope.inputVO.sTime  = $scope.inputVO.sCreDate;
		
		/*判斷計積年月是否為空*/
		if('' == $scope.inputVO.sTime || null == $scope.inputVO.sTime){
			$scope.showErrorMsg("尚未選擇報表年月");
			return;
		}
		
		var dialog = ngDialog.open({
			template: 'assets/txn/PMS227/PMS227_ACTUAL.html',
			className: 'PMS227_ACTUAL',
			showClose: false,
			controller: ['$scope', function($scope) {
				$scope.yearMon = yearmon;
				$scope.uploadName = 'importActualCust';
            }]
		}).closePromise.then(function(data) {
    		if(data.value == 'cancel'){
    		//	 $scope.query();
			}
    	});
	}
	
	/** 匯出 **/
	$scope.exportRPT = function() {
		$scope.sendRecv("PMS227", "export",	"com.systex.jbranch.app.server.fps.pms227.PMS227OutputVO", {'list':$scope.csvList}, function(tota, isError) {
			if (!isError) {
				$scope.resultList = tota[0].body.resultList;
				$scope.outputVO = tota[0].body;
				return;
			}
		});
	};		
	
	/** 查詢 **/
	$scope.inquire = function() {
		if ($scope.parameterTypeEditForm.$invalid) {
    		$scope.showErrorMsg('欄位檢核錯誤:請選擇必填欄位');
    		return;
    	}
		
		$scope.inputVO.sTime  = $scope.inputVO.sCreDate;
		$scope.inputVO.region = $scope.inputVO.region_center_id;
		$scope.inputVO.op     = $scope.inputVO.branch_area_id;
		$scope.inputVO.branch = $scope.inputVO.branch_nbr;
		
		$scope.sendRecv("PMS227", "inquire", "com.systex.jbranch.app.server.fps.pms227.PMS227InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				$scope.paramList = [];
				$scope.csvList = [];
				
				if (tota[0].body.resultList.length == 0) {
					$scope.showMsg("ehl_01_common_009");
        			return;
        		}
				
				$scope.paramList = tota[0].body.resultList;
				$scope.csvList=tota[0].body.csvList;
				$scope.outputVO = tota[0].body;
				
				return;
			} else {
				$scope.showBtn = 'none';
			}	
		});
	};
});