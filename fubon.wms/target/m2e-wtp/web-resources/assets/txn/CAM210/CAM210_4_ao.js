'use strict';
eSoafApp.controller('CAM210_4_ao_Controller', function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "CAM210_4_ao_Controller";
	
	$scope.input = $scope.input || {};
	$scope.row = $scope.row || {};
	
	$scope.init = function() {
		$scope.inputVO = {		
				uhrmRC:	$scope.input.uhrmRC,
				uhrmOP:	$scope.input.uhrmOP,
				region:	($scope.input.region != undefined && $scope.input.region != '' && $scope.input.region != null ? $scope.input.region : $scope.row.REGION_CENTER_ID),
				op: ($scope.input.op != undefined && $scope.input.op != '' && $scope.input.op != null ? $scope.input.op : $scope.row.BRANCH_AREA_ID),
				branch: ($scope.input.branch != undefined && $scope.input.branch != '' && $scope.input.branch != null ? $scope.input.branch : $scope.row.BRANCH_NBR),
				aoCode: $scope.input.aoCode ? $scope.input.aoCode : $scope.row.aoCode,
				campaignId: $scope.row.CAMPAIGN_ID,
				stepId: $scope.row.STEP_ID,
				empId: $scope.row.EMP_ID,
				leadType: $scope.row.LEAD_TYPE
		};
	};
	
	$scope.totalLeadCounts = 0;
	$scope.totalOkLeadCounts = 0;
	$scope.totalCcdSum = 0;
	$scope.totalLoan1Sum = 0;
	$scope.totalLoan2Sum = 0;
	
	$scope.inquire = function(){
		$scope.sendRecv("CAM210", "sightActivities_aoList", "com.systex.jbranch.app.server.fps.cam210.CAM210InputVO", $scope.inputVO, function(tota, isError) {
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
    
    $scope.$on("CAM210_4_ao_Controller.txtAO_4", function(event, row) {
    	$scope.txtAO_4(row);
    });
    
    $scope.txtAO_4 = function(row) {
    	$scope.row = row;	
		$scope.init();
		$scope.inquire();
	};
	
	$scope.$on("CAM210_4_ao_Controller.inquireInit", function() {
		$scope.inquireInit();
    });
	
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
