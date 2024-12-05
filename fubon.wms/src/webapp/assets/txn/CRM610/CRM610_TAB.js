/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM610_TABController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService,getParameter) {
		$controller('BaseController', {$scope: $scope}); 
		$scope.controllerName = "CRM610_TABController";
		
		$scope.inputVO.cust_id = '' ;
		$scope.resultList = [];
//		$scope.inputVO.cust_id = $scope.connector('get','CRM110_CUST_ID');
		$scope.inputVO.cust_id =  $scope.custVO.CUST_ID;
		getParameter.XML(["CRM.FAMILY_DEGREE"], function(totas) {
			if (totas) {
				$scope.mappingSet['CRM.FAMILY_DEGREE'] = totas.data[totas.key.indexOf('CRM.FAMILY_DEGREE')];
			}
		});
		
		
		
		$scope.sendRecv("CRM610", "initial", "com.systex.jbranch.app.server.fps.crm610.CRM610InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.resultList != null && tota[0].body.resultList.length > 0) {
							$scope.resultList = tota[0].body.resultList;
							$scope.cust_id = $scope.resultList[0].CUST_ID;
							$scope.cust_name = $scope.resultList[0].CUST_NAME;
							$scope.branch_name =  $scope.resultList[0].BRANCH_NAME;
							$scope.vip_degree = $scope.resultList[0].VIP_DEGREE;
							$scope.ao_code = $scope.resultList[0].AO_CODE;
							$scope.ao_type_name = $scope.resultList[0].C_TYPE_NAME;
							$scope.emp_name = $scope.resultList[0].EMP_NAME;
							$scope.uemp_id = $scope.resultList[0].UEMP_ID;
							$scope.uemp_name = $scope.resultList[0].UEMP_NAME;
							$scope.family_degree = $scope.resultList[0].FAMILY_DEGREE;
                		}
					}
			});
		
		
		
});