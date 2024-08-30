/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPS400DetailController',
  function ($scope, $controller, ngDialog) {
    $controller('BaseController', {
      $scope: $scope
    });
    $scope.controllerName = "FPS400DetailController";

    /* parameter */
    $scope.paramList = [];
    // 從前面來
    // $scope.planID
    // $scope.helpingFn = {
    //   dateConvert: dateConvert
    // };

    /* init */
    $scope.init = function () {
      getPlanProposal();
    };

    /* main function */
    var getPlanProposal = function () {
      console.log($scope.planID);

      $scope.sendRecv('FPS400', 'inquireProposal', 'com.systex.jbranch.app.server.fps.fps400.FPS400InputVO', {
          planID: $scope.planID
        },
        function (tota, isError) {
          if (!isError) {
            $scope.paramList = tota[0].body.outputList;
            $scope.paramList.forEach(function (row) {
              row.buildTime = $scope.helpingFn.dateConvert(row.CREATETIME, '-', '/');
            });
            $scope.outputVO = tota[0].body;
            return true;
          }
          return false;
        });
    };

    $scope.go = function (where, data, type) {
      switch (where) {
        case 'download':
          savePdfnEncrypt(where, data, type);
          break;
        case 'resend':
          savePdfnEncrypt(where, data, type);
          break;
      }
    };

    var savePdfnEncrypt = function (action, data, type) {
      debugger
      $scope.sendRecv('FPS340', 'generatePdf', 'com.systex.jbranch.app.server.fps.fpsutils.FPSUtilsPdfInputVO', {
          planID: $scope.planID,
          custID: $scope.custID,
          action: action,
          printSEQ: data,
          isFps410: type === 'SPP' ? 'N' : 'Y'
        },
        function (tota, isError) {
          if (!isError) {
            console.log(tota);
            if (action === 'resend')
              $scope.showMsg('轉寄完成');
          }
        });
    };
    /* helping function */

    /* watch */

    /* main progress */
    console.log('FPS240DetailController');
    $scope.init();
  });
