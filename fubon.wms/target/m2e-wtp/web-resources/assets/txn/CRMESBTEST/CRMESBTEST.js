/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRMESBTESTController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRMESBTESTController";

	    //export
	    $scope.esbtest = function() {
			$scope.sendRecv("CRMESBTEST", "esbtest", "com.systex.jbranch.app.server.fps.crmesbtest.CRMESBTESTInputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						return;
					}
				}
		)};
	    
		//test
		$scope.crmtest = function() {
			$scope.sendRecv("CRMESBTEST", "crmtest", "com.systex.jbranch.app.server.fps.crmesbtest.CRMESBTESTInputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						return;
					}
				}
		)};
	    
});