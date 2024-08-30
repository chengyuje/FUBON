/*

 */
'use strict';
eSoafApp.controller('CAM210_4_Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CAM210_4_Controller";
		
		$scope.input = $scope.input || {};
		$scope.row = $scope.row || {};	
		
		$scope.init = function() {
			$scope.inputVO = {			
					region:	($scope.input.region != undefined && $scope.input.region != '' && $scope.input.region != null ? $scope.input.region : $scope.row.REGION_CENTER_ID),
					op: ($scope.input.op != undefined && $scope.input.op != '' && $scope.input.op != null ? $scope.input.op : $scope.row.BRANCH_AREA_ID),
					branch: ($scope.input.branch != undefined && $scope.input.branch != '' && $scope.input.branch != null ? $scope.input.branch : ''),
					aoCode: $scope.input.aoCode ? $scope.input.aoCode : '',
					campaignId: $scope.row.CAMPAIGN_ID,
					stepId: $scope.row.STEP_ID,
					leadType: $scope.row.LEAD_TYPE
			};
		};
		$scope.init();
		
		$scope.tabIdx = 0;
		$scope.disableBranchTab = true;
		$scope.disableAoTab = true;
		
		$scope.totalLeadCounts = 0;
		$scope.totalOkLeadCounts = 0;
		$scope.totalWaitLeadCounts = 0;
		$scope.totalCcdSum = 0;
		$scope.totalInvsSum = 0;
		$scope.totalInssSum = 0;
		$scope.totalLoan1Sum = 0;
		$scope.totalLoan2Sum = 0;
		
		$scope.inquire = function(){
			$scope.sendRecv("CAM210", "sightActivities_opArea", "com.systex.jbranch.app.server.fps.cam210.CAM210InputVO", $scope.inputVO,
	        		function(tota, isError) {
	        			if (!isError) {
	        				if (tota[0].body.resultList == null || tota[0].body.resultList.length == 0) {
	        					$scope.showMsg("ehl_01_common_009");
	        	                return;
	        	            }
	        				
	        				$scope.sightActivitiesOpAreaList = tota[0].body.resultList;
	        				$scope.outputVO = tota[0].body;
	        				
	        				for (var i=0; i<$scope.sightActivitiesOpAreaList.length; i++) {
	        					$scope.totalLeadCounts += $scope.sightActivitiesOpAreaList[i].LEAD_COUNTS;
	        					$scope.totalOkLeadCounts += $scope.sightActivitiesOpAreaList[i].LEAD_CLOSE;
	        					$scope.totalWaitLeadCounts += $scope.sightActivitiesOpAreaList[i].LEAD_WAIT_COUNTS;
	        					$scope.totalCcdSum += $scope.sightActivitiesOpAreaList[i].CARD_SUM;
	        					$scope.totalInvsSum += $scope.sightActivitiesOpAreaList[i].INVS_BAL;
	        					$scope.totalInssSum += $scope.sightActivitiesOpAreaList[i].INSS_BAL;	 
	        					$scope.totalLoan1Sum += $scope.sightActivitiesOpAreaList[i].LOAN1_SUM;
	        					$scope.totalLoan2Sum += $scope.sightActivitiesOpAreaList[i].LOAN2_SUM;	        						        					
	        				}
	        				return;
	        			}
	        	});
		}
		
		// 初始分頁資訊
        $scope.inquireInit = function() {
        	$scope.sightActivitiesOpAreaList = [];
        	$scope.outputVO = {};
        };
        $scope.inquireInit();
        $scope.inquire();
        
        $scope.txtOP_4 = function(row) {
        	// 2017/9/13 add AO_CODE
        	row.aoCode = angular.copy($scope.inputVO.aoCode);
        	row.LEAD_TYPE = angular.copy($scope.inputVO.leadType);
        	//
        	$scope.$broadcast("CAM210_4_branch_Controller.txtOP_4", row);
        	$scope.tabIdx = 1;
        	$scope.disableBranchTab = false;
        	$scope.disableAoTab = true;
        	$scope.$broadcast("CAM210_4_ao_Controller.inquireInit");
   		};
   		
   		$scope.$on("CAM210_4_Controller.txtAO_4", function(event, row) {
   			$scope.$broadcast("CAM210_4_ao_Controller.txtAO_4", row);
   			$scope.disableAoTab = false;
        });

   		$scope.$on("CAM210_4_Controller.changeTabIdx", function(event, idx) {
   			$scope.tabIdx = idx;
        });
});
