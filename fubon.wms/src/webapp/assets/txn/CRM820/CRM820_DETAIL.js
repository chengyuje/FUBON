/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM820_DETAILController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM820_DETAILController";
		
//		$scope.activeJustified = $scope.connector("get","CRM820_TAB");
		
		$scope.pagechose = function(chose){
        	switch(chose){
	        	/* 基金 */
	        	case 1:
	        		$scope.page_title="基金";
	        		$scope.page_inde= "assets/txn/CRM821/CRM821.html";
	        		break;
	    		/* 海外ETF海外股票 */
	        	case 2:
	        		$scope.page_title="海外ETF海外股票";
	        		$scope.page_inde= "assets/txn/CRM822/CRM822.html";
	        		break;
	    		/* 海外債 */
	        	case 3:
	        		$scope.page_title="海外債";
	        		$scope.page_inde= "assets/txn/CRM823/CRM823.html";
	        		break;
        		/* 組合式商品(SI) */
	        	case 4:
	        		$scope.page_title="組合式商品(SI)";
	        		$scope.page_inde= "assets/txn/CRM824/CRM824.html";
	        		break;
        		/* 境外結構型商品(SN) */
	        	case 5:
	        		$scope.page_title="境外結構型商品(SN)";
	        		$scope.page_inde= "assets/txn/CRM825/CRM825.html";
	        		break;
        		/* 外匯雙享利(DCI) */
	        	case 6:
	        		$scope.page_title="外匯雙享利(DCI)";
	        		$scope.page_inde= "assets/txn/CRM826/CRM826.html";
	        		break;
        		/*金錢信託*/
	        	case 7:
	        		$scope.page_title="金錢信託";
	        		$scope.page_inde= "assets/txn/CRM827/CRM827.html";
	        		break;
        		/*黃金存摺*/
	        	case 8:
	        		$scope.page_title="黃金存摺";
	        		$scope.page_inde= "assets/txn/CRM828/CRM828.html";
	        		break;
	        	/*奈米投*/
	        	case 9:
	        		$scope.page_title="奈米投";
	        		$scope.page_inde= "assets/txn/CRM829/CRM829.html";
	        		break;
	        	/*海外債-金市*/
		    	case 10:
		    		$scope.page_title="海外債-金市";
		    		$scope.page_inde= "assets/txn/CRM82A/CRM82A.html";
		    		break;
	    		/*境外結構型債券-金市*/
		    	case 11:
		    		$scope.page_title="境外結構型債券-金市";
		    		$scope.page_inde= "assets/txn/CRM82B/CRM82B.html";
		    		break;
	        	}
        };
        
     
		
		if($scope.connector('get','CRM820_TAB')!=undefined){
			$scope.activeJustified = $scope.connector('get','CRM820_TAB');
			var choose = $scope.activeJustified+1;
			$scope.pagechose(choose);
		}else{
			$scope.pagechose(1);
		}
});