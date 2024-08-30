/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('INS000Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "INS000Controller";
		
		// 預設值
		$scope.ACTIVE =0;
		//switch 導向所要的頁面		
		if($scope.connector('get','INS_PARGE')!=undefined ){
			var page = $scope.connector('get','INS_PARGE');
			switch(page) {
				case 'INS110':
					$scope.ACTIVE=1;
					break;
				case 'INS130':
					$scope.ACTIVE=2;
					$scope.connector('set', "INS130_query", true);
					break;		
				case 'INS140':
					$scope.ACTIVE=3;
					$scope.connector('set', "INS140_query", true);
					break;	
			}
		}
		$scope.connector('set', "INS_PARGE", undefined);
});