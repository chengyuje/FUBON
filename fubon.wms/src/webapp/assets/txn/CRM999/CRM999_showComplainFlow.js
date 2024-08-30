/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM999showController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM999showController";

		
		/*--------------------------------------------------------------------------------------------
		 * 初始化
		 * --------------------------------------------------------------------------------------------
		 */
		$scope.inputVO = {};
		$scope.inputVO.complainListId = $scope.row.COMPLAIN_LIST_ID;  //row 為 導入之物件
		
		getParameter.XML('CRM.COMPLAIN_FLOWDTL_STATUS', function(totas) {
			if (totas) {
				$scope.COMPLAIN_FLOWDTL_STATUS = totas.data[totas.key.indexOf('CRM.COMPLAIN_FLOWDTL_STATUS')];
			}
		});
		
		$scope.query = function() {
			$scope.sendRecv("CRM999", "showComplainFlow", 
					"com.systex.jbranch.app.server.fps.crm999.CRM999InputVO", 
					$scope.inputVO, function(tota, isError) {
				if (!isError) {
					var result = angular.copy(tota[0].body.resultList1);
					$scope.outputVO = tota[0].body;
					if (result && result.length) {
						$scope.paramList = result;
					} else {
						//查無資料
						$scope.showMsg('ehl_01_common_009');
					}
				}
			});
		}
		$scope.query();
	    
});