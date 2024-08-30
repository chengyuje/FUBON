/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD290_LinearController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD290_LinearController";
		
		$scope.inputVO = {
			type: '1',
			prd_id: $scope.row.PRD_ID
		};
		
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
		// 繪圖程式 LINE CHART
		$scope.options = {
        	chart: {
        		type: 'lineChart',
        		height: 450,
        		margin : {
        			top: 20,
        			right: 40,
        			bottom: 40,
        			left: 65
        		},
        		x: function(d){ return d.x; },
        		y: function(d){ return d.y; },
        		useInteractiveGuideline: true,
        		xAxis: {
        			axisLabel: '年月',
        			tickFormat: function(d){
        				return d3.time.format("%Y-%m-%d")(new Date(d));
        			},
        			staggerLabels: true
        		},
        		yAxis: {
        			axisLabel: '',
        			tickFormat: function(d){
        				if($scope.inputVO.type == '3')
        					return d3.format('.02f')(d * 100) + '%';
        				else
        					return d3.format('.02f')(d);
        			}
        			//axisLabelDistance: -10
        		},
        	},
        	title: {
        		enable: true,
        		text: ''
        	},
        };
      	// 繪圖資料
        $scope.data = [{"key" : '',"color" : "#278F54","values" : []}];
        // 繪圖資料設定
        $scope.getLinear = function() {
        	if($scope.inputVO.type == '1') {
        		$scope.data[0].key = '淨值';
	        	$scope.options.chart.yAxis.axisLabel = '淨值';
        	}
        	else if ($scope.inputVO.type == '2') {
        		$scope.data[0].key = '單日報酬';
	        	$scope.options.chart.yAxis.axisLabel = '單日報酬';
        	}
        	else {
        		$scope.data[0].key = '每日進場累計報酬';
	        	$scope.options.chart.yAxis.axisLabel = '每日進場累計報酬';
        	}
			$scope.sendRecv("PRD290", "getLinear", "com.systex.jbranch.app.server.fps.prd290.PRD290InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.CHART = tota[0].body.resultList;
							$scope.data[0].values = [];
							for (var i = 0; i < $scope.CHART.length; i++) {
								$scope.data[0].values.push({ x: $scope.toJsDate($scope.CHART[i].SDATE), y: $scope.CHART[i].SDATA});
							}
							return;
						}
			});
		};
		
		$scope.clearData = function() {
			$scope.data = [{"key" : '',"color" : "#278F54","values" : []}];
		};
		
		
		
});