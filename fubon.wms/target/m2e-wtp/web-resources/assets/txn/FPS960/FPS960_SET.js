'use strict';
eSoafApp.controller('FPS960_SETController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "FPS960_SETController";
		
		$scope.paramList = [];
		$scope.totalList = [];
		$scope.sendRecv("CUS130", "getRole", "com.systex.jbranch.app.server.fps.cus130.CUS130InputVO", {},
			function(tota, isError) {
				if (!isError) {
					if(tota[0].body.resultList.length == 0) {
						$scope.showMsg("ehl_01_common_009");
            			return;
            		}
					$scope.totalList = tota[0].body.resultList;
					$scope.outputVO = tota[0].body;
				}
		});
		
		// click all
		$scope.$watchCollection('paramList', function(newNames, oldNames) {
        	$scope.clickAll = false;
        });
		$scope.checkrow = function() {
        	if ($scope.clickAll) {
        		angular.forEach($scope.paramList, function(row){
    				row.CHECK = true;
    			});
        	} else {
        		angular.forEach($scope.paramList, function(row){
    				row.CHECK = false;
    			});
        	}
        };
		
		$scope.next = function () {
			var ans = $scope.totalList.filter(function(obj) {
	    		return (obj.CHECK == true);
	    	});
			if(ans.length == 0) {
	    		$scope.showErrorMsg('欄位檢核錯誤:至少選擇一個');
        		return;
        	}
			$scope.closeThisDialog(ans);
		};
		
});