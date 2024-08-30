/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('FPS814Controller', function ($rootScope, $scope,
  $controller, ngDialog, sysInfoService, socketService, alerts,
  projInfoService, $q, getParameter, $filter) {
  $controller('BaseController', {
    $scope: $scope
  });
  $scope.controllerName = 'FPS814Controller';
  var log = console.log;

  let priID = sysInfoService.getPriID();
  let regionID = sysInfoService.getRegionID();
  let areaID = sysInfoService.getAreaID();
  let branchID = sysInfoService.getBranchID();
  $scope.roleType = -1;
  // roletype -1: 空, 1: 業務處處長,2: 督導,3: 業務主管OR分行個金主管, 4: 理專
  var mapping = {
    roleType: {
      1: ['013'],
      2: ['012'],
      3: ['009', '010', '011'],
      4: ['002']
    }
  };

  /* init */
  $scope.init = function () {
    $scope.inputVO = {
      regionCenterID: $scope.roleType === 3 ? regionID : undefined,
      branchNBR: $scope.roleType === 4 ? branchID : undefined,
      type: $scope.type,
      roleType: $scope.roleType
    };
  };

  /* main function */
  $scope.dispatcher = function () {
    $scope.sendRecv('FPS814', 'dispatcher',
      'com.systex.jbranch.app.server.fps.fps814.FPS814InputVO',
      $scope.inputVO,
      function (tota, isError) {
        if (!isError) {
          $scope.paramList = tota[0].body.outputList;
          $scope.vipA = tota[0].body.vipA;
          $scope.vipB = tota[0].body.vipB;
          $scope.vipV = tota[0].body.vipV;
          console.log($scope.paramList)
        }
      });
  };

  $scope.getPlans = function (data) {

    // 點姓名時，先判斷是否大於1，大於1 表示要彈跳視窗讓使用者選導向特定/非特定
    // 若等於1，INV: 非特定 SPP:特定
    if (data.CNT > 1) {
      openDialog(data);
    } else {
      jump({
        planType: data.PLAN_TYPE,
        custID: data.CUST_ID
      });
    }

  };

  /* helping function */

  var getRoleType = function () {
    if (mapping.roleType[3].indexOf(priID[0]) >= 0) {
      $scope.roleType = 3;
    } else if (mapping.roleType[2].indexOf(priID[0]) >= 0) {
      $scope.roleType = 2;
    } else if (mapping.roleType[1].indexOf(priID[0]) >= 0) {
      $scope.roleType = 1;
    } else if (mapping.roleType[4].indexOf(priID[0]) >= 0) {
      $scope.roleType = 4;
    }
  };

  var getData = function () {
    for (var i = 0; i < $scope.data.length; i++) {
      var tmp = $scope.data[i];
      for (var j = 0; j < tmp.length; j++) {
        if (tmp[j].title === '全行各級客戶使用比例')
          $scope.all = tmp[j];
        if (tmp[j].title === '轄下各級客戶使用比例')
          $scope.domination = tmp[j];
      }
    }
  }

  var openDialog = function (data) {
    var dialog = ngDialog.open({
      template: 'assets/txn/FPS814/FPS814_PLAN.html',
      className: 'FPS814_PLAN',
      showClose: false,
      controller: ['$scope', function ($scope) {
        $scope.custID = data.CUST_ID
      }]
    });

    dialog.closePromise.then(function (data) {
      if (data.value != 'cancel') {
        jump(data.value);
      }
    });
  }

  var jump = function (data) {
    log(data.planType);
    projInfoService.fromFPS814 = true;
    var path = (data.planType == 'SPP') ? 'assets/txn/FPS300/FPS300.html' : 'assets/txn/FPS200/FPS200.html';
    var inputType = (data.planType == 'SPP') ? 'FPS300' : 'FPS200';
    $scope.connector('set', inputType, {
      custID: data.custID
    });
    $scope.closeThisDialog('cancel');
    $rootScope.menuItemInfo.url = path;
  }

  /* main process */
  getRoleType();
  $scope.init();
  getData();
  $scope.dispatcher();
});
