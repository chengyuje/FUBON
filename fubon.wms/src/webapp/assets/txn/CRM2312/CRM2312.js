/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM2312Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService , getParameter, crmService) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "CRM2312Controller";
	
	crmService.getForbiddenList();
	
	// combobox
	getParameter.XML(["PRD.FUND_NEW_LEVEL_LIST"], function(totas) {
		if (totas) {
			$scope.mappingSet['PRD.FUND_NEW_LEVEL_LIST'] = totas.data[totas.key.indexOf('PRD.FUND_NEW_LEVEL_LIST')];
		}
	});
	
	$scope.init = function(){
		$scope.inputVO = {
				cust_id : undefined,
				companyName : undefined
		};
	};
	$scope.init();
	
	$scope.inquire_init = function(){
		$scope.outputVO = [];
		$scope.resultList = [];
	}
	
	$scope.inquire = function(){
		if(!$scope.inputVO.cust_ID) {
			$scope.showErrorMsg("客戶ID為必填");
			return;
		}
		debugger;
		if(crmService.checkCustId($rootScope.forbiddenData,$scope.inputVO.cust_ID)) {
			$scope.showErrorMsg("ehl_01_CRM_002");
			return;
		}
		debugger;
		$scope.sendRecv("CRM2312", "inquire", "com.systex.jbranch.app.server.fps.crm2312.CRM2312InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.resultList.length == 0) {
							$scope.showMsg("ehl_01_common_009");
							$scope.resultList=[];
							return;
                		}
						$scope.resultList = tota[0].body.resultList;
						$scope.outputVO = tota[0].body;
						return;
					}						
		});
	}

});