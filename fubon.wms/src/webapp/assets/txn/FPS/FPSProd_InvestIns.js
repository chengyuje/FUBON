/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPSProdInvestInsController',
  function ($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, getParameter, $q) {
    $controller('BaseController', {
      $scope: $scope
    });
    $scope.controllerName = "FPSProdInvestInsController";

    /* parameter */
    $scope.arrowUp = true;
    $scope.inputVO = {};
    $scope.paramList = [];
    var detailPath = './assets/txn/PRD160/PRD160_DETAIL.html';
    // $scope.selection = []; 這個在主畫面

    /* init */
    $scope.init = function () {
      $scope.inputVO.type = 'investIns';
      $scope.inputVO.isRanked = 'Y';
      $scope.inputVO.riskType = $scope.riskType;
      $scope.inputVO.OBU = $scope.OBU;
      $scope.inputVO.isPro = $scope.isPro;
      $scope.inputVO.insType = '2';
      $scope.inputVO.insID = '';
      $scope.inputVO.insName = '';
      $scope.inputVO.insCurrency = '';
      $scope.inputVO.isCom01 = '';
      // $scope.inputVO.insAnnual = '';
      // $scope.inputVO.insGuarntee = '';
      // $scope.inputVO.isMain = '';
      // $scope.prodList = [];
      $scope.outputVO = {};
    };

    var initList = function () {
      $scope.prodList = angular.copy($scope.outputVO.prodList)
        .map(function (row) {
          row.targets = [];
          row.targetList = [];
          row.showTarget = false;
          row.name = row.PRD_ID + ' ' + row.INSPRD_NAME;
          // for main
          row.PTYPE = 'INS';
          row.INV_PRD_TYPE = '3';
          row.CURRENCY_TYPE = row.CURR_CD;
          row.INV_PERCENT = 0;
          row.PRD_CNAME = row.INSPRD_NAME;
          row.MAIN_PRD = row.PRD_RANK ? 'Y' : 'N';
          row.GEN_SUBS_MINI_AMT_FOR = row.BASE_AMT_OF_PURCHASE;
          if (row.TARGETS && row.PRD_RANK) {
            getTarget(row)
              .then(function (list) {
                row.targetList = list;
                var splitTg = (row.TARGETS || '').split('/');
                row.targetList.forEach(function (tg) {
                  if (splitTg.indexOf(tg.TARGET_ID) >= 0) {
                    row.targets.push(tg);
                    tg.selected = true;
                  }
                });
              });
          }
          return row;
        });
    };

    /* main function */
    $scope.inquire = function (isRanked) {
      $scope.inputVO.isRanked = isRanked;
      inquire()
        .then(function () {
          $scope.arrowUp = true;
          initList();
        });
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

    // 選擇標的
    $scope.toggleTarget = function (row) {
      if (row.targetList.length <= 0 && !row.showTarget) {
        getTarget(row)
          .then(function (list) {
            if (list && list.length > 0) {
              row.targetList = list;
              row.showTarget = !row.showTarget;
            } else {
              console.log('no target');
              $scope.showMsg('此保險無基金標的');
            }
          });
      } else {
        row.showTarget = !row.showTarget;
      }
    };

    var noTargets = function () {
      $scope.showMsg('請選擇標的');
    };

    var limitTargets = function () {
      $scope.showMsg('超過標的上限(3檔)');
    };

    // 確認標的
    $scope.selectTarget = function (row) {
      row.targets = row.targetList && row.targetList.length >= 0 ? row.targetList.filter(function (item) {
        return item.selected;
      }) || [] : [];
      if (row.targets.length <= 0) {
        noTargets();
        row.selected = false;
        return false;
      } else if (row.targets.length > 3) {
        limitTargets();
        row.selected = false;
        return false;
      }
      row.showTarget = row.targets.length >= 0 ? false : row.showTarget;
      row.TARGETS = (row.targets || []).map(function (key) {
        return key.TARGET_ID;
      }).join('/');
      row.selected = true;
      $scope.selected(row, true);
    };

    // toggle checkbox
    $scope.selected = function (row, flag) {
      if (row.targets.length <= 0) {
        noTargets();
        row.selected = false;
        return false;
      }
      var index = -1;
      $scope.selection.forEach(function (item, i) {
        if (item && item.KEY_NO === row.KEY_NO) {
          index = i;
        }
      });

      if (row.selected && index < 0) {
        $scope.selection.push(row);
        // 6.25 更改畫面
        $scope.addCart();
      } else if (index >= 0) {
        $scope.selection.splice(index, 1, flag ? row : undefined);
      }
    };

    /* sub function */
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

    var getTarget = function (row) {
      var defer = $q.defer();
      $scope.sendRecv('FPSProd', 'inquire', 'com.systex.jbranch.app.server.fps.fpsprod.FPSProdInputVO', {
          insID: row.PRD_ID,
          riskType: $scope.riskType,
          type: 'insTarget'
        },
        function (tota, isError) {
          if (!isError) {
            defer.resolve(tota[0].body.prodList);
            return true;
          }
          defer.reject(false);
        });
      return defer.promise;
    };

    /* helping function */

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
    $scope.setKeyOfType('KEY_NO');
  }
);
