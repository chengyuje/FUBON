/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SQM320UController',
	function($rootScope,$scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService ,getParameter, $timeout) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SQM320UController";
		
		// 繼承
		$controller('SQM320Controller', {$scope: $scope});
		
		//選取月份下拉選單 --> 重新設定可視範圍
        $scope.dateChangeU = function(){
        	if ($scope.inputVO.yearQtr != '') {
        		$scope.inputVO.dataMonth = $scope.inputVO.yearQtr; 
        		$scope.inputVO.dataMon = $scope.inputVO.yearQtr; 
        		
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
        
		$scope.initSQM320U = function() {
			$scope.inputVO.uhrmRC = '031';
			$scope.inputVO.uhrmOP = '031A';
			
			$scope.dateChangeU();
		};
		
		$scope.initSQM320U();
		
});
