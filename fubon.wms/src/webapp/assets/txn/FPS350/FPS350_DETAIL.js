/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPS350DetailController',
  function ($scope, $controller, ngDialog, socketService, alerts, projInfoService, $q, getParameter, $filter) {
    $controller('BaseController', {
      $scope: $scope
    });
    $scope.controllerName = "FPS350DetailController";

    var log = console.log;
    var init = function () {
      log('planId->' + $scope.plan.PLAN_ID);
      $scope.inputVO = {
        planID: $scope.plan.PLAN_ID
      };
    };

    var inquire = function () {
      $scope.sendRecv('FPS350', 'inquireProposal', 'com.systex.jbranch.app.server.fps.fps350.FPS350InputVO', $scope.inputVO,
        function (tota, isError) {
          if (!isError) {
            $scope.outputVO = tota[0].body;
            log('outputList->' + tota[0].body.outputList);
            $scope.paramList = tota[0].body.outputList;
            $scope.paramList.forEach(function (row) {
              row.buildTime = dateConvert(row.BUILDTIME, '-', '/');
            });
          } else $scope.showErrorMsg(tota);
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
      $scope.sendRecv('FPS340', 'generatePdf', 'com.systex.jbranch.app.server.fps.fpsutils.FPSUtilsPdfInputVO', {
    	  planID: $scope.inputVO.planID,
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
    var dateConvert = function (date, b, c, time) {
      date = date.toString();
      date = time ? date : date.split(' ')[0];
      return date.replace(new RegExp(b, 'g'), c);
    };

    // main process
    init();
    inquire();
  });
