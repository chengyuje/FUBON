'use strict';
eSoafApp.controller('PQC100Controller', function(sysInfoService, $rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter, $timeout, $filter, $q) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "PQC100Controller";
	
	$scope.memLoginFlag = sysInfoService.getMemLoginFlag();
	
	$scope.init = function() {
		$scope.data = [];
		$scope.paramList = [];
		$scope.mappingSet['ACTIVE_PRD_LIST'] = [];
		
		$scope.inputVO = {     		
			prdType		: '',
			prdID  		: '',
			reportType	: ''
    	}
	};
	
    $scope.init();
    
    $scope.getActivePrd = function() {
    	$scope.mappingSet['ACTIVE_PRD_LIST'] = [];
    	
    	$scope.sendRecv("PQC100", "getActivePrd", "com.systex.jbranch.app.server.fps.pqc100.PQC100InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				return;
			}
			if (tota.length > 0) {
				$scope.mappingSet['ACTIVE_PRD_LIST'] = tota[0].body.activePrdList;
			}
    	});
    };
    
    $scope.query = function(){
		$scope.sendRecv("PQC100", "getPrdList", "com.systex.jbranch.app.server.fps.pqc100.PQC100InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				if(tota[0].body.resultList.length == 0) {
					$scope.paramList = [];
					$scope.showMsg("ehl_01_common_009");
        			return;
        		}
				
				$scope.paramList = tota[0].body.resultList;
				$scope.totalData = tota[0].body.totalList;
				$scope.outputVO = tota[0].body;
				
				return;
			}						
		});
	};
	
	$scope.getExample = function() {
		$scope.sendRecv("PQC100", "getExample", "com.systex.jbranch.app.server.fps.pqc100.PQC100InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(tota.body.msgData);
				return;
			}
			if (tota.length > 0) {
			}
		});
	};
	
	$scope.updateProdList = function(name, rname) {
		$scope.inputVO.FILE_NAME = name;
		$scope.inputVO.ACTUAL_FILE_NAME = rname;
		$confirm({text: '是否上傳：' + rname + '?'}, {size: '200px'}).then(function() {
			$scope.sendRecv("PQC100", "updPrdList", "com.systex.jbranch.app.server.fps.pqc100.PQC100InputVO", $scope.inputVO, function(tota, isError) {
				if (isError) {
					$scope.showErrorMsgInDialog(tota.body.msgData);
					return;
				}
				
				if (tota.length > 0) {
					$scope.showMsg("ehl_01_common_010");
				}
				
				$scope.query();
			});
		});
		angular.element($("#csvUpload")).remove();
		angular.element($("#csvBox")).append("<e-upload id='csvUpload' success='updateProdList(name,rname)' text='上傳' accept='.csv'></e-upload>");
		var content = angular.element($("#csvBox"));
		var scope = content.scope();
		$compile(content.contents())(scope);
	};
	
	$scope.doAction = function(row, action){
		switch (action) {
			case "D":
				$confirm({text: '是否刪除該商品'}, {size: 'sm'}).then(function() {
	    			$scope.sendRecv("PQC100", "delPrdQuota", "com.systex.jbranch.app.server.fps.pqc100.PQC100InputVO", {delStartDate: row.SEARCH_START_DATE, delEndDate: row.SEARCH_END_DATE, delPrdType: row.PRD_TYPE, delPrdID: row.PRD_ID}, function(totas, isError) {
						if (isError) {
			            	$scope.showErrorMsgInDialog(totas.body.msgData);
			            	return;
			            } else {
			            	$scope.query();
			            }
					});
	    		});
				
				break;
		}
    };
});
