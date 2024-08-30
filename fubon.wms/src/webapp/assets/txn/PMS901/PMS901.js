/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS901Controller',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS901Controller";
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
        
        $scope.mappingSet['SETUP_CAT'] =[];
        $scope.mappingSet['SETUP_CAT'].push({LABEL: '以日期區間統計', DATA: '1'});
        $scope.mappingSet['SETUP_CAT'].push({LABEL: '以組織統計', DATA: '2'});
//        $scope.mappingSet['SETUP_CAT'].push({LABEL: '以商品統計', DATA: '3'});
        
        $scope.mappingSet['SETUP_PROD'] =[];
        $scope.mappingSet['SETUP_PROD'].push({LABEL: 'A/C板塊', DATA: '25'});
        $scope.mappingSet['SETUP_PROD'].push({LABEL: '個人/法人', DATA: '26'});
        $scope.mappingSet['SETUP_PROD'].push({LABEL: '全商品(投資、存款、保險)', DATA: '1'});
        $scope.mappingSet['SETUP_PROD'].push({LABEL: '投資全商品', DATA: '2'});
        $scope.mappingSet['SETUP_PROD'].push({LABEL: '存款全商品', DATA: '3'});
        $scope.mappingSet['SETUP_PROD'].push({LABEL: '保險全商品', DATA: '4'});
        $scope.mappingSet['SETUP_PROD'].push({LABEL: '投資_基金', DATA: '5'});
        $scope.mappingSet['SETUP_PROD'].push({LABEL: '投資_股票', DATA: '6'});
        $scope.mappingSet['SETUP_PROD'].push({LABEL: '投資_ETF', DATA: '7'});
        $scope.mappingSet['SETUP_PROD'].push({LABEL: '投資_SI', DATA: '8'});
        $scope.mappingSet['SETUP_PROD'].push({LABEL: '投資_SN', DATA: '9'});
        $scope.mappingSet['SETUP_PROD'].push({LABEL: '投資_海外債', DATA: '10'});
        $scope.mappingSet['SETUP_PROD'].push({LABEL: '投資_RP複委託', DATA: '11'});
        $scope.mappingSet['SETUP_PROD'].push({LABEL: '投資_指定單獨信託', DATA: '12'});
        $scope.mappingSet['SETUP_PROD'].push({LABEL: '投資_黃金存摺', DATA: '13'});
        $scope.mappingSet['SETUP_PROD'].push({LABEL: '投資_DCI', DATA: '14'});
        $scope.mappingSet['SETUP_PROD'].push({LABEL: '投資_奈米投', DATA: '15'});
        $scope.mappingSet['SETUP_PROD'].push({LABEL: '存款_台幣活存', DATA: '16'});
        $scope.mappingSet['SETUP_PROD'].push({LABEL: '存款_台幣定存', DATA: '17'});
        $scope.mappingSet['SETUP_PROD'].push({LABEL: '存款_外幣活存', DATA: '18'});
        $scope.mappingSet['SETUP_PROD'].push({LABEL: '存款_外幣定存', DATA: '19'});
        $scope.mappingSet['SETUP_PROD'].push({LABEL: '存款_台幣支存', DATA: '20'});
        $scope.mappingSet['SETUP_PROD'].push({LABEL: '保險_投資型', DATA: '21'});
        $scope.mappingSet['SETUP_PROD'].push({LABEL: '保險_躉繳票', DATA: '22'});
        $scope.mappingSet['SETUP_PROD'].push({LABEL: '保險_短年期繳', DATA: '23'});
        $scope.mappingSet['SETUP_PROD'].push({LABEL: '保險_長年期繳', DATA: '24'});
        
        /** 
         * 數字格式化
         * c: 小數點後顯示幾位, d: 小數點符號, t: 千分位符號
         */
        Number.prototype.numberFormat = function(c, d, t) {
        	var n = this, 
        	    c = isNaN(c = Math.abs(c)) ? 2 : c, 
        	    d = d == undefined ? "." : d, 
        	    t = t == undefined ? "," : t, 
        	    s = n < 0 ? "-" : "", 
        	    i = String(parseInt(n = Math.abs(Number(n) || 0).toFixed(c))), 
        	    j = (j = i.length) > 3 ? j % 3 : 0;
        	   return s + (j ? i.substr(0, j) + t : "") + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + t) + (c ? d + Math.abs(n - i).toFixed(c).slice(2) : "");
        };
        	 
		// 初始化
		$scope.init = function(){
			$scope.inputVO = {
					yyyymm				: '',
					region_center_id	: '',   // 區域中心
					branch_area_id		: '', 	// 營運區
					branch_nbr			: '',	// 分行	
					emp_id				: '',	// 員編
					memLoginFlag		: String(sysInfoService.getMemLoginFlag())
        	};
			var min_mon = new Date();
			min_mon.setMonth(min_mon.getMonth() - 6);
			min_mon.setHours(0, 0, 0, 0);
			$scope.inputVO.sDate = min_mon;
			var min_mon2 = new Date();
			min_mon2.setHours(0, 0, 0, 0);
			$scope.inputVO.eDate = min_mon2;
			$scope.limitDate();
			
			$scope.dateChange();
			
			$scope.inputVO.setupCategory  ='1';
			$scope.inputVO.setupProd  ='1';
		};
		$scope.init();
		
		var drawChartByDate = function (data) {
			var keyList = Object.keys(data[0]);
			
			var chartData = {
					animationEnabled: true,
					dataPointWidth: 35,
					title:{
						text: "AUM資產分布圖",
						fontFamily: "arial black",
						fontColor: "#695A42"
					},
					axisX: {
						interval: 1,
						intervalType: "month",
						valueFormatString: "YYYY/MM"
					},
					axisY:{
						valueFormatString:"$###,###,###,##0(萬元)",
						gridColor: "#B6B1A8",
						tickColor: "#B6B1A8"
					},
					toolTip: {
						shared: true,
						content: toolTipContentByDate
					}	
			}
			
			var colorSet = ["#E9967A", "#C1CDC1", "#8B7B8B", "#A2B5CD", "#9370DB", "#FFDAB9", "#EEC591", "#E6E6FA", "#BEBEBE", "#ADD8E6", "#CDBE70", "#CD9B9B"];
//			var colorSet = ["#BFC8D7", "#D2D2D2", "#E3E2B4", "#A2B59F", "#E8E7D2", "#BDC2BB", "#C9BA9B", "#C9CBE0", "#CCDBE2", "#ECD4D4", "#D5E1DF", "#83B1C9"];
			chartData.data = [];
			angular.forEach(keyList, function(key, index) {
				if (key != 'YEARMON') {
					var points = [];
					angular.forEach(data, function(row) {
						var year = row.YEARMON.substring(0, 4);
						var mon = row.YEARMON.substring(4);
						mon = mon.substring(0, 1) == '0' ? mon.substring(1) -1 : mon -1;
						points.push({y: row[key], x: new Date(year, mon)});
					});
					var map = {        
						type: "stackedColumn",
						showInLegend: true, 
						name: key,
						xValueFormatString: "MMM, YYYY",
						color: colorSet[index],
						dataPoints: points
					}
					chartData.data.push(map);
//					alert(JSON.stringify(points));
				}
			});
			var chart = new CanvasJS.Chart('PMS901chartContainer', chartData);
			chart.render();
		}

		function toolTipContentByDate(e) {
			var str = "";
			var total = 0;
			var str2, str3;
			for (var i = 0; i < e.entries.length; i++){
				var  str1 = "<span style= \"color:"+e.entries[i].dataSeries.color + "\"> "+e.entries[i].dataSeries.name+"</span>: $<strong>"+e.entries[i].dataPoint.y.numberFormat(2, '.', ',')+"(萬)</strong><br/>";
				total = e.entries[i].dataPoint.y + total;
				str = str.concat(str1);
			}
			var mon = (e.entries[0].dataPoint.x).getMonth() + 1;
			if(mon < 10) mon = "0" + mon;
			str2 = "<span style = \"color:DodgerBlue;\"><strong>"+(e.entries[0].dataPoint.x).getFullYear()+"/"+mon+"</strong></span><br/>";
			total = Math.round(total * 100) / 100;
			str3 = "<span style = \"color:Tomato\">總計:</span><strong> $"+total.numberFormat(2, '.', ',')+"(萬)</strong><br/>";
			return (str2.concat(str)).concat(str3);
		}
		
		var drawChartByOrg = function (data) {
			var keyList = Object.keys(data[0]);
			
			var chartData = {
					animationEnabled: true,
					dataPointWidth: 35,
					title:{
						text: "AUM資產分布圖",
						fontFamily: "arial black",
						fontColor: "#695A42"
					},
					axisX: {
						valueFormatString: "組織"
					},
					axisY:{
						valueFormatString:"$###,###,###,##0(萬元)",
						gridColor: "#B6B1A8",
						tickColor: "#B6B1A8"
					},
					toolTip: {
						shared: true,
						content: toolTipContentByOrg
					}	
			}
			
//			var colorSet = ["#BFC8D7", "#D2D2D2", "#E3E2B4", "#A2B59F", "#E8E7D2", "#BDC2BB", "#C9BA9B", "#C9CBE0", "#CCDBE2", "#ECD4D4", "#D5E1DF", "#83B1C9"];
			var colorSet = ["#E9967A", "#C1CDC1", "#8B7B8B", "#A2B5CD", "#9370DB", "#FFDAB9", "#EEC591", "#E6E6FA", "#BEBEBE", "#ADD8E6", "#CDBE70", "#CD9B9B"];
//			var colorSet = ["#4661EE", "#EC5657", "#1BCDD1", "#8FAABB", "#B08BEB", "#3EA0DD", "#F5A52A", "#23BFAA", "#FAA586", "#EB8CC6"];
			chartData.data = [];
			angular.forEach(keyList, function(key, index) {
				debugger
				if (key != 'ORG_NAME') {
					var points = [];
					var count = 1;
					angular.forEach(data, function(row) {
						points.push({x: count++, y: row[key], label: row.ORG_NAME});
					});
					var map = {        
						type: "stackedColumn",
						showInLegend: true, 
						name: key,
						color: colorSet[index],
						dataPoints: points
					}
					chartData.data.push(map);
//					alert(JSON.stringify(points));
				}
			});
			
			var chart = new CanvasJS.Chart('PMS901chartContainer', chartData);
			chart.render();
		}
		
		function toolTipContentByOrg(e) {
			var str = "";
			var total = 0;
			var str2, str3;
			for (var i = 0; i < e.entries.length; i++){
				var  str1 = "<span style= \"color:"+e.entries[i].dataSeries.color + "\"> "+e.entries[i].dataSeries.name+"</span>: $<strong>"+e.entries[i].dataPoint.y.numberFormat(2, '.', ',')+"(萬)</strong><br/>";
				total = e.entries[i].dataPoint.y + total;
				str = str.concat(str1);
			}
			
			str2 = "<span style = \"color:DodgerBlue;\"><strong>"+e.entries[0].dataPoint.label+"</strong></span><br/>";
			total = Math.round(total * 100) / 100;
			str3 = "<span style = \"color:Tomato\">總計:</span><strong> $"+total.numberFormat(2, '.', ',')+"(萬)</strong><br/>";
			return (str2.concat(str)).concat(str3);
		}
		
		$scope.query = function() {
			$scope.sendRecv("PMS901", "query", "com.systex.jbranch.app.server.fps.pms901.PMS901InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.resultList.length == 0) {
							$scope.showMsg("ehl_01_common_009");	//查無資料
                			return;
                		}
						$scope.resultList = tota[0].body.resultList;
						
						if($scope.inputVO.setupCategory == '1') {
							//以日期區間統計
							drawChartByDate($scope.resultList);
						} else if($scope.inputVO.setupCategory == '2') {
							//以組織統計
							drawChartByOrg($scope.resultList);
						} if($scope.inputVO.setupCategory == '3') {
							//以商品統計
							
						}
					}
			});
		}
		$scope.query();
		
});