/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM321_addController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM321_addController";
		
		$scope.region_list = projInfoService.getAvailRegion();
		$scope.area_list = projInfoService.getAvailArea();
		$scope.bra_list = _.sortBy(projInfoService.getAvailBranch(), ['BRANCH_NBR']);
		
		//初始化
		$scope.init = function(){
			
			if($scope.row == undefined){
				$scope.inputVO = {
						regionCenterId: '',
						branchAreaId: '',
						branchNbr: '',
						
						fch_regionCenterId: '',
						fch_branchAreaId: '',
						fch_branchNbr: '',
						
						priority_order: ''
				};				
			}else{
				$scope.inputVO = {
						regionCenterId: '',
						branchAreaId: '',
						branchNbr: $scope.row.ASS_BRH,
						
						fch_regionCenterId: '',
						fch_branchAreaId: '',
						fch_branchNbr: $scope.row.FCH_MAST_BRH,
						
						priority_order: $scope.row.PRIORITY_ORDER,
						
						oriass_brh: $scope.row.ASS_BRH,
						orifch_mast_brh: $scope.row.FCH_MAST_BRH,
						oripriority_order: $scope.row.PRIORITY_ORDER
				};
			}
			$scope.getFchRegionCenterId();
			$scope.getFchBra_areaID();
			$scope.getFchBranchNbr();
			
			$scope.getRegionCenterId();
			$scope.getBra_areaID();
			$scope.getBranchNbr();
		};
		
		//get業務處
		$scope.mappingSet['region_center_id'] = [];
		angular.forEach(projInfoService.getAvailRegion(), function(row, index, objs){
			$scope.mappingSet['region_center_id'].push({LABEL: row.REGION_CENTER_NAME, DATA: row.REGION_CENTER_ID});
		});
		
     	/*取的FCH駐點行資料*/
		//get區域all
		$scope.getFchRegionCenterId = function() {
			if ($scope.inputVO.fch_regionCenterId == '' && $scope.inputVO.fch_branchAreaId != '')
				$scope.inputVO.fch_regionCenterId = $filter('filter')($scope.area_list, {BRANCH_AREA_ID: $scope.inputVO.fch_branchAreaId})[0].REGION_CENTER_ID;
		}
		
		//get營運區
		$scope.getFchBra_areaID = function() {

			$scope.mappingSet['fch_branch_area_id'] = [];
			
			if ($scope.inputVO.fch_regionCenterId == '' ) {
				angular.forEach($scope.area_list, function(row, index, objs){
					$scope.mappingSet['fch_branch_area_id'].push({LABEL: row.BRANCH_AREA_NAME, DATA: row.BRANCH_AREA_ID});
				});
				if($scope.inputVO.fch_branchAreaId != '' && $scope.inputVO.fch_branchNbr != ''){
					$scope.inputVO.fch_branchAreaId = '';
					$scope.inputVO.fch_branchNbr = '';
				}
				if ($scope.inputVO.fch_branchNbr != '' && $scope.inputVO.fch_branchAreaId == ''){
					$scope.inputVO.fch_branchAreaId = $filter('filter')($scope.bra_list, {BRANCH_NBR: $scope.inputVO.fch_branchNbr})[0].BRANCH_AREA_ID;
					$scope.inputVO.fch_regionCenterId = $filter('filter')($scope.area_list, {BRANCH_AREA_ID: $scope.inputVO.fch_branchAreaId})[0].REGION_CENTER_ID;					
				}
			} else {
				if ($scope.inputVO.fch_branchAreaId != '') {
					if ($filter('filter')($scope.area_list, {BRANCH_AREA_ID: $scope.inputVO.fch_branchAreaId})[0].REGION_CENTER_ID != $scope.inputVO.fch_regionCenterId){
						$scope.inputVO.fch_branchAreaId = '';						
					}						
					angular.forEach($scope.area_list, function(row, index, objs){
						if(row.REGION_CENTER_ID == $scope.inputVO.fch_regionCenterId) {
							$scope.mappingSet['fch_branch_area_id'].push({LABEL: row.BRANCH_AREA_NAME, DATA: row.BRANCH_AREA_ID});
						}
					});
				} else {
					angular.forEach($scope.area_list, function(row, index, objs){
						if(row.REGION_CENTER_ID == $scope.inputVO.fch_regionCenterId) {
							$scope.mappingSet['fch_branch_area_id'].push({LABEL: row.BRANCH_AREA_NAME, DATA: row.BRANCH_AREA_ID});
						}
					});
					if ($scope.inputVO.fch_branchNbr != ''){
						$scope.inputVO.fch_branchAreaId = $filter('filter')($scope.bra_list, {BRANCH_NBR: $scope.inputVO.fch_branchNbr})[0].BRANCH_AREA_ID;	
					}
				}
			}
		};
		
		//get分行別
		$scope.getFchBranchNbr = function() {
			$scope.mappingSet['fch_branch_nbr'] = [];			
			if ($scope.inputVO.fch_branchAreaId != '') {
				if ($filter('filter')($scope.bra_list, {BRANCH_NBR: $scope.inputVO.fch_branchNbr})[0].BRANCH_AREA_ID == $scope.inputVO.fch_branchAreaId) {
					angular.forEach($scope.bra_list, function(row, index, objs){
						if(row.BRANCH_AREA_ID == $scope.inputVO.fch_branchAreaId) {
							$scope.mappingSet['fch_branch_nbr'].push({LABEL: row.BRANCH_NBR + '-' + row.BRANCH_NAME, DATA: row.BRANCH_NBR});
						}
					});
				} else {					
					angular.forEach($scope.bra_list, function(row, index, objs){
						if(row.BRANCH_AREA_ID == $scope.inputVO.fch_branchAreaId) {
							$scope.mappingSet['fch_branch_nbr'].push({LABEL: row.BRANCH_NBR + '-' + row.BRANCH_NAME, DATA: row.BRANCH_NBR});
						}
					});
				}
			} else if ($scope.inputVO.fch_regionCenterId != '') {
				$scope.inputVO.fch_branchNbr = '';
				var temp_fch_branch_area_id_list = [];
				
				angular.forEach($scope.area_list, function(row, index, objs){
					if(row.REGION_CENTER_ID == $scope.inputVO.fch_regionCenterId) {
						temp_fch_branch_area_id_list.push({LABEL: row.BRANCH_AREA_NAME, DATA: row.BRANCH_AREA_ID});
					}
				});
				
				angular.forEach($scope.bra_list, function(row, index, objs){
					for(var i = 0; i < temp_fch_branch_area_id_list.length; i++) {
						if(row.BRANCH_AREA_ID == temp_fch_branch_area_id_list[i].DATA) {
							$scope.mappingSet['fch_branch_nbr'].push({LABEL: row.BRANCH_NBR + '-' + row.BRANCH_NAME, DATA: row.BRANCH_NBR});
						}
					}
				});			
			} else {
				angular.forEach($scope.bra_list, function(row, index, objs){
					$scope.mappingSet['fch_branch_nbr'].push({LABEL: row.BRANCH_NBR + '-' + row.BRANCH_NAME, DATA: row.BRANCH_NBR});
				});
			}
		}
		
		/*get分行別*/		
		//get區域all
		$scope.getRegionCenterId = function() {	
			if ($scope.inputVO.regionCenterId == '' && $scope.inputVO.branchAreaId != '')
				$scope.inputVO.regionCenterId = $filter('filter')($scope.area_list, {BRANCH_AREA_ID: $scope.inputVO.branchAreaId})[0].REGION_CENTER_ID;
		}
		
		//get營運區
		$scope.getBra_areaID = function() {

			$scope.mappingSet['branch_area_id'] = [];
			
			if ($scope.inputVO.regionCenterId == '' ) {
				angular.forEach($scope.area_list, function(row, index, objs){
					$scope.mappingSet['branch_area_id'].push({LABEL: row.BRANCH_AREA_NAME, DATA: row.BRANCH_AREA_ID});
				});
				if($scope.inputVO.branchAreaId != '' && $scope.inputVO.branchNbr != ''){
					$scope.inputVO.branchAreaId = '';
					$scope.inputVO.branchNbr = '';
				}
				if ($scope.inputVO.branchNbr != '' && $scope.inputVO.branchAreaId == ''){
					$scope.inputVO.branchAreaId = $filter('filter')($scope.bra_list, {BRANCH_NBR: $scope.inputVO.branchNbr})[0].BRANCH_AREA_ID;
					$scope.inputVO.regionCenterId = $filter('filter')($scope.area_list, {BRANCH_AREA_ID: $scope.inputVO.branchAreaId})[0].REGION_CENTER_ID;
				}
			} else {
				if ($scope.inputVO.branchAreaId != '') {
					if ($filter('filter')($scope.area_list, {BRANCH_AREA_ID: $scope.inputVO.branchAreaId})[0].REGION_CENTER_ID != $scope.inputVO.regionCenterId){
						$scope.inputVO.branchAreaId = '';						
					}						
					angular.forEach($scope.area_list, function(row, index, objs){
						if(row.REGION_CENTER_ID == $scope.inputVO.regionCenterId) {
							$scope.mappingSet['branch_area_id'].push({LABEL: row.BRANCH_AREA_NAME, DATA: row.BRANCH_AREA_ID});
						}
					});

				} else {
					angular.forEach($scope.area_list, function(row, index, objs){
						if(row.REGION_CENTER_ID == $scope.inputVO.regionCenterId) {
							$scope.mappingSet['branch_area_id'].push({LABEL: row.BRANCH_AREA_NAME, DATA: row.BRANCH_AREA_ID});
						}
					});
					if ($scope.inputVO.branchNbr != ''){
						$scope.inputVO.branchAreaId = $filter('filter')($scope.bra_list, {BRANCH_NBR: $scope.inputVO.branchNbr})[0].BRANCH_AREA_ID;						
					}
				}
			}
		};
		
		//get分行別
		$scope.getBranchNbr = function() {
			$scope.mappingSet['branch_nbr'] = [];			
			if ($scope.inputVO.branchAreaId != '') {
				if ($filter('filter')($scope.bra_list, {BRANCH_NBR: $scope.inputVO.branchNbr})[0].BRANCH_AREA_ID == $scope.inputVO.branchAreaId) {
					angular.forEach($scope.bra_list, function(row, index, objs){
						if(row.BRANCH_AREA_ID == $scope.inputVO.branchAreaId) {
							$scope.mappingSet['branch_nbr'].push({LABEL: row.BRANCH_NBR + '-' + row.BRANCH_NAME, DATA: row.BRANCH_NBR});
						}
					});
				} else {					
					angular.forEach($scope.bra_list, function(row, index, objs){
						if(row.BRANCH_AREA_ID == $scope.inputVO.branchAreaId) {
							$scope.mappingSet['branch_nbr'].push({LABEL: row.BRANCH_NBR + '-' + row.BRANCH_NAME, DATA: row.BRANCH_NBR});
						}
					});
				}
			} else if ($scope.inputVO.regionCenterId != '') {
				$scope.inputVO.branchNbr = '';
				var temp_branch_area_id_list = [];
				
				angular.forEach($scope.area_list, function(row, index, objs){
					if(row.REGION_CENTER_ID == $scope.inputVO.regionCenterId) {
						temp_branch_area_id_list.push({LABEL: row.BRANCH_AREA_NAME, DATA: row.BRANCH_AREA_ID});
					}
				});
				
				angular.forEach($scope.bra_list, function(row, index, objs){
					for(var i = 0; i < temp_branch_area_id_list.length; i++) {
						if(row.BRANCH_AREA_ID == temp_branch_area_id_list[i].DATA) {
							$scope.mappingSet['branch_nbr'].push({LABEL: row.BRANCH_NBR + '-' + row.BRANCH_NAME, DATA: row.BRANCH_NBR});
						}
					}
				});			
			} else {
				angular.forEach($scope.bra_list, function(row, index, objs){
					$scope.mappingSet['branch_nbr'].push({LABEL: row.BRANCH_NBR + '-' + row.BRANCH_NAME, DATA: row.BRANCH_NBR});
				});
			}
		}

        //取的優先順序資料
		$scope.mappingSet['order'] = [{LABEL: "1", DATA: "1"},
			                          {LABEL: "2", DATA: "2"},
                                      {LABEL: "3", DATA: "3"},
			                          {LABEL: "4", DATA: "4"},
			                          {LABEL: "5", DATA: "5"},
			                          {LABEL: "6", DATA: "6"},
			                          {LABEL: "7", DATA: "7"},
			                          {LABEL: "8", DATA: "8"},
			                          {LABEL: "9", DATA: "9"},
			                          {LABEL: "10", DATA: "10"}
			                          ];
		
		//修改 & 新增
		$scope.addConfirm = function(){
			if($scope.row != undefined){			
				//修改				
	        	$scope.sendRecv("CRM321", "editConfirm", "com.systex.jbranch.app.server.fps.crm321.CRM321InputVO", $scope.inputVO,
	    			function(totas, isError) {
//	        			if (isError) {
//	        				$scope.showErrorMsg("ehl_01_common_016");
//							return;
//		                }
		                if (totas.length > 0) {
		                	$scope.showMsg('ehl_01_common_002');
		                	$scope.closeThisDialog('successful');
		                };
		        });
			}else{
				//新增
				$scope.sendRecv("CRM321", "addConfirm", "com.systex.jbranch.app.server.fps.crm321.CRM321InputVO", $scope.inputVO,
						function(totas, isError) {
//					if (isError) {
//						$scope.showErrorMsg("ehl_01_common_016");
//						return;
//					}
					if (totas.length > 0) {
						$scope.showMsg('ehl_01_common_001');
						$scope.closeThisDialog('successful');
					};
				});
			}
			
        }
		
		$scope.init();
	
	}
);