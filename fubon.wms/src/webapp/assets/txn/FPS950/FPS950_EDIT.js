'use strict';
eSoafApp.controller('FPS950_EDITController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "FPS950_EDITController";
		
		// combobox
		getParameter.XML(["FPS.TREND_TYPE", "PRD.MKT_TIER3"], function(totas) {
			if (totas) {
				$scope.TREND_TYPE = totas.data[totas.key.indexOf('FPS.TREND_TYPE')];
				$scope.MKT_TIER3 = totas.data[totas.key.indexOf('PRD.MKT_TIER3')];
				$scope.MKT_TIER3 = _.filter($scope.MKT_TIER3, function(o) { return !(o.DATA == '033' || o.DATA == '999'); });
			}
		});
		
		$scope.init = function() {
			$scope.Datarow = $scope.Datarow || {};
			$scope.inputVO = {
				type: $scope.Datarow.TYPE,
				trend: $scope.Datarow.TREND,
				overview: $scope.Datarow.OVERVIEW
            };
		};
		$scope.init();
		
		$scope.save = function() {
			if($scope.parameterTypeEditForm.$invalid) {
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
			$scope.closeThisDialog($scope.inputVO);
		};
		
		
		
});