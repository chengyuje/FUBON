'use strict';
eSoafApp.controller('PMS417Controller',	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService) {

	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "PMS417Controller";

	//組織連動繼承
	$controller('PMSRegionController', {$scope: $scope});
	$controller('RegionController', {$scope: $scope});
	
	$scope.priID = sysInfoService.getPriID();
	
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
    		$scope.RegionController_getORG($scope.inputVO);
    	} else {   
    		$scope.inputVO.importSDate = '201701';
    		$scope.inputVO.reportDate = $scope.inputVO.importSDate;
    		$scope.RegionController_getORG($scope.inputVO);        
    		$scope.inputVO.importSDate = '';
    	} 
    	
    	$scope.inputVO.dataMonth = $scope.inputVO.importSDate; 
    }; 
    
	$scope.init = function(){ 
		$scope.inputVO = {					
			importSDate     : '',	
			importEDate     : '',		
			rc_id           : '',
			op_id           : '' ,	
			region_center_id: '',
			branch_nbr      : '' ,
			branch_area_id  : '',
			aoCode          : '',
			exportList      : [],
			resultList      : [], 
			memLoginFlag    : String(sysInfoService.getMemLoginFlag())
    	}; 
    	console.log("$scope.inputVO.memLoginFlag", $scope.inputVO.memLoginFlag);
		$scope.resultList = [];
		$scope.curDate = new Date();
		$scope.dateChange();
	    
		$scope.disableRegionCombo = false;
		$scope.disableAreaCombo = false;
		$scope.disableBranchCombo = false;
		$scope.disableAoCombo = false;

		$scope.limitDate();
		
        //組織連動
        $scope.region = ['N', $scope.inputVO, "region_center_id", "REGION_LIST", "branch_area_id", "AREA_LIST", "branch_nbr", "BRANCH_LIST", "ao_code", "AO_LIST", "emp_id", "EMP_LIST"];
	
        $scope.sendRecv("PMS401U", "isMainten", "com.systex.jbranch.app.server.fps.pms401u.PMS401UInputVO", {'itemID': 'PMS417'}, function(tota, isError) {
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
	
	$scope.inquireInit = function(){
		$scope.initLimit();
		$scope.resultList = [];
		$scope.outputVO = [];
		$scope.originalList = [];
		$scope.exportList = [];
	}
	$scope.inquireInit();
	
	$scope.query = function(){
		if($scope.inputVO.sCreDate=='') {
    		$scope.showErrorMsgInDialog('欄位檢核錯誤:日期必要輸入欄位');
    		return;
    	}
		
		$scope.inputVO.dataMon = angular.copy($scope.inputVO.sCreDate);
		
		$scope.sendRecv("PMS417", "queryData", "com.systex.jbranch.app.server.fps.pms417.PMS417InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				if(tota[0].body.resultList.length == 0) {
					$scope.resultList =[];
					$scope.showMsg("ehl_01_common_009");
        			return;
        		}
				
				angular.forEach(tota[0].body.resultList, function(row) {
		    		if(row.REF_DATE == null) { 
		    			row.PROFIT_RATE = null;
		    		}
				});
				
				$scope.exportList =	tota[0].body.resultList;
				$scope.resultList = tota[0].body.resultList;
				$scope.outputVO = tota[0].body;
			}						
		});
	};
	
	$scope.exportRPT = function(){			
		angular.copy($scope.exportList,$scope.inputVO.exportList);
		$scope.sendRecv("PMS417", "export", "com.systex.jbranch.app.server.fps.pms417.PMS417InputVO", $scope.inputVO, function(tota, isError) {						
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
});
