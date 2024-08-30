/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('EXPORTFILEController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "EXPORTFILEController";
		
		$rootScope.etablesaw();
		
		$scope.init = function() {
			$scope.inputVO.sql = '';
			$scope.inputVO.type = '';
			$scope.resultList = [];
			$scope.cols = [];
		}
		$scope.init();
		
		
		
		//test
		$scope.test = function() {
			$scope.sendRecv("EXPORTFILE", "test", "com.systex.jbranch.app.server.fps.exportfile.EXPORTFILEInputVO", $scope.inputVO,
					function(totas, isError) {
						if (isError) {
							$scope.showErrorMsg(totas[0].body.msgData);
						}
						if(totas[0].body.resultList.length == 0) {
							$scope.showMsg("ehl_01_common_009");
							return;
						}
						$scope.resultList = totas[0].body.resultList;
						$scope.outputVO = totas[0].body;
						$scope.cols = _.orderBy(Object.keys($scope.resultList[0]));
					}
		)};
	    
	    //export
	    $scope.export = function(type) {
	    	$scope.inputVO.type = type;
			$scope.sendRecv("EXPORTFILE", "export", "com.systex.jbranch.app.server.fps.exportfile.EXPORTFILEInputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						return;
					}
				}
		)};
	    
	    
});