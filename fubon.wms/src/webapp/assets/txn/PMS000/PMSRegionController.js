'use strict';
eSoafApp.controller('PMSRegionController',
	function($rootScope, $scope, $controller, $confirm, $filter, socketService, ngDialog, projInfoService, sysInfoService, $q, getParameter) {
		$scope.controllerName = "PMSRegionController";
		
		$scope.priID = String(sysInfoService.getPriID());
		$scope.memLoginFlag = String(sysInfoService.getMemLoginFlag());

		$scope.RegionController_getAllORG = function(inputVO) {
			var deferred = $q.defer();
        	$scope.sendRecv("PMS000", "getAllOrg", "com.systex.jbranch.app.server.fps.pms000.PMS000InputVO", inputVO,
				function(totas, isError) {
        			$scope.regionList = totas[0].body.regionList;
        			$scope.region_areaList = totas[0].body.region_areaList;
        			$scope.areaList = totas[0].body.areaList;
        			$scope.area_branchList = totas[0].body.area_branchList;
        			$scope.branchList = totas[0].body.branchList;
        			
        			// 區域中心
        			$scope.REGION_LIST = [];        			
        			angular.forEach($scope.regionList, function(row) {
        				$scope.REGION_LIST.push({LABEL: row.REGION_NAME, DATA: row.REGION_ID});        				
        			});
        			$scope.checkRegionEnable(inputVO);
        			        			
        			// 營運區
        			$scope.AREA_LIST = [];
        			angular.forEach($scope.areaList, function(row) {
        				$scope.AREA_LIST.push({LABEL: row.AREA_NAME, DATA: row.AREA_ID});
        			});
        			$scope.checkAreaEnable(inputVO);
        			
        			// 分行別
        			$scope.BRANCH_LIST = [];
        			angular.forEach($scope.branchList, function(row) {
        				$scope.BRANCH_LIST.push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
        			});
        			$scope.checkBranchEnable(inputVO);
        			
        			deferred.resolve("success");
			 	}
			);
        	return deferred.promise;
		}
		
		$scope.RegionController_getORG = function(inputVO) {
			var deferred = $q.defer();
        	$scope.sendRecv("PMS000", "getOrg", "com.systex.jbranch.app.server.fps.pms000.PMS000InputVO", inputVO,
				function(totas, isError) {
        			$scope.regionList = totas[0].body.regionList;
        			$scope.region_areaList = totas[0].body.region_areaList;
        			$scope.areaList = totas[0].body.areaList;
        			$scope.area_branchList = totas[0].body.area_branchList;
        			$scope.branchList = totas[0].body.branchList;
        			$scope.aoList = totas[0].body.aoList;
        			$scope.empList = totas[0].body.empList;
        			
        			
        			// 區域中心
        			$scope.REGION_LIST = [];        			
        			angular.forEach($scope.regionList, function(row) {
        				$scope.REGION_LIST.push({LABEL: row.RIGION_NAME, DATA: row.REGION_ID});        				
        			});
        			$scope.checkRegionEnable(inputVO);
        			        			
        			// 營運區
        			$scope.AREA_LIST = [];
        			angular.forEach($scope.areaList, function(row) {
        				$scope.AREA_LIST.push({LABEL: row.AREA_NAME, DATA: row.AREA_ID});
        			});
        			$scope.checkAreaEnable(inputVO);
        			
        			// 分行別
        			$scope.BRANCH_LIST = [];
        			angular.forEach($scope.branchList, function(row) {
        				$scope.BRANCH_LIST.push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
        			});
        			$scope.checkBranchEnable(inputVO);
        			
        			// AO_CODE
        			$scope.AO_LIST = [];
        			angular.forEach($scope.aoList, function(row) {
        				$scope.AO_LIST.push({LABEL: row.EMP_NAME, DATA: row.AO_CODE});
        			});
//        			$scope.checkAoEnable(inputVO);
        			
        			//參照Jacky範例新增
        			// 顯示EMP_LIST+AO_CODE+EMP_NAME   DATA: AO_CODE   使用'F3'
        			$scope.AO_EMP_LIST = [];
        			angular.forEach($scope.aoList, function(row) {
//        				$scope.AO_EMP_LIST.push({LABEL: row.EMP_ID+"-"+row.AO_CODE+"-"+row.EMP_NAME, DATA: row.AO_CODE});
        				$scope.AO_EMP_LIST.push({LABEL: row.EMP_ID+"-"+row.EMP_NAME, DATA: row.AO_CODE});
        			});
        			$scope.checkAoEnable(inputVO);
        			// 員編
        			$scope.EMP_LIST = [];
        			angular.forEach($scope.empList, function(row) {
        				$scope.EMP_LIST.push({LABEL: row.EMP_NAME, DATA: row.EMP_ID});
        			});
//        			$scope.checkEmpEnable(inputVO);
        			
        			//參照Jacky範例新增
        			// 顯示EMP_LIST+AO_CODE+EMP_NAME   DATA: EMP_ID   使用'F3'
        			$scope.EMP_AO_LIST = [];
        			angular.forEach($scope.empList, function(row) {
        				$scope.EMP_AO_LIST.push({LABEL: row.EMP_ID+"-"+row.AO_CODE+"-"+row.EMP_NAME, DATA: row.EMP_ID});
        			});
        			$scope.checkEmpEnable(inputVO);
        			
        			deferred.resolve("success");
			 	}
			);
        	return deferred.promise;
		};
//		$scope.RegionController_getORG();
				
		// 連動 by walala
		// 區域
		$scope.ChangeRegion = function() {
			if($scope.disableRegionCombo == true) return;
			
			if (!$scope.inputVO.region_center_id) {
				// 營運區
    			$scope.AREA_LIST = [];
    			angular.forEach($scope.areaList, function(row) {
    				$scope.AREA_LIST.push({LABEL: row.AREA_NAME, DATA: row.AREA_ID});
    			});
    			// 分行別
    			$scope.BRANCH_LIST = [];
    			angular.forEach($scope.branchList, function(row) {
    				$scope.BRANCH_LIST.push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
    			});
    			// AO_CODE
    			$scope.AO_LIST = [];
    			angular.forEach($scope.aoList, function(row) {
    				$scope.AO_LIST.push({LABEL: row.EMP_NAME, DATA: row.AO_CODE});
    			});
    			//參照Jacky範例新增
    			// 顯示AO_CODE+EMP_LIST+EMP_NAME  ,DATA:AO_CODE  'F3'
    			$scope.AO_EMP_LIST = [];
    			angular.forEach($scope.aoList, function(row) {
    				$scope.AO_EMP_LIST.push({LABEL: row.EMP_ID+"-"+row.EMP_NAME, DATA: row.AO_CODE});
    			});   			
    			// 員編
    			$scope.EMP_LIST = [];
    			angular.forEach($scope.empList, function(row) {
    				$scope.EMP_LIST.push({LABEL: row.EMP_NAME, DATA: row.EMP_ID});
    			});
    			//參照Jacky範例新增
    			// 顯示AO_CODE+EMP_LIST+EMP_NAME  ,DATA:EMP_ID    'F3'
    			$scope.EMP_AO_LIST = [];
    			angular.forEach($scope.empList, function(row) {
    				$scope.EMP_AO_LIST.push({LABEL: row.EMP_ID+"-"+row.AO_CODE+"-"+row.EMP_NAME, DATA: row.EMP_ID});
    			});   
    			$scope.inputVO.branch_area_id = "";
			} else {
				$scope.resetArea($scope.inputVO.region_center_id);
				$scope.resetBranch($scope.inputVO.branch_area_id); //filter area list
				$scope.resetAO($scope.inputVO.branch_nbr); //filter area list
				
				if($scope.inputVO.branch_area_id)
					if ($filter('filter')($scope.AREA_LIST, {DATA: $scope.inputVO.branch_area_id}).length <= 0)
						$scope.inputVO.branch_area_id = '';
				
				if($scope.inputVO.branch_nbr)
					if ($filter('filter')($scope.BRANCH_LIST, {DATA: $scope.inputVO.branch_nbr}).length <= 0)
						$scope.inputVO.branch_nbr = '';
				
				if ($scope.inputVO.ao_code && $scope.inputVO.ao_code != '0'){
					if ($filter('filter')($scope.AO_LIST, {DATA: $scope.inputVO.ao_code}).length <= 0)
						$scope.inputVO.ao_code = '';
					//參照Jacky範例清除理專資訊
					if ($filter('filter')($scope.AO_EMP_LIST, {DATA: $scope.inputVO.ao_code}).length <= 0)
						$scope.inputVO.ao_code = '';
					
				}
				
				if ($filter('filter')($scope.EMP_LIST, {DATA: $scope.inputVO.emp_id}).length <= 0)
					$scope.inputVO.emp_id = '';
				//參照新增  EMP_AO_LIST ,DATA:EMP_ID
				if ($filter('filter')($scope.EMP_AO_LIST, {DATA: $scope.inputVO.emp_id}).length <= 0)
					$scope.inputVO.emp_id = '';
			}
		};
		
		// 營運區
		$scope.ChangeArea = function() {
			if($scope.disableAreaCombo == true) return;
			
			if (!$scope.inputVO.branch_area_id) {
				$scope.ChangeRegion();
				
    			$scope.inputVO.branch_nbr = "";
    			$scope.inputVO.emp_id = "";
    			$scope.inputVO.ao_code = "";
			} else {
				console.log($scope.inputVO.branch_nbr);
				$scope.resetBranch($scope.inputVO.branch_area_id);
				$scope.resetAO($scope.inputVO.branch_nbr); //filter area list
				
				if($scope.inputVO.branch_area_id){
					angular.forEach($scope.region_areaList, function(row) {
						if(row.AREA_ID == $scope.inputVO.branch_area_id){
							$scope.inputVO.region_center_id = row.REGION_ID;
							return;
						}
					});
				}
				
				if($scope.inputVO.branch_nbr)
					if ($filter('filter')($scope.BRANCH_LIST, {DATA: $scope.inputVO.branch_nbr}).length <= 0)
						$scope.inputVO.branch_nbr = '';
				
				if ($scope.inputVO.ao_code && $scope.inputVO.ao_code != '0'){
					if ($filter('filter')($scope.AO_LIST, {DATA: $scope.inputVO.ao_code}).length <= 0)
						$scope.inputVO.ao_code = '';
					//參照Jacky範例清除理專資訊
					if ($filter('filter')($scope.AO_EMP_LIST, {DATA: $scope.inputVO.ao_code}).length <= 0)
						$scope.inputVO.ao_code = '';
				}
					
				
				if ($filter('filter')($scope.EMP_LIST, {DATA: $scope.inputVO.emp_id}).length <= 0)
					$scope.inputVO.emp_id = '';
				//參照新增  EMP_AO_LIST ,DATA:EMP_ID
				if ($filter('filter')($scope.EMP_AO_LIST, {DATA: $scope.inputVO.emp_id}).length <= 0)
					$scope.inputVO.emp_id = '';
			}
		};
		
		// 分行
		$scope.ChangeBranch = function() {
			if($scope.disableBranchCombo == true) return;
			
			if (!$scope.inputVO.branch_nbr) {
				$scope.ChangeArea();
    			
    			$scope.inputVO.emp_id = "";
    			$scope.inputVO.ao_code = "";
			} else {
				$scope.resetAO($scope.inputVO.branch_nbr);				
				
				if($scope.inputVO.branch_nbr){
					angular.forEach($scope.area_branchList, function(row) {
						if(row.BRANCH_NBR == $scope.inputVO.branch_nbr){
							$scope.inputVO.branch_area_id = row.AREA_ID;
							return;
						}
					});
				}
				
				if ($scope.inputVO.ao_code && $scope.inputVO.ao_code != '0'){
					if ($filter('filter')($scope.AO_LIST, {DATA: $scope.inputVO.ao_code}).length <= 0)
						$scope.inputVO.ao_code = '';
					//參照Jacky範例清除理專資訊
					if ($filter('filter')($scope.AO_EMP_LIST, {DATA: $scope.inputVO.ao_code}).length <= 0)
						$scope.inputVO.ao_code = '';					
				}
				
				if ($filter('filter')($scope.EMP_LIST, {DATA: $scope.inputVO.emp_id}).length <= 0)
					$scope.inputVO.emp_id = '';
				//參照新增  EMP_AO_LIST ,DATA:EMP_ID
				if ($filter('filter')($scope.EMP_AO_LIST, {DATA: $scope.inputVO.emp_id}).length <= 0)
					$scope.inputVO.emp_id = '';
			}
		};
		
		//AO
		$scope.ChangeAo_code = function() {
			if($scope.disableAoCombo == true || $scope.disableEmpCombo == true) return;
			
			if($scope.inputVO.ao_code){
				angular.forEach($scope.aoList, function(row) {					
					if(row.AO_CODE == $scope.inputVO.ao_code){
						$scope.inputVO.branch_nbr = row.BRANCH_NBR;
						return;
					}
				});
			}
			if($scope.inputVO.emp_id){
				angular.forEach($scope.empList, function(row) {
					if(row.EMP_ID == $scope.inputVO.emp_id){
						$scope.inputVO.branch_nbr = row.BRANCH_NBR;
						return;
					}
				});
			}
		}
		
		$scope.resetArea = function(regionID){
			if(regionID){
				//branch_area_id
				$scope.AREA_LIST = [];
				angular.forEach($scope.region_areaList, function(row) {
					if(row.REGION_ID == regionID) {
						$scope.AREA_LIST.push({LABEL: row.AREA_NAME, DATA: row.AREA_ID, REGION_CENTER_ID: row.REGION_ID});
					}
				});
			}else{
				// 營運區-全部
    			$scope.AREA_LIST = [];
    			angular.forEach($scope.areaList, function(row) {
    				$scope.AREA_LIST.push({LABEL: row.AREA_NAME, DATA: row.AREA_ID, REGION_CENTER_ID: row.REGION_ID});
    			});
			}
		}
		
		$scope.resetBranch = function(areaID){
			if(areaID){
				//branch_nbr filter 傳入值
				$scope.BRANCH_LIST = [];
				angular.forEach($scope.area_branchList, function(row) {
					if(row.AREA_ID == areaID) {
						$scope.BRANCH_LIST.push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR, BRANCH_AREA_ID: row.AREA_ID});
					}
				});
			}else{
				//branch_nbr filter 營運區選單
				$scope.BRANCH_LIST = [];
				angular.forEach($scope.area_branchList, function(row) {
					angular.forEach($scope.AREA_LIST, function(row2) {
						if(row.AREA_ID == row2.DATA) {
							$scope.BRANCH_LIST.push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR, BRANCH_AREA_ID: row.AREA_ID});
						}
					});
				});
			}
		}
		
		$scope.resetAO = function(branchNbr){
			if(branchNbr){
				//AO EMP filter 傳入值
				$scope.AO_LIST = [];
				$scope.EMP_LIST = [];
				//新增額外list
				$scope.AO_EMP_LIST = [];     //DATA 為AO_CODE
				$scope.EMP_AO_LIST = [];     //DATA 為員編
				angular.forEach($scope.aoList, function(row) {
					if(row.BRANCH_NBR == branchNbr) {
						$scope.AO_LIST.push({LABEL: row.EMP_NAME, DATA: row.AO_CODE, BRANCH_NBR: row.BRANCH_NBR});
						//參照Jacky範例增加List的push    DATA:AO_CODE
						$scope.AO_EMP_LIST.push({LABEL: row.EMP_ID+"-"+row.EMP_NAME, DATA: row.AO_CODE, BRANCH_NBR: row.BRANCH_NBR});	
					}
				});
				angular.forEach($scope.empList, function(row) {
					if(row.BRANCH_NBR == branchNbr) {						
						$scope.EMP_LIST.push({LABEL: row.EMP_NAME, DATA: row.EMP_ID, BRANCH_NBR: row.BRANCH_NBR});
						//參照Jacky範例增加List的push    DATA:EMP_ID
						$scope.EMP_AO_LIST.push({LABEL: row.EMP_ID+"-"+row.AO_CODE+"-"+row.EMP_NAME, DATA: row.EMP_ID, BRANCH_NBR: row.BRANCH_NBR});	
					}
				});
			}else{
				//AO EMP filter 分行選單
				$scope.AO_LIST = [];
				$scope.EMP_LIST = [];
				//新增額外list
				$scope.AO_EMP_LIST = [];     //DATA 為AO_CODE
				$scope.EMP_AO_LIST = [];     //DATA 為員編
				angular.forEach($scope.aoList, function(row) {
					angular.forEach($scope.BRANCH_LIST, function(row2) {
						if(row.BRANCH_NBR == row2.DATA) {
							$scope.AO_LIST.push({LABEL: row.EMP_NAME, DATA: row.AO_CODE, BRANCH_NBR: row.BRANCH_NBR});	
							//參照Jacky範例增加List的push    DATA:AO_CODE
							$scope.AO_EMP_LIST.push({LABEL: row.EMP_ID+"-"+row.EMP_NAME, DATA: row.AO_CODE, BRANCH_NBR: row.BRANCH_NBR});	
						}
					});
				});
				angular.forEach($scope.empList, function(row) {
					angular.forEach($scope.BRANCH_LIST, function(row2) {
						if(row.BRANCH_NBR == row2.DATA) {
							$scope.EMP_LIST.push({LABEL: row.EMP_NAME, DATA: row.EMP_ID, BRANCH_NBR: row.BRANCH_NBR});
							//參照Jacky範例增加List的push    DATA:EMP_ID
							$scope.EMP_AO_LIST.push({LABEL: row.EMP_ID+"-"+row.AO_CODE+"-"+row.EMP_NAME, DATA: row.EMP_ID, BRANCH_NBR: row.BRANCH_NBR});	
						
						}
					});
				});
			}
		}
		
		$scope.checkRegionEnable = function(inputVO){
			if($scope.regionList != null &&  $scope.regionList.length > 1){
				inputVO.region_center_id = "";
				$scope.disableRegionCombo = false;
			}else if($scope.regionList != null && $scope.regionList.length == 1){
				inputVO.region_center_id = $scope.regionList[0].REGION_ID;
				$scope.disableRegionCombo = true;
			}
		}
		
		$scope.checkAreaEnable = function(inputVO){
			if($scope.areaList != null &&  $scope.areaList.length > 1){
				inputVO.branch_area_id = "";
				$scope.disableAreaCombo = false;
			}else if($scope.areaList != null && $scope.areaList.length == 1){
				inputVO.branch_area_id = $scope.areaList[0].AREA_ID;
				$scope.disableAreaCombo = true;
			}
		}
		
		$scope.checkBranchEnable = function(inputVO){
			if($scope.branchList != null &&  $scope.branchList.length > 1){
				inputVO.branch_nbr = "";
				$scope.disableBranchCombo = false;
			}else if($scope.branchList != null && $scope.branchList.length == 1){
				inputVO.branch_nbr = $scope.branchList[0].BRANCH_NBR;
				$scope.disableBranchCombo = true;
			}
		}
		
		$scope.checkAoEnable = function(inputVO){
			if($scope.aoList != null &&  $scope.aoList.length > 1){
				inputVO.ao_code = "";
				$scope.disableAoCombo = false;
			}else if($scope.aoList != null && $scope.aoList.length == 1){
				inputVO.ao_code = $scope.aoList[0].AO_CODE;
				$scope.disableAoCombo = true;
			}
		}
		
		$scope.checkEmpEnable = function(inputVO){
			if($scope.empList != null &&  $scope.empList.length > 1){
				inputVO.emp_id = "";
				$scope.disableEmpCombo = false;
			}else if($scope.empList != null && $scope.empList.length == 1){
				switch($filter('uppercase')($scope.memLoginFlag)) {
					case "UHRM":
					case "BS":
						inputVO.emp_id = $scope.empList[0].EMP_ID;
						$scope.disableEmpCombo = true;
						
						break;
					case "BRHMEM":
						switch ($scope.priID) {
							case "001":		// AFC/RA
							case "002":		// FC
							case "003":		// FCH
							case "004":		// 消金PS
							case "005":		// 存匯人員
							case "008":		// 覆核人員
							case "004AO":	// PAO
								inputVO.emp_id = $scope.empList[0].EMP_ID;
								$scope.disableEmpCombo = true;
								
								break;
							default:
								inputVO.emp_id = "";
								$scope.disableEmpCombo = false;
								
								break;
						}
						
						break;
					default:
						inputVO.emp_id = "";
						$scope.disableEmpCombo = false;
						
						break;
				}
			}
		}
});
