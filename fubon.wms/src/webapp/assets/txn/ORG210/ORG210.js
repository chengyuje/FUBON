/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('ORG210Controller', function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "ORG210Controller";

	var orgTree = null;
	var setting = {
		data : {
			simpleData : {
				enable : true
			}
		},
		callback : {
			onClick : function(event, treeId, treeNode) {
				$scope.orgClick(event, treeId, treeNode)
			}
		},
		view : {
			dblClickExpand : false
		}
	};
	var zNodes = [];
	var parentLst = {};

	$scope.deptEmpLst = [];
	$scope.empGroupLst = [];
	$scope.mappingSet['parentDeptLst'] = [];
	$scope.mappingSet['deptGroup'] = [];
	
	$scope.inputVO = {
		DEPT_ID : '',
		DEPT_NAME : '',
		ORG_TYPE : '',
		PARENT_DEPT_ID : '',
		DEPT_DEGREE : '',
		DEPT_GROUP : '',
		EMP_GROUP_LST : [],
		DEL_MEMBER_LST : []
	};
	
	$scope.clear = function() {
		$scope.inputVO.DEPT_ID = '';
		$scope.inputVO.DEPT_NAME = '';
		$scope.inputVO.ORG_TYPE = '';
		$scope.inputVO.PARENT_DEPT_ID = '';
		$scope.inputVO.DEPT_DEGREE = '';
		$scope.inputVO.DEPT_GROUP = '';
		$scope.inputVO.EMP_GROUP_LST = [];
		$scope.inputVO.DEL_MEMBER_LST = [];
		$scope.deptEmpLst = [];
		$scope.empGroupLst = [];
	};

	$scope.orgClick = function(event, treeId, treeNode) {
		$scope.clear();
		$scope.inputVO.DEPT_ID = treeNode.id;
		if (treeNode.isParent) {
			orgTree.removeChildNodes(treeNode);
		} else {
			$scope.sendRecv("ORG210", "getSubDeptLst", "com.systex.jbranch.app.server.fps.org210.ORG210InputVO", $scope.inputVO, function(totas, isError) {
				if (isError) {
					$scope.showErrorMsgInDialog(totas.body.msgData);
					return;
				}
				if (totas.length > 0) {
					var subDeptLst = totas[0].body.subDeptLst;
					var newNodes = [];
					for (var i = 0; i < subDeptLst.length; i++) {
						newNodes.push({
							'id' : subDeptLst[i].DEPT_ID,
							'pId' : subDeptLst[i].PARENT_DEPT_ID,
							'name' : subDeptLst[i].DEPT_NAME
						});
					}
					orgTree.addNodes(treeNode, newNodes);
				}
			});
		}
		$scope.getParentLst();		
		$scope.getDeptDetail();
	};

	$scope.modDept = function() {
		if ($scope.inputVO.DEPT_ID.trim().length == 0) {
			$scope.showErrorMsgInDialog("ehl_01_org210_002");
			return;
		}
		
		angular.forEach($scope.deptEmpLst, function(deptEmp, key){
			angular.forEach($scope.empGroupLst, function(empGroup, key) {
				if (deptEmp.EMP_ID == empGroup.EMP_ID && deptEmp.GROUP_TYPE != empGroup.GROUP_TYPE) {
					$scope.inputVO.EMP_GROUP_LST.push(deptEmp);
				}
			});
		});
		
		$confirm({text: '是否修改' + $scope.inputVO.DEPT_ID + '-' + $scope.inputVO.DEPT_NAME + '的資料?'}, {size: '200px'}).then(function() {
			$scope.sendRecv("ORG210", "modDept", "com.systex.jbranch.app.server.fps.org210.ORG210InputVO", $scope.inputVO, function(totas, isError) {
				if (isError) {
					$scope.showErrorMsgInDialog(totas.body.msgData);
					return;
				}
				if (totas.length > 0) {
					$scope.showMsg("ehl_01_common_002");
				}
			});
		});
	};
	
	$scope.getDeptDetail = function() {
		$scope.sendRecv("ORG210", "getDeptDetail", "com.systex.jbranch.app.server.fps.org210.ORG210InputVO", $scope.inputVO, function(totas, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(totas.body.msgData);
				return;
			}
			if (totas.length > 0) {
				var deptDetail = totas[0].body.deptDetail[0];
				
				$scope.inputVO.DEPT_ID = deptDetail.DEPT_ID;
				$scope.inputVO.DEPT_NAME = deptDetail.DEPT_NAME;
				$scope.inputVO.ORG_TYPE = deptDetail.ORG_TYPE;
				$scope.inputVO.PARENT_DEPT_ID = deptDetail.PARENT_DEPT_ID;
				$scope.inputVO.DEPT_DEGREE = deptDetail.DEPT_DEGREE;
				$scope.inputVO.DEPT_GROUP = deptDetail.DEPT_GROUP;
				
				$scope.deptEmpLst = totas[0].body.deptEmpLst;
				angular.copy(totas[0].body.deptEmpLst, $scope.empGroupLst);
				
				$scope.getOrgType();
			}
		});
	};
	
	$scope.getParentLst = function() {
		$scope.sendRecv("ORG210", "getParentLst", "com.systex.jbranch.app.server.fps.org210.ORG210InputVO", $scope.inputVO, function(totas, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(totas.body.msgData);
				return;
			}
			if (totas.length > 0) {
				$scope.mappingSet['parentDeptLst'] = [];
				parentLst = totas[0].body.parentLst;
				console.log('parentLst: ' + JSON.stringify(parentLst));
				for (var i = 0; i < parentLst.length; i++) {
					$scope.mappingSet['parentDeptLst'].push({
						LABEL : parentLst[i].DEPT_NAME,
	    				DATA : parentLst[i].DEPT_ID
					});
				}
			}
		});
	};
	
	$scope.getOrgType = function() {
		$scope.sendRecv("ORG210", "getOrgType", "com.systex.jbranch.app.server.fps.org210.ORG210InputVO", $scope.inputVO, function(totas, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(totas.body.msgData);
				return;
			}
			if (totas.length > 0) {
				if (totas[0].body.deptDetail.length > 0) {
					var deptDetail = totas[0].body.deptDetail[0];
					$scope.inputVO.ORG_TYPE = deptDetail.ORG_TYPE;
				}
			}
		});

		if ($scope.inputVO.ORG_TYPE == '40') {
			$scope.mappingSet['deptGroup'] = projInfoService.mappingSet['ORG.BRANCH_AREA_GROUP']; 
		} else if ($scope.inputVO.ORG_TYPE == '50') {
			$scope.mappingSet['deptGroup'] = projInfoService.mappingSet['ORG.BRANCH_GROUP'];
		} else {
			$scope.mappingSet['deptGroup'] = [];
		}
	};
	
	$scope.delDeptMember = function() {
		var confirmMemLst = [];
		$scope.inputVO.DEL_MEMBER_LST = [];
		angular.forEach($scope.deptEmpLst, function(deptEmp, key){
			if (deptEmp.DEL) {
				$scope.inputVO.DEL_MEMBER_LST.push(deptEmp);
				confirmMemLst.push(' ' + deptEmp.EMP_ID + '-' + deptEmp.EMP_NAME + ' ');
			}
		});
		if ($scope.inputVO.DEL_MEMBER_LST.length == 0) {
			$scope.showErrorMsgInDialog("ehl_01_org210_001");
			return;
		}

		$confirm({text: '是否從' + $scope.inputVO.DEPT_ID + '-' + $scope.inputVO.DEPT_NAME + '刪除' + JSON.stringify(confirmMemLst)}, {size: '200px'}).then(function() {
			$scope.sendRecv("ORG210", "delDeptMember", "com.systex.jbranch.app.server.fps.org210.ORG210InputVO", $scope.inputVO, function(totas, isError) {
				if (isError) {
					$scope.showErrorMsgInDialog(totas.body.msgData);
					return;
				}
				if (totas.length > 0) {
					$scope.showMsg("ehl_01_common_003");
				}
				$scope.getDeptDetail();
			});
		});
	};
	
	$scope.showORG210ADD = function() {
		
		if ($scope.inputVO.DEPT_ID.trim().length == 0) {
			$scope.showErrorMsgInDialog("ehl_01_org210_002");
			return;
		}
		
		$scope.org210add_dialog = ngDialog.open({
			template: 'assets/txn/ORG210/ORG210ADD.html',
			className : 'ORG210',
			showClose : false,
			closeByDocument : true,
			scope : $scope
		});
	};
	
	$scope.init = function() {
		
		$scope.clear();
		
		var zNodes = [];
		$scope.sendRecv("ORG210", "getTopMostDept", "com.systex.jbranch.app.server.fps.org210.ORG210InputVO", $scope.inputVO, function(totas, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(totas.body.msgData);
				return;
			}
			if (totas.length > 0) {
				var parentLst = totas[0].body.parentLst;

				for (var i = 0; i < parentLst.length; i++) {
					zNodes.push({
						'id' : parentLst[i].DEPT_ID,
						'pId' : parentLst[i].PARENT_DEPT_ID,
						'name' : parentLst[i].DEPT_NAME
					});
				}
				orgTree = $.fn.zTree.init($("#orgTree"), setting, zNodes);
			}
		});
		
		var vo = {'param_type': 'ORG.BRANCH_AREA_GROUP', 'desc': false};
		$scope.requestComboBox(vo, function(totas) {
    		if (totas[totas.length - 1].body.result === 'success') {
    			projInfoService.mappingSet['ORG.BRANCH_AREA_GROUP'] = totas[0].body.result;
    		}
    	});
		
		vo = {'param_type': 'ORG.BRANCH_GROUP', 'desc': false};
		$scope.requestComboBox(vo, function(totas) {
    		if (totas[totas.length - 1].body.result === 'success') {
    			projInfoService.mappingSet['ORG.BRANCH_GROUP'] = totas[0].body.result;
    		}
    	});
		
		$scope.tnsfData = $scope.connector('get', 'ORG110_tnsfData');
		if ($scope.tnsfData != undefined && $scope.tnsfData.BRANCH_NBR != undefined && $scope.tnsfData.BRANCH_NBR != '' && $scope.tnsfData.BRANCH_NBR != null) {
			$scope.inputVO.DEPT_ID = $scope.tnsfData.BRANCH_NBR;
			$scope.getParentLst();
			$scope.getDeptDetail();
			$scope.tnsfData.BRANCH_NBR = null;
			$scope.connector('set', 'ORG110_tnsfData', $scope.tnsfData);
		}
	};
	
	$scope.init();
	
	$scope.export = function() {
		$scope.inputVO.exportList = $scope.deptEmpLst;
		$scope.sendRecv("ORG210", "export", "com.systex.jbranch.app.server.fps.org210.ORG210InputVO", $scope.inputVO, 
				function(tota, isError) {
					if (isError) {
						$scope.showErrorMsgInDialog(tota.body.msgData);
						return;
					}
					if (tota.length > 0) {
					}
				}
		);
	};
	
	$scope.reBack = function() {
		$scope.connector('set', 'ORG110_queryCondition', $scope.connector('get', 'ORG110_queryCondition'));
		$rootScope.menuItemInfo.url = "assets/txn/ORG110/ORG110.html";
	}
});
