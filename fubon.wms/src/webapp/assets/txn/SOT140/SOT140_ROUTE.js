
'use strict';
eSoafApp.controller('SOT140_ROUTEController', 
    function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $confirm) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "SOT140_ROUTEController"; 
    }
);