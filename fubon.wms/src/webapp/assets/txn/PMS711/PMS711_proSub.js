/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS711_proSubController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		console.log('111')
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS711_proSubController";
		$scope.select = function() {
			$scope.inputVO.date_year = $scope.date_year;
			$scope.inputVO.personType = $scope.personType;
			$scope.inputVO.branchs = $scope.branchs;
			$scope.showRowList = new Object();
			for(var key in $scope.showMonthList){
				$scope.showRowList[$scope.showMonthList[key]] = key; 
			}
			$scope.inputVO.subProjectSeqId = $scope.subProjectSeqId;
			$scope.sendRecv("PMS711","queryProSub","com.systex.jbranch.app.server.fps.pms711.PMS711InputVO",$scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.showList = tota[0].body.showList;
						return;
					}
				});
			
		}
		$scope.select();
		
});