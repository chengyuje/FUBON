/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD260Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD260Controller";
		
		// date picker
		$scope.last_sDateOptions = {};
		$scope.last_eDateOptions = {};
		// config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.last_sDateOptions.maxDate = $scope.inputVO.last_eDate;
			$scope.last_eDateOptions.minDate = $scope.inputVO.last_sDate;
			if($scope.inputVO.last_eDate < $scope.last_eDateOptions.minDate)
				$scope.inputVO.last_eDate = "";
		};
		// date picker end
		
		// init
		$scope.init = function() {
			$scope.inputVO = {};
			$scope.inputVO.ptype = $scope.connector('get','PRD260_ptype');
			$scope.checkVO = {};
			$scope.conDis = false;
			$scope.limitDate();
		};
		$scope.init();
		$scope.inquireInit = function() {
			$scope.roleList = [];
			$scope.outputVO = {};
		}
		$scope.inquireInit();
		
		// === 區域中心、營運區、分行 連動
		$scope.regionDisabled = false;
		$scope.areaDisabled = true;
		$scope.branchDisabled = true;
		$scope.REGION_LIST = [];
		angular.forEach(projInfoService.getAvailRegion(), function(row) {
			$scope.REGION_LIST.push({LABEL: row.REGION_CENTER_NAME, DATA: row.REGION_CENTER_ID});
		});
		
		$scope.getArea = function() {
			$scope.AREA_LIST = [];
			$scope.inputVO.area = '';
			$scope.BRANCH_LIST = [];
			$scope.inputVO.branch = '';
			angular.forEach(projInfoService.getAvailArea(), function(row, index, objs){
				if(row.REGION_CENTER_ID == $scope.inputVO.region)
					$scope.AREA_LIST.push({LABEL: row.BRANCH_AREA_NAME, DATA: row.BRANCH_AREA_ID});
			});
			if($scope.inputVO.region)
				$scope.areaDisabled = false;
			else
				$scope.areaDisabled = true;
    		$scope.branchDisabled = true;
        };
        
		$scope.getBranch = function() {
			$scope.BRANCH_LIST = [];
			$scope.inputVO.branch = '';
			angular.forEach(projInfoService.getAvailBranch(), function(row, index, objs){
				if(row.BRANCH_AREA_ID == $scope.inputVO.area)
					$scope.BRANCH_LIST.push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
			});
			if($scope.inputVO.area)
				$scope.branchDisabled = false;
			else
				$scope.branchDisabled = true;
        };
		//
		
		// inquire
		$scope.currUser = projInfoService.getUserID();
		$scope.inquire = function() {
			// toUpperCase
			if($scope.inputVO.prd_id)
				$scope.inputVO.prd_id = $scope.inputVO.prd_id.toUpperCase();
			if($scope.inputVO.cust_id)
				$scope.inputVO.cust_id = $scope.inputVO.cust_id.toUpperCase();
			$scope.sendRecv("PRD260", "inquire", "com.systex.jbranch.app.server.fps.prd260.PRD260InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.roleList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;
							$scope.conDis = false;
							// follow mao151
							$scope.connector('set','MAO151_PARAMS',undefined);
							angular.forEach($scope.roleList, function(row, index, objs){
								if(row.REVIEW_STATUS == 'W' && row.CREATOR != $scope.currUser)
									$scope.conDis = true;
								row.finIndex = index + 1;
							});
							return;
						}
			});
		};
		
		// follow prd230
		if($scope.connector('get','MAO151_PARAMS')!=undefined) {
			if($scope.connector('get','MAO151_PARAMS').PAGE == 'CUSTREVIEW') {
				$scope.inputVO.prd_id = $scope.connector('get','MAO151_PARAMS').PRD_ID;
				$scope.inquire();
			}
		}
		
		$scope.checkrow = function() {
			if ($scope.checkVO.clickAll) {
        		angular.forEach($scope.data, function(row) {
        			if(row.REVIEW_STATUS == 'W' && row.CREATOR != $scope.currUser)
        				row.SELECTED = true;
    			});
        	} else {
        		angular.forEach($scope.data, function(row) {
        			if(row.REVIEW_STATUS == 'W' && row.CREATOR != $scope.currUser)
        				row.SELECTED = false;
    			});
        	}
        };
		
		$scope.review = function (status) {
			// get select
			var ans = $scope.roleList.filter(function(obj) {
	    		return (obj.SELECTED == true);
	    	});
        	if(ans.length == 0){
        		return;
        	}
			$confirm({text: '是否' + ((status == 'N') ? '退回' : '核可')}, {size: 'sm'}).then(function() {
				$scope.sendRecv("PRD260", "review", "com.systex.jbranch.app.server.fps.prd260.PRD260InputVO", {'review_list': ans,'ptype':$scope.inputVO.ptype,'status': status},
						function(tota, isError) {
							if (isError) {
		                		$scope.showErrorMsg(tota[0].body.msgData);
		                	}
		                	if (tota.length > 0) {
		                		$scope.showMsg((status == 'N') ? "ehl_01_common_020" : "ehl_01_common_021");
		                	};
		                	$scope.inquireInit();
							$scope.inquire();
				});
			});
		};
		
		$scope.deleteR = function(row) {
			$confirm({text: '是否刪除此筆資料!!'}, {size: 'sm'}).then(function() {
				$scope.sendRecv("PRD260", "deleteData", "com.systex.jbranch.app.server.fps.prd260.PRD260InputVO", {'ptype': $scope.inputVO.ptype,'prd_id': row.PRD_ID,'cust_id':row.CUST_ID,'region':row.REGION_CENTER_ID,'area':row.BRANCH_AREA_ID,'branch':row.BRANCH_NBR},
        				function(totas, isError) {
                        	if (isError) {
                        		$scope.showErrorMsg(totas[0].body.msgData);
                        	}
                        	if (totas.length > 0) {
                        		$scope.showSuccessMsg('刪除成功');
                        		$scope.inquireInit();
                        		$scope.inquire();
                        	};
        				}
        		);
			});
		};
		
		$scope.edit = function () {
			var ptype = $scope.inputVO.ptype;
			var dialog = ngDialog.open({
				template: 'assets/txn/PRD260/PRD260_EDIT.html',
				className: 'PRD260',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.ptype = ptype;
                }]
			});
			dialog.closePromise.then(function (data) {
				if(data.value === 'successful'){
					$scope.inquireInit();
					$scope.inquire();
				}
			});
		};
		
		
});