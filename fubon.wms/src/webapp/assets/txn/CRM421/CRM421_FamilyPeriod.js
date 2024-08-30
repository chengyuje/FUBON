/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM421_FamilyPeriodController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM421_FamilyPeriodController";
		
		$scope.init = function(){
//			alert($scope.cust_id);
			$scope.resultList = [];
			$scope.outputVO = [];
			if($scope.cust_id != undefined){
				$scope.sendRecv("CRM421", "getFamilyPeriod", "com.systex.jbranch.app.server.fps.crm421.CRM421InputVO", 
					{'cust_id': $scope.cust_id},
						function(tota, isError) {
							if (!isError) {
//								alert(JSON.stringify(tota[0].body.resultList));
								$scope.resultList = tota[0].body.resultList;
								$scope.outputVO = tota[0].body;
							}
				});
			}
		}
		$scope.init();
});
