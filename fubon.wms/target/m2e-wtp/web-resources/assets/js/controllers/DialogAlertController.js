/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('DialogAlertController', ['$rootScope', '$scope', 'socketService', 'ngDialog', 'projInfoService', '$confirm', '$timeout',
    function($rootScope, $scope, socketService, ngDialog, projInfoService, $confirm, $timeout) {
        $scope.controllerName = "DialogAlertController";

    	$scope.list = $rootScope.connector('get','DialogAlertController');
    	
    	$scope.stopAlert = function(){
    		$rootScope.$calendar.stop==true?$rootScope.$calendar.stop=false:$rootScope.$calendar.stop=true;
    	}
    	
}]);