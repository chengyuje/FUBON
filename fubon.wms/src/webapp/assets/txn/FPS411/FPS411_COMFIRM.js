/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('FPS411_COMFIRM_Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope : $scope});
		$scope.controllerName = "FPS411_COMFIRM_Controller";	
		
		$scope.textContent = '特定目的規劃之〝投資天期〞或〝期初投資〞或〝每月投資〞或〝目標金額〞或〝風險承受度級數〞已變更，同意本次調整內容?';			
	}
);
