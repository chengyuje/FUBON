/*

 */
'use strict';
eSoafApp.controller('CAM211_2_Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CAM211_2_Controller";
		
		$scope.totalList = 0;		
		$scope.totalClose = 0;
		
		$scope.inquire = function(){
			$scope.sendRecv("CAM211", "queryAoData_2", "com.systex.jbranch.app.server.fps.cam211.CAM211InputVO", {'empId': $scope.row.EMP_ID},
	        		function(tota, isError) {
	        			if (!isError) {
	        				if(tota[0].body.resultList.length == 0) {
	        					$scope.showMsg("ehl_01_common_009");
	        					$("#toralTr").hide();
	        	                return;
	        	            }
	        				
	        				$scope.AoParamList2 = tota[0].body.resultList;
	        				$scope.outputVO = tota[0].body;
	        				for(var i=0; i<$scope.AoParamList2.length; i++){
	        	        		$scope.totalList += $scope.AoParamList2[i].TOTAL_COUNTS;
	        	        		$scope.totalClose += $scope.AoParamList2[i].CLOSE_COUNTS;
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
