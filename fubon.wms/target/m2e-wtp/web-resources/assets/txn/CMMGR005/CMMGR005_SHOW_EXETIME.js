/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CMMGR005showExeTimeController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CMMGR005showExeTimeController";

		/*
		 * 載入初始資訊
		 */
		$scope.inquireExecuteTime = function() {
			$scope.sendRecv("CMMGR005", "inquireExecuteTime", "com.systex.jbranch.app.server.fps.cmmgr005.CMMGR005InputVO", {},
					function(tota, isError) {
				if (!isError) {
					var data = tota[0].body
					$scope.outputVO = angular.copy(data);
					$scope.ap1List = data.ap1List;
					$scope.ap2List = data.ap2List;
				}
			});
		}
		$scope.inquireExecuteTime();
});