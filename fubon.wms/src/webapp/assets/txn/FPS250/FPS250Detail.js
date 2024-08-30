/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPS240DetailController',
  function ($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, getParameter) {
    $controller('BaseController', {
      $scope: $scope
    });
    $scope.controllerName = "FPS240DetailController";

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

      $scope.sendRecv('FPS250', 'inquireProposal', 'com.systex.jbranch.app.server.fps.fps250.FPS250InputVO', {
          planID: $scope.planID
        },
        function (tota, isError) {
          if (!isError) {
            $scope.paramList = tota[0].body.outputList;
            $scope.paramList.forEach(function (row) {
              row.buildTime = $scope.helpingFn.dateConvert(row.CREATETIME, '-', '/');
            });
            return true;
          }
          return false;
        });
    };

    $scope.go = function (where, data) {
      switch (where) {
        case 'download':
          savePdfnEncrypt(where, data);
          break;
        case 'resend':
          savePdfnEncrypt(where, data);
          break;
      }
    };

    var savePdfnEncrypt = function (action, data) {
      $scope.sendRecv('FPS240', 'generatePdf', 'com.systex.jbranch.app.server.fps.fpsutils.FPSUtilsPdfInputVO', {
          planID: $scope.planID,
          custID: $scope.custID,
          action: action,
          printSEQ: data
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
  }
);
