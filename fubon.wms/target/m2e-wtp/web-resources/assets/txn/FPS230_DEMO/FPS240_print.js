/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPSDEMOPrintController',
  function ($scope, $controller, $timeout) {
    $controller('BaseController', {
      $scope: $scope
    });
    $scope.controllerName = 'FPSDEMOPrintController';

    /* parameter */
    $scope.path = {
      header: './assets/images/FPS_PDF_Header_3.png',
      noSignAlert: './assets/txn/FPS_PDF_Template_DEMO/FPSutils_component/noSignAlert.html',
      brief: './assets/txn/FPS_PDF_Template_DEMO/FPSutils_component/FPS_print_brief.template.html',
      custInfo: './assets/txn/FPS/FPS_CUST.template.html',

      validAmount: './assets/txn/FPS_PDF_Template_DEMO/FPS240_component/validAmount.html',
      validAmount_ps: './assets/txn/FPS_PDF_Template_DEMO/FPS240_component/validAmount_ps.html',

      firstPlanProduct: './assets/txn/FPS_PDF_Template_DEMO/FPS240_component/firstPlanProduct.html',
      hasStockProduct: './assets/txn/FPS_PDF_Template_DEMO/FPS240_component/hasStockProduct.html',

      firstPlanChart: './assets/txn/FPS_PDF_Template_DEMO/FPS240_component/firstPlanChart.html',
      firstPlanChart_ps: './assets/txn/FPS_PDF_Template_DEMO/FPS240_component/firstPlanChart_ps.html',
      hasStockChart: './assets/txn/FPS_PDF_Template_DEMO/FPS240_component/hasStockChart.html',
      hasStockChartConclusion: './assets/txn/FPS_PDF_Template_DEMO/FPS240_component/hasStockChartConclusion.html',

      historyPerformance: './assets/txn/FPS_PDF_Template_DEMO/FPS240_component/historyPerformance.html',
      historyPerformance_ps: './assets/txn/FPS_PDF_Template_DEMO/FPSutils_component/historyPerformance_ps.html',

      productPerformance: './assets/txn/FPS_PDF_Template_DEMO/FPSutils_component/productPerformance.html',
      productPerformance_ps: './assets/txn/FPS_PDF_Template_DEMO/FPSutils_component/productPerformance_ps.html',
      pendingExecution: './assets/txn/FPS_PDF_Template_DEMO/FPSutils_component/pendingExecution.html',
      transaction: './assets/txn/FPS_PDF_Template_DEMO/FPS240_component/transaction.html',

      manual: './assets/txn/FPS_PDF_Template_DEMO/FPSutils_component/manual.html',
      notice: './assets/txn/FPS_PDF_Template_DEMO/FPSutils_component/notice.html',
    };

    var YRcolor = canvasJsConfig.YRSet;
    var fpsChart = $scope.fpsChart;
    var charts = {
      p_amountChart: null,
      p_stockChart: null,
      p_beforeAmountChart: null,
      p_afterAmountChart: null,
      p_beforeStockChart: null,
      p_afterStockChart: null
    };
    $scope.chartTables = {
      p_amountChart: [],
      p_stockChart: [],
      p_beforeAmountChart: [],
      p_afterAmountChart: [],
      p_beforeStockChart: [],
      p_afterStockChart: []
    };
    var compareMap = {
      '0': '持平',
      '1': '加碼',
      '-1': '減碼',
    };

    var stockChartCell = [
      '股票型',
      '債券型',
      '平衡型'
    ];

    /* init */
    var init = function () {
    	debugger
      // block objects of products
      $scope.blockObjects = [{
        title: '類債券商品',
        nowPct: $scope.fixedNowPct,
        pct: $scope.fixedPct,
        amt: $scope.fixedAmt,
        list: $scope.fixedList
      }, {
        title: '類股票商品',
        nowPct: $scope.stockNowPct,
        pct: $scope.stockPct,
        amt: $scope.stockAmt,
        list: $scope.stockList
      }];

      // count yearRate
      var yearRateNums = $scope.yearRateList && $scope.yearRateList.length > 0 ? $scope.yearRateList.map(function (row) {
        return row.RETURN_ANN_M;
      }) : null;

      $scope.yearRate = {
        best: yearRateNums ? Math.max.apply(Math, yearRateNums).toFixed(2) : '--',
        worst: yearRateNums ? Math.min.apply(Math, yearRateNums).toFixed(2) : '--',
        avg: yearRateNums ? ($scope.yearRateList.reduce(function (a, b) {
          return a + Number(b.RETURN_ANN_M);
        }, 0) / $scope.yearRateList.length).toFixed(2) : '--'
      };

      // charts
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
//        $scope.rptUrl = 'data:image/png;base64,' + btoa(result);
        $scope.rptUrl = $scope.rptPicture[0];
        $scope.rptUrleName = rpt.RPT_PIC_NAME;
      }
      // hasIns
      if ($scope.hasIns) {
        $scope.compared = {};
        $scope.mapping.keys.forEach(function (key) {
          $scope.compared[key] = compareMap[compareTo($scope[key + 'Amt'], $scope.headList[key + 'Amt'])];
        });
      }

      // no data
      $scope.notShow = {
        historyPerformance: true,
        productPerformance: true,
        transaction: $scope.hasIns && $scope.transactionList.length <= 0
      };
    };

    /* main function */
    var drawDoughnut = function () {
    	debugger
      $timeout(function () {
        if ($scope.hasIns) {
          amountDought('p_beforeAmountChart', fpsChart.amountDataPoints($scope.headList, true), $scope.chartTables, '80');
          amountDought('p_afterAmountChart', fpsChart.amountDataPoints($scope, true), $scope.chartTables, '80');
          stockDought('p_beforeStockChart', extendWholeTableCell(stockChartCell, fpsChart.stockDataPoints($scope.stockList, 'NOW_AMT_TWD', false), 'legendText'), $scope.chartTables, '80');
          stockDought('p_afterStockChart', extendWholeTableCell(stockChartCell, fpsChart.stockDataPoints($scope.stockList, 'PURCHASE_TWD_AMT', false), 'legendText'), $scope.chartTables, '80');
        } else {
          amountDought('p_amountChart', fpsChart.amountDataPoints($scope, true), $scope.chartTables, '100');
          stockDought('p_stockChart', fpsChart.stockDataPoints($scope.stockList, 'PURCHASE_TWD_AMT', true), $scope.chartTables, '100');
        }
        yearRateBar('yearRateChart', $scope.yearRateList);
        console.log($scope.chartTables);
      });
    };

    var amountDought = function (chartName, dataPoints, tableRoot, radius) {
      var chartData = {
        colorSet: 'tri',
        backgroundColor: 'transparent',
        data: [{
          indexLabelFontFamily: 'Microsoft JhengHei',
          indexLabelFontSize: 12,
          radius: radius || '100' + '%',
          innerRadius: '40%',
          type: 'doughnut',
          showInLegend: false,
          dataPoints: dataPoints
        }]
      };

      charts[chartName] = null;
      charts[chartName] = new CanvasJS.Chart(chartName, chartData);
      charts[chartName].render();
      tableRoot[chartName] = fpsChart.setChartTable.call(this, charts[chartName], true);
    };

    var stockDought = function (chartName, dataPoints, tableRoot, radius) {
      var chartData = {
        colorSet: 'blue',
        backgroundColor: 'transparent',
        data: [{
          indexLabelFontFamily: 'Microsoft JhengHei',
          indexLabelFontSize: 12,
          radius: radius || '100' + '%',
          innerRadius: '40%',
          type: 'doughnut',
          showInLegend: false,
          dataPoints: dataPoints
        }]
      };

      charts[chartName] = null;
      charts[chartName] = new CanvasJS.Chart(chartName, chartData);
      charts[chartName].render();
      tableRoot[chartName] = fpsChart.setChartTable.call(this, charts[chartName], true);
    };

    // 沒有顯示的補上
    var extendWholeTableCell = function (points, dataPoints, key) {
      if (!dataPoints.length) return [];
      var i = 0;
      var _dataPoints = points.map(function (cell, index) {
        if (!dataPoints[i] || cell !== dataPoints[i][key])
          return {
            y: 0,
            indexLabel: '(' + (index + 1) + ') 0%',
            legendText: cell,
            indexLabelLineColor: 'transparent'
          };
        else
          dataPoints[i].indexLabel = '(' + (index + 1) + ') ' + dataPoints[i].y + '%';;
        return dataPoints[i++];
      });
      return _dataPoints;
    };

    // yearRate chart
    var yearRateBar = function (chartName, data) {
      if ($scope.custInfo.SIGN_AGMT_YN !== 'Y' || !data || data.length <= 0) return false;

      var temp = {};
      var len = data.length;
      var chartData = {
        axisY: {
          // interval: 5,
          indexLabelFontFamily: 'Microsoft JhengHei',
          valueFormatString: '#,##0\'%\'',
          //title: '(報酬率%)',
          labelFontSize: 14,
          gridThickness: 0.5,
          gridDashType: 'dash',
          gridColor: '#fff',
          stripLines: [{
            value: 0,
            color: '#bbb',
            thickness: 2
          }]
        },
        axisX: {
          labelFontSize: 14,
          //title: '(年份)',
        },
        dataPointWidth: 20,
        //dataPointWidth: 30
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

      var max = limit.reduce(function (a, b) {
        return Math.max(a, b);
      });
      var min = limit.reduce(function (a, b) {
        return Math.min(a, b);
      });

      // y軸最大值上限 多3%放數字
      chartData.axisY.maximum = max + 5;
      // y軸最小值下限 多3%放數字
      chartData.axisY.minimum = min - 5;
      chartData.axisY.interval = Math.round((max - (min > 0 ? 0 : min)) / 3);

      var dataPoints = Object.keys(temp).map(function (key) {
        return temp[key];
      });
      if (len < 5) {
        chartData.data = [{
          indexLabelFontFamily: 'Microsoft JhengHei',
          type: 'bar',
          yValueFormatString: '#0.00\'%\'',
          indexLabel: '{y}',
          indexLabelFontSize: 14,
          indexLabelPlacement: 'outside',
          indexLabelOrientation: 'horizontal',
          dataPoints: dataPoints
        }];
      } else {
        chartData.data = [{
          indexLabelFontFamily: 'Microsoft JhengHei',
          type: 'column',
          yValueFormatString: '#0.00\'%\'',
          indexLabel: '{y}',
          indexLabelFontSize: 14,
          indexLabelPlacement: 'outside',
          indexLabelOrientation: 'horizontal',
          dataPoints: dataPoints
        }];
      }
      var chart = new CanvasJS.Chart(chartName, chartData);
      chart.render();
    };

    /* helping function */
    var compareTo = function (a, b) {
      var x = a - b;
      if (x === 0) {
        return 0;
      } else if (x > 0) {
        return 1;
      } else if (x < 0) {
        return -1;
      }
    };

    /* main progress */
    console.log('FPSPrintController');
    debugger
    var watchCharts = $scope.$watch(function () {
      return document.getElementById('charts') != null;
    }, function (a, b) {
      if (a) {
    	  debugger
        if (typeof (webViewParamObj) != 'undefined') document.querySelector('.FPS .print.A4').classList.add('iPad');
        init();
        watchCharts();
      }
    });
  }
);
