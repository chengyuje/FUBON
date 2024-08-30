'use strict';
eSoafApp.controller('CAM210_CHGLEADS_Controller', function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
	
	$controller('BaseController', {$scope: $scope});
	
	$scope.controllerName = "CAM210_CHGLEADS_Controller";
	
	getParameter.XML(["CAM.DISPATCH_CHANNEL"], function(totas) {
		if (totas) {
			// 改派人員類別選項
		    $scope.mappingSet['CAM.DISPATCH_CHANNEL'] = totas.data[totas.key.indexOf('CAM.DISPATCH_CHANNEL')];
		}
	});
	
	$scope.init = function(){
		$scope.inputVO = {
			branch_Name: $scope.reLeads[0].BRANCH_NAME,
			branchNbr  : $scope.reLeads[0].BRANCH_NBR,
			channel    : '',
			aoList     : '', 
			reSetLeList: []
    	};
		
		$scope.sendRecv("CAM210", "getBranch", "com.systex.jbranch.app.server.fps.cam210.CAM210InputVO",  $scope.inputVO, function(totas, isError) {
			if (!isError) {
				if (totas[0].body.branchList.length == 0) {
					$scope.showMsg("ehl_01_common_009");
					$scope.mappingSet['branchList'] = [];
  	                return;
				} else {
					$scope.mappingSet['branchList'] = [];
					angular.forEach(totas[0].body.branchList, function(row, index, objs){    		                	
	                	$scope.mappingSet['branchList'].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NAME, BRANCH_NBR: row.BRANCH_NBR});
	            	});
				}
			}
		});
	};
	
    $scope.init();
    
    $scope.reSetDefault = function() {
    	$scope.inputVO.channel = '';
    	$scope.inputVO.aoList = '';
    	$scope.inputVO.reSetLeList = [];

    	angular.forEach($scope.mappingSet['branchList'], function(row, index, objs){
    		if ($scope.inputVO.branch_Name == row.DATA) {
    			$scope.inputVO.branchNbr = row.BRANCH_NBR;
    		}
    	});
    }
    
    $scope.queryAoData = function(row) {
    	if ($scope.inputVO.channel != '') {
    		if ($scope.inputVO.channel != '002' && $scope.inputVO.channel != '003') {
    			$scope.inputVO.style = 'once';
    		}
    		
    		$scope.sendRecv("CAM210", "queryAoData_3", "com.systex.jbranch.app.server.fps.cam210.CAM210InputVO",  $scope.inputVO, function(totas, isError) {
				if (!isError) {
					if (totas[0].body.resultList.length == 0) {
						$scope.showMsg("ehl_01_common_009");
						$scope.mappingSet['aoList'] = [];
      	                return;
					} else {
						$scope.mappingSet['aoList'] = [];
    					angular.forEach(totas[0].body.resultList, function(row, index, objs){    		                	
		                	$scope.mappingSet['aoList'].push({LABEL: row.AO_LABEL, DATA: row.AO_DATA});
		            	});
					}
				}
    		});
        }
	}
    
	$scope.reSetLeads = function() {
		$scope.inputVO.reSetLeList = $scope.reLeads;
		
		if($scope.inputVO.channel == '') {
			$scope.showErrorMsg("請選擇改派人員類別");
		} else if ($scope.inputVO.aoList == '') {
			$scope.showErrorMsg("請選擇改派人員");
		} else {
			$scope.sendRecv("CAM210", "reSetLeads", "com.systex.jbranch.app.server.fps.cam210.CAM210InputVO",  $scope.inputVO, function(totas, isError) {	
				if (isError) {
					$scope.showErrorMsg(totas[0].body.MsgData);
				}
				
				$scope.showSuccessMsg("ehl_01_common_006");  
				$scope.closeThisDialog('successful');
			});
		}
	}
 	
    $scope.init();    
});
