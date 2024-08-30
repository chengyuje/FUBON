/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('IOT150_INSSIGRPTController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "IOT150_INSSIGRPTController";

		$scope.init = function() {
			$scope.inputVO.unsign = "N";
			$scope.inputVO.signed = "N";
			$scope.inputVO.reject = "N";
		};
		$scope.init();
		
		//產生報表
        $scope.genReport = function(){
           	$scope.sendRecv("IOT150", "genInsSigReport", "java.util.HashMap", {UNSIGN: $scope.inputVO.unsign, 
           																	   SIGNED: $scope.inputVO.signed, 
           																	   REJECT: $scope.inputVO.reject}, 
           		function(tota , isError){
           			if(!isError){}
        	});
        }
});