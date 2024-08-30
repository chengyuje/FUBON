/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPS325Controller',
  function ($rootScope, $scope, $controller) {
    $controller('BaseController', {
      $scope: $scope
    });
    $scope.controllerName = 'FPS325Controller';

    var mapping = {
      MFDType: [
        '股票型',
        '債券型',
        '平衡型'
      ],
    };

    var charts = {
      lineChart: null,
      doughnutChart: null
    };

    // webview extend
    if (typeof (webViewParamObj) != 'undefined') {
      $scope.closeThisDialog = function (errObj) {
        if (errObj.errorList.length == 0) {
          errObj.errorList = Object.keys($scope.listMap);
        }
        var itemList = [];
        errObj.errorList.forEach(function (row) {
          var errTxt = $scope.listMap[row];
          if (!errTxt) return false;
          itemList.push(errTxt);
        });
        webViewParamObj.formatWebViewResParam(true, itemList);
        window.webkit.messageHandlers.resultCompleted.postMessage(webViewParamObj.getResult());
      }
    }

    // $scope.stockAmt
    // $scope.rateMap
    // $scope.riskType
    //init
    $scope.init = function () {
      $scope.sendRecv('FPS325', 'trialBalance', 'com.systex.jbranch.app.server.fps.fps325.FPS325InputVO',
        $scope.inputVO,
        function (tota, isError) {
          if (!isError) {
            if (tota[0].body.isError) {
              var invalid = tota[0].body.invalidPrdID;
              console.log(invalid);
              $scope.closeThisDialog({
                status: 'error',
                errorList: invalid || []
              });
              return false;
            }

            console.log(tota[0].body);

            $scope.paramList = tota[0].body.outputList.map(function (row) {
              var obj = $scope.inputVO.prdList.filter(function (item) {
                return item.PRD_ID === row.PRD_ID;
              });
              if (obj.length > 0) {
                row.CURRENCY_TYPE = obj[0].CURRENCY_TYPE;
                row.FUND_TYPE = obj[0].FUND_TYPE;
                row.FUND_TYPE_NAME = obj[0].FUND_TYPE_NAME;
                // row.rate = $scope.rateMap[obj[0].CURRENCY_TYPE];
                // row.PURCHASE_ORG_AMT = obj[0].PURCHASE_ORG_AMT;
                // row.PURCHASE_TWD_AMT = obj[0].PURCHASE_TWD_AMT;
              }
              return row;
            });
            $scope.efList = tota[0].body.efList;
            // $scope.cmlList = tota[0].body.cmlList;
            $scope.maxPfoRtn = tota[0].body.maxPfoRtn;
            $scope.minPfoRtn = tota[0].body.minPfoRtn;
            $scope.periodDate = '最大共同資料區間：' + tota[0].body.periodStartDate.substring(0, 4) + '/' + tota[0].body.periodStartDate.substring(4, 6) + ' ~ ' + tota[0].body.periodEndDate.substring(0, 4) + '/' + tota[0].body.periodEndDate.substring(4, 6);

            $scope.yearRtnRange = $scope.minPfoRtn + ' ~ ' + $scope.maxPfoRtn;
            // $scope.cmlYearRtn = $scope.cmlList[1].yearRtn;

            drawLineChart();
            // sharp ratio
            sharpRatio($scope.efList);

            // test
            // var t = {};
            // $scope.efList.forEach((a, i) => {
            //   t[i] = {
            //     yearRtn: a.yearRtn,
            //     yearStd: a.yearStd,
            //     products: a.PfoAllocList.map(a => {
            //       return {
            //         prdID: a.PRD_ID,
            //         twdAmt: a.STORE_NTD,
            //         rawAmt: a.STORE_RAW,
            //         weight: a.WEIGHT
            //       };
            //     }),
            //     divide: a.yearRtn / a.yearStd
            //   };
            // });
            // console.log(JSON.stringify(t));

          } else {
            // $scope.showErrorMsg(tota);
            $scope.closeThisDialog('cancel');
          }
        });
    };

    var drawLineChart = function () {
      var data = [];
      var maxY = 0;
      var minY = 100;
      var maxX = 0;
      var minX = 100;
      angular.forEach($scope.efList, function (e) {
        var yStd = Math.round(e.yearStd * 10000) / 100;
        var yRtn = Math.round(e.yearRtn * 10000) / 100;
        maxY = Math.max(maxY, Math.round(yRtn));
        minY = Math.min(minY, Math.round(yRtn));
        maxX = Math.max(maxX, Math.round(yStd));
        minX = Math.min(minX, Math.round(yStd));
        data.push({
          x: yStd,
          y: yRtn,
          toolTipContent: '年化報酬率: ' + yRtn + '%' + '<br/>' + '年化波動率: ' + yStd + '%',
          PfoAllocList: e.PfoAllocList,
        });
      });

      var chart = new CanvasJS.Chart('lineContainer', {
        // animationEnabled: true,
        toolTip: {
          fontFamily: 'Microsoft JhengHei',
        },
        axisX: {
          includeZero: false,
          minimum: (minX >= 0 && minX - 0.5 < 0) < 0 ? 0 : minX - 0.5,
          maximum: maxX + 0.5
        },
        axisY: {
          includeZero: false,
          gridThickness: 0.5,
          gridColor: '#fff',
          minimum: (minY >= 0 && minY - 0.5 < 0) ? 0 : minY - 0.5,
          maximum: maxY + 0.5
        },
        data: [{
          click: function (e) {
            showData(e);
          },
          markerSize: 10,
          type: 'line',
          dataPoints: data
        }]
      });

      chart.render();
    };

    var showData = function (e) {
      var chartData = [];
      var PfoAllocData = {};
      var fundType = {};

      angular.forEach(e.dataPoint.PfoAllocList, function (value) {
        PfoAllocData[value.PRD_ID] = value;
      });

      var total = 0;
      angular.forEach($scope.paramList, function (value) {
        value.INV_PERCENT = Math.round(PfoAllocData[value.PRD_ID].WEIGHT);
        value.PURCHASE_TWD_AMT = PfoAllocData[value.PRD_ID].STORE_NTD;
        value.PURCHASE_ORG_AMT = PfoAllocData[value.PRD_ID].STORE_RAW;
        if (value.FUND_TYPE_NAME === '貨幣型') return false;
        if (!value.FUND_TYPE_NAME) value.FUND_TYPE_NAME = '平衡型';
        if (fundType[value.FUND_TYPE_NAME] === undefined) {
          fundType[value.FUND_TYPE_NAME] = value.INV_PERCENT;
        } else {
          fundType[value.FUND_TYPE_NAME] += value.INV_PERCENT;
        }
        total += value.INV_PERCENT;
      });

      chartData = Object.keys(fundType).map(function (key) {
        return {
          name: key,
          y: Math.round(fundType[key] / total * 100)
        };
      });

      drawDoughnutChart(chartData);
    };

    var drawDoughnutChart = function (data) {
      var chart = new CanvasJS.Chart('doughnutContainer', {
        colorSet: 'blue',
        data: [{
          type: 'doughnut',
          radius: '100%',
          innerRadius: '40%',
          showInLegend: false,
          dataPoints: data
        }]
      });
      chart.render();
      setChartTable(chart);
    };

    // set chart table datas with colorsets and datapoints
    var setChartTable = function (chart) {
      var chartColor = canvasJsConfig.bgcSets[chart.options.colorSet];
      var chartData = chart.options.data[0].dataPoints;
      var colorCnt = chartColor.length;

      $scope.chartTable = chartData.map(function (data, index) {
        return {
          color: chartColor[index % colorCnt],
          label: data.name,
          value: data.y
        };
      });
    };

    $scope.confirm = function () {
      // 2018/6/13 WV
      if (typeof (webViewParamObj) != 'undefined') {
        webViewParamObj.formatWebViewResParam(false, $scope.paramList);
        window.webkit.messageHandlers.resultCompleted.postMessage(webViewParamObj.getResult());
      } else
        $scope.closeThisDialog({
          status: 'success',
          addList: $scope.paramList || []
        });
    };

    // rtn/std
    var sharpRatio = function (list) {
      var param = 0;
      var index = 0;
      list.reduce(function (a, b, i) {
        var ratio = (b.yearRtn - param) / b.yearStd;
        if (a < ratio) {
          a = ratio;
          index = i;
        }
        return a;
      }, 0);
      var fakePoint = {
        dataPoint: {
          PfoAllocList: list[index].PfoAllocList,
          // VOLATILITY: list[index].VOLATILITY
        }
      };
      showData(fakePoint);
    };

    /* watch */
    $scope.$watch(function () {
      return $('.FPS325>.modal-dialog').css('max-width');
    }, function (oldValue, newValue) {
      if (oldValue !== newValue) {
        Object.keys(charts).forEach(function (chart) {
          drawLineChart();
          sharpRatio($scope.efList);
        });
      }
    });

    //關掉Dialog
    $scope.closePop = function (status) {
      $scope.closeThisDialog({
        status: status,
        addList: $scope.paramList || []
      });
    };

    $scope.init();
  });
