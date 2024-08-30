/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPS230ReviewController',
  function ($rootScope, $scope, $controller) {
    $controller('BaseController', {
      $scope: $scope
    });
    $scope.controllerName = 'FPS230ReviewController';

    var sortOrder = {
      deposit: {
        '1': 2,
        '3': 1,
        '2': 3
      },
      transaction: {
        MFD: 1,
        ETF: 2,
        INS: 3,
        BND: 4,
        SN: 5,
        SI: 6,
        FIX: 7
      }
    };

    /** init **/
    $scope.init = function () {
      console.log($scope.data);
      var depositList = [];
      var elseList = [];

      $scope.data.forEach(function (row) {
        if (row.INV_PRD_TYPE === '1') depositList.push(row);
        else elseList.push(row);
      });

      $scope.paramList = [].concat(depositList.sort(function (a, b) {
        return sortOrder.deposit[a.INV_PRD_TYPE_2] - sortOrder.deposit[b.INV_PRD_TYPE_2];
      }), elseList.sort(function (a, b) {
        return sortOrder.transaction[a.PTYPE] - sortOrder.transaction[b.PTYPE];
      })).map(function (row) {
        row.INV_PERCENT = Math.round(Number(row.INV_PERCENT));
        return row;
      });


      var sum = $scope.paramList.reduce(function (a, b) {
        return a + Number(b.INV_PERCENT);
      }, 0);

      if (sum < 100) {
        $scope.paramList.push({
          textPTYPE: '固定收益商品',
          textPRD_CNAME: '--',
          CURRENCY_TYPE: '--',
          INV_PERCENT: 100 - sum
        });
      }
    };

    $scope.back2Main = function () {
    	var itemList = $scope.paramList.filter(function (row) {
    		return row.selected;
    	});
    	// 2018/6/12 WV
        if(typeof(webViewParamObj) != 'undefined'){
        	webViewParamObj.formatWebViewResParam(itemList);
        	window.webkit.messageHandlers.resultCompleted.postMessage(webViewParamObj.getResult());
        }
        else
        	$scope.closeThisDialog(itemList);
    };

    $scope.init();
  }
);
