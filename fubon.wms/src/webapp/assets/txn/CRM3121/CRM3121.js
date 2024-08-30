/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM3121Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM3121Controller";	
		// 繼承這個
		$controller('RegionController', {$scope: $scope});

		// init
		$scope.init = function() {
			$scope.inputVO = {};
			$scope.test = ['N', $scope.inputVO, "region_center_id", "REGION_LIST", "branch_area_id", "AREA_LIST", "branch_nbr", "BRANCH_LIST", "new_ao_code", "AO_LIST", "emp_id", "EMP_LIST"];
	        $scope.RegionController_setName($scope.test);
		};
		$scope.init();
		$scope.inquireInit = function(){
			$scope.resultList = [];
		}
		$scope.inquireInit();
		
		// date picker
		$scope.limit_s_dateOptions = {};
		$scope.limit_e_dateOptions = {};
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.limit_s_dateOptions.maxDate = $scope.inputVO.limit_e_date;
			$scope.limit_e_dateOptions.minDate = $scope.inputVO.limit_s_date;
		};
		
		$scope.inquire = function(){
			$scope.sendRecv("CRM3121", "inquire", "com.systex.jbranch.app.server.fps.crm3121.CRM3121InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.resultList.length == 0) {
							$scope.showMsg("ehl_01_common_009");
							return;
						}
						$scope.resultList = tota[0].body.resultList;
						$scope.outputVO = tota[0].body;
						return;
					}
				}
		)};
		
		$scope.detail = function (row) {
			var dialog = ngDialog.open({
				template: 'assets/txn/CRM3121/CRM3121_DETAIL.html',
				className: 'CRM3121_DETAIL',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.row = row;
                }]
			});
		};
		
	    
});