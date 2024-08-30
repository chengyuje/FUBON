/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPS400Controller',
  function ($scope, $controller, ngDialog, projInfoService, $q) {
    $controller('BaseController', {
      $scope: $scope
    });

    $scope.controllerName = 'FPS400Controller';

    // get function type
    $scope.functionID = projInfoService.getAuthorities().MODULEID.WMS.ITEMID.FPS400 ?
      projInfoService.getAuthorities().MODULEID.WMS.ITEMID.FPS400.FUNCTIONID : {};
    $scope.isMaintenance = $scope.functionID.maintenance;
    /* main function */

    /* parameter */
    var fps300 = $scope.fps300;
    $scope.mapping = {};
    $scope.mapping.planning = fps300.mapping.planning;

    // ugly guy for status
    $scope.hitRateGuy = {
      '1': './assets/images/money_SVG/money_happy.svg',
      '0': './assets/images/money_SVG/money_shoulder.svg',
      '-1': './assets/images/money_SVG/money_sweat.svg'
    };

    /* init */
    $scope.init = function () {
      $scope.inputVO = {};
    };

    //Main
    $scope.inquire = function () {
      var deferred = $q.defer();
      $scope.inputVO.custID = fps300.inputVO.custID;
      $scope.sendRecv('FPS400', 'inquire', 'com.systex.jbranch.app.server.fps.fps400.FPS400InputVO',
        $scope.inputVO,
        function (tota, isError) {
          if (!isError) {
            $scope.paramList = tota[0].body.outputList;
            $scope.outputVO = tota[0].body;
            $scope.paramList.forEach(function (row) {
              row.HIT_RATE = fps300.floatFormat(parseFloat(row.HIT_RATE), 2);
              row.tpFlag = (row.TRACE_P_FLAG == 'Y');
              row.tvFlag = (row.TRACE_V_FLAG == 'Y');
            });
            $scope.rate = tota[0].body.AchiveRate;
            fps300.stored.rate = $scope.rate; // set to fps300 stored
            console.log(tota[0].body);
            deferred.resolve();
          }
        });
      return deferred.promise;
    };

    var tag = function (num, args) {
      fps300.chgTab(num, args);
    };

    $scope.goFPS410 = function (row) {
      fps300.setPlanID(row.PLAN_ID);
      fps300.setRISK_ATTR(row.RISK_ATTR);
      fps300.setCurrentProgress(row);
      fps300.setIsOld(false);
      //            fps300.chgTab('FPS410');
      tag('FPS410', row.SPP_TYPE);
    };


    $scope.go = function (where, data) {
      switch (where) {
        case 'detail':
          detail(data);
          break;
        case 'download':
          if (data.IS_PRINT > 0)
            $scope.go('detail', data);
          break;
        case 'resend':
          if (data.IS_EMAIL > 0)
            $scope.go('detail', data);
          break;
      }
    };

    // 開啟轉寄規劃書小視窗
    var detail = function (row) {
      var scope = $scope;
      var dialog = ngDialog.open({
        template: 'assets/txn/FPS400/FPS400_DETAIL.html',
        className: 'FPS400_DETAIL',
        showClose: true,
        controller: ['$scope', function ($scope) {
          $scope.plan = row;
          $scope.custID = scope.inputVO.custID;
          $scope.planID = row.PLAN_ID;
          $scope.helpingFn = {
            dateConvert: dateConvert
          };
        }]
      });
      dialog.closePromise.then(function (data) {
        if (data.value === 'cancel') {}
      });
    };

    //列印規畫書
    $scope.goPrint = function (row) {
      $scope.inputVO.planID = row.PLAN_ID;
      console.log(row)
      console.log($scope.inputVO.planID)
      $scope.sendRecv('FPS400', 'print', 'com.systex.jbranch.app.server.fps.fps400.FPS400InputVO',
        $scope.inputVO,
        function (tota, isError) {
          if (!isError) {}
          $scope.showErrorMsg(tota);
          return false;
        });
    };

    // 開啟轉寄規劃書小視窗
    $scope.goDetail = function (row) {
      var dialog = ngDialog.open({
        template: 'assets/txn/FPS400/FPS400_DETAIL.html',
        className: 'FPS400_DETAIL',
        showClose: true,
        controller: ['$scope', function ($scope) {
          $scope.plan = row;
        }]
      });
      dialog.closePromise.then(function (data) {
        if (data.value === 'cancel') {}
      });
    };

    $scope.clickFlag = function (row, flagType) {
      if (row.REVIEW_P_FLAG == 'P') {
        if (flagType == 'tp') {
          row.tpFlag = true;
        } else {
          row.tvFlag = true;
        }
        return;
      }
    }

    $scope.changeFlag = function (row, flagType) {
      var temp = {
        planID: row.PLAN_ID,
        flagType: flagType,
        flagYN: (flagType == 'tp' ? row.tpFlag : row.tvFlag) ? 'Y' : 'N'
      };
      $scope.sendRecv('FPS400', 'setFlag', 'com.systex.jbranch.app.server.fps.fps400.FPS400InputVO',
        temp,
        function (tota, isError) {
          if (!isError) {
            if (temp.flagYN == 'N') {
              flagType == 'tp' ? row.REVIEW_P_FLAG = 'P' : row.REVIEW_V_FLAG = 'P';
            } else {
              flagType == 'tp' ? row.REVIEW_P_FLAG = '' : row.REVIEW_V_FLAG = '';
            }
            return;
          }
          $scope.showErrorMsg(tota);
          return false;
        });
    }

    $scope.review = function (row, flagType, reviewFlag) {
      var temp = {
        planID: row.PLAN_ID,
        flagType: flagType,
        flagYN: reviewFlag,
        action: 'review'
      };

      $scope.sendRecv('FPS400', 'setFlag', 'com.systex.jbranch.app.server.fps.fps400.FPS400InputVO',
        temp,
        function (tota, isError) {
          if (!isError) {
            flagType == 'tp' ? (row.REVIEW_P_FLAG = reviewFlag) : (row.REVIEW_V_FLAG = reviewFlag);
            flagType == 'tp' ? (row.tpFlag = (reviewFlag != 'A')) : (row.tvFlag = (reviewFlag != 'A'));
            return;
          }
          $scope.showErrorMsg(tota);
          return false;
        });

    }

    /* helping function */
    var dateConvert = function (date, b, c, time) {
      date = date.toString();
      date = time ? date : date.split(' ')[0];
      return date.replace(new RegExp(b, 'g'), c);
    };

    $scope.init();
    $scope.inquire();
    // $scope.getDot();
  });
