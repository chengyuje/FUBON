/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPS330Controller',
  function ($scope, $controller, $timeout) {
    $controller('BaseController', {
      $scope: $scope
    });
    $scope.controllerName = 'FPS330Controller';

    var fps300 = $scope.fps300;
    var YRcolor = canvasJsConfig.YRSet;
    $scope.$on('FPS330_drawHistoryChart', function (event, data) {
      debugger
      var total = 0;
      angular.forEach(data, function (obj, index) {
    	  total += obj.PURCHASE_TWD_AMT;
      });
      
      if(total == 0 ) {
    	  $scope.errorText = '投資標的調整後金額為 0 ，無法進行歷史績效表現試算';
          $scope.connector('set', 'FPS330_error', true);
          $scope.$emit('FPS330_error', '投資標的調整後金額為 0 ，無法進行歷史績效表現試算');
          return false;
      }
      console.log({
        planID: fps300.getPlanID(),
        paramList: data ? data : undefined
      });
      $scope.sendRecv('FPS330', 'inquire', 'com.systex.jbranch.app.server.fps.fps330.FPS330InputVO', {
          planID: fps300.getPlanID(),
          paramList: data ? data : undefined
        },
        function (tota, isError) {
          $scope.errorText = '';
          if (!isError && !tota[0].body.isError && tota[0].body.yearRateList) {
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
//            	if(yearList.length === 1) {
//            		yearList.push({RETURN_ANN_M: null, YEAR: '0'});
//            	}
                drawChart(yearList);
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
          $scope.connector('set', 'FPS330.errorText', $scope.errorText);
        });
    });

    var drawChart = function (yRList) {
      var temp = {};
      var len = yRList.length;
      var chartData = {
        axisY: {
          valueFormatString: '#,##0\'%\'',
          gridThickness: 0.5,
          gridColor: '#fff',
          stripLines: [{
            value: 0,
            color: '#bbb',
            thickness: 2
          }]
        },
        dataPointWidth: 20,
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

      var dataPoints = Object.keys(temp).map(function (key) {
        return temp[key];
      });
      if (len < 5) {
        chartData.data = [{
          type: 'bar',
          yValueFormatString: '#0.##\'%\'',
          indexLabel: '{y}',
          indexLabelPlacement: 'outside',
          indexLabelOrientation: 'horizontal',
          dataPoints: dataPoints
        }];
      } else {
        chartData.data = [{
          type: 'column',
          yValueFormatString: '#0.##\'%\'',
          indexLabel: '{y}',
          indexLabelPlacement: 'outside',
          indexLabelOrientation: 'horizontal',
          dataPoints: dataPoints
        }];
      }
      $scope.connector('set', 'FPS330.drawChart.chartData', chartData);
      var chart = new CanvasJS.Chart('rangeBartChart', chartData);
      chart.render();
    };

  }
);
