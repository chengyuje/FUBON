/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD290_SettingController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD290_SettingController";
		
		$scope.inputVO = {};
		$scope.sendRecv("PRD290", "getSetting", "com.systex.jbranch.app.server.fps.prd290.PRD290InputVO", {},
				function(tota, isError) {
					if (!isError) {
						var code = tota[0].body.resultList.map(function(e) { return e.PARAM_CODE; });
						$scope.inputVO.fund = tota[0].body.resultList[code.indexOf('FUND')].PARAM_NAME;
						$scope.inputVO.fund_month = tota[0].body.resultList[code.indexOf('FUND_MONTH')].PARAM_NAME;
						return;
					}
		});
		
		$scope.save = function() {
			if($scope.parameterTypeEditForm.$invalid){
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
			$scope.sendRecv("PRD290", "updateSetting", "com.systex.jbranch.app.server.fps.prd290.PRD290InputVO", $scope.inputVO,
					function(totas, isError) {
	                	if (isError) {
	                		$scope.showErrorMsg(totas[0].body.msgData);
	                	}
	                	if (totas.length > 0) {
	                		$scope.showSuccessMsg('ehl_01_common_004');
                    		$scope.closeThisDialog('successful');
	                	};
					}
			);
		};
		
		
});