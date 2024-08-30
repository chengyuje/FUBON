/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CAM140_SELECTController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CAM140_SELECTController";
		
		$scope.inputVO = {};
		
		$scope.sendRecv("CAM140", "getDoc", "com.systex.jbranch.app.server.fps.cam140.CAM140InputVO", {},
				function(tota, isError) {
					if (!isError) {
						$scope.roleList = tota[0].body.resultList;
						$scope.outputVO = tota[0].body;
						return;
					}
		});
		
		$scope.checkrow = function() {
        	if ($scope.clickAll) {
        		angular.forEach($scope.data, function(row) {
        			row.SELECTED = true;
    			});
        	} else {
        		angular.forEach($scope.data, function(row) {
        			row.SELECTED = false;
    			});
        	}
        };
		
		$scope.save = function() {
			// get select
			var ans = $scope.roleList.filter(function(obj) {
	    		return (obj.SELECTED == true);
	    	});
        	if(ans.length == 0) {
        		return;
        	}
        	var total = $scope.count + ans.length;
        	if(total > 5){
	    		$scope.showErrorMsg('欄位檢核錯誤:最多選擇五筆');
        		return;
        	}
			$scope.closeThisDialog(ans);
        };
        
		
		
});