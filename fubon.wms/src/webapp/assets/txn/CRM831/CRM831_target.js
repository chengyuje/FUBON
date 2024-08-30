/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM831_moneyController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM831_moneyController";
		
		//初始化
		$scope.init = function(){
        	$scope.inputVO = {
        			cust_id: $scope.connector('get','CRM830ID'),
        			ins_type: $scope.connector('get','CRM830TYPE'),
					policy_nbr: $scope.row.POLICY_NBR
            };
		};
		$scope.init();
		
		//初始化
		$scope.inquireInit = function(){
			$scope.resultList3 = [];
		}
		$scope.inquireInit();
		
		//查詢
		$scope.inquire3 = function() {
			$scope.sendRecv("CRM831", "inquire3", "com.systex.jbranch.app.server.fps.crm831.CRM831InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList3.length == 0) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.resultList3 = tota[0].body.resultList3;
							return;
						}
			});
	    };
	    $scope.inquire3();
		
		
		
});