/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('FPSSearchController',
  function ($rootScope, $scope, $controller, $confirm, socketService, projInfoService, sysInfoService, getParameter) {
    $controller('BaseController', {
      $scope: $scope
    });
    $scope.controllerName = 'FPSSearchController';

    //組織連動繼承
    $controller('RegionController', {
      $scope: $scope
    });

    /* parameter */
    // 組織
    $scope.region = [
      'N', $scope.inputVO,
      'region', 'REGION_LIST',
      'op', 'AREA_LIST',
      'branchID', 'BRANCH_LIST',
      'aoCode', 'AO_LIST',
      'qq', 'EMP_LIST'
    ];
    var aoCode = sysInfoService.getAoCode();
    var branch = sysInfoService.getBranchID();
    /* init */
    $scope.init = function () {
      $scope.inputVO = {
        custID: '',
        aoCode: aoCode,
        branchID: branch || ''
      };
      $scope.paramList = [];
    };

    /* main function */
    $scope.inquire = function () {
      if (!$scope.inputVO.branchID || ($scope.inputVO.branchID === '000' && !$scope.inputVO.custID)) {
        return false;
      }
      $scope.sendRecv('FPS200', 'searchCust', 'com.systex.jbranch.app.server.fps.fps200.FPS200InputVO',
        $scope.inputVO,
        function (tota, isError) {
          if (!isError) {
            //接取回傳 客戶資訊
            $scope.paramList = tota[0].body.outputList;
            $scope.outputVO = tota[0].body;
            if ($scope.paramList.length === 0) {
              $scope.showMsg('ehl_01_common_009'); // 查無資料
              return;
            }
          } else {
            console.log('no data');
            $scope.showMsg('ehl_01_common_009'); // 查無資料
          }
        });
    };

    $scope.go = function (where, data) {
      switch (where) {
        case 'back':
          $scope.closeThisDialog(data);
          break;
      }
    };

    /* main progress */
    $scope.RegionController_setName($scope.region);
    $scope.init();
    // $scope.inquire();
  }
);
