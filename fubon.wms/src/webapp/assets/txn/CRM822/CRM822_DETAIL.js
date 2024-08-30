/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM822_DETAILController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM822_DETAILController";
		
		$scope.mappingSet['ProductType'] = [];
		$scope.mappingSet['ProductType'].push({LABEL: '海外ETF', DATA: 'E'},{LABEL: '海外股票', DATA: 'S'});
        
		$scope.mappingSet['State'] = [];
		$scope.mappingSet['State'].push({LABEL: '持有中', DATA: '1'});
		
		$scope.mappingSet['TrustBusinessType'] = [];
		$scope.mappingSet['TrustBusinessType'].push({LABEL: '外幣', DATA: 'Y'},{LABEL: '台幣', DATA: 'N'});
		//最新幣值查詢
		$scope.sendRecv("CRM821", "inquire_Currency", "com.systex.jbranch.app.server.fps.crm821.CRM821InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.Currency = tota[0].body.resultList;
						$scope.cod = 1;
						//台幣不轉換
						if($scope.row.CurCode != 'TWD'){
							//幣值轉換
							for(var j = 0; j < $scope.Currency.length; j++) {
								if($scope.row.CurCode == $scope.Currency[j].CUR_COD){
									$scope.cod = $scope.Currency[j].BUY_RATE;
								}
							}
						}
					}
			
		});
});
