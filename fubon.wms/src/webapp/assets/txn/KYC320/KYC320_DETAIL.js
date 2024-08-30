/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('KYC320_DETAILController',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "KYC320_DETAILController";
		
		$scope.init = function(){
			
			$scope.inputVO = {	
					TYPE:$scope.row.TYPE || '',
					OUT_ACCESS:$scope.row.OUT_ACCESS || '',
					SEQ:$scope.row.SEQ || ''
					
        	};		
		
		};		
		$scope.init();	
	
		
		$scope.update = function(){
			$scope.sendRecv("KYC320", "getDetail", "com.systex.jbranch.app.server.fps.kyc320.KYC320InputVO", 
				$scope.inputVO,
					function(tota, isError) {
						if (tota.length > 0) {
							if(tota[0].body != null){
		            			if(tota[0].body.ErrorMsg != null)
		            				$scope.showErrorMsg('ehl_01_KYC310_009',tota[0].body.ErrorMsg);
		            			else if(tota[0].body.ErrorMsg016 != null)
		            				$scope.showErrorMsg(tota[0].body.ErrorMsg016);
		            			return;
		            		}
		            	}
						if (!isError) {
							$scope.showMsg("ehl_01_common_006");
							$scope.closeThisDialog('successful');						
							return;
						}
			});
		};

		// 初始分頁資訊

        
        
        
      
   
        
     
        
		
});
