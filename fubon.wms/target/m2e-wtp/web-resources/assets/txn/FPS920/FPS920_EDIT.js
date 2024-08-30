'use strict';
eSoafApp.controller('FPS920_EDITController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "FPS920_EDITController";
		
		$scope.init = function() {
			$scope.Datarow = $scope.Datarow || {};
			$scope.inputVO = {
				age_start: $scope.Datarow.AGE_START,
				age_end: $scope.Datarow.AGE_END,
				child_yn: $scope.Datarow.CHILD_YN || 'N',
				ln_house_yn: $scope.Datarow.LN_HOUSE_YN || 'N',
				education: $scope.Datarow.FP_EDUCATION_YN || 'N',
				retire: $scope.Datarow.FP_RETIRE_YN || 'N',
				buyhouse: $scope.Datarow.FP_BUYHOUSE_YN || 'N',
				buycar: $scope.Datarow.FP_BUYCAR_YN || 'N',
				marry: $scope.Datarow.FP_MARRY_YN || 'N',
				ov_education: $scope.Datarow.FP_OVERSEA_EDUCATION_YN || 'N',
				travel: $scope.Datarow.FP_TRAVEL_YN || 'N',
				other: $scope.Datarow.FP_OTHER_YN || 'N'
            };
		};
		$scope.init();
		
		$scope.save = function() {
			if($scope.parameterTypeEditForm.$invalid) {
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
			// 財神推薦特定目的項目
			var temp = [];
			if($scope.inputVO.education == 'Y') temp.push('子女教育規劃');
			if($scope.inputVO.retire == 'Y') temp.push('退休規劃');
			if($scope.inputVO.buyhouse == 'Y') temp.push('購屋規劃');
			if($scope.inputVO.buycar == 'Y') temp.push('購車規劃');
			if($scope.inputVO.marry == 'Y') temp.push('結婚規劃');
			if($scope.inputVO.ov_education == 'Y') temp.push('留遊學規劃');
			if($scope.inputVO.travel == 'Y') temp.push('旅遊規劃');
			if($scope.inputVO.other == 'Y') temp.push('其他規劃');
			$scope.inputVO.money_god = temp.toString();
			//
			$scope.closeThisDialog($scope.inputVO);
		};
		
		
		
});