/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPSProdCurrencyController',
  function ($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, getParameter, $q) {
    $controller('BaseController', {
      $scope: $scope
    });
    $scope.controllerName = "FPSProdCurrencyController";

    /* parameter */
    $scope.inputVO = {};
    $scope.outputVO = [];
    $scope.paramCurList = [];
    $scope.paramList = [];
    $scope.inputVO.type = 'currency';
    // $scope.selection = []; 這個在主畫面
    /* init */

    /* main function */

    // toggle checkbox
    $scope.selected = function (row) {
      var index = -1;
      var temp = $scope.selection.filter(function (item, i) {
        if (item.CURRENCY_TYPE === row.CURRENCY_TYPE) {
          index = i;
        }
        return item.CURRENCY_TYPE === row.CURRENCY_TYPE;
      });

      if (row.selected && index < 0) {
        $scope.selection.push(row);
        // 7.19 更改畫面
        $scope.addCart();
      } else if (index >= 0) {
        $scope.selection.splice(index, 1);
      }
    };

    /* sub function */
    var initList = function () {
      $scope.paramList = angular.copy($scope.outputVO)
        .reduce(function (ls, row) {
          if ($scope.paramCurList.indexOf(row.DATA) >= 0) {
            ls.push({
              name: row.LABEL + ' ' + row.DATA,
              INV_PRD_TYPE: '1',
              INV_PRD_TYPE_2: row.DATA === 'TWD' ? '1' : '2',
              CURRENCY_TYPE: row.DATA,
              CURRENCY_NAME: row.LABEL,
              INV_PERCENT: 0,
              MAIN_PRD: ''
            });
          }
          return ls;
        }, []);
    };

    var inquire = function () {
      var defer = $q.defer();
      var cnt = 0;
      var end = 2;
      var done = function (err) {
        if (err) defer.reject(false);
        if (cnt === end) defer.resolve(true);
      };
      getParameter.XML([
        'FPS.DEPOSIT_CUR'
      ], function (totas) {
        if (totas) {
          $scope.outputVO = totas.data[totas.key.indexOf('FPS.DEPOSIT_CUR')] || [];
          cnt += 1;
          done();
          return true;
        }
        done(true);
      });
      $scope.sendRecv('FPSProd', 'inquire', 'com.systex.jbranch.app.server.fps.fpsprod.FPSProdInputVO',
        $scope.inputVO,
        function (tota, isError) {
          if (!isError) {
            if (tota[0].body.length <= 0 || tota[0].body.prodList.length <= 0) {
              done(true);
            }
            $scope.paramCurList = tota[0].body.prodList[0].DEPOSIT_CURR.split(',');
            cnt += 1;
            done();
            return true;
          }
          done(true);
        });

      return defer.promise;
    };

    /* emit */
    $scope.$on('FPS200ProdAddCart', function (event, data) {
      initList();
      $scope.paramList.map(function (row) {
        row.selected = undefined;
      });
    });

    /* main progress */
    inquire()
      .then(function () {
        initList();
      });
    // 選購時檢核有無重複的key
    $scope.setKeyOfType('CURRENCY_TYPE');
  }
);
