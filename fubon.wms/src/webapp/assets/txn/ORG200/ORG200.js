/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('ORG200Controller', function($scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
	$controller('BaseController', {
		$scope : $scope
	});
	$scope.controllerName = "ORG200Controller";

	$scope.orgTree = null;
	$scope.selectedTreeNode = null;
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

	$scope.mappingSet['parentDeptLst'] = [];
	
	$scope.inputVO = {
		DEPT_ID : '',
		OLD_DEPT_ID : '',
		DEPT_NAME : '',
		ORG_TYPE : '',
		PARENT_DEPT_ID : '', 
		PARENT_ORG_TYPE : ''
	};
	
	$scope.clear = function() {
		$scope.inputVO.DEPT_ID = '';
		$scope.inputVO.OLD_DEPT_ID = '';
		$scope.inputVO.DEPT_NAME = '';
		$scope.inputVO.ORG_TYPE = '';
		$scope.inputVO.PARENT_DEPT_ID = '';
		$scope.inputVO.PARENT_ORG_TYPE = '';
	};

	$scope.orgClick = function(event, treeId, treeNode) {
		$scope.clear();
		$scope.selectedTreeNode = treeNode;
		$scope.inputVO.OLD_DEPT_ID = treeNode.id;
		$scope.inputVO.DEPT_ID = treeNode.id;
		if (treeNode.isParent) {
			$scope.orgTree.removeChildNodes(treeNode);
		} else {
			$scope.sendRecv("ORG200", "getSubDeptLst", "com.systex.jbranch.app.server.fps.org200.ORG200InputVO", $scope.inputVO, function(totas, isError) {
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
					console.log(JSON.stringify(newNodes));
					$scope.orgTree.addNodes(treeNode, newNodes);
				}
			});
		}
		$scope.getParentLst();		
		$scope.getDeptDetail();
	};

	$scope.init = function() {
		$scope.clear();
		var zNodes = [];
		$scope.sendRecv("ORG200", "getTopMostDept", "com.systex.jbranch.app.server.fps.org200.ORG200InputVO", $scope.inputVO, function(totas, isError) {
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
				$scope.orgTree = $.fn.zTree.init($("#orgTree"), setting, zNodes);
			}
		});
	};
	$scope.init();
	
	$scope.modDeptDetail = function() {
		$confirm({text: '是否修改' + $scope.inputVO.DEPT_ID + '-' + $scope.inputVO.DEPT_NAME + '?'}, {size: '200px'}).then(function() {
			$scope.sendRecv("ORG200", "modDeptDetail", "com.systex.jbranch.app.server.fps.org200.ORG200InputVO", $scope.inputVO, function(totas, isError) {
				if (isError) {
					$scope.showErrorMsgInDialog(totas.body.msgData);
					return;
				}
				if (totas.length > 0) {
				}
				if ($scope.selectedTreeNode.getParentNode() == null || $scope.selectedTreeNode.getParentNode() == undefined) {
					$scope.init();
					$scope.selectedTreeNode = null;
				} else {
					$scope.orgTree.removeChildNodes($scope.selectedTreeNode.getParentNode());
					$scope.orgClick(null, null, $scope.selectedTreeNode.getParentNode());
				}
			});
		});
	};
	
	$scope.getDeptDetail = function() {
		$scope.sendRecv("ORG200", "getDeptDetail", "com.systex.jbranch.app.server.fps.org200.ORG200InputVO", $scope.inputVO, function(totas, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(totas.body.msgData);
				return;
			}
			if (totas.length > 0) {
				var deptDetail = totas[0].body.deptDetail[0];
				console.log('deptDetail: ' + JSON.stringify(deptDetail));
				$scope.inputVO.DEPT_ID = deptDetail.DEPT_ID;
				$scope.inputVO.DEPT_NAME = deptDetail.DEPT_NAME;
				$scope.inputVO.ORG_TYPE = deptDetail.ORG_TYPE;
				$scope.inputVO.PARENT_DEPT_ID = deptDetail.PARENT_DEPT_ID;
				$scope.inputVO.PARENT_ORG_TYPE = deptDetail.PARENT_ORG_TYPE;
			}
		});
	};
	
	$scope.getParentLst = function() {
		$scope.sendRecv("ORG200", "getParentLst", "com.systex.jbranch.app.server.fps.org200.ORG200InputVO", $scope.inputVO, function(totas, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(totas.body.msgData);
				return;
			}
			if (totas.length > 0) {
				$scope.mappingSet['parentDeptLst'] = [];
				parentLst = totas[0].body.parentLst;
				console.log('parentLst: ' + JSON.stringify(parentLst));
				for (var i = 0; i < parentLst.length; i++) {
					console.log('parentLst LABEL: ' + parentLst[i].DEPT_NAME + 'parentLst DATA: ' + parentLst[i].DEPT_ID);
					$scope.mappingSet['parentDeptLst'].push({
						LABEL : parentLst[i].DEPT_NAME,
	    				DATA : parentLst[i].DEPT_ID
					});
				}
			}
		});
	};
	
//	$scope.getOrgType = function() {
//		$scope.sendRecv("ORG200", "getOrgType", "com.systex.jbranch.app.server.fps.org200.ORG200InputVO", $scope.inputVO, function(totas, isError) {
//			if (isError) {
//				$scope.showErrorMsgInDialog(totas.body.msgData);
//				return;
//			}
//			if (totas.length > 0) {
//				if (totas[0].body.deptDetail.length > 0) {
//					var deptDetail = totas[0].body.deptDetail[0];
//					console.log('deptDetail: ' + JSON.stringify(deptDetail));
//					$scope.inputVO.ORG_TYPE = deptDetail.ORG_TYPE;
//				}
//			}
//		});
//	};
	
	$scope.delDept = function() {
		$confirm({text: '是否刪除' + $scope.inputVO.DEPT_ID + '-' + $scope.inputVO.DEPT_NAME + '？警告！除非組織已消滅，才可執行刪除作業，若只是營運區或單位組織異動，請直接於父層級欄位進行修改。'}, {size: '200px'}).then(function() {
			$scope.sendRecv("ORG200", "getSubDeptLst", "com.systex.jbranch.app.server.fps.org200.ORG200InputVO", $scope.inputVO, function(totas, isError) {
				if (isError) {
					$scope.showErrorMsgInDialog(totas.body.msgData);
					return;
				}
				if (totas.length > 0) {
					var subDeptLst = totas[0].body.subDeptLst;
					if (subDeptLst.length == 0) {
						$scope.sendRecv("ORG200", "delDept", "com.systex.jbranch.app.server.fps.org200.ORG200InputVO", $scope.inputVO, function(totas, isError) {
							if (isError) {
								$scope.showErrorMsgInDialog(totas.body.msgData);
								return;
							}
							if (totas.length > 0) {
							}
							if ($scope.selectedTreeNode.getParentNode() == null || $scope.selectedTreeNode.getParentNode() == undefined) {
								$scope.init();
								$scope.selectedTreeNode = null;
							} else {
								$scope.orgTree.removeChildNodes($scope.selectedTreeNode.getParentNode());
								$scope.orgClick(null, null, $scope.selectedTreeNode.getParentNode());
							}
						});
					} else {
						$scope.showErrorMsgInDialog("仍有子類型");
						return;
					}
				}
			});
		});
	};
	
	$scope.showORG200ADD = function() {
		$scope.org200add_dialog = ngDialog
		.open({
			template: 'assets/txn/ORG200/ORG200ADD.html',
			className : 'ORG200',
			showClose : false,
			closeByDocument : true,
			scope : $scope
		});
	};
	
});
