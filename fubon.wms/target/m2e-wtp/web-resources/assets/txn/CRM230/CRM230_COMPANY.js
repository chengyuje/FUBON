/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM230_COMPANYController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM230_COMPANYController";
		
		//初始
		$scope.com_list = [];
		$scope.outputVO = []
//		$scope.mappingSet['TYPE'] = [];
//		$scope.mappingSet['TYPE'].push({LABEL: "薪轉公司名稱", DATA: "1"},{LABEL: "薪轉公司代號", DATA: "2"});
		$scope.inputVO.companyTYPE = '1';
		$scope.inputVO.keyWord ='';
		
		//查詢
		$scope.query = function(){
	    	$scope.sendRecv("CRM230", "query_company", "com.systex.jbranch.app.server.fps.crm230.CRM230InputVO", $scope.inputVO,
	    		function(tota, isError) {
					if (!isError) {	
						if(tota[0].body.resultList.length == 0){
							$scope.showMsg("ehl_01_common_009");
						}else{
							$scope.com_list = tota[0].body.resultList;	
							$scope.outputVO = tota[0].body;
						}
					}
			});
		}
		
		//取得
		$scope.get = function(row){
			$scope.closeThisDialog(row);
		}
});
