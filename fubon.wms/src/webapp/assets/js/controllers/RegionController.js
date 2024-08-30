'use strict';
eSoafApp.controller('RegionController',["$rootScope", "$scope", "$controller", "$confirm", "$filter", "socketService", "ngDialog", "projInfoService", "$q", "getParameter",
	function($rootScope, $scope, $controller, $confirm, $filter, socketService, ngDialog, projInfoService, $q, getParameter) {
		$scope.controllerName = "RegionController";
		
		$scope.RegionController_setName = function(Name) {
			var ALLdeferred = $q.defer();
			$scope.DoRegionController(Name).then(function(data) {
				ALLdeferred.resolve();
			});
			return ALLdeferred.promise;
		};
		
		$scope.DoRegionController = function(ControllerData) {
			var ALLdeferred = $q.defer();
			
			// 區域中心
			$scope.AVAIL_REGION = projInfoService.getAvailRegion();
			// 2017/8/10 ocean:FA,IA only have one region but null
			if($scope.AVAIL_REGION.length == 1)
				ControllerData[1][ControllerData[2]] = $scope.AVAIL_REGION[0].REGION_CENTER_ID;
			else
				ControllerData[1][ControllerData[2]] = ControllerData[1][ControllerData[2]] ? ControllerData[1][ControllerData[2]] : ("null" == projInfoService.getRegionID() ? "" : projInfoService.getRegionID());
			$scope[ControllerData[3]] = [];
			angular.forEach($scope.AVAIL_REGION, function(row) {
				$scope[ControllerData[3]].push({LABEL: row.REGION_CENTER_NAME, DATA: row.REGION_CENTER_ID});
			});
			
			// 營運區
			$scope.AVAIL_AREA = projInfoService.getAvailArea();
			// 2017/8/10 ocean:FA,IA can only have one area but getAreaID() can null
			if($scope.AVAIL_AREA.length == 1)
				ControllerData[1][ControllerData[4]] = $scope.AVAIL_AREA[0].BRANCH_AREA_ID;
			else
				ControllerData[1][ControllerData[4]] = ControllerData[1][ControllerData[4]] ? ControllerData[1][ControllerData[4]] : ("null" == projInfoService.getAreaID() ? "" : projInfoService.getAreaID());
			$scope[ControllerData[5]] = [];
			angular.forEach($scope.AVAIL_AREA, function(row) {
				if(ControllerData[1][ControllerData[2]]) {
					if(row.REGION_CENTER_ID == ControllerData[1][ControllerData[2]])
						$scope[ControllerData[5]].push({LABEL: row.BRANCH_AREA_NAME, DATA: row.BRANCH_AREA_ID});
				} else
					$scope[ControllerData[5]].push({LABEL: row.BRANCH_AREA_NAME, DATA: row.BRANCH_AREA_ID});
			});
			
			// 分行別
			$scope.AVAIL_BRANCH = projInfoService.getAvailBranch();
			// 2017/8/10 ocean:FA,IA can only have one branch but getBranchID() can null
			if($scope.AVAIL_BRANCH.length == 1)
				ControllerData[1][ControllerData[6]] = $scope.AVAIL_BRANCH[0].BRANCH_NBR;
			else
				ControllerData[1][ControllerData[6]] = ControllerData[1][ControllerData[6]] ? ControllerData[1][ControllerData[6]] : ("000" == projInfoService.getBranchID() ? "" : projInfoService.getBranchID());
			$scope[ControllerData[7]] = [];
			angular.forEach($scope.AVAIL_BRANCH, function(row) {
				if(ControllerData[1][ControllerData[4]]) {
					if(row.BRANCH_AREA_ID == ControllerData[1][ControllerData[4]])
						$scope[ControllerData[7]].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
				} else
					$scope[ControllerData[7]].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
			});
			
			// AO_CODE
			$scope.AVAIL_AO_CODE = projInfoService.getAoCode();
			ControllerData[1][ControllerData[8]] = ControllerData[1][ControllerData[8]] ? ControllerData[1][ControllerData[8]] : projInfoService.getAoCode()[0] ? projInfoService.getAoCode()[0] : "";
			$scope.RegionController_quireAo = function(ControllerData) {
				var deferred = $q.defer();
	        	$scope.sendRecv("CRM381", "ao_inquire", "com.systex.jbranch.app.server.fps.crm381.CRM381InputVO", {'region_center_id': ControllerData[1][ControllerData[2]],
	        																									   'branch_area_id': ControllerData[1][ControllerData[4]],
	        																									   'branch_nbr': ControllerData[1][ControllerData[6]]},
					function(totas, isError) {
	        			$scope["TOTAL_"+ControllerData[9]] = totas[0].body.ao_list;
	        			
	        			$scope[ControllerData[9]] = [];
	    				if(ControllerData[0] == 'Y')
	    					$scope[ControllerData[9]].push({LABEL: "空CODE", DATA: "0"});
	    				if($scope.AVAIL_AO_CODE.length > 0) {
	    					if($scope.AVAIL_AO_CODE.length > 1 ) {
	    						angular.forEach($scope["TOTAL_" + ControllerData[9]], function(row) {
	    							if($scope.AVAIL_AO_CODE.indexOf(row.AO_CODE) > -1)
	    								$scope[ControllerData[9]].push({LABEL: row.EMP_NAME, DATA: row.AO_CODE});
	    						});	
	    					}
	    					else {
	    						$scope[ControllerData[9]].push({LABEL: projInfoService.getUserName(), DATA: $scope.AVAIL_AO_CODE[0]});
	    					}
	    				}
	    				else {
	    					angular.forEach($scope["TOTAL_" + ControllerData[9]], function(row) {
    							$scope[ControllerData[9]].push({LABEL: row.EMP_NAME, DATA: row.AO_CODE});
    						});
	    				}
	    				deferred.resolve();
				 	}
				);
	        	return deferred.promise;
			};
			
			// EMP_ID
			ControllerData[1][ControllerData[10]] = ControllerData[1][ControllerData[10]] ? ControllerData[1][ControllerData[10]] : projInfoService.getUserID();
			$scope.RegionController_quireEmp = function(ControllerData) {
				var deferred = $q.defer();
	        	$scope.sendRecv("ORG220", "getEmpList", "com.systex.jbranch.app.server.fps.org220.ORG220InputVO", {'region_center_id': ControllerData[1][ControllerData[2]],
	        																									   'branch_area_id': ControllerData[1][ControllerData[4]],
	        																									   'branch_nbr': ControllerData[1][ControllerData[6]]},
					function(totas, isError) {
	        			$scope[ControllerData[11]] = totas[0].body.empList;
	        			deferred.resolve();
				 	}
				);
	        	return deferred.promise;
			};
			// 2017/2/20 test
			$q.all([
			    $scope.RegionController_quireAo(ControllerData).then(function(data) {}),
			    $scope.RegionController_quireEmp(ControllerData).then(function(data) {})
        	]).then(function() {
        		ALLdeferred.resolve();
        	});
			
			// 連動 by walala
			// 區域
			$scope.ChangeRegion = function(data) {
				if (!data[1][data[2]]) {
					//branch_area_id
					data[1][data[4]] = '';
					$scope[data[5]] = [];
					angular.forEach($scope.AVAIL_AREA, function(row) {
						$scope[data[5]].push({LABEL: row.BRANCH_AREA_NAME, DATA: row.BRANCH_AREA_ID});
					});
					
					//branch_nbr
					data[1][data[6]] = '';
					$scope[data[7]] = [];
					angular.forEach($scope.AVAIL_BRANCH, function(row) {
						$scope[data[7]].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
					});
					
					//ao_code
					$scope.RegionController_quireAo(data);
					
					//emp_id
					$scope.RegionController_quireEmp(data);
				} else {
					//branch_area_id
					$scope[data[5]] = [];
					angular.forEach($scope.AVAIL_AREA, function(row) {
						if(row.REGION_CENTER_ID == data[1][data[2]]) {
							$scope[data[5]].push({LABEL: row.BRANCH_AREA_NAME, DATA: row.BRANCH_AREA_ID});
						}
					});
					
					//branch_nbr
					if (!data[1][data[4]]) {
						$scope[data[7]] = [];
						angular.forEach($scope.AVAIL_BRANCH, function(row) {
							angular.forEach($scope[data[5]], function(row2) {
								if(row.BRANCH_AREA_ID == row2.DATA) {
									$scope[data[7]].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
								}
							});
						});
					}
					
					//ao_code
					$scope.RegionController_quireAo(data);
					
					//emp_id
					$scope.RegionController_quireEmp(data);
					
					if(data[1][data[4]])
						if (data[1][data[2]] != $filter('filter')($scope.AVAIL_AREA, {BRANCH_AREA_ID: data[1][data[4]]})[0].REGION_CENTER_ID)
							data[1][data[4]] = '';
					
					if(data[1][data[6]])
						if (data[1][data[4]] != $filter('filter')($scope.AVAIL_BRANCH, {BRANCH_NBR: data[1][data[6]]})[0].BRANCH_AREA_ID)
							data[1][data[6]] = '';
					
					if (data[1][data[8]] && data[1][data[8]] != '0')
						if (data[1][data[6]] != $filter('filter')($scope["TOTAL_"+data[9]], {AO_CODE: data[1][data[8]]})[0].BRANCH_NBR)
							data[1][data[8]] = '';
					
					if (data[1][data[6]] != $filter('filter')($scope[data[11]], {DATA: data[1][data[10]]})[0].BRANCH_NBR)
						data[1][data[10]] = '';
				}
			};
			
			// 營運區
			$scope.ChangeArea = function(data) {
				if (!data[1][data[4]]) {
					data[1][data[6]] = '';
					if (data[1][data[2]]) { //若區域中心為空
						//branch_area_id
						$scope[data[5]] = [];
						angular.forEach($scope.AVAIL_AREA, function(row) {
							if(row.REGION_CENTER_ID == data[1][data[2]]) {
								$scope[data[5]].push({LABEL: row.BRANCH_AREA_NAME, DATA: row.BRANCH_AREA_ID});
							}
						});
						
						//branch_nbr
						$scope[data[7]] = [];
						angular.forEach($scope.AVAIL_BRANCH, function(row) {
							angular.forEach($scope[data[5]], function(row2) {
								if(row.BRANCH_AREA_ID == row2.DATA) {
									$scope[data[7]].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
								}
							});
						});
					} else {
						$scope[data[7]] = [];
						angular.forEach($scope.AVAIL_BRANCH, function(row) {
							$scope[data[7]].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
						});
					}
					
					//ao_code
					$scope.RegionController_quireAo(data);
					
					//emp_id
					$scope.RegionController_quireEmp(data);
				} else {
					data[1][data[2]] = $filter('filter')($scope.AVAIL_AREA, {BRANCH_AREA_ID: data[1][data[4]]})[0].REGION_CENTER_ID;
				
					//branch_nbr
					$scope[data[7]] = [];
					angular.forEach($scope.AVAIL_BRANCH, function(row) {
						if(row.BRANCH_AREA_ID == data[1][data[4]]) {
							$scope[data[7]].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
						}
					});
					
					//ao_code
					$scope.RegionController_quireAo(data);
					
					//emp_id
					$scope.RegionController_quireEmp(data);
					
					if(data[1][data[6]])
						if (data[1][data[4]] != $filter('filter')($scope.AVAIL_BRANCH, {BRANCH_NBR: data[1][data[6]]})[0].BRANCH_AREA_ID)
							data[1][data[6]] = '';
					
					if (data[1][data[8]] && data[1][data[8]] != '0')
						if (data[1][data[6]] != $filter('filter')($scope["TOTAL_"+data[9]], {AO_CODE: data[1][data[8]]})[0].BRANCH_NBR)
							data[1][data[8]] = '';
					
					if (data[1][data[6]] != $filter('filter')($scope[data[11]], {DATA: data[1][data[10]]})[0].BRANCH_NBR)
						data[1][data[10]] = '';
				}
			};
			
			// 分行
			$scope.ChangeBranch = function(data) {
				if (!data[1][data[6]]) {
					if (data[1][data[4]]) { //若營運區非空
						//branch_nbr
						$scope[data[7]] = [];
						angular.forEach($scope.AVAIL_BRANCH, function(row) {
							if(row.BRANCH_AREA_ID == data[1][data[4]]) {
								$scope[data[7]].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
							}
						});
					} else if (data[1][data[2]]) { //若區域中心非空
						//branch_area_id
						$scope[data[5]] = [];
						angular.forEach($scope.AVAIL_AREA, function(row) {
							if(row.REGION_CENTER_ID == data[1][data[2]]) {
								$scope[data[5]].push({LABEL: row.BRANCH_AREA_NAME, DATA: row.BRANCH_AREA_ID});
							}
						}); 

						//branch_nbr
						$scope[data[7]] = [];
						angular.forEach($scope.AVAIL_BRANCH, function(row) {
							angular.forEach($scope[data[5]], function(row2) {
								if(row.BRANCH_AREA_ID == row2.DATA) {
									$scope[data[7]].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
								}
							});
						});
					} else {
						//branch_area_id
						data[1][data[4]] = '';
						$scope[data[5]] = [];
						angular.forEach($scope.AVAIL_AREA, function(row) {
							$scope[data[5]].push({LABEL: row.BRANCH_AREA_NAME, DATA: row.BRANCH_AREA_ID});
						});
						
						//branch_nbr
						data[1][data[6]] = '';
						$scope[data[7]] = [];
						angular.forEach($scope.AVAIL_BRANCH, function(row) {
							$scope[data[7]].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
						});
					}
					
					//ao_code
					$scope.RegionController_quireAo(data);
					
					//emp_id
					$scope.RegionController_quireEmp(data);
				} else {
					data[1][data[4]] = $filter('filter')($scope.AVAIL_BRANCH, {BRANCH_NBR: data[1][data[6]]})[0].BRANCH_AREA_ID;
					
					//ao_code
					$scope.RegionController_quireAo(data);
					
					//emp_id
					$scope.RegionController_quireEmp(data);
					
					if (data[1][data[8]] && data[1][data[8]] != '0')
						if (data[1][data[6]] != $filter('filter')($scope["TOTAL_" + data[9]], {AO_CODE: data[1][data[8]]})[0].BRANCH_NBR)
							data[1][data[8]] = '';
					
					if (data[1][data[6]] != $filter('filter')($scope[data[11]], {DATA: data[1][data[10]]})[0].BRANCH_NBR)
						data[1][data[10]] = '';
				}
			};
			
			// ao_code
			$scope.ChangeAo_code = function(data) {
				if (data[1][data[8]] && data[1][data[8]] != '0') {
					data[1][data[2]] = $filter('filter')($scope["TOTAL_" + data[9]], {AO_CODE: data[1][data[8]]})[0].REGION_CENTER_ID;
					data[1][data[4]] = $filter('filter')($scope["TOTAL_" + data[9]], {AO_CODE: data[1][data[8]]})[0].BRANCH_AREA_ID;
					data[1][data[6]] = $filter('filter')($scope["TOTAL_" + data[9]], {AO_CODE: data[1][data[8]]})[0].BRANCH_NBR;
					data[1][data[10]] = $filter('filter')($scope["TOTAL_"+ data[9]], {AO_CODE: data[1][data[8]]})[0].EMP_ID;
				}
			};
			
			// emp_id
			$scope.ChangeEmp_id = function(data) {
				if (data[1][data[10]]) {
					data[1][data[2]] = $filter('filter')($scope[data[11]], {DATA: data[1][data[10]]})[0].REGION_CENTER_ID;
					data[1][data[4]] = $filter('filter')($scope[data[11]], {DATA: data[1][data[10]]})[0].BRANCH_AREA_ID;
					data[1][data[6]] = $filter('filter')($scope[data[11]], {DATA: data[1][data[10]]})[0].BRANCH_NBR;
					data[1][data[8]] = $filter('filter')($scope[data[11]], {DATA: data[1][data[10]]})[0].AO_CODE;
				}
			};
			
			return ALLdeferred.promise;
		};
}]);