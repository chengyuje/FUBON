/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('FPS500Controller',
	function($rootScope, $scope, $controller, $confirm,$filter, socketService, ngDialog, projInfoService, getParameter, $q) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "FPS500Controller";
		
		$scope.sendRecv("PRD500", "inquire", "com.systex.jbranch.app.server.fps.prd500.PRD500InputVO", {},
			function(totas, isError) {
            	if (!isError) {
            		$scope.link_url = totas[0].body.link_url;
					$scope.link_url2 = totas[0].body.link_url2;
					$scope.link_url3 = totas[0].body.link_url3;
            	};
			}
		);
		
		$scope.windowOpenFubonSys = function(url) {
			window.open(url, '_blank', 'top=5,width=850,height=700');
		}		
});