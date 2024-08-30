'use strict';
eSoafApp.controller('FPS818Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$controller('RegionController', {$scope: $scope});
		$scope.controllerName = "FPS818Controller";
		
		// combobox
		getParameter.XML(["FPS.PLANNING", "COMMON.YES_NO", "FPS.MEMO", "FPS.SPP_PLAN_STATUS", "CRM.VIP_DEGREE"], function(totas) {
			if (totas) {
				$scope.PLANNING = totas.data[totas.key.indexOf('FPS.PLANNING')];
				$scope.COM_YN = totas.data[totas.key.indexOf('COMMON.YES_NO')];
				$scope.MEMO = totas.data[totas.key.indexOf('FPS.MEMO')];
				$scope.PLAN_STATUS = totas.data[totas.key.indexOf('FPS.SPP_PLAN_STATUS')];
				$scope.VIP_DEGREE = totas.data[totas.key.indexOf('CRM.VIP_DEGREE')];
			}
		});
		$scope.PLAN_TYPE = [{'LABEL': '特定目的', 'DATA': 'SPP'}, {'LABEL': '非特定目的', 'DATA': 'INV'}];
		
		// date picker
		$scope.plan_sDateOptions = {};
		$scope.plan_eDateOptions = {};
		$scope.plan_update_sDateOptions = {};
		$scope.plan_update_eDateOptions = {};
		// config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.plan_sDateOptions.maxDate = $scope.inputVO.plan_eDate;
			$scope.plan_eDateOptions.minDate = $scope.inputVO.plan_sDate;
		};
		$scope.limitDate2 = function() {
			$scope.plan_update_sDateOptions.maxDate = $scope.inputVO.plan_update_eDate;
			$scope.plan_update_eDateOptions.minDate = $scope.inputVO.plan_update_sDate;
		};
		// date picker end
        
		$scope.init = function() {
			$scope.inputVO = {
				plan_type: 'INV',
				chkPlan: []
			};
			//組織連動
	        $scope.region = ['N', $scope.inputVO, "region_center_id", "REGION_LIST", "branch_area_id", "AREA_LIST", "branch_nbr", "BRANCH_LIST", "ao_code", "AO_LIST", "emp_id", "EMP_LIST"];
	        $scope.RegionController_setName($scope.region);
		};
		$scope.init();
		$scope.inquireInit = function() {
			$scope.queryUse = {
				SHOW_PCH: false,
				SHOW_NPCH: false,
				SHOW_RDM: false,
				SHOW_FEE: false,
				SHOW_RDM_IN: false
			};
			$scope.totalList = [];
			$scope.outputVO = {};
		}
		$scope.inquireInit();
		
		$scope.toggleSelection = function(data) {
        	var idx = $scope.inputVO.chkPlan.indexOf(data);
        	if (idx > -1) {
        		$scope.inputVO.chkPlan.splice(idx, 1);
        	} else {
        		$scope.inputVO.chkPlan.push(data);
        	}
        };
		
		$scope.inquire = function() {
			if(!$scope.inputVO.plan_type) {
				$scope.showErrorMsg('請選擇投組分類');
    			return;
			}
			$scope.sendRecv("FPS818", "inquire", "com.systex.jbranch.app.server.fps.fps818.FPS818InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.resultList.length == 0) {
							$scope.showMsg("ehl_01_common_009");
                			return;
                		}
						$scope.totalList = tota[0].body.resultList;
						$scope.outputVO = tota[0].body;
					}
			});
		};
		
		$scope.download = function() {
			if($scope.totalList.length == 0)
				return;
			$scope.sendRecv("FPS818", "download", "com.systex.jbranch.app.server.fps.fps818.FPS818InputVO", {'plan_type': $scope.inputVO.plan_type,'totalList': $scope.totalList},
				function(tota, isError) {
					if (!isError) {
						
					}
			});
		};
		
		
		
		
});