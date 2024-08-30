/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS105_DETAILController',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS105_DETAILController";
		// filter
		var vo = {'param_type': 'CRM.VIP_DEGREE', 'desc': false};
        if(!projInfoService.mappingSet['CRM.VIP_DEGREE']) {
        	$scope.requestComboBox(vo, function(totas) {
        		if (totas[totas.length - 1].body.result === 'success') {
        			projInfoService.mappingSet['CRM.VIP_DEGREE'] = totas[0].body.result;
        			$scope.mappingSet['CRM.VIP_DEGREE'] = projInfoService.mappingSet['CRM.VIP_DEGREE'];
        		}
        	});
        } else
        	$scope.mappingSet['CRM.VIP_DEGREE'] = projInfoService.mappingSet['CRM.VIP_DEGREE'];
        var vo = {'param_type': 'CRM.CON_DEGREE', 'desc': false};
        if(!projInfoService.mappingSet['CRM.CON_DEGREE']) {
        	$scope.requestComboBox(vo, function(totas) {
        		if (totas[totas.length - 1].body.result === 'success') {
        			projInfoService.mappingSet['CRM.CON_DEGREE'] = totas[0].body.result;
        			$scope.mappingSet['CRM.CON_DEGREE'] = projInfoService.mappingSet['CRM.CON_DEGREE'];
        		}
        	});
        } else
        	$scope.mappingSet['CRM.CON_DEGREE'] = projInfoService.mappingSet['CRM.CON_DEGREE'];
       
		$scope.inquire = function(){
			
			var inputVO = {'ao_code': $scope.row.AO_CODE,'reportDate':$scope.row.YEARMON,
					'branch_area_id':$scope.row.BRANCH_AREA_ID,currentPageIndex: $scope.inputVO.currentPageIndex,'type':$scope.row.set};
			$scope.sendRecv("PMS105", "getDetail", "com.systex.jbranch.app.server.fps.pms105.PMS105InputVO",inputVO ,
					function(tota, isError) {
 						if (!isError) {
							$scope.paramList = tota[0].body.resultList;
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
