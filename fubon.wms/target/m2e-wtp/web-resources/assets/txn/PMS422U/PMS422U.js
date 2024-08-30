/**================================================================================================
 @Description 分行人員與客戶資金往來異常報表
 =================================================================================================**/
'use strict';
eSoafApp.controller('PMS422UController', function ($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "PMS422UController";
    
    // 繼承
	$controller('PMS422Controller', {$scope: $scope});
    
	$scope.dateChangeU = function() {
		if ($scope.inputVO.start != '') {
			$scope.sendRecv("PMS416U", "getEmpNameByYYYYMMDD", "com.systex.jbranch.app.server.fps.pms416u.PMS416UInputVO", {'sCreDate': $scope.inputVO.start}, function(tota, isError) {
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
	}
	
	$scope.uhrmRC = '031';
	$scope.uhrmOP = '031A';
	
	$scope.initPMS422U = function() {
		$scope.inputVO.uhrmRC = '031';
		$scope.inputVO.uhrmOP = '031A';
		
		$scope.dateChangeU();
	};
	
	$scope.initPMS422U();
});
