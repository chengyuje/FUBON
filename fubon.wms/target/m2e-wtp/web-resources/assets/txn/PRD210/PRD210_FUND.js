'use strict';
eSoafApp.controller('PRD210_FUNDController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD210_FUNDController";
		
		$scope.checkVO = {
			clickAll: false
		};
		$scope.listBase= [];
		
		$scope.sendRecv("PRD210", "getFund", "com.systex.jbranch.app.server.fps.prd210.PRD210InputVO", 
			{'prd_id': $scope.dataRow.prd_id, 'stock_bond_type': $scope.stock_bond_type},
			function(tota, isError) {
				if (!isError) {
					if(tota[0].body.resultList.length == 0) {
						$scope.showMsg("ehl_01_common_009");
            			return;
            		}
					$scope.listBase = tota[0].body.resultList;
				}
		});
		
		$scope.checkrow = function() {
        	if ($scope.checkVO.clickAll) {
        		angular.forEach($scope.listBase, function(row) {
        			row.SELECTED = true;
    			});
        	} else {
        		angular.forEach($scope.listBase, function(row) {
        			row.SELECTED = false;
    			});
        	}
        };
        
        $scope.save = function() {
        	var data = $scope.listBase.filter(function(row) {
				return (row.SELECTED == true);
	    	});
        	if(data.length > 3) {
        		$scope.showErrorMsg('最多選擇三筆');
        		return;
        	}
        	$scope.closeThisDialog(data);
        };
		
		
		
});