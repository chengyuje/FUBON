/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('FPSProdETFDialogController', function($rootScope, $confirm, $scope,$filter, $controller, socketService, ngDialog, projInfoService,sysInfoService,getParameter,$timeout,$http) {
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "FPSProdETFDialogController";
	
	$scope.url = 'https://www.fubon.com/banking/personal/fund_trust/ETF_search/ETF_search.htm?show=m5&Pram=$ETFWEB$HTML$ET011001]DJHTM#ETFID}' + $scope.url;
									
});
