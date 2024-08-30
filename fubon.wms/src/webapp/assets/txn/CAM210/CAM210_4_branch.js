'use strict';
eSoafApp.controller('CAM210_4_branch_Controller', function($scope, $controller, socketService, projInfoService, $q, $filter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "CAM210_4_branch_Controller";
	
	$scope.input = $scope.input || {};
	$scope.row = $scope.row || {};	
	
	$scope.init = function() {
		$scope.inputVO = {			
			uhrmRC:	$scope.input.uhrmRC,
			uhrmOP:	$scope.input.uhrmOP,
			region:	($scope.input.region != undefined && $scope.input.region != '' && $scope.input.region != null ? $scope.input.region : $scope.row.REGION_CENTER_ID),
			op: ($scope.input.op != undefined && $scope.input.op != '' && $scope.input.op != null ? $scope.input.op : $scope.row.BRANCH_AREA_ID),
			branch: ($scope.input.branch != undefined && $scope.input.branch != '' && $scope.input.branch != null ? $scope.input.branch : ''),
			aoCode: $scope.input.aoCode ? $scope.input.aoCode : $scope.row.aoCode,
			campaignId: $scope.row.CAMPAIGN_ID,
			stepId: $scope.row.STEP_ID,
			leadType: $scope.row.LEAD_TYPE
		};
		
		$scope.saba_totalLeadCounts = 0;
		$scope.saba_totalOkLeadCounts = 0;
		$scope.saba_totalWaitLeadCounts = 0;
		$scope.saba_totalInvsSum = 0;
		$scope.saba_totalInssSum = 0;
		$scope.saba_totalCcdSum = 0;
		$scope.saba_totalLoan1Sum = 0;
		$scope.saba_totalLoan2Sum = 0;
	};
	

	
	$scope.inquire = function(){
		$scope.sendRecv("CAM210", "sightActivities_branchList", "com.systex.jbranch.app.server.fps.cam210.CAM210InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				if(tota[0].body.resultList.length == 0) {
					$scope.showMsg("ehl_01_common_009");
	                return;
	            }
				
				$scope.sightActivitiesBranchAreaList = tota[0].body.resultList;
				$scope.branchOutputVO = tota[0].body;
				
				for (var i = 0; i < $scope.sightActivitiesBranchAreaList.length; i++) {
					$scope.saba_totalLeadCounts += $scope.sightActivitiesBranchAreaList[i].LEAD_COUNTS;
					$scope.saba_totalOkLeadCounts += $scope.sightActivitiesBranchAreaList[i].LEAD_CLOSE;
					$scope.saba_totalWaitLeadCounts += $scope.sightActivitiesBranchAreaList[i].LEAD_WAIT_COUNTS;
					$scope.saba_totalInvsSum += $scope.sightActivitiesBranchAreaList[i].INVS_BAL;
					$scope.saba_totalInssSum += $scope.sightActivitiesBranchAreaList[i].INSS_BAL;
					$scope.saba_totalCcdSum += $scope.sightActivitiesBranchAreaList[i].CARD_SUM;
					$scope.saba_totalLoan1Sum += $scope.sightActivitiesBranchAreaList[i].LOAN1_SUM;
					$scope.saba_totalLoan2Sum += $scope.sightActivitiesBranchAreaList[i].LOAN2_SUM;	  	        						        					
				}

				return;
			}
    	});
	}
	
	// 初始分頁資訊
    $scope.inquireInit = function() {
    	$scope.sightActivitiesBranchAreaList = [];
    	$scope.branchOutputVO = {};
    };
    $scope.inquireInit();
    
    $scope.$on("CAM210_4_branch_Controller.txtOP_4", function(event, row) {
    	$scope.txtOP_4(row);
    });
    
    $scope.txtOP_4 = function(row) {
    	$scope.row = row;	
		$scope.init();
		$scope.inquire();
	};
    
	$scope.txtAO_4 = function(row) {
		// 2017/9/13 add AO_CODE
    	row.aoCode = angular.copy($scope.inputVO.aoCode);
    	row.LEAD_TYPE = angular.copy($scope.inputVO.leadType);
    	//
		$scope.$emit("CAM210_4_Controller.txtAO_4", row);
		$scope.$emit("CAM210_4_Controller.changeTabIdx", 2);
	};
});
