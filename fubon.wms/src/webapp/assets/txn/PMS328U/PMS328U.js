
'use strict';
eSoafApp.controller('PMS328UController', function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService,sysInfoService,$filter) {
	
	$controller('BaseController', {$scope: $scope});
	
	// 繼承
	$controller('PMS328Controller', {$scope: $scope});
	
	$scope.controllerName = "PMS328Controller";
	
	// 繼承共用的組織連動選單
	$controller('PMSRegionController', {$scope: $scope});
	
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
	
	$scope.mappingSet['EmpName'] = [];
    $scope.dateChangeU = function(){
    	if ($scope.inputVO.importSDate != '') {
    		$scope.inputVO.reportDate = $scope.inputVO.importSDate;
    		$scope.inputVO.dataMonth = $scope.inputVO.importSDate; 
    		$scope.inputVO.dataMon = $scope.inputVO.importSDate; 
    		
        	$scope.sendRecv("PMS416U", "getEmpNameByYMMD_NOCODE", "com.systex.jbranch.app.server.fps.pms416u.PMS416UInputVO", $scope.inputVO, function(tota, isError) {
				if (!isError) {
					$scope.empList = tota[0].body.UHRMList;
					$scope.mappingSet['EmpName'] = [];
					
					for(var i = 0; i < $scope.empList.length ; i++){
						$scope.mappingSet['EmpName'].push({
							LABEL : $scope.empList[i].EMP_NAME,
							DATA  : $scope.empList[i].EMP_ID
						});
					}
				}						
        	});
    	} else {
    		$scope.mappingSet['EmpName'] = [];
    	}
    };
    
	$scope.initPMS_U = function() {
		$scope.isMainten = false;
		$scope.sendRecv("PMS401U", "isMainten", "com.systex.jbranch.app.server.fps.pms401u.PMS401UInputVO", {'itemID': 'PMS328U'},function(tota, isError) {
			if (!isError) {
				if(tota[0].body.resultList[0].MAIN_YN == 'Y') {
					$scope.isMainten = true;
				}
				
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
		
		$scope.inputVO.NOT_EXIST_UHRM = "U";
	};
	
	$scope.initPMS_U();
	
    $scope.inquireU = function() {
    	if($scope.parameterTypeEditForm.region_center_id.$invalid) {
    		$scope.showErrorMsg('欄位檢核錯誤:業務處為必填欄位');
    		return;
    	}
    	
    	//年月檢核
    	if ($scope.inputVO.importSDate == '' || $scope.inputVO.importEDate == '') {
    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位(資料月份)起訖!!');
    		return;
    	}

		$scope.sendRecv("PMS328", "queryData", "com.systex.jbranch.app.server.fps.pms328.PMS328InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				
				if(tota[0].body.resultList.length == 0) {
					$scope.paramList2 = [];
					$scope.param = [];
					$scope.paramList = [];
					
					$scope.showMsg("ehl_01_common_009");
					
        			return;
        		}
				
				$scope.paramList2 = tota[0].body.resultList;
				$scope.totalList = tota[0].body.totalList;
				$scope.param = tota[0].body.DATA;
				$scope.csvList = tota[0].body.csvList;
				$scope.outputVO = tota[0].body;	
				$scope.cp = tota[0].body.currentPageIndex + 1;
				$scope.tp = tota[0].body.totalPage;
				
				$scope.totalRecord = tota[0].body.totalRecord;
				$scope.rp = $scope.totalRecord % 20;
			}
		});
		
		return;
	};
});
