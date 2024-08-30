/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SOT526Controller',
    function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter, getParameter, $q) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "SOT526Controller";

        // 特金 SN 贖回繼承
        $controller('SOT521Controller', {$scope: $scope});

        // 進入畫面時...
        $scope.SOT526init = function() {
            $scope.inputVO.trustTS = 'M'; // S=特金/M=金錢信託
            $scope.inputVO.GUARDIANSHIP_FLAG = $scope.connector('get', 'SOT521GUARDIANSHIP_FLAG');
            $scope.inputVO.trustPeopNum = $scope.connector('get', 'SOTTrustPeopNum');
        }

        $scope.SOT526init();
    });