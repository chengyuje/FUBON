/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('I18N_EXAMPLEController', 
    function($scope, $controller, socketService, ngDialog, projInfoService) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "I18N_EXAMPLEController";
        
        $scope.errorCode1 = 'E99';
        $scope.errorCode2 = 'W99';
    }
);
