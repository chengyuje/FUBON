'use strict';
eSoafApp.controller('CAM210_5_branch_Controller', function($scope, $controller, socketService, projInfoService, $q, $filter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "CAM210_5_branch_Controller";
	
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
	};
	
	$scope.saba_totalLeadCounts = 0;
	$scope.saba_totalOkLeadCounts = 0;
	$scope.saba_totalInvsSum = 0;
	$scope.saba_totalInssSum = 0;
	$scope.saba_totalCcdSum = 0;
	$scope.saba_totalLoan1Sum = 0;
	$scope.saba_totalLoan2Sum = 0;
	
	$scope.inquire = function(){
		$scope.sendRecv("CAM210", "expiredActivities_branchList", "com.systex.jbranch.app.server.fps.cam210.CAM210InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				if (tota[0].body.resultList.length == 0) {
					$scope.showMsg("ehl_01_common_009");
	                return;
	            }
				
				$scope.expiredActivitiesBranchAreaList = tota[0].body.resultList;
				$scope.branchOutputVO = tota[0].body;
				
				for (var i=0; i<$scope.expiredActivitiesBranchAreaList.length; i++) {
					$scope.saba_totalLeadCounts += $scope.expiredActivitiesBranchAreaList[i].LEAD_COUNTS;
					$scope.saba_totalOkLeadCounts += $scope.expiredActivitiesBranchAreaList[i].LEAD_CLOSE;
					$scope.saba_totalInvsSum += $scope.expiredActivitiesBranchAreaList[i].INVS_BAL;
					$scope.saba_totalInssSum += $scope.expiredActivitiesBranchAreaList[i].INSS_BAL;
					$scope.saba_totalCcdSum += $scope.expiredActivitiesBranchAreaList[i].CARD_SUM;
					$scope.saba_totalLoan1Sum += $scope.expiredActivitiesBranchAreaList[i].LOAN1_SUM;
					$scope.saba_totalLoan2Sum += $scope.expiredActivitiesBranchAreaList[i].LOAN2_SUM;	        						        					
				}
				return;
			}
    	});
	}
	
	// 初始分頁資訊
    $scope.inquireInit = function() {
    	$scope.expiredActivitiesBranchAreaList = [];
    	$scope.branchOutputVO = {};
    };
    
    $scope.inquireInit();
    
    $scope.$on("CAM210_5_branch_Controller.txtOP_5", function(event, row) {
    	$scope.txtOP_5(row);
    });
    
    $scope.txtOP_5 = function(row) {
    	$scope.row = row;	
		$scope.init();
		$scope.inquire();
	};
    
	$scope.txtAO_5 = function(row) {
		// 2017/9/14 add AO_CODE
    	row.aoCode = angular.copy($scope.inputVO.aoCode);
    	row.LEAD_TYPE = angular.copy($scope.inputVO.leadType);
    	//
		$scope.$emit("CAM210_5_Controller.txtAO_5", row);
		$scope.$emit("CAM210_5_Controller.changeTabIdx", 2);
	};
});
