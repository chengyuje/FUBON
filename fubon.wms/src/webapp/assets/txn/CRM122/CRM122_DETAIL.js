/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM122_DETAILController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter , getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM122_DETAILController";
		
		 getParameter.XML(['PMS.SALE_PLAN_PRD_TYPE' ], function(totas) {
				if(len(totas)>0) {
					$scope.mappingSet['PMS.SALE_PLAN_PRD_TYPE'] = totas.data[totas.key.indexOf('PMS.SALE_PLAN_PRD_TYPE')];
				}
		});

		 $scope.init = function () {
			if($scope.row)
				$scope.isUpdate = true;
			$scope.inputVO={
				CUST_ID :'',
				CUST_NAME:'',
				EST_PRD  :'',
				EST_AMT  :'',
				EST_EARNINGS:'',
				MEETING_DATE_S:undefined,
				MEETING_DATE_E:undefined,
				ACTION_DATE :undefined,
				CLOSE_DATE  :undefined
			}
			if($scope.isUpdate) {
				$scope.inputVO.CUST_ID = $scope.row.CUST_ID;
				$scope.inputVO.CUST_NAME = $scope.row.CUST_NAME;
				$scope.inputVO.EST_PRD = $scope.row.EST_PRD;
				$scope.inputVO.EST_AMT = $scope.row.EST_AMT;
				$scope.inputVO.EST_EARNINGS = $scope.row.EST_EARNINGS;
				$scope.inputVO.MEETING_DATE_S = $scope.toJsDate($scope.row.MEETING_DATE_S);
				$scope.inputVO.MEETING_DATE_E = $scope.toJsDate($scope.row.MEETING_DATE_E);
				$scope.inputVO.ACTION_DATE = $scope.toJsDate($scope.row.ACTION_DATE);
				$scope.inputVO.CLOSE_DATE = $scope.toJsDate($scope.row.CLOSE_DATE);
			
			}
		};
		$scope.init();
		
});