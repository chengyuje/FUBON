'use strict';
eSoafApp.controller('PMS496UController', function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService, getParameter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "PMS496UController";

	// 繼承
	$controller('PMS496Controller', {$scope: $scope});
	
	//選取月份下拉選單 --> 重新設定可視範圍
	$scope.mappingSet['EmpName'] = [];
    $scope.dateChangeU = function(){
    	if ($scope.inputVO.importSDate != '') {
    		$scope.inputVO.reportDate = $scope.inputVO.importSDate;
    		$scope.inputVO.dataMonth = $scope.inputVO.importSDate; 
    		$scope.inputVO.dataMon = $scope.inputVO.importSDate; 
    		
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
    
    $scope.sendRecv("PMS401U", "isMainten", "com.systex.jbranch.app.server.fps.pms401u.PMS401UInputVO", {'itemID': 'PMS496U'}, function(tota, isError) {
		if (!isError) {
			$scope.chkMaintenanceU = tota[0].body.isMaintenancePRI == 'Y' ? true : false;
		}						
	});
});
