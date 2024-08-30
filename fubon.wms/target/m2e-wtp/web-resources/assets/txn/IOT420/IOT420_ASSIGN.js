/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('IOT420_ASSIGNController', function($rootScope, $scope, $controller) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "IOT420_ASSIGNController";
	
	$scope.init = function() {
		$scope.inputVO.assign_seq = $scope.assign_seq;
//		alert(JSON.stringify($scope.inputVO.assign_seq));
		
		$scope.sendRecv("IOT420", "getEmpList", "com.systex.jbranch.app.server.fps.iot420.IOT420InputVO", $scope.inputVO,
		function(tota, isError) {
			if (!isError) {
				$scope.mappingSet['CALL_PERSON'] = [];
				if (tota[0].body.resultList.length > 0) {
					angular.forEach(tota[0].body.resultList, function(row) {
						$scope.mappingSet['CALL_PERSON'].push({LABEL: row.EMP_NAME, DATA: row.EMP_ID});
					});
//					alert(JSON.stringify($scope.mappingSet['CALL_PERSON']));
				} else {
					$scope.showMsg("查無電訪人員，請洽系統管理員");
					return;
				}
			}
		});
	}
	
	$scope.assign = function() {
		if ($scope.inputVO.call_person == undefined || $scope.inputVO.call_person == '') {
			$scope.showErrorMsg("請選擇電訪人員");
			return;
		}
		$scope.sendRecv("IOT420", "assign", "com.systex.jbranch.app.server.fps.iot420.IOT420InputVO", $scope.inputVO,
		function(tota, isError) {
			if (!isError) {
				$scope.showSuccessMsg('派件成功');
				$scope.closeThisDialog('successful');
			}
		});
	}
	
	$scope.init();	
});