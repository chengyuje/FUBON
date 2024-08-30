/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD211Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD211Controller";
		
		// init
		$scope.init = function() {
			$scope.inputVO = {};
		};
		$scope.init();
		
		$scope.uploadINV = function() {
			var dialog = ngDialog.open({
				template: 'assets/txn/PRD211/PRD211_INV_UPLOAD.html',
				className: 'PRD211_INV_UPLOAD',
				showClose: false,
                controller: ['$scope', function($scope) {
                	
                }]
			});
		};
		
		$scope.uploadREST = function() {
			var dialog = ngDialog.open({
				template: 'assets/txn/PRD211/PRD211_REST_UPLOAD.html',
				className: 'PRD211_REST_UPLOAD',
				showClose: false,
                controller: ['$scope', function($scope) {
                	
                }]
			});
		};		

		$scope.uploadDIVIDEND = function() {
			var dialog = ngDialog.open({
				template: 'assets/txn/PRD211/PRD211_DIVIDEND_UPLOAD.html',
				className: 'PRD211_DIVIDEND_UPLOAD',
				showClose: false,
                controller: ['$scope', function($scope) {
                	
                }]
			});
		};		
		
		$scope.uploadMATURITY = function() {
			var dialog = ngDialog.open({
				template: 'assets/txn/PRD211/PRD211_MATURITY_UPLOAD.html',
				className: 'PRD211_MATURITY_UPLOAD',
				showClose: false,
                controller: ['$scope', function($scope) {
                	
                }]
			});
		};				
		
		$scope.downloadSample_inv = function(){
        	$scope.sendRecv("PRD280", "downloadSample_inv", "com.systex.jbranch.app.server.fps.prd280.PRD280InputVO", {},
					function(tota, isError) {
						if (!isError) {
							return;
						}
			});
		};
		
		$scope.downloadSample_rest = function(){
        	$scope.sendRecv("PRD280", "downloadSample_rest", "com.systex.jbranch.app.server.fps.prd280.PRD280InputVO", {},
					function(tota, isError) {
						if (!isError) {
							return;
						}
			});
		};
		
		$scope.downloadSample_dividend = function(){
        	$scope.sendRecv("PRD280", "downloadSample_dividend", "com.systex.jbranch.app.server.fps.prd280.PRD280InputVO", {},
					function(tota, isError) {
						if (!isError) {
							return;
						}
			});
		};
		
		$scope.downloadSample_maturity = function(){
        	$scope.sendRecv("PRD280", "downloadSample_maturity", "com.systex.jbranch.app.server.fps.prd280.PRD280InputVO", {},
					function(tota, isError) {
						if (!isError) {
							return;
						}
			});
		};
		
});