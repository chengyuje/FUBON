/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPSProdSNController',
  function ($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, getParameter, $q) {
    $controller('BaseController', {
      $scope: $scope
    });
    $scope.controllerName = 'FPSProdSNController';

    /* parameter */
    $scope.arrowUp = true;
    $scope.mapping = {};
    $scope.inputVO = {};
    $scope.paramList = [];
    var detailPath = './assets/txn/PRD140/PRD140_DETAIL.html';
    // $scope.selection = []; 這個在主畫面
    // PRD.BOND_TYPE
    getParameter.XML(['PRD.SN_RATE_GUARANTEEPAY'], function (totas) {
      if (totas) {
        $scope.mapping.guaranteePay = totas.data[totas.key.indexOf('PRD.SN_RATE_GUARANTEEPAY')] || [];
      }
    });

    /* init */
    $scope.init = function () {
      $scope.inputVO.type = 'SN';
      $scope.inputVO.isRanked = 'Y';
      $scope.inputVO.riskType = $scope.riskType;
      $scope.inputVO.OBU = $scope.OBU;
      $scope.inputVO.isPro = $scope.isPro;
      $scope.inputVO.SNID = '';
      $scope.inputVO.SNName = '';
      $scope.inputVO.currency = '';
      $scope.inputVO.maturity = '';
      $scope.inputVO.rateGuarantee = '';
      $scope.inputVO.riskLev = '';
      $scope.inputVO.stockBondType = $scope.type === 'fixed' ? 'B':'S';
      // $scope.prodList = [];
      $scope.outputVO = {};
    };

    /* main function */
    $scope.inquire = function (isRanked) {
      $scope.inputVO.isRanked = isRanked;
      inquire()
        .then(function () {
          initList();
        });
    };

    $scope.getName = function () {
      var deferred = $q.defer();
      if ($scope.inputVO.SNID) {
        $scope.sendRecv('PRD140', 'getSnName', 'com.systex.jbranch.app.server.fps.prd140.PRD140InputVO', {
            sn_id: $scope.inputVO.SNID.toUpperCase()
          },
          function (tota, isError) {
            if (!isError) {
              if (tota[0].body.sn_name) {
                $scope.inputVO.SNName = tota[0].body.sn_name;
              }
              deferred.resolve();
            }
          });
      } else
        deferred.resolve();
      return deferred.promise;
    };

    // toggle checkbox
    $scope.selected = function (row) {
      if (row.selected) {
        row.selectedIndex = $scope.selection.length;
        $scope.selection.push(row);
        // 6.25 更改畫面
        $scope.addCart();
      } else if (row.selectedIndex !== undefined && row.selectedIndex >= 0) {
        $scope.selection.splice(row.selectedIndex, 1);
        row.selectedIndex = -1;
      }
    };

    /* sub function */
    var initList = function () {
      $scope.prodList = angular.copy($scope.outputVO.prodList)
        .map(function (row) {
          row.name = row.PRD_ID + ' ' + row.SN_CNAME;
          // for main
          row.CURRENCY_TYPE = row.CURRENCY_STD_ID;
          row.PTYPE = 'SN';
          row.INV_PRD_TYPE = '2';
          row.INV_PERCENT = 0;
          row.RISK_TYPE = row.RISKCATE_ID;
          row.PRD_CNAME = row.SN_CNAME;
          row.MAIN_PRD = row.PRD_RANK ? row.PRD_RANK : 'N';
          row.GEN_SUBS_MINI_AMT_FOR = row.BASE_AMT_OF_PURCHASE || undefined;
          row.SML_SUBS_MINI_AMT_FOR = row.BASE_AMT_OF_BUYBACK || undefined;
          row.PRD_UNIT = row.UNIT_OF_VALUE || undefined;

          return row;
        });
      console.log($scope.paramList);
    };

    var inquire = function () {
      var defer = $q.defer();
      $scope.sendRecv('FPSProd', 'inquire', 'com.systex.jbranch.app.server.fps.fpsprod.FPSProdInputVO',
        $scope.inputVO,
        function (tota, isError) {
          if (!isError) {
            $scope.outputVO = tota[0].body;
            console.log($scope.outputVO);
            defer.resolve(tota);
            return true;
          }
          defer.reject(false);
        });
      return defer.promise;
    };

    $scope.go = function (where, data) {
      switch (where) {
        case 'detail':
          openDialog(detailPath, data);
          break;
      }
    };

    var openDialog = function (path, data) {
      if (!path) return false;
      var scope = $scope;
      var dialog = ngDialog.open({
        template: path,
        className: 'FPS200',
        controller: ['$scope', function ($scope) {
          $scope.row = angular.copy(data);
          $scope.cust_id = scope.cust_id;
        }]
      });
    };

    /* watch */

    /* emit */
    $scope.$on('FPS200ProdAddCart', function () {
      // initList();
      $scope.paramList.map(function (row) {
        row.selected = undefined;
      });
    });

    /* main progress */
    $scope.init();
    $scope.inquire('Y');
    // 選購時檢核有無重複的key
    $scope.setKeyOfType('PRD_ID');
  }
);
