'use strict';
eSoafApp.controller('PMS418_EXCLUDEIPController', function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter, $compile) {
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "PMS418_EXCLUDEIPController";
	
	$scope.init = function(){

    };
    
    $scope.init();
    
	// 取得範例
	$scope.getExample = function() {
		
		$scope.sendRecv("PMS418", "getExample", "com.systex.jbranch.app.server.fps.pms418.PMS418InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(tota.body.msgData);
				return;
			}
			
			if (tota.length > 0) {
			}
		});
	};
	
	// 查詢
	$scope.queryExcludeIP = function() {
		$scope.totals = {};
		$scope.paramList = [];
		
		$scope.sendRecv("PMS418", "queryExcludeIP", "com.systex.jbranch.app.server.fps.pms418.PMS418InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				$scope.paramList = tota[0].body.excludeList;
				$scope.totalData = tota[0].body.excludeList;
				$scope.outputVO = tota[0].body;
				
				return;
			}						
		});
	};
	
	$scope.queryExcludeIP();
	
	// 刪除
	$scope.delExcludeIP = function(row) {
		$confirm({text: '是否刪除此筆資料!!'}, {size: 'sm'}).then(function() {
           	$scope.sendRecv("PMS418", "delExcludeIP", "com.systex.jbranch.app.server.fps.pms418.PMS418InputVO", {'ipAddrDel': row.IP_ADDR, 'branchNbrDel': row.BRANCH_NBR}, function(tota, isError) {
    			if(isError){
    				$scope.showErrorMsg(tota[0].body.msgData);
    			}
               	if (tota.length > 0) {
            		$scope.showSuccessMsg('ehl_01_common_003');
            		$scope.queryExcludeIP();
            	};
        	});
		});	
	};
	
	// 上傳
	$scope.updExcludeIP = function(name, rname) {
		
		$scope.inputVO.FILE_NAME = name;
		$scope.inputVO.ACTUAL_FILE_NAME = rname;
		$confirm({text: '是否上傳：' + rname + '?'}, {size: '200px'}).then(function() {
			$scope.sendRecv("PMS418", "updExcludeIP", "com.systex.jbranch.app.server.fps.pms418.PMS418InputVO", $scope.inputVO, function(tota, isError) {
				if (isError) {
					$scope.showErrorMsgInDialog(tota.body.msgData);
					return;
				}
				if (tota.length > 0) {
					$scope.showMsg("ehl_01_common_010");
					$scope.queryExcludeIP();
				}
			});
		});
		
		angular.element($("#csvUpload")).remove();
		angular.element($("#csvBox")).append("<e-upload id='csvUpload' success='updExcludeIP(name, rname)' text='上傳' accept='.csv'></e-upload>");
		var content = angular.element($("#csvBox"));
		var scope = content.scope();
		$compile(content.contents())(scope);
	};
	
	// 匯出
	$scope.export = function() {		
//		angular.copy($scope.paramList, $scope.inputVO.exportList);
		$scope.inputVO.exportList = $scope.paramList;
		
		$scope.sendRecv("PMS418", "expExcludeIP", "com.systex.jbranch.app.server.fps.pms418.PMS418InputVO", $scope.inputVO, function(totas, isError) {
        	if (isError) {
        		$scope.showErrorMsg(totas[0].body.msgData);
        	}
        	if (totas.length > 0) {
        		if(totas[0].body.paramList && totas[0].body.paramList.length == 0) {
        			$scope.showMsg("ehl_01_common_009");
        			return;
        		}
        	};
		});
	};
});