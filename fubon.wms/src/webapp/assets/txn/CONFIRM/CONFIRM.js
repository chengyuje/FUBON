/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('CONFIRMController', 
    function($scope, projInfoService, $controller, $confirm) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "CONFIRMController";
        
        $scope.name = 'Angus';
        
        $scope.confirm = function(){
        	$confirm({text: '確定要重置？'/*, ok: 'OK', cancel: 'Cancel'*/})
            .then(function() {
            	//執行某些事
            });
        };
        
        $scope.del = function(){
        	console.log('delete...');
        }
    }
);
