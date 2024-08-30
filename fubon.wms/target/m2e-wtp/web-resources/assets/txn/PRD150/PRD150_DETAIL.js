'use strict';
eSoafApp.controller('PRD150_DETAILController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD150_DETAILController";
		
		// combobox
		getParameter.XML(["PRD.BOND_TYPE","PRD.BOND_CURRENCY","FPS.PROD_RISK_LEVEL","PRD.SI_RATE_GUARANTEEPAY","PRD.SI_PROJECT","PRD.SI_CUSTOMER_LEVEL"], function(totas) {
			if (totas) {
				$scope.BOND_CURRENCY = totas.data[totas.key.indexOf('PRD.BOND_CURRENCY')];
				$scope.PROD_RISK_LEVEL = totas.data[totas.key.indexOf('FPS.PROD_RISK_LEVEL')];
				$scope.SI_RATE_GUARANTEEPAY = totas.data[totas.key.indexOf('PRD.SI_RATE_GUARANTEEPAY')];
				$scope.SI_PROJECT = totas.data[totas.key.indexOf('PRD.SI_PROJECT')];
				$scope.SI_CUSTOMER_LEVEL = totas.data[totas.key.indexOf('PRD.SI_CUSTOMER_LEVEL')];
			}
		});
		$scope.TwNum = ['一','二','三','四','五','六','七','八','九','十','十一','十二','十三','十四','十五','十六','十七','十八','十九','二十'];
		
		// INFO
		$scope.sendRecv("PRD150", "getSiInfo", "com.systex.jbranch.app.server.fps.prd150.PRD150InputVO", {'si_id':$scope.row.PRD_ID},
				function(tota, isError) {
					if (!isError) {
						
						if(tota[0].body.resultList.length == 0) {
							$scope.showMsg("目前商品主檔無此商品資訊，請聯繫IT人員或相關PM人員。");
                			return;
                		}
						$scope.siInfo = tota[0].body.resultList[0];
						if ($scope.siInfo.INVESTMENT_TARGETS)
							$scope.invest = $scope.siInfo.INVESTMENT_TARGETS.split(";");
					}
		});
		$scope.sendRecv("PRD150", "getSiRestriction", "com.systex.jbranch.app.server.fps.prd150.PRD150InputVO", {'si_id':$scope.row.PRD_ID},
			function(tota, isError) {
				if (!isError) {
					$scope.restriction = tota[0].body.resultList[0];
					return;
				}
		});
		
		$scope.goDownload = function() {
			var id = $scope.row.PRD_ID;
			var name = $scope.row.SI_CNAME;
			var dialog = ngDialog.open({
				template: 'assets/txn/PRD120/PRDDocument.html',
				className: 'PRDDocument',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.PRD_ID = id;
                	$scope.PRD_NAME = name;
                	$scope.PTYPE = "SI";
                	$scope.SUBSYSTEM_TYPE = "PRD";
                }]
			});
		};
		// -------------------------------------PRICE----------------------------------------
		$scope.PriceVO = {
			si_id: $scope.row.PRD_ID,
			stime: "1",
			s2time: "1"
        };
		$scope.mappingSet['Time'] = [];
		$scope.mappingSet['Time'].push({LABEL:'一個月', DATA:'1'}, {LABEL:'兩個月', DATA:'2'}, {LABEL:'三個月', DATA:'3'}, {LABEL:'六個月', DATA:'4'}, {LABEL:'一年', DATA:'5'}, {LABEL:'二年', DATA:'6'}, {LABEL:'三年', DATA:'7'}, {LABEL:'五年', DATA:'8'});
		// date picker
		$scope.sDateOptions = {};
		$scope.eDateOptions = {
			maxDate: $scope.nowDate
		};
		$scope.s2DateOptions = {};
		$scope.e2DateOptions = {
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
			$scope.sDateOptions.maxDate = $scope.PriceVO.eDate || undefined;
			$scope.eDateOptions.minDate = $scope.PriceVO.sDate || undefined;
		};
		$scope.setDate = function() {
			var now = new Date();
			now.setHours(23, 59, 59, 59);
			$scope.PriceVO.eDate = now;
			var min = new Date();
			min.setHours(0, 0, 0, 0);
			switch ($scope.PriceVO.stime) {
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
			$scope.PriceVO.sDate = min;
			$scope.limitDate();
		};
		$scope.setDate();
		$scope.limitDate2 = function() {
			$scope.s2DateOptions.maxDate = $scope.PriceVO.e2Date || undefined;
			$scope.e2DateOptions.minDate = $scope.PriceVO.s2Date || undefined;
		};
		$scope.setDate2 = function() {
			var now = new Date();
			now.setHours(23, 59, 59, 59);
			$scope.PriceVO.e2Date = now;
			var min = new Date();
			min.setHours(0, 0, 0, 0);
			switch ($scope.PriceVO.s2time) {
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
			$scope.PriceVO.s2Date = min;
			$scope.limitDate2();
		};
		$scope.setDate2();
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
        $scope.data = [{"key" : '參考贖回報價',"color" : "#278F54","values" : []}];
      	// 繪圖資料設定
        $scope.getPrice = function() {
			$scope.sendRecv("PRD150", "getSiPrice", "com.systex.jbranch.app.server.fps.prd150.PRD150InputVO", $scope.PriceVO,
					function(tota, isError) {
						if (!isError) {
							$scope.CHART = tota[0].body.resultList;
							$scope.data[0].values = [];
							for (var i = 0; i < $scope.CHART.length; i++) {
								$scope.data[0].values.push({ x: $scope.toJsDate($scope.CHART[i].SDDTE), y: $scope.CHART[i].SDAMT3});
							}
							// table
							$scope.history = [];
							for (var i = $scope.CHART.length - 1; i >= 0; i--) {
								$scope.history.push({'date':$scope.toJsDate($scope.CHART[i].SDDTE),'value':$scope.CHART[i].SDAMT3});
							
							}
							return;
						}
			});
		};
		$scope.getPrice2 = function() {
			$scope.sendRecv("PRD150", "getSiPrice2", "com.systex.jbranch.app.server.fps.prd150.PRD150InputVO", $scope.PriceVO,
					function(tota, isError) {
						if (!isError) {
							$scope.history2 = tota[0].body.resultList;
							$scope.history2VO = tota[0].body;
							return;
						}
			});
		};
		// -------------------------------------DIVIDEND----------------------------------------
		$scope.dividending= function() {
			$scope.sendRecv("PRD150", "getSiDividend", "com.systex.jbranch.app.server.fps.prd150.PRD150InputVO", {'si_id':$scope.row.PRD_ID},
					function(tota, isError) {
						if (!isError) {
							$scope.dividend = tota[0].body.resultList;
							$scope.dividendVO = tota[0].body;
						}
			});
		}
		// -------------------------------------庫存明細----------------------------------------
		$scope.sendRecv("PRD150", "getSiStocks", "com.systex.jbranch.app.server.fps.prd150.PRD150InputVO", {'si_id':$scope.row.PRD_ID},
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