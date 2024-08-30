/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPSProdBNDController',
  function ($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, getParameter, $q) {
    $controller('BaseController', {
      $scope: $scope
    });
    $scope.controllerName = 'FPSProdBNDController';

    /* parameter */
    $scope.arrowUp = true;
    $scope.mapping = {};
    $scope.inputVO = {};
    $scope.paramList = [];
    var detailPath = './assets/txn/PRD130/PRD130_DETAIL.html';
    // $scope.selection = []; 這個在主畫面

    /* init */
    $scope.init = function () {
      $scope.inputVO.type = 'bond';
      $scope.inputVO.isRanked = 'Y';
      $scope.inputVO.riskType = $scope.riskType;
      $scope.inputVO.OBU = $scope.OBU;
      $scope.inputVO.isPro = $scope.isPro;
      $scope.inputVO.bondID = '';
      $scope.inputVO.bondName = '';
      $scope.inputVO.currency = '';
      $scope.inputVO.bondCate = '';
      $scope.inputVO.faceVal = '';
      $scope.inputVO.maturity = '';
      $scope.inputVO.YTM = '';
      $scope.inputVO.ratingSP = '';
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
      if ($scope.inputVO.bondID) {
        $scope.sendRecv('PRD130', 'getBondName', 'com.systex.jbranch.app.server.fps.prd130.PRD130InputVO', {
            prd_id: $scope.inputVO.bondID.toUpperCase()
          },
          function (tota, isError) {
            if (!isError) {
              if (tota[0].body.bond_name) {
                $scope.inputVO.bondName = tota[0].body.bond_name;
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
          row.name = row.PRD_ID + ' ' + row.BOND_CNAME;
          // for main
          row.CURRENCY_TYPE = row.CURRENCY_STD_ID;
          row.PTYPE = 'BND';
          row.INV_PRD_TYPE = '2';
          row.INV_PERCENT = 0;
          row.RISK_TYPE = row.RISKCATE_ID;
          row.PRD_CNAME = row.BOND_CNAME;
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
