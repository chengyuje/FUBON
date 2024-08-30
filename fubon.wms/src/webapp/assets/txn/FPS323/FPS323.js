/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPS323Controller',
  function ($scope, $controller, $timeout) {
    $controller('BaseController', {
      $scope: $scope
    });
    $scope.controllerName = 'FPS330Controller';

    $scope.$on('FPS323_drawMonteCarlo', function (event, data) {
      var total = 0;
      angular.forEach(data.ret, function (obj, index) {
    	  total += obj.PURCHASE_TWD_AMT;
      });
      
      if(total == 0 ) {
    	  $scope.errorText = '投資標的調整後金額為 0 ，無法進行績效模擬試算';
          $scope.connector('set', 'FPS323_error', true);
          $scope.$emit('FPS323_error', '投資標的調整後金額為 0，無法進行績效模擬試算');
          return false;
      }
    	
    	
      if (!data || data.length <= 0) return false;
      $scope.sendRecv('FPS323', 'inquire', 'com.systex.jbranch.app.server.fps.fps323.FPS323InputVO',
        data || {},
        function (tota, isError) {
          if (!isError && tota[0].body) {
            $scope.errorText = '';
            if (tota[0].body.errCode) {
//              $scope.errCodes = [];
//              $scope.errCodes = tota[0].body.errCode.split(',');
//              for (var i = 0; i < $scope.errCodes.length; i++) {
//                $scope.showErrorMsg('「' + $scope.errCodes[i] + '」' + '成立期間未滿 3 年，無法進行﹝績效模擬﹞及﹝下一步﹞，請調整標的。');
//              }
              $scope.errorText = '投資標的共同區間小於 3 年，無法進行績效模擬試算';
              $scope.connector('set', 'FPS323_error', true);
              $scope.$emit('FPS323_error', '投資標的共同區間小於 3 年，無法進行績效模擬試算');
              return;
            } else {
              var resultArr = tota[0].body;
              $scope.invSum = Math.round(resultArr[3]);
              $scope.resultBad = Math.round(resultArr[2]);
              $scope.resultNormal = Math.round(resultArr[1]);
              $scope.resultGood = Math.round(resultArr[0]);
              $scope.expect = data.target;
              $scope.connector('set', 'FPS323.resultNormal', $scope.resultNormal);
              $timeout(function () {
            	$scope.connector('set', 'FPS323_error', false);
                drawChart();
              });
              return true;
            }
          } else {
            $scope.errorText = '投資標的共同區間小於 3 年，無法進行績效模擬試算';
            $scope.$emit('FPS323_error', '投資標的共同區間小於 3 年，無法進行績效模擬試算');
            $scope.connector('set', 'FPS323_error', true);
//            $scope.showErrorMsg('投資標的共同區間小於 3 年，無法進行績效模擬試算');
          }
          return false;
        });
    });

    var drawChart = function () {
      var wbRatio = 13;
      var dataPoints = [{
          label: '投入金額',
          y: $scope.invSum,
          indexLabelFontColor: parseInt($scope.expect) > (parseInt($scope.invSum) * wbRatio) ? 'black' : 'white'
        },
        {
          label: '較差情境-預期金額',
          y: $scope.resultBad,
          indexLabelFontColor: parseInt($scope.expect) > (parseInt($scope.resultBad) * wbRatio) ? 'black' : 'white'
        },
        {
          label: '一般情境-預期金額',
          y: $scope.resultNormal,
          indexLabelFontColor: parseInt($scope.expect) > (parseInt($scope.resultNormal) * wbRatio) ? 'black' : 'white'
        },
        {
          label: '較佳情境-預期金額',
          y: $scope.resultGood,
          indexLabelFontColor: parseInt($scope.expect) > (parseInt($scope.resultGood) * wbRatio) ? 'black' : 'white'
        },
        {
          label: '目標金額',
          y: $scope.expect
        }
      ];

      var chartData = {
        colorSet: 'performance',
        dataPointWidth: 20,
        axisX: {
          labelFontFamily: 'Microsoft JhengHei'
        },
        axisY: {
          gridThickness: 0.5,
          gridColor: '#fff',
          labelAngle: -0.1,
          labelFontSize: 12,
          labelFontFamily: 'Microsoft JhengHei'
        },
        toolTip: {
          fontFamily: 'Microsoft JhengHei',
        },
        data: [{
          type: 'bar',
          indexLabel: '{y}',
          indexLabelPlacement: 'inside',
          indexLabelFontColor: '#fff',
          yValueFormatString: '#,###',
          showInLegend: false,
          legendText: '單位：新台幣元',
          dataPoints: dataPoints
        }]
      };
      debugger;
      var max = dataPoints.map(function (point) {
        return point.y;
      }).reduce(function (a, b) {
        return Math.max(a, Math.abs(b));
      });

      // 區間
      var divide = Math.ceil(max / 5);
      // 位數^10
      var len = Math.pow(10, Math.ceil(Math.log10(divide + 1)) - 1);
      // 數值
      var num = Math.ceil(divide / len);
      // 結果
      var result = len * num;
      chartData.axisY.interval = result;

      $scope.connector('set', 'FPS323.calExpect.chartData', chartData);
      var chart = new CanvasJS.Chart('calExpect', chartData);
      chart.render();
    };
  }
);
