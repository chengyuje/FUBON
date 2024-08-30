/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPS800Controller',
  function ($rootScope, $scope, $controller, sysInfoService, getParameter, $q) {
    $controller('BaseController', {
      $scope: $scope
    });
    $scope.controllerName = 'FPS800Controller';

    /* parameter */
    $scope.priID = sysInfoService.getPriID();
    $scope.regionID = sysInfoService.getRegionID();
    $scope.areaID = sysInfoService.getAreaID();
    $scope.branchID = sysInfoService.getBranchID();
    // SPP, INV
    $scope.planType = 'SPP';
    // -1: 空, 1: 理專,2: 主管,3: 總行
    $scope.roleType = -1;
    $scope.regions = [];
    $scope.mapping = {
      roleType: {
        1: ['002'], // 理專
        2: ['009', '010', '011', '012', '013'], // 主管
        3: ['000'] // 總行
      },
      path: {
        // regular report
        fps: 'assets/txn/FPS810/FPS810.html',
        // detail path
        empCnt: 'assets/txn/FPS813/FPS813.html',
        custCnt: 'assets/txn/FPS814/FPS814.html',
        amountCnt: 'assets/txn/FPS815/FPS815.html',
        // advance query
        advAmt: 'assets/txn/FPS816/FPS816.html',
        advPfm: 'assets/txn/FPS817/FPS817.html',
        advAll: 'assets/txn/FPS818/FPS818.html',
      }
    };
    $scope.Tab = null;
    /* init */
    var param = function () {
      var defer = $q.defer();
      $scope.sendRecv('FPS810', 'getRegions', 'com.systex.jbranch.app.server.fps.fps810.FPS810InputVO', {},
        function (tota, isError) {
          if (!isError) {
            $scope.regions = tota[0].body || [];
            defer.resolve(true);
            return true;
          }
          defer.reject(tota);
          return false;
        });

      return defer.promise;
    };
    /* main function */
    $scope.tab = function (i) {
      $scope.tabSelected = parseInt(i, 10) > 3 ? 3 : parseInt(i, 10);
      switch (i) {
        case '0':
          $scope.planType = undefined;
          $scope.fps800Path = $scope.mapping.path.fps;
          break;
        case '1':
          $scope.planType = 'SPP';
          $scope.fps800Path = $scope.mapping.path.fps;
          break;
        case '2':
          $scope.planType = 'INV';
          $scope.fps800Path = $scope.mapping.path.fps;
          break;
        case '3':
          if ($scope.roleType === 3) {
            $scope.fps800Path = $scope.mapping.path.advAll;
          } else {
            $scope.fps800Path = $scope.mapping.path.advAmt;
          }
          break;
        case '4':
          if ($scope.roleType === 3) {
            $scope.fps800Path = $scope.mapping.path.advAll;
          } else {
            $scope.fps800Path = $scope.mapping.path.advAmt;
          }
          break;
        case '5':
          if ($scope.roleType === 3) {
            $scope.fps800Path = $scope.mapping.path.advAll;
          } else {
            $scope.fps800Path = $scope.mapping.path.advPfm;
          }
          break;
        default:
          if ($scope.roleType === 3) {
            $scope.tab('1');
          } else {
            $scope.tab('0');
          }
          break;
      }

    };

    var getRoleType = function () {
      if ($scope.mapping.roleType[2].indexOf($scope.priID[0]) >= 0) {
        $scope.roleType = 2;
      } else if ($scope.mapping.roleType[1].indexOf($scope.priID[0]) >= 0) {
        $scope.roleType = 1;
      } else if ($scope.mapping.roleType[3].indexOf($scope.branchID) >= 0) {
        $scope.roleType = 3;
      }
    };


    /* main progress */
    getRoleType();
    param()
      .then(function () {
        $scope.tab();
      });

  }
);
