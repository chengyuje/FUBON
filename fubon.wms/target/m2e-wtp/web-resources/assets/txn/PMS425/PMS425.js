'use strict';
eSoafApp.controller('PMS425Controller', function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService, getParameter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "PMS425Controller";;
	
	// 繼承共用的組織連動選單
	$controller('PMSRegionController', {$scope: $scope});
	
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
			exportList          : []
    	};
		
		var min_mon2 = new Date();
		min_mon2.setHours(0, 0, 0, 0);
		$scope.inputVO.eDate = min_mon2;
		$scope.limitDate();
		
		$scope.dateChange();
		$scope.queryInit();
	};
	
	$scope.init();
	
	//資料查詢
	$scope.query = function() {	
		if ($scope.inputVO.sDate == undefined || $scope.inputVO.sDate == null || $scope.inputVO.sDate == '' || 
			$scope.inputVO.eDate == undefined || $scope.inputVO.eDate == undefined || $scope.inputVO.eDate == undefined) {
    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位(資料統計月份)');
    		return;
    	}
		
		$scope.sendRecv("PMS425", "query", "com.systex.jbranch.app.server.fps.pms425.PMS425InputVO", $scope.inputVO, function(tota, isError) {
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
		
		$scope.sendRecv("PMS425", "export", "com.systex.jbranch.app.server.fps.pms425.PMS425InputVO", $scope.inputVO, function(tota, isError) {						
			if (isError) {
        		$scope.showErrorMsgInDialog(tota[0].body.msgData);		            		
        	}
		});
	};
	
});