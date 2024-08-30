/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPS210CashController',
  function ($rootScope, $scope, $controller) {
    $controller('BaseController', {
      $scope: $scope
    });
    $scope.controllerName = "FPS210CashController";
    var tmp = angular.copy($scope.originLoan);
    var loanMap = {
      houseLoan: '房貸',
      creditLoan: '信貸',
      stdLoan: '學貸'
    };

    /** initialize **/
    $scope.init = function () {
      if ($scope.cashVO === undefined) {
        $scope.cashVO = {
          houseLoan: 0,
          creditLoan: 0,
          stdLoan: 0,
          payForHouse: 0,
          payForCar: 0,
          study: 0,
          travel: 0,
          other: 0,
          total: 0,
          AmtTotal_1: 0,
          AmtTotal_2: 0,
        };
      } else {
        $scope.getAmtTotal();
      }
    };

    $scope.clearAll = function () {
      $scope.cashVO = {
        houseLoan: tmp.houseLoan,
        creditLoan: tmp.creditLoan,
        stdLoan: tmp.creditLoan,
        payForHouse: 0,
        payForCar: 0,
        study: 0,
        travel: 0,
        other: 0,
        total: 0,
        AmtTotal_1: 0,
        AmtTotal_2: 0,
      };
    };

    $scope.getAmtTotal = function (key) {
      $scope.cashVO[key] = parseInt(Number($scope.cashVO[key] || 0), 10);
      $scope.cashVO.AmtTotal_1 = Number($scope.cashVO.houseLoan) + Number($scope.cashVO.creditLoan) +
        Number($scope.cashVO.stdLoan);
      $scope.cashVO.AmtTotal_2 = Number($scope.cashVO.payForHouse) + Number($scope.cashVO.payForCar) +
        Number($scope.cashVO.study) + Number($scope.cashVO.travel) +
        Number($scope.cashVO.other);
      $scope.cashVO.total = 
        Number($scope.cashVO.houseLoan) + Number($scope.cashVO.creditLoan) +
        Number($scope.cashVO.stdLoan) + Number($scope.cashVO.payForHouse) +
        Number($scope.cashVO.payForCar) + Number($scope.cashVO.study) +
        Number($scope.cashVO.travel) + Number($scope.cashVO.other);
    };

    $scope.compareLoan = function (key) {
      console.log(key);
      if (tmp[key] > (parseInt($scope.cashVO[key], 10) || 0)) {
        $scope.showErrorMsg('填入的金額不可小於' + loanMap[key]);
        $scope.cashVO[key] = tmp[key];
        $scope.getAmtTotal();
        return false;
      }
      return true;
    };

    $scope.saveCash = function () {
      $scope.closeThisDialog($scope.cashVO || 0);
    };

    $scope.init();

  }
);
