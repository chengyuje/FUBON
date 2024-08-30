/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('ORG200_ADDController', function($scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
	$controller('BaseController', { $scope : $scope });
	$scope.controllerName = "ORG200_ADDController";
	
	
	$scope.mappingSet['newParentDeptLst'] = [{
		LABEL : $scope.inputVO.DEPT_NAME,
		DATA : $scope.inputVO.DEPT_ID
	}];
	
	$scope.newInputVO = {
		DEPT_ID : '',
		DEPT_NAME : '',
		ORG_TYPE : '',
		PARENT_DEPT_ID : $scope.inputVO.DEPT_ID, 
		PARENT_ORG_TYPE : $scope.inputVO.ORG_TYPE
	};
	
	$scope.closeORG200ADD = function() {
		$scope.org200add_dialog.close();
	};
	
	$scope.getNewOrgType = function() {
		if ($scope.newInputVO.PARENT_DEPT_ID == '') {
			$scope.newInputVO.ORG_TYPE = '00';
		} else {
			$scope.sendRecv("ORG200", "getOrgType", "com.systex.jbranch.app.server.fps.org200.ORG200InputVO", $scope.newInputVO, function(totas, isError) {
				if (isError) {
					$scope.showErrorMsgInDialog(totas.body.msgData);
					return;
				}
				if (totas.length > 0) {
					if (totas[0].body.deptDetail.length > 0) {
						var deptDetail = totas[0].body.deptDetail[0];
						console.log('deptDetail: ' + JSON.stringify(deptDetail));
						$scope.newInputVO.ORG_TYPE = deptDetail.ORG_TYPE;
					}
				}
			});
		}
	};
	$scope.getNewOrgType();
	
	$scope.addDeptDetail = function() {
		$confirm({text: '是否新增' + $scope.newInputVO.DEPT_ID + '-' + $scope.newInputVO.DEPT_NAME + '?'}, {size: '200px'}).then(function() {
			$scope.sendRecv("ORG200", "addDeptDetail", "com.systex.jbranch.app.server.fps.org200.ORG200InputVO", $scope.newInputVO, function(totas, isError) {
				if (isError) {
					$scope.showErrorMsg(totas[0].body.msgData);
					return;
				}
				if (totas.length > 0) {
				}
				if ($scope.selectedTreeNode == null || $scope.selectedTreeNode == undefined) {
					$scope.init();
				} else {
					console.log('SELECTED selectedTreeNode: ' + JSON.stringify($scope.selectedTreeNode));
					$scope.orgTree.removeChildNodes($scope.selectedTreeNode);
					$scope.orgClick(null, null, $scope.selectedTreeNode);
				}
				$scope.org200add_dialog.close();
			});
		});
	};
});
