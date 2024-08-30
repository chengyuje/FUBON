/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS104_DETAILController',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS104_DETAILController";
	
		var vo = {'param_type': 'PMS.SALE_PLAN_PRD_TYPE', 'desc': false};
		if(!projInfoService.mappingSet['PMS.SALE_PLAN_PRD_TYPE']) {
	    	$scope.requestComboBox(vo, function(totas) {
	    		if (totas[totas.length - 1].body.result === 'success') {
	    			projInfoService.mappingSet['PMS.SALE_PLAN_PRD_TYPE'] = totas[0].body.result;
	    			$scope.mappingSet['PMS.SALE_PLAN_PRD_TYPE'] = projInfoService.mappingSet['PMS.SALE_PLAN_PRD_TYPE'];
	    		}
	    	});
	    } else {
	    	$scope.mappingSet['PMS.SALE_PLAN_PRD_TYPE'] = projInfoService.mappingSet['PMS.SALE_PLAN_PRD_TYPE'];
	    }
		$scope.init = function(){
			$scope.inputVO.cfYearomn = $scope.row.CF_YEARMON;
			$scope.inputVO.custID = $scope.row.CUST_ID;
			$scope.inputVO.reportDate=$scope.row.YEARMON ;
			$scope.inputVO.custID=$scope.row.CUST_ID;
		};
		
        $scope.init();
		$scope.inquire = function(){
			$scope.sendRecv("PMS104", "getDetailInv", "com.systex.jbranch.app.server.fps.pms104.PMS104InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.paramList = tota[0].body.resultList;
							$scope.outputVO1 = tota[0].body;
							return;
						}
			});
		};
		$scope.inquire();
		
		$scope.inquireIns = function(){
			$scope.sendRecv("PMS104", "getDetailIns", "com.systex.jbranch.app.server.fps.pms104.PMS104InputVO",$scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.paramListIns = tota[0].body.resultList;
							$scope.outputVO2 = tota[0].body;
							return;
						}
			});
		};
		$scope.inquireIns();
		// 初始分頁資訊
        $scope.inquireInit = function(){
        	$scope.initLimit();
        	$scope.paramList = [];
        };
        $scope.inquireInit();
        
        
		
});
