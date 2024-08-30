'use strict';
eSoafApp.controller('PQC200Controller', function(sysInfoService, $rootScope, $scope, $controller, socketService, ngDialog, projInfoService, getParameter, $confirm) {
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "PQC200Controller";
	
	//組織連動繼承
	$controller('RegionController', {$scope: $scope});
	
	$scope.memLoginFlag = String(sysInfoService.getMemLoginFlag()).toUpperCase();
		
	/* 取得UHRM人員清單(由員工檔+角色檔) */
	$scope.sendRecv("ORG260", "get031EmpList", "com.systex.jbranch.app.server.fps.org260.ORG260InputVO", $scope.inputVO, function(tota, isError) {
		if (isError) {
			return;
		}
		if (tota.length > 0) {
			$scope.mappingSet['UHRM_LIST'] = tota[0].body.uhrmList;
			$scope.inputVO.uEmpID = sysInfoService.getUserID();
		}
	});
	
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
	
	$scope.init = function() {
		$scope.paramList = [];
		
		$scope.inputVO = {
			prdType: '', 
			prdID: '',
			reportType: ''
		};
		
		//組織連動
        $scope.region = ['N', $scope.inputVO, "region_center_id", "REGION_LIST", "branch_area_id", "AREA_LIST", "branch_nbr", "BRANCH_LIST", "ao_code", "AO_LIST", "emp_id", "EMP_LIST"];
        $scope.RegionController_setName($scope.region);
	};
	
	$scope.init();	
	
	$scope.query = function(){
		$scope.sendRecv("PQC200", "query", "com.systex.jbranch.app.server.fps.pqc200.PQC200InputVO", $scope.inputVO, function(tota, isError) {
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
	
	$scope.doAction = function(row, type) {
		switch (type) {
			case 'A':
				var dialog = ngDialog.open({
					template: 'assets/txn/PQC200/PQC200_APPLY.html',
					className: 'PQC200_APPLY',
					showClose: false,
	                controller: ['$scope', function($scope) {
	                	$scope.row = row;
	                	$scope.type = type;
	                }]
				});
				
				dialog.closePromise.then(function (data) {
		            $scope.query();
		        });
				break;
			case 'D':
				$confirm({text: '是否刪除此筆資料？ '}, {size: 'sm'}).then(function() {
					$scope.inputVO.SEQNO = row.SEQNO;
					
					$scope.sendRecv("PQC200", "del", "com.systex.jbranch.app.server.fps.pqc200.PQC200InputVO", $scope.inputVO, function(tota, isError) {
						if (!isError) {
							$scope.showSuccessMsg('ehl_01_common_003');
						}	
						
						$scope.query();	
					});
	            });
				
				break;
			case 'U':
				var dialog = ngDialog.open({
					template: 'assets/txn/PQC200/PQC200_UPDATE.html',
					className: 'PQC200_UPDATE',
					showClose: false,
	                controller: ['$scope', function($scope) {
	                	$scope.row = row;
	                	$scope.type = type;
	                }]
				});
				
				dialog.closePromise.then(function (data) {
		            $scope.query();
		        });
				
				break;
			case 'R':
				$scope.sendRecv("PQC200", "report", "com.systex.jbranch.app.server.fps.pqc200.PQC200OutputVO", $scope.outputVO, function(tota, isError) {						
					if (isError) {
	            		$scope.showErrorMsgInDialog(tota[0].body.msgData);
	            		return;
	            	}						
				});
				break;
		}
		
	};
});
