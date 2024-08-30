/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS210_setNewBountyController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS210_setNewBountyController";
		
		//初始化頁面，先查詢
		$scope.select = function(){
        	$scope.inputVO.yearMon = $scope.resultList.YEARMON;
			$scope.sendRecv("PMS210", "queryNewBounty", "com.systex.jbranch.app.server.fps.pms210.PMS210InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.newBountyList = tota[0].body.newBountyList;
							$scope.outputVO = tota[0].body;
						}
			});
		}
		$scope.select();
		
		
		//跳轉到上傳資料頁面
        $scope.upload = function(YEARMON){
        	var dialog = ngDialog.open({
        		template: 'assets/txn/PMS210/PMS210_UPLOAD.html',
        		className: 'PMS210_UPLOAD',
        		showClose : false,
        		controller: ['$scope', function($scope) {
        			$scope.yearMon = YEARMON;
        			$scope.from = 'newBounty'
        		}]
        	});
        	dialog.closePromise.then(function(data) {
        		if(data.value == 'cancel'){
        			 $scope.select();
 				}
        	});
        };
});