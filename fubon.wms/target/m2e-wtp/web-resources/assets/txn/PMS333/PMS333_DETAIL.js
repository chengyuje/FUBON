/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS333_DETAILController',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS333_DETAILController";
		
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
     
		if ($scope.row.ind == '1') {
			$scope.type = 'H';
		}
		if ($scope.row.ind == '2') {
			$scope.type = 'T';
		}
		if ($scope.row.ind == '3') {
			$scope.type = 'K';
		}
		if ($scope.row.ind == '4') {
			$scope.type = "%";
		}
	
		$scope.inquire = function(){
			$scope.sendRecv("PMS333", "getDetail", "com.systex.jbranch.app.server.fps.pms333.PMS333InputVO", {'CUST_ID': $scope.row.CUST_ID,'YEARMON':$scope.row.YEARMON
				,'branch': $scope.row.BRANCH_NBR
				,'CUST_DEGREE': $scope.row.CON_DEGREE
				,'IND':$scope.type
			
			},
					function(tota, isError) {
						if (!isError) {
							
//							$scope.pagingList($scope.paramList, tota[0].body.resultList);
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
