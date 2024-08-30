/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('PRD311_MODIFYController', function($rootScope, $scope, $controller) {
    $controller('BaseController', {$scope: $scope});
    $scope.controllerName = "PRD311_MODIFYController";
    
    // date picker
 	// 參考日期不能 > 系統日期
 	$scope.complete_dateOptions = {
 		maxDate: new Date()
 	};
	$scope.model = {};
	$scope.open = function($event, elementOpened) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.model[elementOpened] = !$scope.model[elementOpened];
	};
    
    $scope.init = function() {
    	$scope.mappingSet = {STATUS: [{LABEL: '未通過', DATA: '0'}, {LABEL: '通過', DATA: '1'}]};
	}
    
    $scope.save = function() {
    	$scope.inputVO.classId = $scope.row.CLASS_ID;	// 課程代碼
    	$scope.inputVO.prodId  = $scope.row.PRD_ID;		// 商品代碼
    	$scope.inputVO.empId   = $scope.row.EMP_ID;		// 員工編碼
    	
    	$scope.sendRecv("PRD311", "save", "com.systex.jbranch.app.server.fps.prd311.PRD311InputVO", $scope.inputVO,
		function(tota, isError) {
			if (!isError) {
				$scope.showSuccessMsg('ehl_01_common_002');		// 修改成功
				$scope.closeThisDialog('successful');
			}
		});
    }
    
    $scope.init();
});