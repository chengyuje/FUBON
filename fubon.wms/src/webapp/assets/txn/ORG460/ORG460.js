/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('ORG460Controller', function($scope, $controller, $filter, socketService, ngDialog, projInfoService) {
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "ORG460Controller";
	
	//組織連動繼承
	$controller('RegionController', {$scope: $scope});
	
	$scope.mappingSet['aoRankLst'] = [];
	
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
		
		$scope.getAoRankLst();
		
		//組織連動
        $scope.region = ['N', $scope.inputVO, "region_center_id", "REGION_LIST", "branch_area_id", "AREA_LIST", "branch_nbr", "BRANCH_LIST", "ao_code", "AO_LIST", "emp_id", "EMP_LIST"];
        $scope.RegionController_setName($scope.region);
	};
	
	$scope.model = {};
	$scope.open = function($event, elementOpened) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.model[elementOpened] = !$scope.model[elementOpened];
	};
	
	$scope.query = function() {
		$scope.sendRecv("ORG460", "query", "com.systex.jbranch.app.server.fps.org460.ORG460InputVO", $scope.inputVO, function(tota, isError) {
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
		
		var vo = {'param_type': 'ORG.BRANCH_DEGREE', 'desc': false};
		if(!projInfoService.mappingSet['ORG.BRANCH_DEGREE']) {
        	$scope.requestComboBox(vo, function(totas) {
        		if (totas[totas.length - 1].body.result === 'success') {
        			projInfoService.mappingSet['ORG.BRANCH_DEGREE'] = totas[0].body.result;
        			$scope.mappingSet['ORG.BRANCH_DEGREE'] = projInfoService.mappingSet['ORG.BRANCH_DEGREE'];
        		}
        	});
        } else {
        	$scope.mappingSet['ORG.BRANCH_DEGREE'] = projInfoService.mappingSet['ORG.BRANCH_DEGREE'];
        }
		
		vo = {'param_type': 'ORG.BRANCH_GROUP', 'desc': false};
		if(!projInfoService.mappingSet['ORG.BRANCH_GROUP']) {
        	$scope.requestComboBox(vo, function(totas) {
        		if (totas[totas.length - 1].body.result === 'success') {
        			projInfoService.mappingSet['ORG.BRANCH_GROUP'] = totas[0].body.result;
        			$scope.mappingSet['ORG.BRANCH_GROUP'] = projInfoService.mappingSet['ORG.BRANCH_GROUP'];
        		}
        	});
        } else {
        	$scope.mappingSet['ORG.BRANCH_GROUP'] = projInfoService.mappingSet['ORG.BRANCH_GROUP'];
        }
	};
	
	$scope.export = function() {
		angular.copy($scope.aoGoalLst, $scope.inputVO.EXPORT_LST);
		$scope.inputVO.EXPORT_LST.push($scope.totals);
		
		console.log(JSON.stringify($scope.inputVO.EXPORT_LST));
		
		$scope.sendRecv("ORG460", "export", "com.systex.jbranch.app.server.fps.org460.ORG460InputVO", $scope.inputVO, 
				function(tota, isError) {
					if (isError) {
						$scope.showErrorMsgInDialog(tota.body.msgData);
						return;
					}
					if (tota.length > 0) {}
				}
		);
	};
	
	$scope.exportForHis = function() {
		angular.copy($scope.aoHistLst, $scope.inputVO.EXPORT_LST);
		$scope.inputVO.EXPORT_LST.push($scope.totals);
		
		console.log(JSON.stringify($scope.inputVO.EXPORT_LST));
		
		$scope.sendRecv("ORG460", "exportForHis", "com.systex.jbranch.app.server.fps.org460.ORG460InputVO", $scope.inputVO, 
				function(tota, isError) {
					if (isError) {
						$scope.showErrorMsgInDialog(tota.body.msgData);
						return;
					}
					if (tota.length > 0) {}
				}
		);
	};
	
	$scope.getAoRankLst = function() {
		$scope.sendRecv("ORG460", "getAoRankLst", "com.systex.jbranch.app.server.fps.org460.ORG460InputVO", $scope.inputVO, function(totas, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(totas.body.msgData);
				return;
			}
			if (totas.length > 0) {
				$scope.mappingSet['aoRankLst'] = [];
				var aoRankLst = totas[0].body.aoRankLst;
				console.log('aoRankLst: ' + JSON.stringify(aoRankLst));
				for (var i = 0; i < aoRankLst.length; i++) {
					$scope.mappingSet['aoRankLst'].push({LABEL: aoRankLst[i].ROLE_NAME, DATA: aoRankLst[i].ROLE_NAME});
				}
			}
		});
	};
	
	$scope.init();
});
