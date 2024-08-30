/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD290_CompareController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD290_CompareController";
		
		$scope.inputVO = {
			prd_id: $scope.ans[0].PRD_ID,
			prd_id2: $scope.ans[1].PRD_ID
		};
		
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
        			axisLabel: '報酬率',
        			tickFormat: function(d){
        				return d3.format('.02f')(d * 100) + '%';
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
        $scope.data = [{"key" : '商品代碼:' + $scope.inputVO.prd_id,"color" : "#278F54","values" : []},{"key" : '商品代碼:' + $scope.inputVO.prd_id2,"color" : "#FF8C26","values" : []}];
      	// 繪圖資料設定
        $scope.getReturn = function() {
			$scope.sendRecv("PRD290", "getReturn", "com.systex.jbranch.app.server.fps.prd290.PRD290InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.CHART = tota[0].body.resultList;
							$scope.data[0].values = [];
							for (var i = 0; i < $scope.CHART.length; i++) {
								$scope.data[0].values.push({ x: $scope.toJsDate($scope.CHART[i].PRICE_DATE), y: $scope.CHART[i].C_RETURN});
							}
							$scope.CHART2 = tota[0].body.resultList2;
							$scope.data[1].values = [];
							for (var i = 0; i < $scope.CHART2.length; i++) {
								$scope.data[1].values.push({ x: $scope.toJsDate($scope.CHART2[i].PRICE_DATE), y: $scope.CHART2[i].C_RETURN});
							}
							return;
						}
			});
		};
		$scope.getReturn();
		
		
		
});