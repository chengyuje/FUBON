/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM641Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM641Controller";
		$scope.inputVO.cust_id = '';
		$scope.cust_id = '';
		$scope.cust_id = $scope.custVO.CUST_ID;
		$scope.resultList = [];
		$scope.resultList2 = [];
		
		$scope.detail = function () {

			//申請中案件
			$scope.sendRecv("CRM641", "initial", "com.systex.jbranch.app.server.fps.crm641.CRM641InputVO", {'cust_id' : $scope.cust_id },
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.resultList != null && tota[0].body.resultList.length > 0) {
							angular.forEach(tota[0].body.resultList, function(row, index, objs){
								$scope.resultList.push(row);
							});
						}
					}
			});
			
			$scope.sendRecv("CRM641", "loan", "com.systex.jbranch.app.server.fps.crm641.CRM641InputVO", {'cust_id' : $scope.cust_id },
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList != null && tota[0].body.resultList.length > 0) {
								angular.forEach(tota[0].body.resultList, function(row, index, objs){
									$scope.resultList.push(row);
								});
							}
						}
			});
			
			$scope.sendRecv("CRM641", "creditCard", "com.systex.jbranch.app.server.fps.crm641.CRM641InputVO", {'cust_id' : $scope.cust_id},
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList2 != null && tota[0].body.resultList2.length > 0) {
								angular.forEach(tota[0].body.resultList2, function(row, index){
									if(row.CRD_TYPE != null && row.CRD_TYPE != ""
											&& row.APPLY_MSG != null && row.APPLY_MSG != ""){
										$scope.resultList2.push(row);
									}
								});
							}
						}
			});
			
        };
		
		

});