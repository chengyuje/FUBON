/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS210_setSpBounsController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS210_setSpBounsController";
		
		//初始化頁面，先查詢
		$scope.select = function(){
        	$scope.inputVO.yearMon = $scope.resultList.YEARMON;
			$scope.sendRecv("PMS210", "querySpBouns", "com.systex.jbranch.app.server.fps.pms210.PMS210InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.spBounsList = tota[0].body.spBounsList;
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
        			$scope.from = 'spBouns'
        		}]
        	});
        	dialog.closePromise.then(function(data) {
        		if(data.value == 'cancel'){
        			 $scope.select();
 				}
        	});
        };
});