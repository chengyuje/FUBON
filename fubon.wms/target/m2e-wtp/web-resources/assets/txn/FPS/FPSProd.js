/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPS200PrdController',
  function ($scope, $controller) {
    $controller('BaseController', {
      $scope: $scope
    });
    $scope.controllerName = "FPS200PrdController";

    /* parameter */
    $scope.itemList = [];
    $scope.path = '';
    $scope.titleMap = angular.copy($scope.data); // 這個從主畫面過來
    $scope.query = null;
    var queryNum = 0;
    $scope.selection = [];
    $scope.arrowPath = {
      up: 'assets/images/ic-up.svg',
      down: 'assets/images/ic-down.svg'
    };
    var keyOfType = null;
    $scope.setKeyOfType = function (key) {
      keyOfType = key;
    };

    /* init */
    $scope.init = function () {
      $scope.itemList = $scope.titleMap.map(function (row) {
        return {
          title: row.title,
          list: []
        };
      });
    };

    /* main function */
    $scope.addCart = function () {
      var index = $scope.titleMap.filter(function (row) {
        return row.active;
      })[0].order;
      $scope.itemList[index].list = $scope.isMore ? [] : mockSet($scope.itemList[index].list.concat($scope.selection), keyOfType);
      $scope.selection = [];
      $scope.$broadcast('FPS200ProdAddCart');
    };

    $scope.deleteRow = function (item, index) {
      item.list.splice(index, 1);
    };

    $scope.back2Main = function () {
      // 2018/6/12 WV
      if (typeof (webViewParamObj) !== 'undefined') {
        webViewParamObj.formatWebViewResParam($scope.itemList);
        window.webkit.messageHandlers.resultCompleted.postMessage($scope.itemList);
      } else
        $scope.closeThisDialog($scope.itemList);
    };

    /* helping function */
    var mockSet = function (arr, key) {
      var resultSet = [];
      var keySet = [];

      arr.forEach(function (row) {
        if (!row) return false;
        var rowKey = key ? row[key] : row;
        if (keySet.length > 0 && keySet.indexOf(rowKey) >= 0) {
          return false;
        }
        resultSet.push(row);
        keySet.push(rowKey);
      });

      return resultSet;
    };

    /* watch */
    $scope.$watch('path', function (val) {
      $scope.selection = [];
    });

    // 只讓他出現一次
    $scope.$watch('query', function (val) {
      if (val) {
        queryNum += 1;
        $scope.isMore = true;
      }
      if (queryNum > 1) $scope.query = undefined;
    });

    /* main progress */
    console.log('FPS200PrdController');
    $scope.init();
  }
);
