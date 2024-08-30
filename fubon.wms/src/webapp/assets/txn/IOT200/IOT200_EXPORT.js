'use strict';
eSoafApp.controller('IOT200_EXPORTController',
	function($rootScope, $scope, $controller, $confirm, $filter, socketService, ngDialog, projInfoService,sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "IOT200_EXPORTController";
		
		$scope.inputVO = {};
		
		$scope.uploadFinshed = function(name, rname) {
	    	$scope.inputVO.fileName = name;
        	$scope.inputVO.realfileName = rname;
        };
		
		$scope.insertExpt = function(set_name) {
			if($scope.parameterTypeEditForm.$invalid){
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
			
			$scope.sendRecv("IOT200", "uploadFile", "com.systex.jbranch.app.server.fps.iot200.IOT200InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.showSuccessMsg(tota[0].body.successMsg);
                		$scope.closeThisDialog('successful');
					}
			});
		};
		

		
		
});