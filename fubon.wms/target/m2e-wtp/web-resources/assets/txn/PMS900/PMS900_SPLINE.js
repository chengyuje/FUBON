/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS900_SPLINEController',
	function(sysInfoService,$scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS900_SPLINEController";
		// 繼承共用的組織連動選單
		$controller('PMSRegionController', {$scope: $scope});
		
		$scope.SETUP_CAT = [];
		$scope.SETUP_CAT.push({LABEL: '以查詢條件統計', DATA: '1'},
        					  {LABEL: '以選取報表統計', DATA: '2'});
		$scope.AO_LEVEL = [];
		$scope.AO_LEVEL.push({LABEL: 'FC1', DATA: 'FC1'},
							 {LABEL: 'FC2', DATA: 'FC2'},
							 {LABEL: 'FC3', DATA: 'FC3'},
							 {LABEL: 'FC4', DATA: 'FC4'},
							 {LABEL: 'FC5', DATA: 'FC5'});
		// date picker
		$scope.sDateOptions = {};
		$scope.eDateOptions = {};
		// config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.sDateOptions.maxDate = $scope.inputVO.eDate;
			$scope.eDateOptions.minDate = $scope.inputVO.sDate;
		};
		// date picker end

        //選取月份下拉選單 --> 重新設定可視範圍
        $scope.dateChange = function(){
        	if(!$scope.inputVO.eDate){
        		return;
        	}
        	//設定回傳時間
        	$scope.inputVO.reportDate = $scope.inputVO.eDate;
        	//可視範圍觸發 
        	$scope.RegionController_getORG($scope.inputVO);
        };

		$scope.init = function() {
			$scope.inputVO = {
					yyyymm				: '',
					region_center_id	: '',   // 區域中心
					branch_area_id		: '', 	// 營運區
					branch_nbr			: '',	// 分行	
					emp_id				: '',	// 員編
//					memLoginFlag		: String(sysInfoService.getMemLoginFlag())
        	};
//			var min_mon = new Date();
//			min_mon.setMonth(min_mon.getMonth() - 1);
//			min_mon.setHours(0, 0, 0, 0);
			$scope.inputVO.sDate = new Date(2020, 0, 1);
//			var min_mon2 = new Date();
//			min_mon2.setHours(0, 0, 0, 0);
			$scope.inputVO.eDate = new Date(2020, 10, 30);
			$scope.limitDate();
			$scope.dateChange();
		};
		$scope.init();
		
		var drawChart = function (data) {
			var chartData = {
					animationEnabled: true,
					exportEnabled: true,
					toolTip: {
						shared: true
					},
					legend:{
						cursor:"pointer"
					},
			}
			chartData.data = [];
			
			angular.forEach(data.line_name_array, function(row) {
				var points = [];
				angular.forEach(data.resultList, function(row2) {
					if (row == row2.LINE_NAME) {
						points.push({ label: row2.YEARMON , y: row2.CONTENT });						
					}
				});
				var map = {        
						type: "spline",  
						name: row,        
						showInLegend: true,
						dataPoints: points
				}
				chartData.data.push(map);
			});
			
			var chart = new CanvasJS.Chart('chartContainer', chartData);
			chart.render();
		}
		
		$scope.getCharData = function() {
			$scope.sendRecv("PMS900", "getCharData", "com.systex.jbranch.app.server.fps.pms900.PMS900InputVO", $scope.inputVO,
				function(tota, isError) {
					if(tota[0].body.resultList.length == 0 || tota[0].body.line_name_array == undefined) {
						$scope.showMsg("ehl_01_common_009");	//查無資料
	        			return;
	        		}
					drawChart(tota[0].body);
			});
		}
		$scope.getCharData();
});
