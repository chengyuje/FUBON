'use strict';
eSoafApp.controller('SQM310_1Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SQM310_1Controller";
		
		$scope.sendRecv("ORG410", "getAoJobRankList", "com.systex.jbranch.app.server.fps.org410.ORG410InputVO", {},
			function(tota, isError) {
				if (!isError) {
					$scope.AO_JOB_RANK = [];
					angular.forEach(tota[0].body.aoJobRankList, function(row) {
						$scope.AO_JOB_RANK.push({LABEL: row.JOB_TITLE_NAME, DATA: row.ROLE_ID});
					});
				}
		});
		
		$scope.init = function() {
			$scope.inputVO = {totalList: []};
			$scope.insertVO = {preq: 0};
			$scope.sendRecv("SQM310", "init_rc_par", "com.systex.jbranch.app.server.fps.sqm310.SQM310InputVO", {},
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.resultList.length == 0)
							return;
						$scope.inputVO.totalList = tota[0].body.resultList;
					}
			});
		};
		$scope.init();
		
		$scope.setRole_Name = function(DataRow) {
			if(DataRow) {
				if(DataRow.ROLE_ID) {
					var index = $scope.AO_JOB_RANK.map(function(e) { return e.DATA; }).indexOf(DataRow.ROLE_ID);
					DataRow.ROLE_NAME = $scope.AO_JOB_RANK[index].LABEL;
				}
			}
			else {
				if($scope.insertVO.role_id) {
					var index = $scope.AO_JOB_RANK.map(function(e) { return e.DATA; }).indexOf($scope.insertVO.role_id);
					$scope.insertVO.role_name = $scope.AO_JOB_RANK[index].LABEL;
				}
			}
		};
		
		$scope.del = function(RowIndex) {
			$confirm({text: '是否刪除此筆資料？ '}, {size: 'sm'}).then(function() {
				$scope.inputVO.totalList.splice(RowIndex, 1);
				$scope.showSuccessMsg('ehl_01_common_004');
            });
		};
		
		$scope.add = function() {
			if(!$scope.insertVO.role_id || !$scope.insertVO.sc_cnt_t
					|| !$scope.insertVO.sc_cnt_l || !$scope.insertVO.sp_cust_pc) {
				$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
			}
			var exiIndex = $scope.inputVO.totalList.map(function(e) { return e.ROLE_ID; }).indexOf($scope.insertVO.role_id);
			if(exiIndex > -1) {
				$scope.showErrorMsg('欄位檢核錯誤:該角色代號已存在');
        		return;
			}
			$scope.inputVO.totalList.push({
				'PREQ': $scope.insertVO.preq,
				'ROLE_ID': $scope.insertVO.role_id,
				'ROLE_NAME': $scope.insertVO.role_name,
				'SC_CNT_TOT': $scope.insertVO.sc_cnt_t,
				'SC_CNT_L': $scope.insertVO.sc_cnt_l,
				'SP_CUST_PC': $scope.insertVO.sp_cust_pc
			});
			$scope.insertVO = {preq: 0};
			$scope.showSuccessMsg('ehl_01_common_004');
		};
		
		$scope.save = function() {
			if($scope.parameterTypeEditForm.$invalid) {
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
			if($scope.inputVO.totalList.length == 0) {
				$scope.showErrorMsg('欄位檢核錯誤:請至少新增一個');
        		return;
			}
			$scope.sendRecv("SQM310", "save_rc", "com.systex.jbranch.app.server.fps.sqm310.SQM310InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.showSuccessMsg('ehl_01_common_004');
						$scope.init();
					}
			});
		};
		
		
		
});