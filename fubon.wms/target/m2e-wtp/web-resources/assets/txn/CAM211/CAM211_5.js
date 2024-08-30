/*

 */
'use strict';
eSoafApp.controller('CAM211_5_Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CAM211_5_Controller";
		
		$scope.input = $scope.input || {};
		$scope.row = $scope.row || {};
		
		$scope.init = function() {
			$scope.inputVO = {			
					campaignId: $scope.row.CAMPAIGN_ID,
					stepId: $scope.row.STEP_ID,
					empId: $scope.row.EMP_ID,
					leadType: $scope.row.LEAD_TYPE
			};
		};
		
		$scope.init();
		
		$scope.tabIdx = 0;
		$scope.disableBranchTab = true;
		$scope.disableAoTab = true;
		
		$scope.totalLeadCounts = 0;
		$scope.totalOkLeadCounts = 0;
		$scope.totalCcdSum = 0;
		$scope.totalInvsSum = 0;
		$scope.totalInssSum = 0;
		$scope.totalLoan1Sum = 0;
		$scope.totalLoan2Sum = 0;
				
		$scope.inquire = function(){
			$scope.sendRecv("CAM211", "expiredActivities_aoList", "com.systex.jbranch.app.server.fps.cam211.CAM211InputVO", $scope.inputVO,
	        		function(tota, isError) {
	        			if (!isError) {
	        				if(tota[0].body.resultList.length == 0) {
	        					$scope.showMsg("ehl_01_common_009");
	        	                return;
	        	            }
	        				
	        				$scope.expiredActivitiesAoAreaList = tota[0].body.resultList;
	        				$scope.aoOutputVO = tota[0].body;
	        				
	        				return;
	        			}
	        	});
		}
		
		// 初始分頁資訊
        $scope.inquireInit = function() {
        	$scope.expiredActivitiesAoAreaList = [];
        	$scope.aoOutputVO = {};
        };
        $scope.inquireInit();
        $scope.inquire();
        
//        $scope.$on("CAM211_5_ao_Controller.txtAO_5", function(event, row) {
//        	$scope.txtAO_5(row);
//        });
//        
//        $scope.txtAO_5 = function(row) {
//        	$scope.row = row;	
//    		$scope.init();
//    		$scope.inquire();
//   		};
//   		
//   		$scope.$on("CAM211_5_ao_Controller.inquireInit", function() {
//   			$scope.inquireInit();
//        });
   		
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
        
		// 初始分頁資訊
        $scope.inquireInit = function() {
        	$scope.expiredActivitiesAoAreaList = [];
        	$scope.outputVO = {};
        };
        
        $scope.inquireInit();
        $scope.inquire();
        
//        $scope.txtOP_5 = function(row) {
//        	// 2017/9/15 add AO_CODE
//        	row.aoCode = angular.copy($scope.inputVO.aoCode);
//        	row.LEAD_TYPE = angular.copy($scope.inputVO.leadType);
//        	//
//        	$scope.$broadcast("CAM211_5_branch_Controller.txtOP_5", row);
//        	$scope.tabIdx = 1;
//        	$scope.disableBranchTab = false;
//        	$scope.disableAoTab = true;
//        	$scope.$broadcast("CAM211_5_ao_Controller.inquireInit");
//   		};
//   		
//   		$scope.$on("CAM211_5_Controller.txtAO_5", function(event, row) {
//   			$scope.$broadcast("CAM211_5_ao_Controller.txtAO_5", row);
//   			$scope.disableAoTab = false;
//        });
//
//   		$scope.$on("CAM211_5_Controller.changeTabIdx", function(event, idx) {
//   			$scope.tabIdx = idx;
//        });
});
