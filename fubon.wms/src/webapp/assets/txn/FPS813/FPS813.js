/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPS813Controller',
  function ($scope, $controller, ngDialog, sysInfoService) {
    $controller('BaseController', {
      $scope: $scope
    });
    $scope.controllerName = 'FPS813Controller';

    var priID = sysInfoService.getPriID();
    var regionID = sysInfoService.getRegionID();
    var areaID = sysInfoService.getAreaID();
    var branchID = sysInfoService.getBranchID();
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
      $scope.sendRecv('FPS813', 'dispatcher', 'com.systex.jbranch.app.server.fps.fps813.FPS813InputVO',
        $scope.inputVO,
        function (tota, isError) {
          if (!isError) {
            $scope.paramList = tota[0].body.outputList;
            console.log($scope.paramList);
          }
        });
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
          if (tmp[j].title === '全行各級理專使用比例')
            $scope.all = tmp[j];
          if (tmp[j].title === '轄下各級理專使用比例')
            $scope.domination = tmp[j];
        }
      }
    }

    /* main process */
    getRoleType();
    $scope.init();
    getData();
    $scope.dispatcher();
  }
);
