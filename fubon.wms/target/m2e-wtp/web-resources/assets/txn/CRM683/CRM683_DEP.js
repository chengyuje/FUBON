/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM683_DEPController',
	function($rootScope, $scope, $controller, $confirm, socketService, getParameter, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM683_DEPController";
		
		//初始化
		$scope.init = function(){
			$scope.inputVO.cust_id = $scope.row.DA_ARR01.trim();
			$scope.inputVO.contract_no = $scope.row.DA_ARR03.trim();
			$scope.cdList = [];
			$scope.tdList = [];
			$scope.ftdList = [];
		};
		$scope.init();
		
		getParameter.XML(['CRM.MON_TRUST_INT_WAY','CRM.MON_TRUST_RENEW_WAY','CRM.INT_RATE_TYPE'], function(totas) {
			if(len(totas)>0){
				$scope.mappingSet['CRM.MON_TRUST_INT_WAY'] = totas.data[totas.key.indexOf('CRM.MON_TRUST_INT_WAY')];
				$scope.mappingSet['CRM.MON_TRUST_RENEW_WAY'] = totas.data[totas.key.indexOf('CRM.MON_TRUST_RENEW_WAY')];
				$scope.mappingSet['CRM.INT_RATE_TYPE'] = totas.data[totas.key.indexOf('CRM.INT_RATE_TYPE')];
			}
		});
		
		//同金錢信託電文
		$scope.inquireDep = function(){
			$scope.sendRecv("CRM827", "inquireCDep", "com.systex.jbranch.app.server.fps.crm827.CRM827InputVO", $scope.inputVO,
				function(tota, isError){
					if(!isError){
						$scope.cdList = tota[0].body.CDepList;  //活存
						$scope.outputVO1 = tota[0].body.CDepList;
					}else{
						$scope.showErrorMsg("ehl_01_common_009");
					}
			})
			
			$scope.sendRecv("CRM827", "inquireTDep", "com.systex.jbranch.app.server.fps.crm827.CRM827InputVO", $scope.inputVO,
				function(tota, isError){
					if(!isError){
						$scope.tdList = tota[0].body.TDepList;  //台幣定存
						$scope.ftdList = tota[0].body.FTDepList;  //外幣定存
						$scope.outputVO2 = tota[0].body.TDepList;
						$scope.outputVO3 = tota[0].body.FTDepList;
					}else{
						$scope.showErrorMsg("ehl_01_common_009");
					}
			})
		
		};
		$scope.inquireDep();
		
});