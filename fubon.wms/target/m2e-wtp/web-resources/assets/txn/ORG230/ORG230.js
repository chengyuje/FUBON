/*
 * Modify LOG
 * 
 * 2017-02-02 組織連動(專案同步修正) modify by ocean
 * 
 */
'use strict';
eSoafApp.controller('ORG230Controller', function(sysInfoService, $scope, $controller, $confirm, $compile, socketService, ngDialog, projInfoService) {
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "ORG230Controller";
	
	//組織連動繼承
	$controller('RegionController', {$scope: $scope});
    
	$scope.init = function() {
		$scope.branchMbrQuotaLst = [];

		$scope.inputVO = {
			region_center_id   : '',
			branch_area_id     : '',
			branch_nbr         : '',
			FILE_NAME          : '',
			ACTUAL_FILE_NAME   : ''
		};
		
		$scope.delInputVO = {
			branch_nbr         : '',
		};
		
		//組織連動
        $scope.region = ['N', $scope.inputVO, "region_center_id", "REGION_LIST", "branch_area_id", "AREA_LIST", "branch_nbr", "BRANCH_LIST", "ao_code", "AO_LIST", "emp_id", "EMP_LIST"];
        $scope.RegionController_setName($scope.region);
	};
	
	$scope.getBranchMbrQuotaLst = function() {
		$scope.sendRecv("ORG230", "getBranchMbrQuotaLst", "com.systex.jbranch.app.server.fps.org230.ORG230InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(tota.body.msgData);
				return;
			}
			if (tota.length > 0) {
				$scope.branchMbrQuotaLst = tota[0].body.branchMbrQuotaLst;
				$scope.outputVO = tota[0].body;
			}
		});
	};
	
	$scope.delBranchMbrQuota = function(row) {
		$scope.delInputVO.branch_nbr = row.BRANCH_NBR;
		$confirm({text: '是否刪除' + row.BRANCH_NBR + '-' + row.BRANCH_NAME + '的資料'}, {size: '200px'}).then(function() {
			$scope.sendRecv("ORG230", "delBranchMbrQuota", "com.systex.jbranch.app.server.fps.org230.ORG230InputVO", $scope.delInputVO, function(tota, isError) {
				if (isError) {
					$scope.showErrorMsgInDialog(tota.body.msgData);
					return;
				}
				if (tota.length > 0) {
					$scope.showMsg("ehl_01_common_003");
				}
				$scope.getBranchMbrQuotaLst();
			});
		});
	};
	
	$scope.getSampleLst = function() {
		$scope.sendRecv("ORG230", "getSampleLst", "com.systex.jbranch.app.server.fps.org230.ORG230InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(tota.body.msgData);
				return;
			}
			if (tota.length > 0) {
			}
		});
	};
	
	$scope.uploadBranchMbrQuotaLst = function(name, rname) {
		$scope.inputVO.FILE_NAME = name;
		$scope.inputVO.ACTUAL_FILE_NAME = rname;
		$confirm({text: '是否上傳：' + rname + '?'}, {size: '200px'}).then(function() {
			$scope.sendRecv("ORG230", "uploadBranchMbrQuotaLst", "com.systex.jbranch.app.server.fps.org230.ORG230InputVO", $scope.inputVO, function(tota, isError) {
				if (isError) {
					$scope.showErrorMsgInDialog(tota.body.msgData);
					return;
				}
				if (tota.length > 0) {
					$scope.showMsg("ehl_01_common_010");
				}
				$scope.getBranchMbrQuotaLst();
			});
		});
		angular.element($("#csvUpload")).remove();
		angular.element($("#csvBox")).append("<e-upload id='csvUpload' success='uploadBranchMbrQuotaLst(name,rname)' text='上傳' accept='.csv'></e-upload>");
		var content = angular.element($("#csvBox"));
		var scope = content.scope();
		$compile(content.contents())(scope);
	};
	
	$scope.init();
});
