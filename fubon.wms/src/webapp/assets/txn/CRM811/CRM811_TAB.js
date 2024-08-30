/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM811_TABController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM811_TABController";
		
		$scope.mappingSet['branchName'] = [];	
		$scope.sendRecv("CRM811", "getBranchName", "com.systex.jbranch.app.server.fps.crm811.CRM811InputVO", {},
				function(tota, isError) {
					angular.forEach(tota[0].body.resultList, function(row){
						$scope.mappingSet['branchName'].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
					});
//					alert(JSON.stringify($scope.mappingSet['branchName']));
		});
		
		$scope.pagechose = function(chose){
        	switch(chose){
	        	/* 台外幣活存 */
	        	case 1:
	        		$scope.page_title="台外幣活存";
	        		$scope.page_inde= "assets/txn/CRM811/CRM811.html";
	        		break;
	    		/* 台幣支存 */
	        	case 2:
	        		$scope.page_title="台幣支存";
	        		$scope.page_inde= "assets/txn/CRM812/CRM812.html";
	        		break;
	    		/* 台外幣定存 */
	        	case 3:
	        		$scope.page_title="台外幣定存";
	        		$scope.page_inde= "assets/txn/CRM813/CRM813.html";
	        		break;
        		/* 交易明細查詢 */
	        	case 4:
	        		$scope.page_title="交易明細查詢";
	        		$scope.page_inde= "assets/txn/CRM814/CRM814.html";
	        		break;
	        	}
        };

		if($scope.connector('get','CRM811_TAB')!=undefined){
			$scope.activeJustified = $scope.connector('get','CRM811_TAB');
			var choose = $scope.activeJustified+1;
			$scope.pagechose(choose);
		}else{
			$scope.pagechose(1);
		}
});
		