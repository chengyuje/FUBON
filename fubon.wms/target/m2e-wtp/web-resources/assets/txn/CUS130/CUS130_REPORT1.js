/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CUS130_REPORT1Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CUS130_REPORT1Controller";
		
		var exist = $scope.connector('get','CUS130_REPORT1');
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
						if(exist.length > 0) {
							var check = exist.map(function(e) { return e.ROLE_ID; });
							angular.forEach($scope.totalList, function(row, index){
								row.CHECK = check.indexOf(row.ROLE_ID) > -1;
							});
						}
						return;
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
			var ans = $scope.totalList.filter(function(obj){
	    		return (obj.CHECK == true);
	    	});
			if(ans.length == 0){
	    		$scope.showErrorMsg('欄位檢核錯誤:至少選擇一個');
        		return;
        	}
			$scope.connector('set','CUS130_REPORT1', ans);
    		$scope.closeThisDialog('report1Next');
		};
		
});
