/*

 */
'use strict';
eSoafApp.controller('CAM211_4_Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CAM211_4_Controller";
		
		$scope.input = $scope.input || {};
		$scope.row = $scope.row || {};
		
		console.log(JSON.stringify($scope.row));
		
		$scope.init = function() {
			$scope.inputVO = {			
				campaignId: $scope.row.CAMPAIGN_ID,
				stepId: $scope.row.STEP_ID,
				empId: $scope.row.EMP_ID
			};
		};
		console.log(JSON.stringify($scope.inputVO));
		
		$scope.totalLeadCounts = 0;
		$scope.totalOkLeadCounts = 0;
		$scope.totalCcdSum = 0;
		$scope.totalLoan1Sum = 0;
		$scope.totalLoan2Sum = 0;
		
		$scope.inquire = function(){
			$scope.sendRecv("CAM211", "sightActivities_aoList", "com.systex.jbranch.app.server.fps.cam211.CAM211InputVO", $scope.inputVO,
	        		function(tota, isError) {
	        			if (!isError) {
	        				
	        				if (tota[0].body.resultList.length == 0) {
	        					$scope.showMsg("ehl_01_common_009");
	        	                return;
	        	            }
	        				
	        				$scope.sightActivitiesAoAreaList = tota[0].body.resultList;
	        				$scope.aoOutputVO = tota[0].body;
	        				
	        				return;
	        			}
	        	});
		}
		
		// 初始分頁資訊
        $scope.inquireInit = function() {
        	$scope.sightActivitiesAoAreaList = [];
        	$scope.aoOutputVO = {};
        };
        $scope.inquireInit();
        
        $scope.$on("CAM211_4_ao_Controller.txtAO_4", function(event, row) {
        	$scope.txtAO_4(row);
        });
        
   		$scope.inquireInit = function() {
   			$scope.sightActivitiesAoAreaList = [];
   			$scope.outputVO = {};
   		};
   		$scope.init();
   		$scope.inquire();
   		
//        $scope.txtAO_4 = function(row) {
//        	$scope.row = row;	
//    		$scope.init();
//    		$scope.inquire();
//   		};
   		

   		
		// 連至客戶首頁
        $scope.custDTL = function(row) {
        	$scope.custVO = {
    				CUST_ID :  row.CUST_ID,
    				CUST_NAME :row.CUST_NAME	
    		}
    		$scope.connector('set','CRM_CUSTVO',$scope.custVO);
        	
        	var path = "assets/txn/CRM610/CRM610_MAIN.html";
			var set = $scope.connector("set","CRM610URL",path);
			var dialog = ngDialog.open({
				template: 'assets/txn/CRM610/CRM610.html',
				className: 'CRM610',
				showClose: false
			});
		}
        
});
