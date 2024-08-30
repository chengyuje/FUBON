/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM831_billJSBController',
	function($rootScope, $scope, $controller, getParameter, $confirm, socketService, ngDialog, projInfoService , $q) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM831_billJSBController";
		
		//初始化
		$scope.init = function(){
        	$scope.inputVO.cust_id = $scope.custID;
        	      	
        	$scope.sendRecv("CRM831", "inquireBillYearMon", "com.systex.jbranch.app.server.fps.crm831.CRM831InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.mappingSet['YEARMONTH'] = tota[0].body.resultList;
					}
			});
		};
		$scope.init();
		
		//初始化
		$scope.inquireInit = function(){
			$scope.resultListBill = [];
		}
		$scope.inquireInit();
		
		//查詢
		$scope.inquireBillJSB = function() {
			$scope.sendRecv("CRM831", "inquireBillJSB", "com.systex.jbranch.app.server.fps.crm831.CRM831InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
								$scope.resultListBill = [];
	                			return;
	                		}
							$scope.resultListBill = tota[0].body.resultList;
							$scope.outputVOBill = tota[0].body;
						}
			});
	    };
		
});