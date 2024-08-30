/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('INSIOTController',
  function ($rootScope, $scope, $controller) {
    $controller('BaseController', {
      $scope: $scope
    });
    $scope.controllerName = 'INSIOTController';

    /* init */
    $scope.init = function () {
      $scope.IOTurl = 'assets/txn/IOT110/IOT110.html';
    };

    $scope.setIOTurl = function(url){
    	$scope.IOTurl = url;
    }
    $scope.init();
  }
);
