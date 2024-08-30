'use strict';
eSoafApp.controller('ORG461Controller', function($scope, $controller, $filter, socketService, ngDialog, projInfoService, getParameter) {
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "ORG461Controller";
	
	// filter
	getParameter.XML(["ORG.UHRM_AO_JOB_RANK"], function(totas) {
		if (totas) {
			$scope.mappingSet['ORG.UHRM_AO_JOB_RANK'] = totas.data[totas.key.indexOf('ORG.UHRM_AO_JOB_RANK')];
		}
	});
	//===
	
	$scope.init = function() {
		$scope.inputVO.AO_JOB_RANK   = '';
		$scope.inputVO.PERF_EFF_DATE = '';
		$scope.data = [];
		$scope.aoGoalLst = [];
		$scope.aoHistLst = [];

		$scope.inputVO = {
			AO_JOB_RANK:   '',
			PERF_EFF_DATE: '', 
			EXPORT_LST: [],
			region_center_id: '',
			branch_area_id: '',
			branch_nbr: '', 
		};
	};
	
	$scope.model = {};
	$scope.open = function($event, elementOpened) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.model[elementOpened] = !$scope.model[elementOpened];
	};
	
	$scope.query = function() {
		$scope.sendRecv("ORG461", "query", "com.systex.jbranch.app.server.fps.org461.ORG461InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(tota.body.msgData);
				return;
			}
			if (tota.length > 0) {
				$scope.aoGoalLst = tota[0].body.aoGoalLst;
				$scope.aoHistLst = tota[0].body.aoHistLst;
				$scope.outputVO = tota[0].body;
			}
		});
	};
	
	$scope.export = function() {
		angular.copy($scope.aoGoalLst, $scope.inputVO.EXPORT_LST);
		$scope.inputVO.EXPORT_LST.push($scope.totals);
		
		
		$scope.sendRecv("ORG461", "export", "com.systex.jbranch.app.server.fps.org461.ORG461InputVO", $scope.inputVO, 
				function(tota, isError) {
					if (isError) {
						$scope.showErrorMsgInDialog(tota.body.msgData);
						return;
					}
					if (tota.length > 0) {}
				}
		);
	};
	
	$scope.init();
});
