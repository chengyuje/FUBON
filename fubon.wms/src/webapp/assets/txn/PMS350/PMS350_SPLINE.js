/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS350_SPLINEController',
	function(sysInfoService,$scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS350_SPLINEController";
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
					char_type			: $scope.char_type,
					setupCategory		: '1'
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
			
			if ($scope.pms900 == true) {
				$scope.inputVO.char_type = '2';
				$scope.title = '查詢條件';
				$scope.showStep1 = false;
				$scope.showStep2 = false;
				$scope.showStep3 = false;
//				alert("pms900")
				
				$scope.COL_SEQ_LIST = [];
				$scope.COL_SEQ_LIST.push({LABEL: '存匯類商品', DATA: '6'},
										 {LABEL: '投資類商品', DATA: '7'},
										 {LABEL: '保險商品', DATA: '19'},
										 {LABEL: '信託商品', DATA: '22'},
										 {LABEL: '黃金存摺', DATA: '23'},
										 {LABEL: '計績收益小計(加減碼後)', DATA: '24'},
										 {LABEL: '績效獎金', DATA: '30'},
										 {LABEL: '績效獎金實發金額', DATA: '32'});
				
				$scope.inputVO.seq_list = ['8341', '8342', '8343', '8344'];
				$scope.inputVO.pms900 = true;
				$scope.getCharData();
			} else {
				$scope.title = "請選擇欲比較報表";
				$scope.showStep1 = true;
				$scope.showStep2 = false;
				$scope.showStep3 = false;
				$scope.inputVO.pms900 = false;
				$scope.resultList = [];
				$scope.outputVO = [];
				$scope.resultList2 = [];
				$scope.outputVO2 = [];
				
				$scope.sendRecv("PMS350", "getReport", "com.systex.jbranch.app.server.fps.pms350.PMS350InputVO", $scope.inputVO,
					function(tota, isError) {
//						alert(JSON.stringify($scope.resultList));
						if(tota[0].body.resultList.length == 0) {
							$scope.showMsg("ehl_01_common_009");	//查無資料
							return;
						}
						$scope.resultList = tota[0].body.resultList;
						$scope.outputVO = tota[0].body;
				});
				
				$scope.sendRecv("PMS350", "getCol", "com.systex.jbranch.app.server.fps.pms350.PMS350InputVO", $scope.inputVO,
					function(tota, isError) {
//						alert(JSON.stringify($scope.resultList));
						if(tota[0].body.resultList.length == 0) {
							$scope.showMsg("ehl_01_common_009");	//查無資料
							return;
						}
						$scope.resultList2 = tota[0].body.resultList;
						$scope.outputVO2 = tota[0].body;
				});
			}
		};
		
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
						if (row2.X_AXIS == '7-(C)-國內基金-單筆')
							row2.X_AXIS = '投資類商品';
						if (row2.X_AXIS == '19-(D)-躉繳')
							row2.X_AXIS = '保險商品';
						
						points.push({ label: row2.X_AXIS , y: row2.VAL });						
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
			$scope.sendRecv("PMS350", "getCharData", "com.systex.jbranch.app.server.fps.pms350.PMS350InputVO", $scope.inputVO,
				function(tota, isError) {
					if(tota[0].body.resultList.length == 0 || tota[0].body.line_name_array == undefined) {
						$scope.showMsg("ehl_01_common_009");	//查無資料
	        			return;
	        		}
					drawChart(tota[0].body);
			});
		}
		
		$scope.next = function() {
			if ($scope.showStep1 == true) {
				$scope.SEQ_LIST = [];
				$scope.inputVO.seq_list = [];
		    	angular.forEach($scope.resultList, function(row){
		    		if (row.SELECTED == true) {
		    			$scope.inputVO.seq_list.push(row.SEQ);
		    			$scope.SEQ_LIST.push({LABEL: row.RPT_NAME, DATA: row.SEQ});
		    		}
		    	});
		    	if ($scope.inputVO.seq_list.length == 0) {
		    		$scope.showErrorMsg("至少需勾選一筆報表。");
		    		return;
		    	}
			}
			
			if ($scope.showStep2 == true) {
				$scope.COL_SEQ_LIST = [];
				$scope.inputVO.col_seq_list = [];
				angular.forEach($scope.resultList2, function(row){
					if (row.SELECTED == true) {
						$scope.inputVO.col_seq_list.push(row.COL_SEQ);
						$scope.COL_SEQ_LIST.push({LABEL: row.COL_NAME, DATA: row.COL_SEQ});
					}
				});
				if ($scope.inputVO.col_seq_list.length == 0) {
					$scope.showErrorMsg("至少需勾選一項欲統計資料。");
					return;
				}
			}
			
			if ($scope.showStep1 == true) {
				$scope.showStep1 = false;
				$scope.showStep2 = true;
				$scope.title = "請選擇欲統計資料";
			} else if ($scope.showStep2 == true) {
				$scope.showStep2 = false;
				$scope.showStep3 = true;
				$scope.title = "查詢條件";
				$scope.getCharData();				
			}
		}
		
		$scope.back = function() {
			if ($scope.showStep3 == true) {
				$scope.showStep3 = false;
				$scope.showStep2 = true;
				$scope.title = "請選擇欲統計資料";
			} else if ($scope.showStep2 == true) {
				$scope.showStep2 = false;
				$scope.showStep1 = true;
				$scope.title = "請選擇欲比較報表";
			}
		}
		$scope.init();
});
