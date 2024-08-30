/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CUS130_REPORT2Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		//組織連動繼承
		$controller('RegionController', {$scope: $scope});
		$scope.controllerName = "CUS130_REPORT2Controller";
		
		// init have a $scope.CanUploadFlag = CanUploadFlag; 
		$scope.role_id = projInfoService.getRoleID();
		getParameter.XML(["FUBONSYS.HEADMGR_ROLE"], function(totas) {
			if (totas) {
				$scope.IS_ALL_BRANCH = false;
				var temp = totas.data[totas.key.indexOf('FUBONSYS.HEADMGR_ROLE')];
				angular.forEach(temp, function(row) {
        			if(row.DATA == $scope.role_id)
        				$scope.IS_ALL_BRANCH = true;
    			});
			}
		});
		
		var member = $scope.connector('get','CUS130_REPORT1');
		$scope.inputVO = {};
		$scope.totalList = [];
		$scope.mappingSet['regionQuery'] = [];
		angular.forEach(projInfoService.getAvailRegion(), function(row, index, objs){
			$scope.mappingSet['regionQuery'].push({LABEL: row.REGION_CENTER_NAME, DATA: row.REGION_CENTER_ID});
		});
		$scope.mappingSet['areaQuery'] = [];
		angular.forEach(projInfoService.getAvailArea(), function(row, index, objs){
			$scope.mappingSet['areaQuery'].push({LABEL: row.BRANCH_AREA_NAME, DATA: row.BRANCH_AREA_ID});
		});
		$scope.mappingSet['branchQuery'] = [];
		angular.forEach(projInfoService.getAvailBranch(), function(row, index, objs){
			$scope.mappingSet['branchQuery'].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
		});
		
		//組織連動
		$scope.region = ['N', $scope.inputVO, "region", "REGION_LIST", "area", "AREA_LIST", "branch", "BRANCH_LIST", "gggg1", "AO_LIST", "gggg2", "EMP_LIST"];
        $scope.RegionController_setName($scope.region);
        $scope.uploadList = [];
        
        $scope.downloadSimple = function() {
        	$scope.sendRecv("CUS130", "downloadSimple", "com.systex.jbranch.app.server.fps.cus130.CUS130InputVO", {'setType': '3'},
					function(tota, isError) {
						if (!isError) {
							return;
						}
			});
		};
        
        $scope.uploadFinshed = function(name, rname) {
        	$scope.sendRecv("CUS130", "readTYPE1CUST", "com.systex.jbranch.app.server.fps.cus130.CUS130InputVO", {'custFileName':name,'custFileRealName':rname},
					function(totas, isError) {
	                	if (!isError) {
	                		$scope.uploadList = totas[0].body.resultList;
	                		if(totas[0].body.resultList2.length > 0)
		            			$scope.showErrorMsg('ehl_01_cus130_001',[totas[0].body.resultList2.toString()]);
	                	};
					}
			);
        };
        
        $scope.del = function() {
        	$confirm({text: '是否刪除資料？ '}, {size: 'sm'}).then(function() {
        		$scope.totalList = $scope.totalList.filter(function (el) { return el.CHECK !== true; });
        		$scope.outputVO = {'data':$scope.totalList};
            });
        };
        
        $scope.add = function() {
        	if($scope.allB) {
        		$scope.totalList = [];
        		$scope.totalList.push({'ALL_BRANCH':'Y'});
        	} else {
        		if(!$scope.inputVO.region && !$scope.inputVO.area && !$scope.inputVO.branch) {
        			return;
        		}
        		// remove allB
        		$scope.totalList = $scope.totalList.filter(function (el) { return el.ALL_BRANCH !== 'Y'; });
        		//
        		$scope.totalList.push({'REGION':$scope.inputVO.region,'AREA':$scope.inputVO.area,'BRANCH':$scope.inputVO.branch});
        		$scope.totalList = $scope.totalList.filter((obj, index, self) => self.findIndex((t) => {return t.REGION === obj.REGION && t.AREA === obj.AREA && t.BRANCH === obj.BRANCH; }) === index);
        	}
        	$scope.outputVO = {'data':$scope.totalList};
        };
        
		$scope.before = function () {
			$scope.closeThisDialog('report2Before');
		};
		
		$scope.fin = function () {
			if($scope.totalList.length == 0){
	    		$scope.showErrorMsg('欄位檢核錯誤:至少選擇一個');
        		return;
        	}
			$scope.connector('set','CUS130_REPORT2', $scope.totalList);
			$scope.connector('set','CUS130_REPORT2_MEMBER', member);
			$scope.connector('set','CUS130_REPORT2_UPLOAD', $scope.uploadList);
    		$scope.closeThisDialog('successful');
		};
		
});
