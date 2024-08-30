/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPS814_PLANController',
    function ($rootScope, $scope, $controller, ngDialog, sysInfoService, socketService, alerts, projInfoService, $q, getParameter, $filter) {
        $controller('BaseController', {
            $scope: $scope
        });
        $scope.controllerName = 'FPS814_PLANController';
        var log = console.log;
        /* init */
        var init = function () {
        	
        };
        
        /* main function */
        $scope.confirm = function(){
        	log($scope.planType);
        	$scope.closeThisDialog({
        		planType:$scope.planType,
        		custID:$scope.custID
        	});
        }
        $scope.cancel = function(){
        	$scope.closeThisDialog('cancel');
        }
        /* helping function */
        
        
        /* main process */
        init();
    }
);
