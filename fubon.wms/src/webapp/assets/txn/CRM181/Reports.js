/**
 * 
 */
'use strict';
eSoafApp.controller('ReportController', 
    function($rootScope,$scope,$controller, $confirm, sysInfoService,socketService, ngDialog, projInfoService, $sce) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "ReportController";     
        // init
		$scope.init = function() {
			$scope.inputVO = {
				rptProgUrl : $scope.row.RPT_PROG_URL,
				rptName : $scope.row.RPT_NAME
			};
		};
		$scope.init();
		
//		$scope.openReport = function() {
//			var getUrl = "https://" + $scope.inputVO.rptProgUrl;
//			$scope.url = $sce.trustAsResourceUrl(getUrl);
//		};
//		$scope.openReport();
	});