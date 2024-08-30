'use strict';
eSoafApp.controller('SQM310_2Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SQM310_2Controller";
		
		// combobox
		getParameter.XML(["CAM.LEAD_TYPE", "CAM.LEAD_SOURCE"], function(totas) {
			if (totas) {
				$scope.LEAD_TYPE = totas.data[totas.key.indexOf('CAM.LEAD_TYPE')];
				$scope.LEAD_SOURCE = totas.data[totas.key.indexOf('CAM.LEAD_SOURCE')];
			}
		});
		
		// date picker
		$scope.sDateOptions = {};
		$scope.eDateOptions = {};
		// config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.sDateOptions.maxDate = $scope.queryVO.eDate
			$scope.eDateOptions.minDate = $scope.queryVO.sDate;
		};
		
		$scope.init = function() {
			$scope.resultList = [];
			$scope.inputVO = {totalList: []};
			$scope.queryVO = {closed: 'N'};
			$scope.limitDate();
			$scope.checkVO = {
				clickAll: false,
				clickAll2: false
			};
			$scope.sendRecv("SQM310", "init_sc_par", "com.systex.jbranch.app.server.fps.sqm310.SQM310InputVO", {},
				function(tota, isError) {
					if (!isError) {
						$scope.inputVO.totalList = tota[0].body.resultList;
					}
			});
		};
		$scope.init();
		
		$scope.inquire = function() {
			$scope.resultList = [];
			$scope.sendRecv("SQM310", "inquire_sc", "com.systex.jbranch.app.server.fps.sqm310.SQM310InputVO", $scope.queryVO,
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.resultList.length == 0) {
							$scope.showMsg("ehl_01_common_009");
                			return;
                		}
						$scope.resultList = tota[0].body.resultList;
						$scope.resultList = _.filter($scope.resultList, function(o) {
							return !_.some($scope.inputVO.totalList.map(function(e) { return {'CAMPAIGN_ID': e.CAMPAIGN_ID, 'STEP_ID': e.STEP_ID}; }), {'CAMPAIGN_ID': o.CAMPAIGN_ID, 'STEP_ID': o.STEP_ID});
						});
					}
			});
		};
		
		$scope.checkrow = function() {
        	if ($scope.checkVO.clickAll) {
        		angular.forEach($scope.resultList, function(row) {
        			row.SELECTED = true;
    			});
        	} else {
        		angular.forEach($scope.resultList, function(row) {
        			row.SELECTED = false;
    			});
        	}
        };
        $scope.checkrow2 = function() {
        	if ($scope.checkVO.clickAll2) {
        		angular.forEach($scope.inputVO.totalList, function(row) {
        			row.SELECTED = true;
    			});
        	} else {
        		angular.forEach($scope.inputVO.totalList, function(row) {
        			row.SELECTED = false;
    			});
        	}
        };
        
        $scope.btnAddFun = function() {
        	for (var i = $scope.resultList.length - 1; i >= 0; i--) {
        		if ($scope.resultList[i].SELECTED) {
        			$scope.resultList[i].SELECTED = false;
        			$scope.inputVO.totalList.push($scope.resultList[i]);
        			$scope.resultList.splice(i, 1);
        		}
        	}
        	$scope.checkVO.clickAll = false;
        	$scope.checkVO.clickAll2 = false;
        	$scope.inputVO.totalList = _.uniqWith($scope.inputVO.totalList, function(oldOBJ, newOBJ) {
        		return (oldOBJ.CAMPAIGN_ID == newOBJ.CAMPAIGN_ID && oldOBJ.STEP_ID == newOBJ.STEP_ID);
        	});
        	$scope.inputVO.totalList = _.orderBy($scope.inputVO.totalList, ['LEAD_TYPE', 'LEAD_SOURCE_ID']);
        };
        
        $scope.btnDelFun = function() {
        	for (var i = $scope.inputVO.totalList.length - 1; i >= 0; i--) {
        		if ($scope.inputVO.totalList[i].SELECTED) {
        			$scope.inputVO.totalList[i].SELECTED = false;
        			$scope.resultList.push($scope.inputVO.totalList[i]);
        			$scope.inputVO.totalList.splice(i, 1);
        		}
        	}
        	$scope.checkVO.clickAll = false;
        	$scope.checkVO.clickAll2 = false;
        	$scope.resultList = _.uniqWith($scope.resultList, function(oldOBJ, newOBJ) {
        		return (oldOBJ.CAMPAIGN_ID == newOBJ.CAMPAIGN_ID && oldOBJ.STEP_ID == newOBJ.STEP_ID);
        	});
        	$scope.resultList = _.orderBy($scope.resultList, ['LEAD_TYPE', 'LEAD_SOURCE_ID']);
        };
        
        $scope.save = function() {
			var check = false;
			angular.forEach($scope.inputVO.totalList, function(row) {
    			if(!row.SC_CNT || row.SC_CNT == 0)
    				check = true;
			});
			if(check) {
				$scope.showErrorMsg('查核戶數必須大於0');
        		return;
			}
			$scope.sendRecv("SQM310", "save_sc", "com.systex.jbranch.app.server.fps.sqm310.SQM310InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.showSuccessMsg('ehl_01_common_004');
						$scope.init();
					}
			});
		};
		
		
		
});