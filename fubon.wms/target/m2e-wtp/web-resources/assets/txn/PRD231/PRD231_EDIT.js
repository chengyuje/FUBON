/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD231_EDITController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD231_EDITController";
		
		$scope.sendRecv("PRD231", "getGlobal", "com.systex.jbranch.app.server.fps.prd231.PRD231InputVO", {},
				function(tota, isError) {
					if (!isError) {
						$scope.mappingSet['CID'] = [];
						angular.forEach(tota[0].body.resultList, function(row) {
							$scope.mappingSet['CID'].push({LABEL: row.GLOBAL_LIPPER_CID, DATA: row.GLOBAL_ID});
	        			});
						return;
					}
		});
		
		$scope.getEID = function() {
			if($scope.inputVO.global_id) {
				$scope.sendRecv("PRD231", "getEID", "com.systex.jbranch.app.server.fps.prd231.PRD231InputVO", $scope.inputVO,
						function(tota, isError) {
							if (!isError) {
								$scope.eid = tota[0].body.eid;
								$scope.ifExist = tota[0].body.ifExist;
								return;
							}
				});
			} else {
				$scope.eid = "";
				$scope.ifExist = null;
			}	
		};
		
		$scope.init = function() {
			if($scope.row) {
				$scope.isUpdate = true;
				$scope.inputVO = {
					mkt_tier1: $scope.row.MKT_TIER1,
					mkt_tier2: $scope.row.MKT_TIER2,
					mkt_tier3: $scope.row.MKT_TIER3,
					global_id: $scope.row.GLOBAL_ID
				};
				$scope.sendRecv("PRD110", "getArea", "com.systex.jbranch.app.server.fps.prd110.PRD110InputVO", {'fund_type':$scope.inputVO.mkt_tier1},
						function(tota, isError) {
							if (!isError) {
								$scope.mappingSet['INV_AREA'] = [];
								angular.forEach(tota[0].body.resultList, function(row) {
									$scope.mappingSet['INV_AREA'].push({LABEL: row.PARAM_NAME, DATA: row.NEXT_TIER_VALUE});
			        			});
								return;
							}
				});
				$scope.sendRecv("PRD110", "getTarget", "com.systex.jbranch.app.server.fps.prd110.PRD110InputVO", {'inv_area':$scope.inputVO.mkt_tier2},
						function(tota, isError) {
							if (!isError) {
								$scope.mappingSet['INV_TARGET'] = [];
								angular.forEach(tota[0].body.resultList, function(row) {
									$scope.mappingSet['INV_TARGET'].push({LABEL: row.PARAM_NAME, DATA: row.NEXT_TIER_VALUE});
			        			});
								return;
							}
				});
				$scope.getEID();
			} else
				$scope.inputVO = {};
		};
		$scope.init();
		
		$scope.getArea = function() {
			if($scope.inputVO.mkt_tier1) {
				$scope.inputVO.mkt_tier3 = "";
				$scope.mappingSet['INV_TARGET'] = [];
				$scope.sendRecv("PRD110", "getArea", "com.systex.jbranch.app.server.fps.prd110.PRD110InputVO", {'fund_type':$scope.inputVO.mkt_tier1},
						function(tota, isError) {
							if (!isError) {
								$scope.mappingSet['INV_AREA'] = [];
								angular.forEach(tota[0].body.resultList, function(row) {
									$scope.mappingSet['INV_AREA'].push({LABEL: row.PARAM_NAME, DATA: row.NEXT_TIER_VALUE});
			        			});
								$scope.inputVO.mkt_tier2 = "";
								return;
							}
				});
			} else {
				$scope.inputVO.mkt_tier2 = "";
				$scope.inputVO.mkt_tier3 = "";
				$scope.mappingSet['INV_AREA'] = [];
				$scope.mappingSet['INV_TARGET'] = [];
			}
		};
		
		$scope.getTarget = function() {
			if($scope.inputVO.mkt_tier2) {
				$scope.sendRecv("PRD110", "getTarget", "com.systex.jbranch.app.server.fps.prd110.PRD110InputVO", {'inv_area':$scope.inputVO.mkt_tier2},
						function(tota, isError) {
							if (!isError) {
								$scope.mappingSet['INV_TARGET'] = [];
								angular.forEach(tota[0].body.resultList, function(row) {
									$scope.mappingSet['INV_TARGET'].push({LABEL: row.PARAM_NAME, DATA: row.NEXT_TIER_VALUE});
			        			});
								$scope.inputVO.mkt_tier3 = "";
								return;
							}
				});
			} else {
				$scope.inputVO.mkt_tier3 = "";
				$scope.mappingSet['INV_TARGET'] = [];
			}
		};
		
		$scope.save = function() {
			if($scope.parameterTypeEditForm.$invalid){
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
			$scope.sendRecv("PRD231", "modify", "com.systex.jbranch.app.server.fps.prd231.PRD231InputVO", $scope.inputVO,
					function(totas, isError) {
	                	if (isError) {
	                		$scope.showErrorMsg(totas[0].body.msgData);
	                	}
	                	if (totas.length > 0) {
	                		$scope.showSuccessMsg('ehl_01_common_004');
                    		$scope.closeThisDialog('successful');
	                	};
					}
			);
		};
		
		
});