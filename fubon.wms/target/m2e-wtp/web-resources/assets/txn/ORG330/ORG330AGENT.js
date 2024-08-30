/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('ORG330_AGENTController', function($scope, $controller, $confirm, $compile, socketService, ngDialog, projInfoService) {
	$controller('BaseController', { $scope : $scope });
	$scope.controllerName = "ORG330_AGENTController";
	
	$scope.roleL = {selected : []};
	$scope.roleR = {selected : []};
	$scope.lstRoleL = [];
	$scope.lstRoleR = [];
	var maxAgentNum = 20;
	
	$scope.agentVO = {
		ROLE_ID: $scope.ROLE_ID,
		ROLE_NAME: $scope.ROLE_NAME,
		AGENT_ROLE: $scope.lstRoleR, 
		ACT: $scope.ACT
	};
	
	$scope.modAgent = function () {
		var roleStr = "";
		angular.forEach($scope.agentVO.AGENT_ROLE, function(row, index, objs){
			roleStr = roleStr + (roleStr.length > 0 ? "," : "") + row.ROLE_ID;
		});
		
		$scope.closeThisDialog(roleStr);
	};
	
	$scope.closeORG330AGENT = function () {
		$scope.closeThisDialog('cancel');
	};
	
	$scope.btnAddChange = function(selected, from, to) {
		// single select
		if ($scope[selected].selected.length == undefined) {
			if (to == 'lstRoleR' && $scope[to].length + 1 > maxAgentNum) {
				var list = [maxAgentNum];
				$scope.showMsg("ehl_01_org330_001", list);
				return;
			}
			
			$scope[to].push({
				ROLE_ID : $scope[selected].selected.ROLE_ID,
				ROLE_NAME : $scope[selected].selected.ROLE_NAME
			});
			
			for (var j = 0; j < $scope[from].length; j++) {
				if ($scope[from][j] == $scope[selected].selected) {
					$scope[from].splice(j, 1);
				}
			}
		} else { // multiple select
			if (to == 'lstRoleR' && $scope[to].length + $scope[selected].selected.length > maxAgentNum) {
				var list = [maxAgentNum];
				$scope.showMsg("ehl_01_org330_001", list);
				return;
			}
			
			for (var i = 0; i < $scope[selected].selected.length; i++) {
				$scope[to].push({
					ROLE_ID : $scope[selected].selected[i].ROLE_ID,
					ROLE_NAME : $scope[selected].selected[i].ROLE_NAME
				});
				
				for (var j = 0; j < $scope[from].length; j++) {
					if ($scope[from][j] == $scope[selected].selected[i]) {
						$scope[from].splice(j, 1);
					}
				}
			}
		}
		
		$scope[selected].selected = [];
	};
	
	$scope.init = function () {
		angular.forEach($scope.roleLst, function(row, index, objs){
			if (row.REVIEW_STATUS == 'Y') {
				$scope.lstRoleL.push({
					ROLE_ID   : row.ROLE_ID,
					ROLE_NAME : row.ROLE_NAME
				});
			}
		});
		
		if ($scope.selRow != null && $scope.selRow.AGENT_ROLE != null ) {
			angular.forEach($scope.selRow.AGENT_ROLE.split(','), function(arId, index, objs){
				var ROLE_NAME = '';
				for (var i = 0; i < $scope.lstRoleL.length; i++){
					if ($scope.lstRoleL[i].ROLE_ID == arId) {
						ROLE_NAME = $scope.lstRoleL[i].ROLE_NAME;
						$scope.lstRoleL.splice(i, 1);
					}
				}
				
				if ($scope.ACT == "READ") {
					for (var i = 0; i < $scope.roleLst.length; i++){
						if ($scope.roleLst[i].ROLE_ID == arId) {
							ROLE_NAME = $scope.roleLst[i].ROLE_NAME;
						}
					}
				}
				
				$scope.lstRoleR.push({
					ROLE_ID   : arId,
					ROLE_NAME : ROLE_NAME
				});
			});
		}
	};
	
	$scope.init();
});
