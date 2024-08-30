/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CUS130_REPORT3Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CUS130_REPORT3Controller";
		
		$scope.regionQuery = [];
		angular.forEach(projInfoService.getAvailRegion(), function(row, index, objs){
			$scope.regionQuery.push({LABEL: row.REGION_CENTER_NAME, DATA: row.REGION_CENTER_ID});
		});
		$scope.areaQuery = [];
		angular.forEach(projInfoService.getAvailArea(), function(row, index, objs){
			$scope.areaQuery.push({LABEL: row.BRANCH_AREA_NAME, DATA: row.BRANCH_AREA_ID});
		});
		$scope.branchQuery = [];
		angular.forEach(projInfoService.getAvailBranch(), function(row, index, objs){
			$scope.branchQuery.push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
		});
		
		$scope.totalList = [];
		$scope.dataList = [];
		$scope.sendRecv("CUS130", "getCONTENT", "com.systex.jbranch.app.server.fps.cus130.CUS130InputVO", {'seq': $scope.seq},
				function(totas, isError) {
                	if (isError) {
                		$scope.showErrorMsg(totas[0].body.msgData);
                	}
                	if (totas.length > 0) {
//                		$scope.totalList = totas[0].body.resultList;
//                		$scope.outputVO = {'data':$scope.totalList};
                		
                		$scope.roleName = totas[0].body.resultList2[0];
                		
                		$scope.branchList = totas[0].body.resultList3;
                		$scope.branchVO = {'data':$scope.branchList};
                		
                		$scope.empList = totas[0].body.resultList;
                		$scope.empVO = {'data':$scope.empList};
                	};
				}
		);
		
});
