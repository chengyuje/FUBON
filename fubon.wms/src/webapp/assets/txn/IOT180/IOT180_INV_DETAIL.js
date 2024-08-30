/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('IOT180_INV_DETAILController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "IOT180_INV_DETAILController";
	
		$scope.init = function(){
			$scope.inputVO = {				
				ins_keyno: $scope.row.INS_KEYNO,
				prematchSeq : $scope.row.PREMATCH_SEQ		
        	};
        };
        $scope.init();
        
        /*** 查詢資料 ***/
		$scope.query = function(){			
			$scope.sendRecv("IOT180", "getInvTarget", "com.systex.jbranch.app.server.fps.iot180.IOT180InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if (!isError) {
								if(tota[0].body.invTargetList.length == 0) {
									$scope.showMsg("ehl_01_common_009");
		                			return;
		                		}
								$scope.paramList = tota[0].body.invTargetList;															
								$scope.outputVO = tota[0].body;														
								return;
							}
						}						
			});
		};
		$scope.query();
                	
});
