/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD130_DETAILController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD130_DETAILController";
		
		// combobox
		getParameter.XML(["PRD.BOND_TYPE","PRD.BOND_CURRENCY","FPS.PROD_RISK_LEVEL","FPS.DIVIDEND_FREQ_BOND",
			"PRD.BOND_COUPON_TYPE", "PRD.BOND_PROJECT", "PRD.BOND_CUSTOMER_LEVEL"], function(totas) {
			if (totas) {
				$scope.BOND_TYPE = totas.data[totas.key.indexOf('PRD.BOND_TYPE')];
				$scope.BOND_CURRENCY = totas.data[totas.key.indexOf('PRD.BOND_CURRENCY')];
				$scope.PROD_RISK_LEVEL = totas.data[totas.key.indexOf('FPS.PROD_RISK_LEVEL')];
				$scope.DIVIDEND_FREQ_BOND = totas.data[totas.key.indexOf('FPS.DIVIDEND_FREQ_BOND')];
				$scope.BOND_COUPON_TYPE = totas.data[totas.key.indexOf('PRD.BOND_COUPON_TYPE')];
				$scope.BOND_PROJECT = totas.data[totas.key.indexOf('PRD.BOND_PROJECT')];
				$scope.BOND_CUSTOMER_LEVEL = totas.data[totas.key.indexOf('PRD.BOND_CUSTOMER_LEVEL')];
			}
		});
				

		$scope.nowDate = new Date();
		// -------------------------------------INFO----------------------------------------
		$scope.sendRecv("PRD130", "getBondInfo", "com.systex.jbranch.app.server.fps.prd130.PRD130InputVO", {'prd_id':$scope.row.PRD_ID},
				function(tota, isError) {
					if (!isError) {
						$scope.bondInfo = tota[0].body.resultList[0];
						return;
					}
		});
		$scope.sendRecv("PRD130", "getBondRestriction", "com.systex.jbranch.app.server.fps.prd130.PRD130InputVO", {'prd_id':$scope.row.PRD_ID},
				function(tota, isError) {
					if (!isError) {
						$scope.restriction = tota[0].body.resultList[0];
						return;
					}
		});
		$scope.goDownload = function() {
			var id = $scope.row.PRD_ID;
			var name = $scope.row.BOND_CNAME;
			var dialog = ngDialog.open({
				template: 'assets/txn/PRD120/PRDDocument.html',
				className: 'PRDDocument',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.PRD_ID = id;
                	$scope.PRD_NAME = name;
                	$scope.PTYPE = "BND";
                	$scope.SUBSYSTEM_TYPE = "PRD";
                }]
			});
		};
		
		//下載風險預告書
		$scope.goRiskFile = function() {
			$scope.sendRecv("PRD252", "download", "com.systex.jbranch.app.server.fps.prd252.PRD252InputVO", {'prdId': $scope.row.PRD_ID},
					function(totas, isError) {
				if (!isError) {
					return;
            	}
			});
		}
		
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
        		x: function(d){ return d.x; },
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
		$scope.data = [{"key" : '參考申購價格',"color" : "#278F54","values" : []},{"key" : '參考贖回價格',"color" : "#FF8C26","values" : []}];
      	// 繪圖資料設定
        $scope.getBondPrice = function() {
        	$scope.sendRecv("PRD130", "getBondPrice", "com.systex.jbranch.app.server.fps.prd130.PRD130InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.CHART = tota[0].body.resultList;
							$scope.data[0].values = [];
							for (var i = 0; i < $scope.CHART.length; i++) {
								$scope.data[0].values.push({ x: $scope.toJsDate($scope.CHART[i].BARGAIN_DATE), y: $scope.CHART[i].BUY_PRICE});
							}
							$scope.data[1].values = [];
							for (var i = 0; i < $scope.CHART.length; i++) {
								$scope.data[1].values.push({ x: $scope.toJsDate($scope.CHART[i].BARGAIN_DATE), y: $scope.CHART[i].SELL_PRICE});
							}
				            return;
						}
			});
		};
		// -------------------------------------DIVIDEND----------------------------------------
		$scope.sendRecv("PRD130", "getBondDividend", "com.systex.jbranch.app.server.fps.prd130.PRD130InputVO", {'prd_id':$scope.row.PRD_ID},
				function(tota, isError) {
					if (!isError) {
						$scope.dividend = tota[0].body.resultList;
						$scope.dividendVO = tota[0].body;
						return;
					}
		});
		// -------------------------------------庫存明細----------------------------------------
		$scope.sendRecv("PRD140", "getSnStocks", "com.systex.jbranch.app.server.fps.prd140.PRD140InputVO", {'sn_id':$scope.row.PRD_ID, 'from': 'PRD130'},
				function(tota, isError) {
					if (!isError) {
						$scope.stocks = tota[0].body.resultList;
						$scope.stocksVO = tota[0].body;
						return;
					}
		});
		// -------------------------------------庫存明細下載----------------------------------------
		$scope.download = function() {
			$scope.sendRecv("PRD140", "download", "com.systex.jbranch.app.server.fps.prd140.PRD140InputVO", {'downloadList': $scope.stocks},
					function(tota, isError) {
						if (!isError) {
							return;
						}
			});
		}
		
		//for App
		if (typeof(webViewParamObj) != 'undefined') {
			$scope.fromApp = true;
		}
});