/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS350_COLUMNController',
	function(sysInfoService,$scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS350_COLUMNController";
		// 繼承共用的組織連動選單
		$controller('PMSRegionController', {$scope: $scope});
		
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
					char_type			: $scope.char_type
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
			
			$scope.title = "請選擇欲統計資料";
			$scope.showStep1 = true;
			$scope.resultList = [];
			$scope.outputVO = [];
			$scope.sendRecv("PMS350", "getCol", "com.systex.jbranch.app.server.fps.pms350.PMS350InputVO", $scope.inputVO,
				function(tota, isError) {
//					alert(JSON.stringify($scope.resultList));
					if(tota[0].body.resultList.length == 0) {
						$scope.showMsg("ehl_01_common_009");	//查無資料
            			return;
            		}
					$scope.resultList = tota[0].body.resultList;
					$scope.outputVO = tota[0].body;
			});
		};
		$scope.init();
		
		var drawChart = function (data) {
			var points = [];
			angular.forEach(data, function(row) {
				points.push({label: row.BRANCH, y: row.VAL});
			});
			
			var chartData = {
					dataPointWidth: 35,
					animationEnabled: true,
					theme: "light2", // "light1", "light2", "dark1", "dark2"
					data: [{
						type: "column",
						yValueFormatString: "#,##0",
						dataPoints: points
					}]
			}
			
			var chart = new CanvasJS.Chart('chartContainer', chartData);
			chart.render();
		}
		
		$scope.getCharData = function() {
			$scope.sendRecv("PMS350", "getCharData", "com.systex.jbranch.app.server.fps.pms350.PMS350InputVO", $scope.inputVO,
				function(tota, isError) {
					if(tota[0].body.resultList.length == 0) {
						$scope.showMsg("ehl_01_common_009");	//查無資料
	        			return;
	        		}
					drawChart(tota[0].body.resultList);
			});
		}
		
		$scope.next = function() {
			$scope.COL_SEQ_LIST = [];
			$scope.inputVO.col_seq_list = [];
	    	angular.forEach($scope.resultList, function(row){
	    		if (row.SELECTED == true) {
	    			$scope.inputVO.col_seq_list.push(row.COL_SEQ);
	    			$scope.COL_SEQ_LIST.push({LABEL: row.COL_NAME, DATA: row.COL_SEQ});
	    		}
	    	});
	    	if ($scope.inputVO.col_seq_list.length == 0) {
	    		$scope.showErrorMsg("至少需勾選一項欲統計資料。");
	    		return;
	    	}
	    	$scope.showStep1 = false;
	    	$scope.title = "查詢條件";
	    	$scope.getCharData();
		}
		
		
		
		$scope.back = function() {
			$scope.showStep1 = true;
			$scope.title = "請選擇欲統計資料";
		}
		
});
