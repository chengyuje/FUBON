/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM3121_DETAILController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM3121_DETAILController";
		
		$scope.init = function(){
			$scope.inputVO = {
					region_center_id: $scope.row.REGION_CENTER_NAME,
					branch_area_id: $scope.row.BRANCH_AREA_NAME,
					branch_nbr: $scope.row.BRANCH_NBR,
					new_ao_code: $scope.row.AO_CODE,
					limit_s_date: $scope.toJsDate($scope.row.START_DATE),
					limit_e_date: $scope.toJsDate($scope.row.END_DATE)
			};
		};
		$scope.init();
		
		
		$scope.detail = function(){
			$scope.sendRecv("CRM3121", "detail", "com.systex.jbranch.app.server.fps.crm3121.CRM3121InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.resultList2.length == 0) {
							$scope.showMsg("ehl_01_common_009");
							return;
						}
						$scope.resultList2 = tota[0].body.resultList2;
						return;
					}
				}
		)};
		$scope.detail();
		$scope.exportfile = function(){
			$scope.sendRecv("CRM3121", "exportfile", "com.systex.jbranch.app.server.fps.crm3121.CRM3121InputVO", {'list': $scope.resultList2},
				function(totas, isError) {
	            	if (isError) {
	            		$scope.showErrorMsg(totas[0].body.msgData);
	            	}
			}	
		)};
		
		
});
