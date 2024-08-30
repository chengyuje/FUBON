/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD280Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD280Controller";
		
		$scope.connector('set','PRD260_ptype', 'SI');
		// follow mao151
		$scope.ACTIVE = 0;
		//switch 導向所要的葉面
		if($scope.connector('get','MAO151_PARAMS')!=undefined ){
			switch($scope.connector('get','MAO151_PARAMS').PAGE) {
				case 'HOME':
					$scope.ACTIVE=0;
					break;
				case 'CUSTREVIEW':
					$scope.ACTIVE=1;
					break;
			}
		}
		//FOR PM專區查詢條件設定 PMS407
		$scope.action = function() {
			if($scope.ACTIVE==4){
				$scope.connector('set','PMS407_TYPE_PRD', '01');   
			}
		}
		
});