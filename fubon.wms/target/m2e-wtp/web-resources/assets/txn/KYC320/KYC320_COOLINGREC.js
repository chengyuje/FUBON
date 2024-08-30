/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('KYC320_COOLINGRECController',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "KYC320_COOLINGRECController";
		
		$scope.init = function(){
			$scope.inputVO = {
					SEQ : $scope.row.SEQ || '',
					COOLING_REC_SEQ : '',
					CUST_ID : $scope.row.CUST_ID ||'',
        	};		
		};		
		$scope.init();	
		
		$scope.update = function(){
			if($scope.inputVO.REC_SEQ == ''){
				$scope.showErrorMsg("ehl_01_common_022");
				return;
			}
			$scope.sendRecv("KYC320", "coolingPeriodReview", "com.systex.jbranch.app.server.fps.kyc320.KYC320InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.showMsg("ehl_01_common_006");
							$scope.closeThisDialog('successful');						
							return;
						}
			});
		};

});
