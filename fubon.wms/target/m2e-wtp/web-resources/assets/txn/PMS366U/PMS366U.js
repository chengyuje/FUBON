'use strict';
eSoafApp.controller('PMS366UController', function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService, getParameter, $timeout) {
	
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "PMS366UController";
	
	// 繼承共用的組織連動選單
	$controller('PMSRegionController', {$scope: $scope});
	
	// 繼承
	$controller('PMS366Controller', {$scope: $scope});
	
	//選取月份下拉選單 --> 重新設定可視範圍
    $scope.dateChangeU = function(){
    	if ($scope.inputVO.sCreDate != '') {
    		$scope.inputVO.reportDate = $scope.inputVO.sCreDate;
    		$scope.inputVO.dataMonth = $scope.inputVO.sCreDate; 
    		$scope.inputVO.dataMon = $scope.inputVO.sCreDate; 
    		
        	$scope.sendRecv("PMS416U", "getEmpNameByYYYYMMDD", "com.systex.jbranch.app.server.fps.pms416u.PMS416UInputVO", $scope.inputVO, function(tota, isError) {
				if (!isError) {
					$scope.empList = tota[0].body.UHRMList;
					$scope.mappingSet['EmpName'] = [];
					
					for(var i = 0; i < $scope.empList.length ; i++){
						$scope.mappingSet['EmpName'].push({
							LABEL : $scope.empList[i].AO_CODE + '-'+ $scope.empList[i].EMP_NAME,
							DATA  : $scope.empList[i].AO_CODE
						})
					}
				}						
        	});
    	} else {
    		$scope.mappingSet['EmpName'] = [];
    	}

    }; 
    
	$scope.initPMS366U = function() {
		$scope.sendRecv("PMS401U", "isMainten", "com.systex.jbranch.app.server.fps.pms401u.PMS401UInputVO", {'itemID': 'PMS366U'}, function(tota, isError) {
			if (!isError) {
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
	
	$scope.initPMS366U();
});
