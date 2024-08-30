'use strict';
eSoafApp.controller('FPS817Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$controller('RegionController', {$scope: $scope});
		$scope.controllerName = "FPS817Controller";
		
		// combobox
		getParameter.XML(["FPS.PLANNING", "CAM.MAX_CONTACT", "CRM.VIP_DEGREE"], function(totas) {
			if (totas) {
				$scope.PLANNING = totas.data[totas.key.indexOf('FPS.PLANNING')];
				$scope.MAX_CONTACT = totas.data[totas.key.indexOf('CAM.MAX_CONTACT')];
				$scope.VIP_DEGREE = totas.data[totas.key.indexOf('CRM.VIP_DEGREE')];
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
			$scope.sDateOptions.maxDate = $scope.inputVO.eDate;
			$scope.eDateOptions.minDate = $scope.inputVO.sDate;
		};
		// date picker end
		
		$scope.pri_id = projInfoService.getPriID()[0];
		$scope.init = function() {
			$scope.inputVO = {};
			// 是否為主管
			if($scope.pri_id == '009'|| $scope.pri_id == '010' || $scope.pri_id == '011' || $scope.pri_id == '012' || $scope.pri_id == '013')
				$scope.inputVO.isManger = true;
			else
				$scope.inputVO.isManger = false;
			//組織連動
	        $scope.region = ['N', $scope.inputVO, "region_center_id", "REGION_LIST", "branch_area_id", "AREA_LIST", "branch_nbr", "BRANCH_LIST", "ao_code", "AO_LIST", "emp_id", "EMP_LIST"];
	        $scope.RegionController_setName($scope.region);
		};
		$scope.init();
		$scope.inquireInit = function() {
			$scope.totalList = [];
			$scope.outputVO = {};
		}
		$scope.inquireInit();
        
		$scope.inquire = function() {
			$scope.sendRecv("FPS817", "inquire", "com.systex.jbranch.app.server.fps.fps817.FPS817InputVO", $scope.inputVO,
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
		
		
		
});