/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS302_CustController', function($rootScope, $scope, $controller) {
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "PMS302_CustController";

	$scope.sendRecv("PMS302", "queryCustDetail", "com.systex.jbranch.app.server.fps.pms302.PMS302InputdetailVO", $scope.inputVO,
		function(tota, isError) {
			if (!isError) {
				if (!tota[0].body.resultList.length) {
					$scope.showMsg("ehl_01_common_009");
					return;
				}
				$scope.totalList = tota[0].body.resultList;
				$scope.outputVO = tota[0].body;
			}
		}
	);
	$scope.mappingSet['itemType'] = [];
	$scope.mappingSet['itemType'].push(
			{LABEL : '基金',DATA : '01'},
			{LABEL : 'SI',DATA : '02'},
			{LABEL : 'SN',DATA : '03'},
			{LABEL : 'DCI',DATA : '04'},
			{LABEL : '海外債',DATA : '05'},
			{LABEL : '海外股票',DATA : '17'},
			{LABEL : '海外ETF',DATA : '06'},
			{LABEL : '躉繳',DATA : '10'},
			{LABEL : '短年期',DATA : '11'},
			{LABEL : '長年期',DATA : '12'},
			{LABEL : '投資型',DATA : '16'}
	);
	
	
	
	
});