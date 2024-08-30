/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPSProdSavingInsController',
  function ($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, getParameter, $q) {
    $controller('BaseController', {
      $scope: $scope
    });
    $scope.controllerName = "FPSProdSavingInsController";

    /* parameter */
    $scope.arrowUp = true;
    $scope.inputVO = {};
    $scope.paramList = [];
    var detailPath = './assets/txn/PRD160/PRD160_DETAIL.html';
    // $scope.selection = []; 這個在主畫面

    /* init */
    $scope.init = function () {
      $scope.inputVO.type = 'savingIns';
      $scope.inputVO.isRanked = 'Y';
      $scope.inputVO.insType = '1';
      $scope.inputVO.OBU = $scope.OBU;
      $scope.inputVO.isPro = $scope.isPro;
      $scope.inputVO.insID = '';
      $scope.inputVO.insName = '';
      $scope.inputVO.insAnnual = '';
      $scope.inputVO.insCurrency = '';
      $scope.inputVO.isIncreasing = '';
      $scope.inputVO.isRePay = '';
      $scope.inputVO.isRateChange = '';
      // $scope.inputVO.insGuarntee = '';
      // $scope.inputVO.isMain = '';
      // $scope.inputVO.isCom01 = '';
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

    // toggle checkbox
    $scope.selected = function (row) {
      var index = -1;
      var temp = $scope.selection.filter(function (item, i) {
        if (item.KEY_NO === row.KEY_NO) {
          index = i;
        }
        return item.KEY_NO === row.KEY_NO;
      });

      if (row.selected && index < 0) {
        $scope.selection.push(row);
        // 6.25 更改畫面
        $scope.addCart();
      } else if (index >= 0) {
        $scope.selection.splice(index, 1);
      }
    };

    /* sub function */
    var initList = function () {
      $scope.prodList = angular.copy($scope.outputVO.prodList)
        .map(function (row) {
          row.name = row.PRD_ID + ' ' + row.INSPRD_NAME;
          // for main
          row.INV_PRD_TYPE = '1';
          row.INV_PRD_TYPE_2 = '3';
          row.CURRENCY_TYPE = row.CURR_CD;
          row.INV_PERCENT = 0;
          row.PRD_CNAME = row.INSPRD_NAME;
          row.MAIN_PRD = row.PRD_RANK ? 'Y' : 'N';
          row.PTYPE = 'INS';
          row.GEN_SUBS_MINI_AMT_FOR = row.BASE_AMT_OF_PURCHASE;

          return row;
        });
    };

    var inquire = function () {
      var defer = $q.defer();
      $scope.sendRecv('FPSProd', 'inquire', 'com.systex.jbranch.app.server.fps.fpsprod.FPSProdInputVO',
        $scope.inputVO,
        function (tota, isError) {
          if (!isError) {
            $scope.outputVO = tota[0].body;
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

    /* helping function */

    /* emit */
    $scope.$on('FPS200ProdAddCart', function (event, data) {
      // initList();
      $scope.paramList.map(function (row) {
        row.selected = undefined;
      });
    });

    /* main progress */
    $scope.init();
    $scope.inquire('Y');
    // 選購時檢核有無重複的key
    $scope.setKeyOfType('KEY_NO');
  }
);
