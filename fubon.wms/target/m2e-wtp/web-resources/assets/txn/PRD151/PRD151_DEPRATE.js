'use strict';
eSoafApp.controller('PRD151_DEPRATEController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD151_DEPRATEController";
		
		// combobox
		getParameter.XML(["PRD.FCI_CURRENCY"], function(totas) {
			if (totas) {
				$scope.FCI_CURRENCY = totas.data[totas.key.indexOf('PRD.FCI_CURRENCY')];
			}
		});
		
		// init
		$scope.init = function() {
			$scope.inputVO = {};
		};
		$scope.init();
		$scope.inquireInit = function() {
			$scope.paramList = [];
			$scope.outputVO = {};
		}
		$scope.inquireInit();
		
		// inquire
		$scope.inquire = function() {
			$scope.sendRecv("PRD151", "inquireDepRate", "com.systex.jbranch.app.server.fps.prd151.PRD151InputVO", {},
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		} else {
	                			debugger
	                			$scope.paramList = tota[0].body.resultList;
	                			$scope.outputVO = tota[0].body;
	                		}
						}
			});
		};
		$scope.inquire();
});