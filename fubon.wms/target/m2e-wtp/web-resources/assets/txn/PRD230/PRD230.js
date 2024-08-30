/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD230Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD230Controller";
		//預設為首頁
		$scope.ACTIVE = 0;
		//switch 導向所要的葉面
		if($scope.connector('get','MAO151_PARAMS')!=undefined ){
			switch($scope.connector('get','MAO151_PARAMS').PAGE) {
				case 'HOME':
					$scope.ACTIVE=0;
					break;
				case 'FUNDBONUSINFO':
					$scope.ACTIVE=2;
					break;			
			}
		}

		
		
});