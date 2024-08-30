/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM613Controller',
	function($rootScope, $scope, $controller,$filter, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM613Controller";

		$scope.inputVO.cust_id = '' ;
//		$scope.inputVO.cust_id = $scope.connector('get','CRM110_CUST_ID');
		$scope.inputVO.cust_id =  $scope.custVO.CUST_ID;
			
		
		 getParameter.XML(['CRM.VIP_CHG_TYPE', 'CRM.VIP_DEGREE'], function(totas) {
				if(len(totas)>0){
					$scope.mappingSet['CRM.VIP_CHG_TYPE'] = totas.data[totas.key.indexOf('CRM.VIP_CHG_TYPE')];
					$scope.mappingSet['CRM.VIP_DEGREE'] = totas.data[totas.key.indexOf('CRM.VIP_DEGREE')];
					$scope.mappingSet['CRM.VIP_DEGREE'].push({LABEL: "私人銀行理財", DATA: "V"});
					$scope.mappingSet['CRM.VIP_DEGREE'].push({LABEL: "白金理財", DATA: "A"});
					$scope.mappingSet['CRM.VIP_DEGREE'].push({LABEL: "個人理財", DATA: "B"});
				}
				 
		});
		 
		
		$scope.inquire = function() {
			$scope.resultList = [];
			$scope.outputVO = {};
			$scope.sendRecv("CRM613", "inquire", "com.systex.jbranch.app.server.fps.crm613.CRM613InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList != null && tota[0].body.resultList.length >0 ) {
								$scope.resultList = tota[0].body.resultList;	
								$scope.outputVO = tota[0].body;
							}
						};
			
					});
		};
		$scope.inquire();
		
		$scope.inquire2 = function() {
			$scope.resultList2 =[];
			$scope.outputVO2  = [];
			$scope.sendRecv("CRM613", "inquire2", "com.systex.jbranch.app.server.fps.crm613.CRM613InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList2 != null && tota[0].body.resultList2.length >0 ) {
//								
							//分頁用
							
							$scope.resultList2 = tota[0].body.resultList2;
							$scope.outputVO2 = tota[0].body;
							
							//總和用
							}
						};
			
					});
		};
		
		$scope.inquire2();

		$scope.getUHRMLog = function() {
			$scope.uhrmChgLogList = [];
			$scope.outputVO3 = [];
			$scope.sendRecv("CRM613", "getUHRMLog", "com.systex.jbranch.app.server.fps.crm613.CRM613InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.uhrmChgLogList != null && tota[0].body.uhrmChgLogList.length >0 ) {
//								
							//分頁用
							$scope.uhrmChgLogList = tota[0].body.uhrmChgLogList;							
							$scope.outputVO3 = tota[0].body;
							
							//總和用
							}
						};
			
					});
		};
		$scope.getUHRMLog();
		
});
		