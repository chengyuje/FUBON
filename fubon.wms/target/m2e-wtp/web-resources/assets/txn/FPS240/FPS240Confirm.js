/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPS240ConfirmController',
  function ($rootScope, $scope, $controller) {
    $controller('BaseController', {
      $scope: $scope
    });
    $scope.controllerName = "FPS240ConfirmController";
    // $scope.data = PLAN_STATUS
    $scope.init = function () {
      $scope.inputVO = {};
      $scope.inputVO.action = '1';
    };

    $scope.ok = function () {
      $scope.closeThisDialog({
        action: $scope.inputVO.action
      });
    };

    $scope.init();

    /* main progress */
    console.log('FPSConfirmController');
    $scope.init();
  }
);
