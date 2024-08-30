/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS324_showDataController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $timeout, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS324_showDataController";
		$scope.init = function(){
			$scope.inputVO.startTime = $scope.startTime;
			$scope.inputVO.endTime = $scope.endTime;
	    	$scope.sendRecv("PMS324", "showData", "com.systex.jbranch.app.server.fps.pms324.PMS324InputVO", $scope.inputVO,
	    			function(tota, isError) {
						if (!isError) {
							if(tota[0].body.dataList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
		            			return;
		            		}
							$scope.datList = tota[0].body.dataList;
							$scope.outputVO = tota[0].body;
							return;
						}else{
							$scope.showBtn = 'none';
						}	
			    	});
		}
		$scope.init(); 
});