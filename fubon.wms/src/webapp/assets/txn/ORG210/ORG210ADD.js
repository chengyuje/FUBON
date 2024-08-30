/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('ORG210_ADDController', function($scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
	$controller('BaseController', {
		$scope : $scope
	});
	$scope.controllerName = "ORG210_ADDController";
	
	$scope.mappingSet['newParentDeptLst'] = [{
		LABEL : $scope.inputVO.DEPT_NAME,
		DATA : $scope.inputVO.DEPT_ID
	}];
	
	$scope.empWODeptLst = [];
	$scope.mappingSet['ridLst'] = [];
	
	$scope.newInputVO = {
		DEPT_ID   : $scope.inputVO.DEPT_ID,
		DEPT_NAME : $scope.inputVO.DEPT_NAME,
		ROLE_ID   : '',
		EMP_ID    : '',
		EMP_NAME  : '',
		AO_CODE   : '',
		ADD_MEMBER_LST : []
	};
	
	$scope.closeORG210ADD = function() {
		$scope.org210add_dialog.close();
	};
	
	$scope.getNewOrgType = function() {
		if ($scope.newInputVO.PARENT_DEPT_ID == '') {
			$scope.newInputVO.ORG_TYPE = '20';
		} else {
			$scope.sendRecv("ORG210", "getOrgType", "com.systex.jbranch.app.server.fps.org210.ORG210InputVO", $scope.newInputVO, function(totas, isError) {
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
	
	$scope.getEmpWODept = function() {
		$scope.sendRecv("ORG210", "getEmpWODept", "com.systex.jbranch.app.server.fps.org210.ORG210InputVO", $scope.newInputVO, function(totas, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(totas.body.msgData);
				return;
			}
			if (totas.length > 0) {
				$scope.empWODeptLst = totas[0].body.empWODeptLst;
				$scope.outputVO = totas[0].body;
			} else {
				$scope.empWODeptLst = [];
				$scope.data = [];
			}
		});
	};
	
	$scope.addEmp = function() {
		var confirmMemLst = [];
		$scope.newInputVO.ADD_MEMBER_LST = [];
		angular.forEach($scope.empWODeptLst, function(emp, key) {
//			console.log('emp.SEL: ' + emp.SEL);
			if (emp.SEL) {
//				console.log('emp: ' + emp);
				$scope.newInputVO.ADD_MEMBER_LST.push(emp);
				confirmMemLst.push(' ' + emp.EMP_ID + '-' + emp.EMP_NAME + ' ');
			}
		});
		
		if ($scope.newInputVO.ADD_MEMBER_LST.length < 1) {
			$scope.showErrorMsgInDialog("ehl_01_org210_001");
			return;
		}
		
		$confirm({text: '是否將' + JSON.stringify(confirmMemLst) + '加入' + $scope.newInputVO.DEPT_ID + '-' + $scope.newInputVO.DEPT_NAME }, {size: '300px'}).then(function() {
			$scope.sendRecv("ORG210", "addEmp", "com.systex.jbranch.app.server.fps.org210.ORG210InputVO", $scope.newInputVO, function(totas, isError) {
				if (isError) {
					$scope.showErrorMsgInDialog(totas.body.msgData);
					return;
				}
				if (totas.length > 0) {
					$scope.showMsg("ehl_01_common_001");
				}
				$scope.getDeptDetail();
				$scope.org210add_dialog.close();
			});
		});
	};
	
	$scope.getTitleLst = function() {
		$scope.sendRecv("ORG210", "getRoleLst", "com.systex.jbranch.app.server.fps.org210.ORG210InputVO", $scope.inputVO, function(totas, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(totas.body.msgData);
				return;
			}
			if (totas.length > 0) {
				$scope.mappingSet['ridLst'] = [];
				var roleLst = totas[0].body.roleLst;
				for (var i = 0; i < roleLst.length; i++) {
					$scope.mappingSet['ridLst'].push({
						LABEL : roleLst[i].JOB_TITLE_NAME,
	    				DATA : roleLst[i].JOB_TITLE_NAME
					});
				}
			}
		});
	};
	
	$scope.init = function() {
		$scope.getTitleLst();
		
		$scope.empWODeptLst = [];
		$scope.data = [];
	};
	$scope.init();
});
