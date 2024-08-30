/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS210_setBsAumController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS210_setBsAumController";
	
		

		$scope.select = function() {
			$scope.inputVO.yearMon = $scope.resultList.YEARMON;
			$scope.inputVO.userID = projInfoService.getUserID();
			$scope.sendRecv("PMS210", "queryBsAum",
					"com.systex.jbranch.app.server.fps.pms210.PMS210InputVO",
					$scope.inputVO, function(tota, isError) {
						if (!isError) {
							$scope.bsAumList = tota[0].body.bsAumList;
							$scope.outputVO = tota[0].body;
							return;
						}
					});
		}
		$scope.select();
		
		/** 上傳窗口彈出 **/
		$scope.upload = function(yearMon) {
			var dialog = ngDialog.open({
				template : 'assets/txn/PMS210/PMS210_UPLOAD.html',
				className : 'PMS210_UPLOAD',
				showClose : false,
				controller : [ '$scope', function($scope) {
					$scope.yearMon = yearMon;
					$scope.from = 'bsAum';
				} ]
			});
			dialog.closePromise.then(function(data) {
				if (data.value == 'cancel') {
					$scope.select();
				}
			});
		}
		
});