/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('KYC320_RECController',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "KYC320_RECController";
		
		$scope.init = function(){
			
			$scope.inputVO = {	
					SEQ:$scope.row.SEQ || '',
					REC_SEQ:$scope.row.REC_SEQ || ''				
        	};		
		
		};		
		$scope.init();	
	
		
		$scope.update = function(){
			if($scope.inputVO.REC_SEQ.length < 12){
				$scope.showErrorMsg("錄音序號長度需大於12位");
				$scope.inputVO.REC_SEQ = "";
				return;
			}
			$scope.sendRecv("KYC320", "updateREC_SEQ", "com.systex.jbranch.app.server.fps.kyc320.KYC320InputVO", 
				$scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.showMsg("ehl_01_common_006");
							$scope.closeThisDialog('successful');						
							return;
						}
			});
		};

		// 初始分頁資訊

       	
});
