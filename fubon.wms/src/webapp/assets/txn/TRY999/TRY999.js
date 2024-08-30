'use strict';
eSoafApp.controller('TRY999Controller',
		function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter, sysInfoService , getParameter) {
			$controller('BaseController', {$scope: $scope});
			$scope.controllerName = "TRY999Controller";
	
	// 初始化在這邊
	$scope.init = function() {
	    
		$scope.paramList = [{
			'firstname' : 'randy',
			'lastname' : 'fox',
		    'sex' : 'm'
		},{
			'firstname' : 'jim',
			'lastname' : 'johnson',
		    'sex' : 'f'
		}];
		$scope.resultList = [];
		$scope.outputVO = {};
		$scope.id = 'F123456789';
		$scope.number = -1000;
	}
	$scope.init();
	
	
	// 三種匯出在這邊
	$scope.exportPDF = function() {
	
	}
	
	$scope.exportCSV = function() {
		
	}
	
	$scope.exportXLS = function() {
		
	}

});