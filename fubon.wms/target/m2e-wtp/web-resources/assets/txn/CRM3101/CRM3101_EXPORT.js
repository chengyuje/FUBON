'use strict';
eSoafApp.controller('CRM3101_EXPORTController',
	function($rootScope, $scope, $controller, $confirm, $filter, socketService, ngDialog, projInfoService,sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM3101_EXPORTController";
		
		$scope.inputVO = {};
		
		$scope.sendRecv("CRM3101", "getAllPRJ", "com.systex.jbranch.app.server.fps.crm3101.CRM3101InputVO", {'comboflag': 'Y'},
			function(tota, isError) {
			   if(!isError) {
				   $scope.PROJNAME = [];
				   angular.forEach(tota[0].body.allPRJ, function(row) {
					   $scope.PROJNAME.push({LABEL: row.PRJ_NAME, DATA: row.PRJ_ID});
				   });
			   }
		});
		
		$scope.uploadFinshed = function(name, rname) {
	    	$scope.inputVO.fileName = name;
        	$scope.inputVO.realfileName = rname;
        };
		
		$scope.insertExpt = function(set_name) {
			if($scope.parameterTypeEditForm.$invalid){
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
			$scope.sendRecv("CRM3101", "upload", "com.systex.jbranch.app.server.fps.crm3101.CRM3101InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.showSuccessMsg('ehl_01_common_004');
                		$scope.closeThisDialog('successful');
					}
			});
		};
		
		$scope.getExample = function() {
			$scope.sendRecv("CRM3101", "getExample", "com.systex.jbranch.app.server.fps.crm3101.CRM3101InputVO", {},
				function(tota, isError) {
					if (!isError) {
						
					}
			});
		};
		
		
});