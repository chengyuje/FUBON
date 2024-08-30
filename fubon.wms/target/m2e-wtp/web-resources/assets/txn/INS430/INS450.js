/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('INS450Controller',
	function($rootScope, $scope, $controller, $confirm, sysInfoService, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "INS450Controller";
		
		//xml參數初始化
		getParameter.XML(["FPS.INS_CURRENCY", "COMMON.YES_NO"], function(totas) {
			if (totas) {
				$scope.mappingSet['FPS.INS_CURRENCY'] = totas.data[totas.key.indexOf('FPS.INS_CURRENCY')];	//保險商品建議幣別
				$scope.mappingSet['COMMON.YES_NO'] = totas.data[totas.key.indexOf('COMMON.YES_NO')];		//共用-是/否
			}
		});
		
//		需求金額 = 每月生活所需生活費用 * 12 * (平均餘命 - 預計退休年齡)
//		已備金額 = 一次給付 + [ 每月給付 * 12 * (平均餘命 - 預計退休年齡)
//		缺口 = 已備金額 – 需求金額
	    $scope.getINS();
});
