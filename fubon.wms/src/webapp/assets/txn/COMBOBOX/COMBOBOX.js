/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('COMBOBOXController', function($scope, projInfoService, $controller) {
	$controller('BaseController', {
		$scope : $scope
	});
	$scope.controllerName = "COMBOBOXController";

	$scope.eComboDisabled = false;
	
	$scope.eComboDs = [{DATA:'A', LABEL:'AAA', TEST:'隱藏屬性A'}, {DATA:'B', LABEL:'BBB',TEST:'隱藏屬性B'}, {DATA:'C', LABEL:'CCCC',TEST:'隱藏屬性C'}];
	
	$scope.sampleData;
	$scope.sampleLabel;
	$scope.sampleItem;

	$scope.disableCombobox = function() {
		$scope.eComboDisabled = true;
	}
	
	$scope.enableCombobox = function() {
		$scope.eComboDisabled = false;
	}
});
