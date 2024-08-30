/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD500Controller',
	function($rootScope, $scope, $controller, $confirm,$filter, socketService, ngDialog, projInfoService, getParameter, $q) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD500Controller";
		
		$scope.init = function() {
			$scope.inputVO = {};
			$scope.sendRecv("PRD500", "inquire", "com.systex.jbranch.app.server.fps.prd500.PRD500InputVO", {},
				function(totas, isError) {
                	if (!isError) {
                		$scope.inputVO.link_url = totas[0].body.link_url;
    					$scope.inputVO.link_url2 = totas[0].body.link_url2;
    					$scope.inputVO.link_url3 = totas[0].body.link_url3;
                	};
				}
			);
		};
		$scope.init();
		
		$scope.save = function() {
			if($scope.parameterTypeEditForm.$invalid) {
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
			$scope.sendRecv("PRD500", "save", "com.systex.jbranch.app.server.fps.prd500.PRD500InputVO", $scope.inputVO,
					function(totas, isError) {
	                	if (!isError) {
	                		$scope.showSuccessMsg('ehl_01_common_004');
	                	};
					}
			);
		};
		
});