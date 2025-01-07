/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
  金市結構債
 */
'use strict';
eSoafApp.controller('PRD141_DETAILController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD141_DETAILController";
	
		// -------------------------------------INFO----------------------------------------
		$scope.sendRecv("PRD141", "getBondSnInfo", "com.systex.jbranch.app.server.fps.prd141.PRD141InputVO", {'prd_id':$scope.row.PRD_ID},
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.resultList.length == 0) {
							$scope.showMsg("目前商品主檔無此商品資訊，請聯繫IT人員或相關PM人員。");
                			return;
                		}
						$scope.snInfo = tota[0].body.resultList[0];
					}
		});
	
		// -------------------------------------PRICE----------------------------------------
		$scope.inputVO = {
			prd_id: $scope.row.PRD_ID,
			stime: "1"
		};
		$scope.mappingSet['Time'] = [];
		$scope.mappingSet['Time'].push({LABEL:'一個月', DATA:'1'}, {LABEL:'兩個月', DATA:'2'}, {LABEL:'三個月', DATA:'3'}, {LABEL:'六個月', DATA:'4'}, {LABEL:'一年', DATA:'5'}, {LABEL:'二年', DATA:'6'}, {LABEL:'三年', DATA:'7'}, {LABEL:'五年', DATA:'8'});
		// date picker
		$scope.sDateOptions = {};
		$scope.eDateOptions = {
			maxDate: $scope.nowDate
		};
		// config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.sDateOptions.maxDate = $scope.inputVO.eDate || undefined;
			$scope.eDateOptions.minDate = $scope.inputVO.sDate || undefined;
		};
		$scope.setDate = function() {
			var now = new Date();
			now.setHours(23, 59, 59, 59);
			$scope.inputVO.eDate = now;
			var min = new Date();
			min.setHours(0, 0, 0, 0);
			switch ($scope.inputVO.stime) {
				case '1':
					min.setMonth(min.getMonth() - 1);
					break;
				case '2':
					min.setMonth(min.getMonth() - 2);
					break;
				case '3':
					min.setMonth(min.getMonth() - 3);
					break;
				case '4':
					min.setMonth(min.getMonth() - 6);
					break;
				case '5':
					min.setFullYear(min.getFullYear()-1);
					break;
				case '6':
					min.setFullYear(min.getFullYear()-2);
					break;
				case '7':
					min.setFullYear(min.getFullYear()-3);
					break;
				case '8':
					min.setFullYear(min.getFullYear()-5);
					break;
			}
			$scope.inputVO.sDate = min;
			$scope.limitDate();
		};
		$scope.setDate();
		// date picker end
		// 繪圖程式 LINE CHART
		$scope.options = {
        	chart: {
        		type: 'lineChart',
        		height: 500,
        		margin : {
        			top: 20,
        			right: 40,
        			bottom: 55,
        			left: 65
        		},
        		x: function(d){ return new Date(d.x); },
        		y: function(d){ return d.y; },
        		useInteractiveGuideline: true,
        		xAxis: {
        			axisLabel: '年月',
        			axisLabelDistance: 15,
        			tickFormat: function(d){
        				return d3.time.format("%Y-%m-%d")(new Date(d));
        			},
        			staggerLabels: true
        		},
        		yAxis: {
        			axisLabel: '價格%',
        			tickFormat: function(d){
        				return d;
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
		$scope.data = [{"key" : '參考贖回價格',"color" : "#FF8C26","values" : []}];
      	// 繪圖資料設定 SN and BOUND same table
        $scope.getBondPrice = function() {
        	$scope.sendRecv("PRD141", "getBondPrice", "com.systex.jbranch.app.server.fps.prd141.PRD141InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.CHART = tota[0].body.resultList;
//							$scope.data[0].values = [];
//							for (var i = 0; i < $scope.CHART.length; i++) {
//								$scope.data[0].values.push({ x: $scope.toJsDate($scope.CHART[i].BARGAIN_DATE), y: $scope.CHART[i].BUY_PRICE});
//							}
			                $scope.data[0].values = [];
			                for (var i = 0; i < $scope.CHART.length; i++) {
			                    $scope.data[0].values.push({ 
			                        x: $scope.CHART[i].NAV_DATE, 
			                        y: $scope.CHART[i].NAV 
			                    });
			                }
				            return;
						}
			});
		};
		// -------------------------------------DIVIDEND----------------------------------------
		$scope.sendRecv("PRD141", "getSnDividend", "com.systex.jbranch.app.server.fps.prd141.PRD141InputVO", {'prd_id':$scope.row.PRD_ID},
				function(tota, isError) {
					if (!isError) {
						$scope.dividend = tota[0].body.resultList;
						$scope.dividendVO = tota[0].body;
						return;
					}
		});
		
		// -------------------------------------庫存明細----------------------------------------
		$scope.sendRecv("PRD141", "getSnStocks", "com.systex.jbranch.app.server.fps.prd141.PRD141InputVO", {'prd_id':$scope.row.PRD_ID, 'from': 'PRD141'},
				function(tota, isError) {
					if (!isError) {
						$scope.stocks = tota[0].body.resultList;
						$scope.stocksVO = tota[0].body;
						return;
					}
		});
		// -------------------------------------庫存明細下載----------------------------------------
		$scope.download = function() {
			$scope.sendRecv("PRD141", "download", "com.systex.jbranch.app.server.fps.prd141.PRD141InputVO", {'downloadList': $scope.stocks},
					function(tota, isError) {
						if (!isError) {
							return;
						}
			});
		}
		
});