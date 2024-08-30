/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPSModelDetailController',
  function ($rootScope, $scope, $controller, socketService, ngDialog) {
    $controller('BaseController', {
      $scope: $scope
    });
    $scope.controllerName = 'FPSModelDetailController';

    /* parameter */
    var model = $scope.data;
    $scope.modelItems = model.items;
    $scope.modelLen = $scope.modelItems.length;
    $scope.ym = model.DATA_YEARMONTH;

    var pathMap = {
      detail: {
        MFD: './assets/txn/PRD110/PRD110_DETAIL.html',
        INS: './assets/txn/PRD160/PRD160_DETAIL.html'
      }
    };
    var custID = $scope.cust_id;

    /* init */
    $scope.init = function () {

    };

    /* main function */


    /**
     * 去那兒
     */
    $scope.go = function (where, type, data) {
      switch (where) {
        case 'detail':
          openDialog(pathMap.detail[type], data, type);
          break;
        default:
          break;
      }
    };

    var openDialog = function (path, data, type) {
      if (!path) return false;
      var scope = $scope;
      var dialog = ngDialog.open({
        template: path,
        className: 'FPS200',
        controller: ['$scope', function ($scope) {
          $scope.data = data;
          $scope.riskType = scope.riskType;
          $scope.row = angular.copy(data);
          switch (type) {
            case 'MFD':
              $scope.row.FUND_CNAME = data.PRD_CNAME;
              break;
            case 'INS':
              $scope.row.INSPRD_NAME = data.PRD_CNAME;
              $scope.row.KEY_NO = (data.KEY_NO).toString().split(',')[0];
              break;
          }
          // }
          $scope.cust_id = custID;
        }]
      });

    };

    /* helping function */

    /* watch */

    /* main progress */
    console.log('FPSModelDetailController');
    $scope.init();
  }
);
