/*

 */
'use strict';
eSoafApp.controller('CAM211_1Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CAM211_1Controller";
		
		$scope.input = $scope.input || {};
		$scope.row = $scope.row || {};	
		
		$scope.totalList = 0;		
		$scope.totalClose = 0;
		
		$scope.inquire = function(){
			$scope.sendRecv("CAM211", "queryAoData_1", "com.systex.jbranch.app.server.fps.cam211.CAM211InputVO", {'empId': $scope.row.EMP_ID},
	        		function(tota, isError) {
	        			if (!isError) {
	        				if(tota[0].body.resultList.length == 0) {
	        					$scope.showMsg("ehl_01_common_009");
	        					$("#toralTr").hide();
	        	                return;
	        	            }
	        				$scope.AoParamList = tota[0].body.resultList;
	        				$scope.outputVO = tota[0].body;
	        				for(var i=0; i<$scope.AoParamList.length; i++) {
	        	        		$scope.totalList += $scope.AoParamList[i].TOTAL_COUNTS;
	        	        		$scope.totalClose += $scope.AoParamList[i].CLOSE_COUNTS;
	        	        	}
	        				return;
	        			}
	        	});
		}
		
		// 初始分頁資訊
        $scope.inquireInit = function(){
        	$scope.paramList = [];
        };
        $scope.inquireInit();
        $scope.inquire();
});
