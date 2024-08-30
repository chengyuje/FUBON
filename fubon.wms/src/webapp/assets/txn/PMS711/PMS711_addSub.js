/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS711_addSubController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS711_addSubController";
		
		//初始化頁面，先查詢
		$scope.select = function() {
			$scope.inputVO.date_year = $scope.date_year;
			$scope.inputVO.personType = $scope.personType;
			$scope.inputVO.subProjectSeqId = $scope.subId;
			$scope.sendRecv("PMS711","queryAddSub","com.systex.jbranch.app.server.fps.pms711.PMS711InputVO",$scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.showList.length>0){
								$scope.showList = tota[0].body.showList;
								$scope.outputVO = tota[0].body;
							}
						}						
					});
		}
		$scope.select();
		
});