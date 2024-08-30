/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD260_EDITController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD260_EDITController";
		
		$scope.init = function() {
			$scope.inputVO = {};
			$scope.inputVO.ptype = $scope.ptype;
		};
		$scope.init();
		
		// === 區域中心、營運區、分行 連動
		$scope.regionDisabled = false;
		$scope.areaDisabled = true;
		$scope.branchDisabled = true;
		
		$scope.REGION_LIST = [];
		angular.forEach(projInfoService.getAvailRegion(), function(row) {
			$scope.REGION_LIST.push({LABEL: row.REGION_CENTER_NAME, DATA: row.REGION_CENTER_ID});
		});
		
		$scope.getArea = function() {
			if($scope.inputVO.region)
				$scope.areaDisabled = false;
			else
				$scope.areaDisabled = true;
			$scope.branchDisabled = true;
			if($scope.inputVO.area)
				$scope.inputVO.area = '';
			else
				$scope.checkID('box');
			
			$scope.AREA_LIST = [];
			angular.forEach(projInfoService.getAvailArea(), function(row) {
				if(row.REGION_CENTER_ID == $scope.inputVO.region)
					$scope.AREA_LIST.push({LABEL: row.BRANCH_AREA_NAME, DATA: row.BRANCH_AREA_ID});
			});
        };
        
		$scope.getBranch = function() {
			if($scope.inputVO.area)
				$scope.branchDisabled = false;
			else
				$scope.branchDisabled = true;
			
			$scope.inputVO.branch = '';
			$scope.BRANCH_LIST = [];
			angular.forEach(projInfoService.getAvailBranch(), function(row) {
				if(row.BRANCH_AREA_ID == $scope.inputVO.area)
					$scope.BRANCH_LIST.push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
			});
			$scope.checkID('box');
        };
		//
		
		$scope.checkID = function(type) {
			if($scope.inputVO.prd_id) {
				// check
				if(type && type == 'text') {
					if($scope.inputVO.cust_id)
						$scope.inputVO.region = '';
				} else if(type && type == 'box') {
					if($scope.inputVO.region) {
						$scope.inputVO.cust_id = '';
						$scope.ifCUST = false;
					}
				} else {
					if($scope.inputVO.cust_id)
						$scope.inputVO.region = '';
				}
				//
				$scope.sendRecv("PRD260", "checkID", "com.systex.jbranch.app.server.fps.prd260.PRD260InputVO", $scope.inputVO,
						function(tota, isError) {
							if (!isError) {
								$scope.inputVO.cname = tota[0].body.cname;
								$scope.canEdit = tota[0].body.canEdit;
								$scope.errMsg = tota[0].body.errorMsg;
								$scope.errMsgList = tota[0].body.resultList;
							}
				});
			}
		};
		
		
		
		$scope.clearOther = function(type) {
			
		};
		
		$scope.checkCUST = function() {
			$scope.sendRecv("PRD260", "checkCUST", "com.systex.jbranch.app.server.fps.prd260.PRD260InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.ifCUST = tota[0].body.canEdit;
							return;
						}
			});
		};
		
		$scope.save = function() {
			if($scope.parameterTypeEditForm.$invalid) {
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
			if(!$scope.canEdit) {
				$scope.showErrorMsg($scope.errMsg, [$scope.errMsgList]);
        		return;
			}
			if($scope.ifCUST) {
				$scope.showErrorMsg('ehl_01_prd260_001');
        		return;
			}
			$scope.sendRecv("PRD260", "addData", "com.systex.jbranch.app.server.fps.prd260.PRD260InputVO", $scope.inputVO,
					function(tota, isError) {
						if (isError) {
	                		$scope.showErrorMsg(tota[0].body.msgData);
	                	}
	                	if (tota.length > 0) {
	                		$scope.showSuccessMsg('ehl_01_common_004');
	                		$scope.closeThisDialog('successful');
	                	};
			});
		};
		
		
		
});