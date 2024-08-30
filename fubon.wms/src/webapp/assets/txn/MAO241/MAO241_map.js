/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('MAO241_mapController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "MAO241_mapController";
		
		//在Login.html 以及  MobilePlatform.html JS 加入API KEY 
		
		$scope.latitude = $scope.row.POS_LONG;
		$scope.longitude = $scope.row.POS_LATI;
		
		$scope.map = { center: { latitude: $scope.latitude, longitude: $scope.longitude }, zoom: 17 };
		$scope.marker = {
			      id: 0,
			      coords: {
			        latitude: $scope.latitude,
			        longitude: $scope.longitude
			      }
		}
		
});