'use strict';
eSoafApp.controller('SOT516Controller',
    function($rootScope, $scope, $controller, $confirm, sysInfoService, socketService, ngDialog, projInfoService, $filter, getParameter, $q) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "SOT516Controller";

        // 特金 SN 繼承
        $controller('SOT511Controller', {$scope: $scope});
        $scope.inputVO.trustTS = 'M'; //M:金錢信託  S:特金交易
    });