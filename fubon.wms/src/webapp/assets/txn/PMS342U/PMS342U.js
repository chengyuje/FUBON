
'use strict';
eSoafApp.controller('PMS342UController', function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
	
	$controller('BaseController', {$scope: $scope});
	
	$scope.controllerName = "PMS342UController";
	
	// 繼承共用的組織連動選單
	$controller('PMSRegionController', {$scope: $scope});
	
	// 繼承
	$controller('PMS342Controller', {$scope: $scope});
	
	$scope.mappingSet['EmpName'] = [];
    $scope.dateChangeU = function(){
    	if ($scope.inputVO.sCreDate != '') {
    		$scope.inputVO.reportDate = $scope.inputVO.sCreDate; 
    		
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
    
	$scope.initPMS_U = function() {
		$scope.isMainten = false;
		$scope.sendRecv("PMS401U", "isMainten", "com.systex.jbranch.app.server.fps.pms401u.PMS401UInputVO", {'itemID': 'PMS342U'},function(tota, isError) {
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
	};
	
	$scope.initPMS_U();
});
