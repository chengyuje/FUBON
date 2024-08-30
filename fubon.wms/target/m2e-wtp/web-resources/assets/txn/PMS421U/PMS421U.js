/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS421UController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS421UController";
		
		// 繼承
		$controller('PMS421Controller', {$scope: $scope});
		
		$scope.dateChangeU = function() {
			if ($scope.inputVO.sCreDate != '') {
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
		}
		
		$scope.initPMS421U = function() {
			$scope.inputVO.uhrmRC = '031';
			$scope.inputVO.uhrmOP = '031A';
			$scope.inputVO.person_role = 'UHRM';
			
			$scope.dateChangeU();
			
			$scope.isMainten = false;
			$scope.sendRecv("PMS401U", "isMainten", "com.systex.jbranch.app.server.fps.pms401u.PMS401UInputVO", {'itemID': 'PMS421U'},
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.resultList[0].MAIN_YN == 'Y') {
							$scope.isMainten = true;
						}
					}
			});
		};
		
		$scope.initPMS421U();
});
