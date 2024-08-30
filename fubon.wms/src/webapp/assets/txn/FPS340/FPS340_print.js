/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPSPrintController',
  function ($scope, $controller, $timeout, FPSUtilsService, $q) {
    $controller('BaseController', {
      $scope: $scope
    });
    $scope.controllerName = 'FPSPrintController';
    var fps300 = $scope.fps300;
    if (typeof (webViewParamObj) != 'undefined') {
    	fps300 = {};
    	fps300.custData = $scope.custInfo;
    }
//    $scope.paramList = fps300.getParamList();
    /* parameter */
    $scope.labelIndex = fps300.custData.SIGN_AGMT_YN === 'Y' ? 7 : 5;
//    var fpsPrint = $scope.fpsPrint;

    /* parameter */
    $scope.path = {
      header: './assets/images/FPS_PDF_Header_3.png',
      noSignAlert: './assets/txn/FPS_PDF_Template/FPSutils_component/noSignAlert.html',
      brief: './assets/txn/FPS_PDF_Template/FPSutils_component/FPS_print_brief.template.html',
      custInfo: './assets/txn/FPS/FPS_CUST.template.html',
      requirement: 'assets/txn/FPS340/FPS340.template.html',

      firstPlanChart: './assets/txn/FPS_PDF_Template/FPS340_component/firstPlanChart.html',
      firstPlanChart_ps: './assets/txn/FPS_PDF_Template/FPS340_component/firstPlanChart_ps.html',

      portRtnSim: './assets/txn/FPS323/FPS323_P.html',
      portRtnSim_ps: './assets/txn/FPS_PDF_Template/FPS340_component/portRtnSim_ps.html',

      firstPlanProduct: './assets/txn/FPS_PDF_Template/FPS340_component/firstPlanProduct.html',
      firstPlanProduct_ps: './assets/txn/FPS_PDF_Template/FPS340_component/firstPlanProduct_ps.html',

      historyPerformance: './assets/txn/FPS_PDF_Template/FPS340_component/historyPerformance.html',
      historyPerformance_ps: './assets/txn/FPS_PDF_Template/FPSutils_component/historyPerformance_ps.html',

      productPerformance: './assets/txn/FPS_PDF_Template/FPSutils_component/productPerformance.html',
      productPerformance_ps: './assets/txn/FPS_PDF_Template/FPSutils_component/productPerformance_ps.html',

      manual: './assets/txn/FPS_PDF_Template/FPSutils_component/manual.html',
      notice: './assets/txn/FPS_PDF_Template/FPSutils_component/notice.html',
    };
    
    $scope.fpsPrevBusiDay = $scope.connector('get', 'fpsPrevBusiDay');

    var YRcolor = canvasJsConfig.YRSet;
    var charts = {
      p_stockChart: null,
      p_classChart: null
    };
    var sortOrder = {
      fundName: {
        股票型: 1,
        債券型: 2,
        平衡型: 3,
        其他: 4
      }
    };
    $scope.chartTables = {
      p_stockChart: [],
      p_classChart: []
    };
    
    var getDrawHistoryChart = function() {
    	var deferred = $q.defer();
    	var formatedData = FPSUtilsService.JLB.formatJLBRow($scope.paramList, {
            AMT: typeof (webViewParamObj) != 'undefined' ? 'PURCHASE_TWD_AMT' : 'NTD_PERCENT',
            TYPE: 'PTYPE'
        });
    	
    	var total = 0;
        angular.forEach(formatedData, function (obj, index) {
      	  total += obj.PURCHASE_TWD_AMT;
        });
        
        if(total == 0) {
      	  $scope.errorText = '投資標的調整後金額為 0 ，無法進行歷史績效表現試算';
            $scope.connector('set', 'FPS330_error', true);
            $scope.$emit('FPS330_error', '投資標的調整後金額為 0 ，無法進行歷史績效表現試算');
            return false;
        }

        $scope.sendRecv('FPS330', 'inquire', 'com.systex.jbranch.app.server.fps.fps330.FPS330InputVO', {
            planID: $scope.planID,
            paramList: formatedData ? formatedData : undefined
          },
          function (tota, isError) {
            $scope.errorText = '';
            if (!isError) {
            	if(!tota[0].body.isError && tota[0].body.yearRateList) {
            		$scope.volatile = 0;
                    $scope.avgVolatile = 0;
                    $scope.minVolatile = 0;
                    $scope.maxVolatile = 0;
                    $scope.yearRateSize = tota[0].body.yearRateList.length;
                    if (tota[0].body.yearRateList.length > 0) {
                      var count = 0;
                      tota[0].body.yearRateList.forEach(function (row) {
                        count += row.RETURN_ANN_M;
                      });
                      var _temp = tota[0].body.yearRateList.map(function (row) {
                        return row.RETURN_ANN_M;
                      });
                      _temp.sort(function (a, b) {
                        return a - b;
                      });
                      $scope.volatile = tota[0].body.volatility ? parseFloat(tota[0].body.volatility).toFixed(2) : '--';
                      $scope.avgVolatile = parseFloat(count / _temp.length).toFixed(2);
                      $scope.minVolatile = parseFloat(_temp[0]).toFixed(2);
                      $scope.maxVolatile = parseFloat(_temp[_temp.length - 1]).toFixed(2);
                      $timeout(function () {
                    	var yearList = tota[0].body.yearRateList;
//                    	if(yearList.length === 1) {
//                    		yearList.push({RETURN_ANN_M: null, YEAR: '0'});
//                    	}
//                        drawChart(yearList);
        				$scope.connector('set', 'FPS330_error', false);
                      });
                      $scope.connector('set', 'FPS330.volatile', $scope.volatile);
                      $scope.connector('set', 'FPS330.avgVolatile', $scope.avgVolatile);
                      $scope.connector('set', 'FPS330.minVolatile', $scope.minVolatile);
                      $scope.connector('set', 'FPS330.maxVolatile', $scope.maxVolatile);
                      $scope.connector('set', 'FPS330.yearRateList', tota[0].body.yearRateList);
                    }
            	} else {
            		$scope.errorText = '投資標的共同區間小於1年，無法進行歷史績效表現試算';
                    $scope.$emit('FPS330_error', '投資標的共同區間小於1年，無法進行歷史績效表現試算');
        			$scope.connector('set', 'FPS330_error', true);
            	}
              
              deferred.resolve('success');
              
            } else {
              $scope.errorText = '投資標的共同區間小於1年，無法進行歷史績效表現試算';
              $scope.$emit('FPS330_error', '投資標的共同區間小於1年，無法進行歷史績效表現試算');
  			  $scope.connector('set', 'FPS330_error', true);
  			  deferred.reject(false);
            }
            $scope.connector('set', 'FPS330.errorText', $scope.errorText);
          });
        return deferred.promise;
    }
    
    var getYearRate = function() {
    	$scope.yearRate = {
    		best: $scope.connector('get', 'FPS330.maxVolatile') ? $scope.connector('get', 'FPS330.maxVolatile') : '--',
    		worst: $scope.connector('get', 'FPS330.minVolatile') ? $scope.connector('get', 'FPS330.minVolatile') : '--',
    		avg: $scope.connector('get', 'FPS330.avgVolatile') ? $scope.connector('get', 'FPS330.avgVolatile') : '--'
    	};
    }

    /* init */
    var init = function () {
    	if(typeof (webViewParamObj) != 'undefined') {
    		if(fps300.custData.SIGN_AGMT_YN === 'Y') {
    			getDrawHistoryChart().then(function(){
    				getYearRate();
    			});
    		}
    	} else {
    		getYearRate();
    	}
    	
    	// chart
    	drawDoughnut();
    	// rpt image
    	var rpt = $scope.rptPicture[0];
    	$scope.rptUrl = '';
    	if (rpt && rpt.RPT_PIC) {
    		var bufferArr = new Uint8Array(rpt.RPT_PIC);
	        var length = bufferArr.length;
	        var result = '';
	        var addition = Math.pow(2, 16) - 1;
	        for (var i = 0; i < length; i += addition) {
	          if (i + addition > length)
	        	  addition = length - i;
	          result += String.fromCharCode.apply(null, bufferArr.subarray(i, i + addition));
	        }
	        $scope.rptUrl = "data:image/png;base64," + btoa(result);
	        $scope.rptUrleName = rpt.RPT_PIC_NAME;
    	}
    };
    
    var getFPSPrintData = function() {
    	var temp = {};
        var temp2 = {};
        var index1 = 0;
        var index2 = 0;
    	$scope.paramList.forEach(function (row) {
            // 股價配置
            if (temp[row.FUND_TYPE_NAME] === undefined) {
              temp[row.FUND_TYPE_NAME] = {
                y: row.INV_PERCENT,
                legendText: row.FUND_TYPE_NAME,
                indexLabelLineColor: 'transparent',
                indexLabel: row.INV_PERCENT + '%'
              };
              index1++;
            } else {
              temp[row.FUND_TYPE_NAME].y += row.INV_PERCENT;
              temp[row.FUND_TYPE_NAME].indexLabel = temp[row.FUND_TYPE_NAME].y + '%';
            }
            
            // 類別配置
            if (temp2[row.NAME] === undefined) {
              temp2[row.NAME] = {
                y: row.INV_PERCENT,
                legendText: row.NAME,
                indexLabelLineColor: 'transparent',
                indexLabel: row.INV_PERCENT + '%'
              };
              index2++;
            } else {
              temp2[row.NAME].y += row.INV_PERCENT;
              temp2[row.NAME].indexLabel = temp2[row.NAME].y + '%';
            }
    	});
    	
    	var dataPoints = Object.keys(temp).map(function (key) {
            return temp[key];
    	});
    	
    	// 股票/債券及平衡排序固定
    	dataPoints = dataPoints.sort(function (a, b) {
    		return sortOrder.fundName[a.legendText] > sortOrder.fundName[b.legendText];
    	});

        // 依佔比高到低排序
        var otherIndex = 3;
        var dataPoints_2 = Object.keys(temp2).map(function (key) {
        	return temp2[key];
        }).sort(function (a, b) {
            return b.y - a.y;
        }).reduce(function (a, b, i) {
            if (i < 4) {
              if (b.legendText === '其他') {
                otherIndex = i;
              }
              a.push(b);
            } else {
              a[otherIndex].y += b.y;
              a[otherIndex].indexLabel = a[otherIndex].y + '%';
              a[otherIndex].legendText = '其他';
            }
            return a;
        }, []).sort(function (a, b) {
            return b.y - a.y;
        });

        var chartData = {
    		colorSet: 'blue',
    		backgroundColor: 'transparent',
    		data: [{
    			radius: '100%',
    			innerRadius: '40%',
    			type: 'doughnut',
    			showInLegend: false,
    			dataPoints: dataPoints
    		}]
        };

        var chartData_2 = {
            colorSet: 'yellow',
            backgroundColor: 'transparent',
            data: [{
            	radius: '100%',
            	innerRadius: '40%',
            	type: 'doughnut',
            	showInLegend: false,
            	dataPoints: dataPoints_2
            }]
        };
        
        var fpsPrint = {
        	stockChart : chartData,
        	classChart : chartData_2
        }
        
        for (var i = 0; i < fpsPrint.stockChart.data["0"].dataPoints.length; i++) {
			fpsPrint.stockChart.data["0"].dataPoints[i].legendText = "(" + (i + 1) + ") " +
			fpsPrint.stockChart.data["0"].dataPoints[i].legendText;
			fpsPrint.stockChart.data["0"].dataPoints[i].indexLabel = "(" + (i + 1) + ") " +
			fpsPrint.stockChart.data["0"].dataPoints[i].indexLabel;
		}
		for (var i = 0; i < fpsPrint.classChart.data["0"].dataPoints.length; i++) {
			fpsPrint.classChart.data["0"].dataPoints[i].legendText = "(" + (i + 1) + ") " +
			fpsPrint.classChart.data["0"].dataPoints[i].legendText;
			fpsPrint.classChart.data["0"].dataPoints[i].indexLabel = "(" + (i + 1) + ") " +
			fpsPrint.classChart.data["0"].dataPoints[i].indexLabel;
		}
        return fpsPrint;
    }

    /* main function */
    var drawDoughnut = function () {
		var fpsPrint = null;
		if(typeof (webViewParamObj) != 'undefined') {
			fpsPrint = getFPSPrintData();
		} else {
			fpsPrint = $scope.fpsPrint;      
		}
      $timeout(function () {
        charts.p_stockChart = null;
        charts.p_stockChart = new CanvasJS.Chart('p_stockChart', fpsPrint.stockChart);
        charts.p_stockChart.render();
        setChartTable('p_stockChart');

        charts.p_classChart = null;
        charts.p_classChart = new CanvasJS.Chart('p_classChart', fpsPrint.classChart);
        charts.p_classChart.render();
        setChartTable('p_classChart');

        $scope.calInvestment();

        $timeout(function () {
          if (fps300.custData.SIGN_AGMT_YN === 'Y') {
            yearRateBar('yearRateChart', $scope.connector('get', 'FPS330.yearRateList'));
            $scope.yearRateSize = $scope.connector('get', 'FPS330.yearRateList').length;
            $scope.errorText = $scope.connector('get', 'FPS330.errorText');
          }
        });
      });
    };
    
    //投組試算 FPS323 start
    $scope.calInvestment = function () {
      var formatedData = FPSUtilsService.JLB.formatJLBRow($scope.paramList, {
    	  AMT: typeof (webViewParamObj) != 'undefined' ? 'PURCHASE_TWD_AMT' : 'NTD_PERCENT',
    	  TYPE: 'PTYPE'
      });
      // 計算單筆金額 計算定額金額
      var oneTime = 0;
      var perMonth = 0;
      $scope.paramList.forEach(function (row) {
        if (row.PTYPE === 'INS')
          oneTime += parseInt(row.NTD_PERCENT, 10);
        else if (row.INV_TYPE === '1')
          oneTime += parseInt(row.NTD_PERCENT, 10);
        else if (row.INV_TYPE === '2')
          perMonth += parseInt(row.NTD_PERCENT, 10);
      });

      // FPS323 績效模擬
      $scope.$broadcast('FPS323_drawMonteCarlo', {
        custID: $scope.custID,
        planID: $scope.planID,
        ret: formatedData,
        beginAmount: parseInt(oneTime, 10),
        eachYear: parseInt(perMonth, 10) * 12,
        year: parseInt($scope.recommendations.PLANHEAD, 10),
        target: parseInt($scope.recommendations.TARGET, 10)
      });
    };

    // set chart table datas with colorsets and datapoints
    var setChartTable = function (chartName) {
      var chartColor = canvasJsConfig.bgcSets[charts[chartName].options.colorSet];
      var chartData = charts[chartName].options.data[0].dataPoints;
      var colorCnt = chartColor.length;

      $scope.chartTables[chartName] = chartData.map(function (data, index) {
        return {
          color: chartColor[index % colorCnt],
          label: data.legendText,
          value: data.y
        };
      });
    };

    // yearRate chart
    var yearRateBar = function (chartName, data) {
      if (!data || data.length <= 0) return false;
      var temp = {};
      var len = data.length;
      var chartData = {
        axisY: {
          valueFormatString: '#,##0',
          labelFontSize: 14,
          gridThickness: 0.5,
          gridColor: '#fff',
          stripLines: [{
            value: 0,
            color: '#bbb',
            thickness: 2
          }]
        },
        axisX: {
          labelFontSize: 14,
        },
        dataPointWidth: 20,
      };
      var limit = [];
      data.forEach(function (row) {
        temp[row.YEAR] = {
          y: row.RETURN_ANN_M,
          label: row.YEAR,
          color: row.RETURN_ANN_M >= 0 ? YRcolor.raise : YRcolor.fall
        };
        limit.push(row.RETURN_ANN_M);
      });

//      var max = limit.reduce(function (a, b) {
//        return Math.max(Math.abs(a), Math.abs(b));
//      });
//      
//      var min = limit.reduce(function (a, b) {
//          return Math.min(a, b);
//      });
//
//		// y軸最大值上限 多3%放數字
//		chartData.axisY.maximum = max + 5;
//		// y軸最小值下限 多3%放數字
//		chartData.axisY.minimum = min - 5;
//		
//		if((max + 5)<0 && (min - 5)<0) {
//		  chartData.axisY.maximum = 5;
//		}
//		
//		if((max + 5)>0 && (min - 5)>0) {
//		  chartData.axisY.minimum = -5;
//		}
//        
//        chartData.axisY.interval = Math.round((max - (min > 0 ? 0 : min)) / 3);
      
//      chartData.axisY.interval = Math.round((max + 10) / 3);
//      // y軸最大值上限 多3%放數字
//      chartData.axisY.maximum = max + Math.floor(chartData.axisY.interval);
//      // y軸最小值下限 多3%放數字
//      chartData.axisY.minimum = (max * -1) - Math.floor(chartData.axisY.interval);
      
      if(limit.length == 1) {
    	  limit.push(0);
      }
      
      var max = limit.reduce(function (a, b) {
          return Math.max(Math.abs(a), Math.abs(b));
      });

      chartData.axisY.interval = Math.round((max + 10) / 3);
      // y軸最大值上限 多3%放數字
      chartData.axisY.maximum = max + Math.floor(chartData.axisY.interval / 2);
      // y軸最小值下限 多3%放數字
      chartData.axisY.minimum = (max * -1) - Math.floor(chartData.axisY.interval / 2);

      chartData.axisX.labelMaxWidth = 40;
      chartData.axisX.labelWrap =  false;
      chartData.axisX.labelAngle = 200;
      
      var dataPoints = Object.keys(temp).map(function (key) {
        return temp[key];
      });

      if (len < 5) {
        chartData.data = [{
          indexLabelFontFamily: 'Microsoft JhengHei',
          type: 'bar',
          yValueFormatString: "#0.##",
          indexLabel: '{y}',
          indexLabelFontSize: 12,
          indexLabelFontWeight: "bold",
          indexLabelPlacement: 'outside',
          indexLabelOrientation: 'horizontal',
          dataPoints: dataPoints
        }];
      } else {
        chartData.data = [{
          indexLabelFontFamily: 'Microsoft JhengHei',
          type: 'column',
          yValueFormatString: "#0.##",
          indexLabel: '{y}',
          indexLabelFontSize: 12,
          indexLabelFontWeight: "bold",
          indexLabelPlacement: 'outside',
          indexLabelOrientation: 'horizontal',
          dataPoints: dataPoints
        }];
      }
      var chart = new CanvasJS.Chart(chartName, chartData);
      chart.render();
    };
    
    $scope.$on('FPS323_error', function (event, data) {
    	if (typeof (webViewParamObj) == 'undefined') {
    		$scope.labelIndex = $scope.labelIndex - 1;
    	}
    	$scope.showFPS323Error = true;
    	$scope.calInvestmentText = data;
    });
    
    $scope.$on('FPS330_error', function (event, data) {
    	if (typeof (webViewParamObj) == 'undefined') {
    		$scope.labelIndex = $scope.labelIndex - 1;
    	}
    	$scope.showFPS330Error = true;
    	$scope.calModelPorfolioText = data;
    });

    /* main progress */
    console.log('FPSPrintController');
    var watchCharts = $scope.$watch(function () {
    	return document.getElementById('charts');
    }, function (a, b) {
    	init();
    	if (a != null) {
	        if (typeof (webViewParamObj) != 'undefined') document.querySelector('.FPS .print.A4').classList.add('iPad');
	        watchCharts();
        }
    });
  }
);
