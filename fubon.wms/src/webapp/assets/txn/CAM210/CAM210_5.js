'use strict';
eSoafApp.controller('CAM210_5_Controller', function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "CAM210_5_Controller";
	
	$scope.input = $scope.input || {};
	$scope.row = $scope.row || {};	
	
	$scope.init = function() {
		$scope.inputVO = {	
			uhrmRC:	$scope.input.uhrmRC,
			uhrmOP:	$scope.input.uhrmOP,
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
	$scope.totalCcdSum = 0;
	$scope.totalInvsSum = 0;
	$scope.totalInssSum = 0;
	$scope.totalLoan1Sum = 0;
	$scope.totalLoan2Sum = 0;

	$scope.inquire = function(){
		$scope.sendRecv("CAM210", "expiredActivities_opArea", "com.systex.jbranch.app.server.fps.cam210.CAM210InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
    			if (tota[0].body.resultList == null || tota[0].body.resultList.length == 0) {
    				$scope.showMsg("ehl_01_common_009");
    	            return;
    	        }
    			
    			$scope.expiredActivitiesOpAreaList = tota[0].body.resultList;
    			$scope.outputVO = tota[0].body;
    			
    			for (var i=0; i<$scope.expiredActivitiesOpAreaList.length; i++) {
    				$scope.totalLeadCounts += $scope.expiredActivitiesOpAreaList[i].LEAD_COUNTS;
    				$scope.totalOkLeadCounts += $scope.expiredActivitiesOpAreaList[i].LEAD_CLOSE;
    				$scope.totalCcdSum += $scope.expiredActivitiesOpAreaList[i].CARD_SUM;
    				$scope.totalInvsSum += $scope.expiredActivitiesOpAreaList[i].INVS_BAL;
					$scope.totalInssSum += $scope.expiredActivitiesOpAreaList[i].INSS_BAL;
    				$scope.totalLoan1Sum += $scope.expiredActivitiesOpAreaList[i].LOAN1_SUM;
    				$scope.totalLoan2Sum += $scope.expiredActivitiesOpAreaList[i].LOAN2_SUM;	        						        					
    			}
    			
    			return;
    		}
		});
	}
	
	// 初始分頁資訊
    $scope.inquireInit = function() {
    	$scope.expiredActivitiesOpAreaList = [];
    	$scope.outputVO = {};
    };
    
    $scope.inquireInit();
    $scope.inquire();
    
    $scope.txtOP_5 = function(row) {
    	// 2017/9/15 add AO_CODE
    	row.aoCode = angular.copy($scope.inputVO.aoCode);
    	row.LEAD_TYPE = angular.copy($scope.inputVO.leadType);
    	//
    	$scope.$broadcast("CAM210_5_branch_Controller.txtOP_5", row);
    	$scope.tabIdx = 1;
    	$scope.disableBranchTab = false;
    	$scope.disableAoTab = true;
    	$scope.$broadcast("CAM210_5_ao_Controller.inquireInit");
	};
	
	$scope.$on("CAM210_5_Controller.txtAO_5", function(event, row) {
		$scope.$broadcast("CAM210_5_ao_Controller.txtAO_5", row);
		$scope.disableAoTab = false;
    });

	$scope.$on("CAM210_5_Controller.changeTabIdx", function(event, idx) {
		$scope.tabIdx = idx;
    });
});
