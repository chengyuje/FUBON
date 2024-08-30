/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('FPS310Controller',
    function ($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService, getParameter) {
        $controller('BaseController', {
            $scope: $scope
        });
        $scope.controllerName = "FPS310Controller";
        
        // get function type
        $scope.functionID = projInfoService.getAuthorities().MODULEID.WMS.ITEMID.FPS310 ?
            projInfoService.getAuthorities().MODULEID.WMS.ITEMID.FPS310.FUNCTIONID : {};
        $scope.isMaintenance = $scope.functionID.maintenance;
        /* main function */
        
    });
