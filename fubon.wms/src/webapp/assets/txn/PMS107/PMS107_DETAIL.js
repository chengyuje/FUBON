/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS107_DETAILController',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS107_DETAILController";
		
		
		//filter
		getParameter.XML(["CRM.VIP_DEGREE", "CRM.CON_DEGREE"], function(totas) {
			if (totas) {				
				$scope.mappingSet['CRM.VIP_DEGREE'] = totas.data[totas.key.indexOf('CRM.VIP_DEGREE')];
				$scope.mappingSet['CRM.CON_DEGREE'] = totas.data[totas.key.indexOf('CRM.CON_DEGREE')];
			}
		});
		
		        
	
		$scope.inquire = function(){
			$scope.sendRecv("PMS107", "getDetail", "com.systex.jbranch.app.server.fps.pms107.PMS107InputVO", {'ao_code': $scope.row.AO_CODE,'YEARMON':$scope.row.YEARMON},
					function(tota, isError) {
						if (!isError) {
							$scope.paramList=tota[0].body.resultList;
							$scope.paramList2=tota[0].body.resultList2;
							$scope.outputVO = tota[0].body;
							return;
						}
			});
		};
		$scope.inquire();
		
		// 初始分頁資訊
        $scope.inquireInit = function(){
        	$scope.initLimit();
        	$scope.paramList = [];
        };
        $scope.inquireInit();
        
     
});
