/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD110_DETAILController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD110_DETAILController";

		$scope.openFubonTrustWeb = (type) => {
			$scope.showBasicFlag = 0;
			window.open(`https://www.fubon.com/banking/personal/fund_trust/fund_focus/fund_detail_ebank.htm?A=${$scope.row.PRD_ID}&B=${type}`, '_blank', 'top=5,width=780,height=500');
		}

		// combobox
		getParameter.XML(["FPS.CURRENCY", "FPS.PROD_RISK_LEVEL", "PRD.MKT_TIER2", "PRD.MKT_TIER3", "PRD.FUND_SUBJECT", "PRD.FUND_PROJECT", "PRD.FUND_CUSTOMER_LEVEL"], function(totas) {
			if (totas) {
				$scope.CURRENCY = totas.data[totas.key.indexOf('FPS.CURRENCY')];
				$scope.PROD_RISK_LEVEL = totas.data[totas.key.indexOf('FPS.PROD_RISK_LEVEL')];
				$scope.MKT_TIER2 = totas.data[totas.key.indexOf('PRD.MKT_TIER2')];
				$scope.MKT_TIER3 = totas.data[totas.key.indexOf('PRD.MKT_TIER3')];
				$scope.PRD_FUND_SUBJECT = totas.data[totas.key.indexOf('PRD.FUND_SUBJECT')];
				$scope.PRD_FUND_PROJECT = totas.data[totas.key.indexOf('PRD.FUND_PROJECT')];
				$scope.PRD_FUND_CUSTOMER_LEVEL = totas.data[totas.key.indexOf('PRD.FUND_CUSTOMER_LEVEL')];
			}
		});
				
		// INFO
		$scope.sendRecv("PRD110", "getFundInfo", "com.systex.jbranch.app.server.fps.prd110.PRD110InputVO", {'fund_id':$scope.row.PRD_ID},
				function(tota, isError) {
					if (!isError) {
						$scope.fundInfo = tota[0].body.resultList[0];
						return;
					}
		});
		$scope.sendRecv("PRD110", "getFundRestriction", "com.systex.jbranch.app.server.fps.prd110.PRD110InputVO", {'fund_id':$scope.row.PRD_ID},
				function(tota, isError) {
					if (!isError) {
						$scope.restriction = tota[0].body.resultList[0];
						return;
					}
		});
		$scope.goDownload = function() {
			var id = $scope.row.PRD_ID;
			var name = $scope.row.FUND_CNAME;
			var dialog = ngDialog.open({
				template: 'assets/txn/PRD120/PRDDocument.html',
				className: 'PRDDocument',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.PRD_ID = id;
                	$scope.PRD_NAME = name;
                	$scope.PTYPE = "MFD";
                	$scope.SUBSYSTEM_TYPE = "PRD";
                }]
			});
		};
		// -------------------------------------WORTH----------------------------------------
//		$scope.WorthVO = {
//			fund_id: $scope.row.PRD_ID,
//			type: "1",
//			stime: "1"
//        };
//		$scope.mappingSet['Time'] = [];
//		$scope.mappingSet['Time'].push({LABEL:'一個月', DATA:'1'}, {LABEL:'兩個月', DATA:'2'}, {LABEL:'三個月', DATA:'3'}, {LABEL:'六個月', DATA:'4'}, {LABEL:'一年', DATA:'5'}, {LABEL:'二年', DATA:'6'}, {LABEL:'三年', DATA:'7'}, {LABEL:'五年', DATA:'8'});
//		// date picker
//		$scope.sDateOptions = {};
//		$scope.eDateOptions = {};
//		// config
//		$scope.model = {};
//		$scope.open = function($event, elementOpened) {
//			$event.preventDefault();
//			$event.stopPropagation();
//			$scope.model[elementOpened] = !$scope.model[elementOpened];
//		};
//		$scope.limitDate = function() {
//			$scope.WorthVO.stime = "";
//			// 五年
//			$scope.sDateOptions.maxDate = $scope.WorthVO.eDate || undefined;
//			if ($scope.WorthVO.eDate) {
//				var min = new Date($scope.WorthVO.eDate.getTime());
//				min.setFullYear(min.getFullYear()-5);
//				$scope.sDateOptions.minDate = min;
//			} else
//				$scope.sDateOptions.minDate = undefined;
//			$scope.eDateOptions.minDate = $scope.WorthVO.sDate || undefined;
//			if ($scope.WorthVO.sDate) {
//				var max = new Date($scope.WorthVO.sDate.getTime());
//				max.setFullYear(max.getFullYear()+5);
//				$scope.eDateOptions.maxDate = max;
//			} else
//				$scope.eDateOptions.maxDate = undefined;
//		};
//		$scope.clearWorthDate = function() {
//			if($scope.WorthVO.stime != "") {
//				$scope.WorthVO.sDate = undefined;
//				$scope.WorthVO.eDate = undefined;
//				$scope.sDateOptions = {};
//				$scope.eDateOptions = {};
//			}
//		}
//		// date picker end
//		// 繪圖程式 LINE CHART
//		$scope.options = {
//        	chart: {
//        		type: 'lineChart',
//        		height: 450,
//        		margin : {
//        			top: 20,
//        			right: 40,
//        			bottom: 40,
//        			left: 65
//        		},
//        		x: function(d){ return d.x; },
//        		y: function(d){ return d.y; },
//        		useInteractiveGuideline: true,
//        		xAxis: {
//        			axisLabel: '年月',
//        			tickFormat: function(d){
//        				return d3.time.format("%Y-%m-%d")(new Date(d));
//        			},
//        			staggerLabels: true
//        		},
//        		yAxis: {
//        			axisLabel: '',
//        			tickFormat: function(d){
//        				return d;
//        			}
//        			//axisLabelDistance: -10
//        		},
//        	},
//        	title: {
//        		enable: true,
//        		text: ''
//        	},
//        };
//      	// 繪圖資料
//        $scope.data = [{"key" : '',"color" : "#278F54","values" : []}];
//      	// 繪圖資料設定
//        $scope.getWorth = function() {
//        	var name = $scope.WorthVO.type == '1' ? '淨值' : '報酬率';
//        	$scope.data[0].key = name;
//        	$scope.options.chart.yAxis.axisLabel = name;
//        	$scope.options.title.text = '歷史' + name;
//			$scope.sendRecv("PRD110", "getWorth", "com.systex.jbranch.app.server.fps.prd110.PRD110InputVO", $scope.WorthVO,
//					function(tota, isError) {
//						if (!isError) {
//							$scope.CHART = tota[0].body.resultList;
//							$scope.data[0].values = [];
//							if($scope.WorthVO.type == '1') {
//								for (var i = 0; i < $scope.CHART.length; i++) {
//									$scope.data[0].values.push({ x: $scope.toJsDate($scope.CHART[i].PRICE_DATE), y: $scope.CHART[i].PRICE});
//								}
//								// 近三十日
//								$scope.thirty = [];
//								for (var i = $scope.CHART.length - 1; i >= 0; i--) {
//									if($scope.thirty.length >= 30)
//										break;
//									$scope.thirty.push({'date':$scope.toJsDate($scope.CHART[i].PRICE_DATE),'value':$scope.CHART[i].PRICE});
//								}
//							} else {
//								for (var i = 0; i < $scope.CHART.length; i++) {
//									$scope.data[0].values.push({ x: $scope.toJsDate($scope.CHART[i].RETURN_DATE), y: $scope.CHART[i].RETUEN});
//								}
//							}
//				            return;
//						}
//			});
//		};
//		$scope.clearWorth = function() {
//			$scope.options.title.text = '';
//			$scope.data = [{"key" : '',"color" : "#278F54","values" : []}];
//			//
//			$scope.thirty = [];
//			//
//		};
		// -------------------------------------WORTH----------------------------------------
		
		//for App
		if (typeof(webViewParamObj) != 'undefined') {
			$scope.fromApp = true;
		}
});