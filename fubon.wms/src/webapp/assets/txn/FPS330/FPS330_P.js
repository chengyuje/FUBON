/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPS330_PController',
  function ($scope, $controller, ngDialog, socketService, alerts, projInfoService, $q, getParameter, $filter, $timeout) {
    $controller('BaseController', {
      $scope: $scope
    });
    $scope.controllerName = 'FPS330_PController';
    $scope.errorText;

    var fps300 = $scope.fps300;
    var YRcolor = canvasJsConfig.YRSet;
    $scope.$on('FPS330_drawHistoryChart_p', function (event, data) {
      console.log({
        planID: fps300.getPlanID(),
        paramList: data ? data : undefined
      });
      $scope.sendRecv('FPS330', 'inquire', 'com.systex.jbranch.app.server.fps.fps330.FPS330InputVO', {
          planID: fps300.getPlanID(),
          paramList: data ? data : undefined
        },
        function (tota, isError) {
          if (!isError) {
            $scope.errorText = '';
            if (tota[0].body.isError) {
              $scope.errorText = '投資標的共同區間小於1年，無法進行歷史績效表現試算';
              $scope.connector('set', 'FPS330_error', true);
              $scope.$emit('FPS330_error', '投資標的共同區間小於1年，無法進行歷史績效表現試算');
              return;
            }
            if (!tota[0].body.yearRateList) {
                $scope.errorText = '投資標的共同區間小於1年，無法進行歷史績效表現試算';
                $scope.connector('set', 'FPS330_error', true);
                $scope.$emit('FPS330_error', '投資標的共同區間小於1年，無法進行歷史績效表現試算');
                return;
            }
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
                drawChart(tota[0].body.yearRateList);
              });
              $scope.connector('set', 'FPS330.volatile', $scope.volatile);
              $scope.connector('set', 'FPS330.avgVolatile', $scope.avgVolatile);
              $scope.connector('set', 'FPS330.minVolatile', $scope.minVolatile);
              $scope.connector('set', 'FPS330.maxVolatile', $scope.maxVolatile);
            }
          } else {
            $scope.errorText = '投資標的共同區間小於1年，無法進行歷史績效表現試算';
            $scope.connector('set', 'FPS330_error', true);
            $scope.$emit('FPS330_error', '投資標的共同區間小於1年，無法進行歷史績效表現試算');
            // $scope.showErrorMsg();
          }
        });
    });

    var drawChart = function (yRList) {
      var temp = {};
      var len = yRList.length;
      var chartData = {
        title: {
          //text: '年報酬率'
        },
        axisY: {
          // interval: 5,
          valueFormatString: '#,##0\'%\'',
          //title: '(報酬率%)',
          gridThickness: 0.5,
          gridColor: '#fff',
          stripLines: [{
            value: 0,
            color: '#bbb',
            thickness: 2
          }]
        },
        axisX: {
          //title: '(年份)',
        },
        dataPointWidth: 20,
        //dataPointWidth: 30
      };
      var limit = [];
      yRList.forEach(function (row) {
        temp[row.YEAR] = {
          y: row.RETURN_ANN_M,
          label: row.YEAR,
          color: row.RETURN_ANN_M >= 0 ? YRcolor.raise : YRcolor.fall
        };
        limit.push(row.RETURN_ANN_M);
      });

      var max = limit.reduce(function (a, b) {
        return Math.max(Math.abs(a), Math.abs(b));
      });

      // y軸最大值上限 多3%放數字
      chartData.axisY.maximum = max + 10;
      // y軸最小值下限 多3%放數字
      chartData.axisY.minimum = (max * -1) - 10;
      chartData.axisY.interval = Math.round((max + 10) / 3);
      // chartData.axisY.interval = 10;

      var dataPoints = Object.keys(temp).map(function (key) {
        return temp[key];
      });
      if (len < 5) {
        chartData.data = [{
          type: 'bar',
          yValueFormatString: '#0.00\'%\'',
          indexLabel: '{y}',
          indexLabelPlacement: 'outside',
          //   indexLabelFontColor: '#fff',
          indexLabelOrientation: 'horizontal',
          dataPoints: dataPoints
        }];
      } else {
        chartData.data = [{
          type: 'column',
          yValueFormatString: '#0.00\'%\'',
          indexLabel: '{y}',
          indexLabelPlacement: 'outside',
          //   indexLabelFontColor: '#fff',
          indexLabelOrientation: 'horizontal',
          dataPoints: dataPoints
        }];
      }
      $scope.connector('set', 'FPS330.drawChart.chartData', chartData);
      var chart = new CanvasJS.Chart('p_rangeBartChart', chartData);
      chart.render();
    };

  }
);
