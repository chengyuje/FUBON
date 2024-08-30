/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('ORG330Controller', function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $location, $anchorScroll, getParameter) {
	$controller('BaseController', { $scope : $scope, $location: $location, $anchorScroll: $anchorScroll });
	$scope.controllerName = "ORG330Controller";
	
	// filter
	getParameter.XML(["COMMON.YES_NO", "COMMON.REVIEW_STATUS", "COMMON.ACT_TYPE", "ORG.SYS_ROLE"], function(totas) {
		if (totas) {
			$scope.mappingSet['COMMON.YES_NO'] = totas.data[totas.key.indexOf('COMMON.YES_NO')];
			$scope.mappingSet['COMMON.REVIEW_STATUS'] = totas.data[totas.key.indexOf('COMMON.REVIEW_STATUS')];
			$scope.mappingSet['COMMON.ACT_TYPE'] = totas.data[totas.key.indexOf('COMMON.ACT_TYPE')];
			$scope.mappingSet['ORG.SYS_ROLE'] = totas.data[totas.key.indexOf('ORG.SYS_ROLE')];
		}
	});
    //
	
	$scope.btnAction = 'n';
	$scope.roleLst = [];

	$scope.delInputVO = {
		ROLE_ID        : ''
	};
	
	$scope.getRoleLst = function() {
		$scope.sendRecv("ORG330", "getRoleLst", "com.systex.jbranch.app.server.fps.org330.ORG330InputVO", $scope.inputVO, 
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.roleLst.length == 0) {
							$scope.showMsg("ehl_01_common_009");
		        			return;
		        		}
						
						$scope.roleLst = tota[0].body.roleLst;
						$scope.outputVO = tota[0].body;
						
//						angular.forEach($scope.roleLst, function(row, index, objs){
//							row.set = [];
//							row.set.push({LABEL: "修改", DATA: "m"});
//							row.set.push({LABEL: "刪除", DATA: "d"});
//							if (row.ACT_TYPE != 'A') {
//								row.set.push({LABEL: "異動歷程", DATA: "h"});
//							}
//						});
						return;
					}
				}
		);
	};
	
	$scope.init = function() {
		$scope.inputVO = {
				ROLE_ID: '',
				ROLE_NAME: '',
				JOB_TITLE_NAME: '',
				IS_AO: '', 
				
				REVIEW_STATUS: '', 
				SEQNO: '',
				
				AGENT_ROLE: '', 
				
				SYS_ROLE: ''
		};
		
		$scope.btnAction = 'n';
		$scope.getRoleLst();
	};
	$scope.init();
	
	$scope.doAction = function(row, action) {
		if (action == 'm') { // 新增
			$scope.btnAction = action;
			$scope.inputVO.ROLE_ID   = row.ROLE_ID;
			$scope.inputVO.ROLE_NAME = row.ROLE_NAME;
			$scope.inputVO.JOB_TITLE_NAME = row.JOB_TITLE_NAME;
			$scope.inputVO.IS_AO = row.IS_AO;
			$scope.inputVO.AGENT_ROLE = row.AGENT_ROLE;
			$scope.inputVO.SYS_ROLE = row.SYS_ROLE;

			$location.hash('top');
			$anchorScroll();
		} else if (action == 'd') { // 刪除
			$scope.btnAction = action;
			$confirm({text: '是否刪除 ' + row.ROLE_NAME + '角色'}, {size: 'sm'}).then(function() {
				$scope.delInputVO.ROLE_ID = row.ROLE_ID;
	        	$scope.sendRecv("ORG330", "delRole", "com.systex.jbranch.app.server.fps.org330.ORG330InputVO", $scope.delInputVO, 
	        			function(totas, isError) {
		        			if (isError) {
			                	$scope.showErrorMsgInDialog('ehl_01_common_005');
			                	return;
			                }
		        			if (totas.length > 0) {
		        				$scope.showMsg("ehl_01_common_019");
				       		}
		        			$scope.init();
			            }
	        	);
        	});
			
			action = '';
			$scope.btnAction = 'n';
		} else if (action == 'h') { // 異動歷程
			var dialog = ngDialog.open({
				template: 'assets/txn/ORG330/ORG330_REVIEW.html',
				className: 'ORG330',
				showClose: false,
				 controller: ['$scope', function($scope) {
                	$scope.ROLE_ID = row.ROLE_ID;
                }]
			});
		} else {
			$scope.clear();
		}
	};
	
	$scope.addOrModRole = function () {
		if ($scope.btnAction == 'n') {
			$confirm({text: '是否新增' + $scope.inputVO.ROLE_NAME + '角色'}, {size: 'sm'}).then(function() {
	        	$scope.sendRecv("ORG330", "addRole", "com.systex.jbranch.app.server.fps.org330.ORG330InputVO", $scope.inputVO, function(totas, isError) {
        			if (isError) {
	                	$scope.showErrorMsgInDialog(totas.body.msgData);
	                	return;
	                }
        			if (totas.length > 0) {
        				$scope.showMsg("ehl_01_common_019");
		       		}
        			$scope.init();
        		});
        	});
		}
		
		if ($scope.btnAction == 'm') {
			$confirm({text: '是否修改' + $scope.inputVO.ROLE_NAME + '角色'}, {size: 'sm'}).then(function() {
	        	$scope.sendRecv("ORG330", "updateRole", "com.systex.jbranch.app.server.fps.org330.ORG330InputVO", $scope.inputVO, function(totas, isError) {
        			if (isError) {
	                	$scope.showErrorMsgInDialog(totas.body.msgData);
	                	return;
	                }
        			if (totas.length > 0) {
        				$scope.showMsg("ehl_01_common_019");
		       		}
        			$scope.init();
        		});
        	});
		}
	};
	
	$scope.review = function (row, status) {
		$confirm({text: '是否' + ((status == 'N') ? '退回' : '核可') + row.ROLE_NAME + '角色'}, {size: 'sm'}).then(function() {
			$scope.sendRecv("ORG330", "review", "com.systex.jbranch.app.server.fps.org330.ORG330InputVO", {ROLE_ID: row.ROLE_ID, 
																										   REVIEW_STATUS: status, 
																										   ACT_TYPE: row.ACT_TYPE,
																										   SEQNO: row.SEQNO}, 
					function(totas, isError) {
						if (isError) {
			            	$scope.showErrorMsgInDialog(totas.body.msgData);
			            	return;
			            }
						if (totas.length > 0) {
							$scope.showMsg((status == 'N') ? "ehl_01_common_020" : "ehl_01_common_021");
			       		}
						$scope.init();
					}
			);
		});
	}
	
	$scope.showORG330AGENT = function(row) {
		if (row == null && $scope.btnAction == 'm') {
			$scope.selRow = $scope.inputVO;
			$scope.ROLE_ID = $scope.inputVO.ROLE_ID;
			$scope.ROLE_NAME = $scope.inputVO.ROLE_NAME;
			$scope.ACT = "SELECT";
		} else if (row != null && $scope.btnAction == 'n') {
			$scope.selRow = row;
			$scope.ROLE_ID = row.ROLE_ID;
			$scope.ROLE_NAME = row.ROLE_NAME;
			$scope.ACT = "READ"; 
		} else { // 新增
			$scope.selRow = null;
			$scope.ROLE_ID = $scope.inputVO.ROLE_ID;
			$scope.ROLE_NAME = $scope.inputVO.ROLE_NAME;
			$scope.lstRoleR = null;
			$scope.ACT = "SELECT"; 
		}

		var dialog = ngDialog.open({
			template: 'assets/txn/ORG330/ORG330AGENT.html',
			className: 'ORG330',
			showClose: false,
			scope : $scope
		}).closePromise.then(function (data) {
			if (data.value != 'cancel') {
				$scope.inputVO.AGENT_ROLE = data.value;
			}
		});
	};
	
	$scope.checkDP = function () {
		if ($scope.btnAction == 'm') {
			
		} else {
			angular.forEach($scope.roleLst, function(row, index, objs){
				if (row.ROLE_ID == $scope.inputVO.ROLE_ID) {
					var list = [$scope.inputVO.ROLE_ID];
					$scope.showErrorMsg("ehl_01_org330_002", list);
					$scope.inputVO.ROLE_ID = "";
				}
			});
		}
	}
});
