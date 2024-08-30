/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM990_FLOWController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM990_FLOWController";
		
		// filter
		getParameter.XML(["CRM.COMPLAIN_FLOW_STATUS"], function(totas) {
			if (totas) {
				$scope.mappingSet['CRM.COMPLAIN_FLOW_STATUS'] = totas.data[totas.key.indexOf('CRM.COMPLAIN_FLOW_STATUS')];
			}
		});
		
		//初始化
		$scope.init = function(){
			$scope.resultList = [];
			$scope.outputVO = {};
			if($scope.complain_list_id != undefined){
				$scope.sendRecv("CRM990", "showFlow", "com.systex.jbranch.app.server.fps.crm990.CRM990InputVO", 
					{'complain_list_id': $scope.complain_list_id},
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