/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('INS140_RELATIONController', function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter, getParameter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "INS140_RELATIONController";
	
	$scope.init = function() {
		$scope.getRelationList();
	}
	
	$scope.closeDlog = function (){
		$scope.closeThisDialog($scope.inputVO.total);
	}
	
	//取關西戶名單
	$scope.getRelationList = function() {
		if($scope.paramList && $scope.paramList.length > 0){
			for(var i = 0 ; i < $scope.paramList.length ; i++){
				$scope.paramList[i].val = false;
				$scope.paramList[i].idx = i;
			}
		}
	}
	
	$scope.seleltChkBox = function(idx) {
		$scope.paramList[idx].val = !$scope.paramList[idx].val; 
	}
	
	$scope.backINS140 = function() {
		var rtnFamilyList = [];
		
		for(var idx in $scope.paramList){
			if($scope.paramList[idx].val){
				rtnFamilyList.push($scope.paramList[idx]);
			}
		}
		
		$scope.closeThisDialog(rtnFamilyList);
	}
	
	//action
	$scope.init();
});