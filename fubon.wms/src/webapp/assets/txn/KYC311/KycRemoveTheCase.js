/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('KcyRemoveTheCase_Controller', function($rootScope, $scope, $filter, $controller,$confirm , socketService, ngDialog, projInfoService,sysInfoService,$timeout,getParameter) {
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "KcyRemoveTheCase_Controller";
	
	$scope.init = function(){
		$scope.delradio = -1;
	}
	
	$scope.init();
	
	$scope.save = function(){
		
		if($scope.delradio != -1){
			$scope.closeThisDialog($scope.delradio);
			return;
		}
	}
});
