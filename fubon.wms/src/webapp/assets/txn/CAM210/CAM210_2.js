/*

 */
'use strict';
eSoafApp.controller('CAM210_2_Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CAM210_2_Controller";
		
		$scope.totalList = 0;		
		$scope.totalClose = 0;
		
		getParameter.XML(['CAM.LEAD_TYPE'], function(tota) {
			if(tota){
				$scope.mappingSet['CAM.LEAD_TYPE'] = tota.data[tota.key.indexOf('CAM.LEAD_TYPE')];
			}
		});
		
		$scope.inquire = function(){
			$scope.sendRecv("CAM210", "queryAoData_1", "com.systex.jbranch.app.server.fps.cam210.CAM210InputVO", {'empId': $scope.row.EMP_ID,'branchID': $scope.row.BRANCH_NBR},
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
