/*
交易畫面邏輯定義
請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPS322Controller',
  function ($scope, $controller, socketService, alerts, projInfoService, $q, getParameter) {
    $controller('BaseController', {
      $scope: $scope
    });
    $scope.controllerName = "FPS322Controller";
    var isDelete = false;

    $scope.mapping = {};
    // 勞保btn.國民年金btn.公保btn.勞工退休金btn.公職退休金btn.公職退休金btn
    getParameter.XML(['INS.INSURANCE_REF_LINK'], function (totas) {
      if (totas) {
        $scope.mapping.links = totas.data[totas.key.indexOf('INS.INSURANCE_REF_LINK')].map(function (link) {
          return link.LABEL;
        });
        // console.log($scope.mapping.links);
      }
    });

    /* parameter */
    var custID = $scope.custID;
    var gender = custID.substring(1, 2);

    //  init 
    $scope.init = function () {
      $scope.inputVO = {
        retired: '0', //預計退休年齡 RETIREMENT_AGE
        spendMonth: '0', //退休後每個月花費 RETIRE_FEE
        money: '0',
        insMonth: '0', //
        insOneTime: '0',
        welfareMonth: '0',
        welfareOneTime: '0',
        insMonth_B: '0',
        insOneTime_B: '0',
        otherMonth: '0',
        otherOneTime: '0',
        hopeMoney: '0'
      };
      $scope.avgAge = '0';
      $scope.retire = '0';
      $scope.money = '0';
      $scope.totalMonth = '0';
      $scope.totalOneTime = '0';
      $scope.retireFunds = '0';
      $scope.getAvgAge();
    };

    $scope.initFps322 = function () {
      $scope.inputVO.retired = $scope.fromFPS320.RETIREMENT_AGE; //預計退休年齡
      $scope.inputVO.spendMonth = $scope.fromFPS320.RETIRE_FEE; //退休後每個月花費
      $scope.inputVO.money = $scope.fromFPS320.PREPARE_FEE; //已準備的退休金額
      $scope.inputVO.insMonth = $scope.fromFPS320.SOCIAL_INS_FEE_1; //社會保險給付/每月
      $scope.inputVO.insOneTime = $scope.fromFPS320.SOCIAL_INS_FEE_2; //社會保險給付/一次
      $scope.inputVO.welfareMonth = $scope.fromFPS320.SOCIAL_WELFARE_FEE_1; //社會福利給付/每月
      $scope.inputVO.welfareOneTime = $scope.fromFPS320.SOCIAL_WELFARE_FEE_2; //社會福利給付/一次
      $scope.inputVO.insMonth_B = $scope.fromFPS320.COMM_INS_FEE_1; //商業保險給付/每月
      $scope.inputVO.insOneTime_B = $scope.fromFPS320.COMM_INS_FEE_2; //商業保險給付/一次
      $scope.inputVO.otherMonth = $scope.fromFPS320.OTHER_FEE_1; //其他給付/每月
      $scope.inputVO.otherOneTime = $scope.fromFPS320.OTHER_FEE_2; //其他給付/一次
      $scope.inputVO.hopeMoney = $scope.fromFPS320.HERITAGE; //希望能傳承給親屬的金額

      $scope.getRetired();
      $scope.sumMonth();
      $scope.sumOneTime();
    }

    // 退休期間
    $scope.getRetired = function () {
      isDelete = false;
      var age = $scope.inputVO.retired || '0';
      var avg = $scope.avgAge || '0';
      $scope.retire = parseInt(avg) - parseInt(age);
      $scope.retiredMoney();
      $scope.Funds();
      return $scope.retire;
    }
    // 平均餘命
    $scope.getAvgAge = function () {
      $scope.sendRecv("FPS324", "avgAge", "com.systex.jbranch.app.server.fps.fps324.FPS324InputVO", {},
        function (tota, isError) {
          if (!isError) {
            $scope.outputList = tota[0].body.outputList;
            angular.forEach($scope.outputList, function (row) {
              if (row.PARAM_CODE == gender) {
                $scope.avgAge = row.PARAM_NAME;
              }
            });
            $scope.initFps322();
          };
        });
    };

    // 每月給付-小計
    $scope.sumMonth = function () {
      isDelete = false;
      $scope.totalMonth = parseInt($scope.inputVO.insMonth || '0') + parseInt($scope.inputVO.welfareMonth || '0') + parseInt($scope.inputVO.insMonth_B || '0') + parseInt($scope.inputVO.otherMonth || '0');
      $scope.retiredMoney();
      $scope.Funds();
      return $scope.totalMonth;
    };

    // 一次給付-小計
    $scope.sumOneTime = function () {
      isDelete = false;
      $scope.totalOneTime = parseInt($scope.inputVO.insOneTime || '0') + parseInt($scope.inputVO.welfareOneTime || '0') + parseInt($scope.inputVO.insOneTime_B || '0') + parseInt($scope.inputVO.otherOneTime || '0');
      $scope.retiredMoney();
      $scope.Funds();
      return $scope.totalOneTime;
    };

    // 已準備的退休金額 
    $scope.retiredMoney = function () {
      isDelete = false;
      $scope.money = (parseInt($scope.totalMonth) * 12 * $scope.retire) + parseInt($scope.totalOneTime);
      return $scope.money;
    };

    // 退休所需資金
    $scope.Funds = function () {
      isDelete = false;
      var inheritance = $scope.inputVO.hopeMoney || '0';
      var spend = $scope.inputVO.spendMonth || '0';
      var money = $scope.money || '0';
      $scope.retireFunds = (parseInt(spend) * 12 * $scope.retire) - parseInt(money) + parseInt(inheritance);
      return $scope.retireFunds;
    };

    // back FPS320
    $scope.backFPS320 = function () {
      $scope.inputVO.isFPS321 = false;
      var inputVoTemp = {
        RETIREMENT_AGE: $scope.inputVO.retired, //預計退休年齡
        RETIRE_FEE: $scope.inputVO.spendMonth, //退休後每個月花費
        PREPARE_FEE: $scope.inputVO.money, //已準備的退休金額
        SOCIAL_INS_FEE_1: $scope.inputVO.insMonth, //社會保險給付/每月
        SOCIAL_INS_FEE_2: $scope.inputVO.insOneTime, //社會保險給付/一次
        SOCIAL_WELFARE_FEE_1: $scope.inputVO.welfareMonth, //社會福利給付/每月
        SOCIAL_WELFARE_FEE_2: $scope.inputVO.welfareOneTime, //社會福利給付/一次
        COMM_INS_FEE_1: $scope.inputVO.insMonth_B, //商業保險給付/每月
        COMM_INS_FEE_2: $scope.inputVO.insOneTime_B, //商業保險給付/一次
        OTHER_FEE_1: $scope.inputVO.otherMonth, //其他給付/每月
        OTHER_FEE_2: $scope.inputVO.otherOneTime, //其他給付/一次
        HERITAGE: $scope.inputVO.hopeMoney, //希望能傳承給親屬的金額
        total: $scope.retireFunds,
        isDelete: isDelete
      }
      $scope.closeThisDialog(inputVoTemp);
    };

    $scope.chgMomey = function () {
      $scope.inputVO.retired = $scope.inputVO.retired ? Math.floor($scope.inputVO.retired) : '0';
      $scope.inputVO.spendMonth = $scope.inputVO.spendMonth ? Math.floor($scope.inputVO.spendMonth) : '0';
      $scope.inputVO.insMonth = $scope.inputVO.insMonth ? Math.floor($scope.inputVO.insMonth) : '0';
      $scope.inputVO.insOneTime = $scope.inputVO.insOneTime ? Math.floor($scope.inputVO.insOneTime) : '0';
      $scope.inputVO.welfareMonth = $scope.inputVO.welfareMonth ? Math.floor($scope.inputVO.welfareMonth) : '0';
      $scope.inputVO.welfareOneTime = $scope.inputVO.welfareOneTime ? Math.floor($scope.inputVO.welfareOneTime) : '0';
      $scope.inputVO.insMonth_B = $scope.inputVO.insMonth_B ? Math.floor($scope.inputVO.insMonth_B) : '0';
      $scope.inputVO.insOneTime_B = $scope.inputVO.insOneTime_B ? Math.floor($scope.inputVO.insOneTime_B) : '0';
      $scope.inputVO.otherMonth = $scope.inputVO.otherMonth ? Math.floor($scope.inputVO.otherMonth) : '0';
      $scope.inputVO.otherOneTime = $scope.inputVO.otherOneTime ? Math.floor($scope.inputVO.otherOneTime) : '0';
      $scope.inputVO.hopeMoney = $scope.inputVO.hopeMoney ? Math.floor($scope.inputVO.hopeMoney) : '0';
    }

    $scope.init();
    $scope.clear = function () {
      isDelete = true;
      $scope.inputVO = {
        retired: '0', //預計退休年齡 RETIREMENT_AGE
        spendMonth: '0', //退休後每個月花費 RETIRE_FEE
        money: '0',
        insMonth: '0', //
        insOneTime: '0',
        welfareMonth: '0',
        welfareOneTime: '0',
        insMonth_B: '0',
        insOneTime_B: '0',
        otherMonth: '0',
        otherOneTime: '0',
        hopeMoney: '0'
      };
      $scope.retire = '0';
      $scope.money = '0';
      $scope.totalMonth = '0';
      $scope.totalOneTime = '0';
      $scope.retireFunds = '0';
    }
  });
