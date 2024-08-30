'use strict';
eSoafApp.controller('CUS130_SET_EMPController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		//組織連動繼承
		$controller('RegionController', {$scope: $scope});
		$scope.controllerName = "CUS130_SET_EMPController";
		
		$scope.inputVO = {};
		$scope.checkVO = {};
		$scope.empList = [];
		$scope.empFinList = [];
		$scope.get_emp_list = function() {
			$scope.sendRecv("CUS130", "getEmpList", "com.systex.jbranch.app.server.fps.cus130.CUS130InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.empList = tota[0].body.resultList;
						}
			});
		};
		//組織連動
		$scope.pri_id = projInfoService.getPriID()[0];
        $scope.region = ['N', $scope.inputVO, "region", "REGION_LIST", "area", "AREA_LIST", "branch", "BRANCH_LIST", "gggg1", "AO_LIST", "gggg2", "EMP_LIST"];
        $scope.RegionController_setName($scope.region).then(function(data) {
        	$scope.get_emp_list();
        });
        
        $scope.checkrow = function() {
        	if ($scope.checkVO.clickAll) {
        		angular.forEach($scope.empList, function(row) {
        			row.SELECTED = true;
    			});
        	} else {
        		angular.forEach($scope.empList, function(row) {
        			row.SELECTED = false;
    			});
        	}
        };
        $scope.checkrow2 = function() {
        	if ($scope.checkVO.clickAll2) {
        		angular.forEach($scope.empFinList, function(row) {
        			row.SELECTED = true;
    			});
        	} else {
        		angular.forEach($scope.empFinList, function(row) {
        			row.SELECTED = false;
    			});
        	}
        };
        
        $scope.btnAddFun = function() {
        	for (var i = $scope.empList.length - 1; i >= 0; i--) {
        		if ($scope.empList[i].SELECTED) {
        			$scope.empList[i].SELECTED = false;
        			$scope.empFinList.push($scope.empList[i]);
        			$scope.empList.splice(i, 1);
        		}
        	}
        	$scope.checkVO.clickAll = false;
        	$scope.checkVO.clickAll2 = false;
        	$scope.empFinList = _.uniqWith($scope.empFinList, _.isEqual);
        	$scope.empFinList = _.orderBy($scope.empFinList, ['BRANCH_NBR', 'ROLE_NAME', 'EMP_ID']);
        };
        
        $scope.btnDelFun = function() {
        	for (var i = $scope.empFinList.length - 1; i >= 0; i--) {
        		if ($scope.empFinList[i].SELECTED) {
        			$scope.empFinList[i].SELECTED = false;
        			$scope.empList.push($scope.empFinList[i]);
        			$scope.empFinList.splice(i, 1);
        		}
        	}
        	$scope.checkVO.clickAll = false;
        	$scope.checkVO.clickAll2 = false;
        	$scope.empList = _.orderBy($scope.empList, ['ROLE_NAME', 'EMP_ID']);
        };
        
        $scope.save = function () {
        	if($scope.empFinList.length == 0){
	    		$scope.showErrorMsg('欄位檢核錯誤:至少選擇一個');
        		return;
        	}
        	$scope.connector('set','CUS130_EMP_LIST', $scope.empFinList);
        	$scope.closeThisDialog('successful');
        };
        
        
		
});