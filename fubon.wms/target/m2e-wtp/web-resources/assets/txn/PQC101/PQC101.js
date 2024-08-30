'use strict';
eSoafApp.controller('PQC101Controller', function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "PQC101Controller";
	
	$scope.initByType = function(type) {
		switch (type) {
	    	case 'Org':
	    		$scope.inputVO.prdType = '';
	    		$scope.inputVO.prdID = '';
	    		$scope.inputVO.reportType = '';
	    		
	    		$scope.orgList = [];
				$scope.outputVO = [];
				
	    		break;
		}
	}
	
	$scope.initByType();

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
    
    $scope.queryByType = function(type) {
    	switch (type) {
	    	case 'Basic':
	    		$scope.sendRecv("PQC101", "queryByType", "com.systex.jbranch.app.server.fps.pqc101.PQC101InputVO", {'queryType': type}, function(tota, isError) {
	    			if (!isError) {
	    				if(tota[0].body.basicList.length == 0) {
	    					$scope.basicList = [];
	    					$scope.showMsg("ehl_01_common_009");
	            			return;
	            		}
	    				$scope.basicList = tota[0].body.basicList;
	    				return;
	    			}
	        	});
	    		
	    		break;
	    	case 'Org':
	    		$scope.inputVO.queryType = type;

	    		$scope.sendRecv("PQC101", "queryByType", "com.systex.jbranch.app.server.fps.pqc101.PQC101InputVO", $scope.inputVO, function(tota, isError) {
	    			if (!isError) {
	    				if(tota[0].body.orgList.length == 0) {
	    					$scope.orgList = [];
	    					$scope.showMsg("ehl_01_common_009");
	            			return;
	            		}
	    				
	    				$scope.orgList = tota[0].body.orgList;
	    				$scope.outputVO = tota[0].body;
	    			}
	        	});
	    		
	    		break;
    	}
    };
    
    $scope.updByType = function(type) {
    	$scope.updateList = [];
    	var checkData = true;
    	
    	switch (type) {
	    	case 'Basic':
	    		angular.forEach($scope.basicList, function(row, index, objs){
	    			if (row.MIN_QUOTA <= 0 || 						// 小於0
	    				row.MIN_QUOTA > row.MAX_QUOTA || 			// 最低 > 最高
	    				row.MIN_QUOTA % 10000 != 0 ||				// 最低非10000倍數
	    				row.MAX_QUOTA % 10000 != 0 					// 最高非10000倍數
	    			) {
	    				checkData = false;
	    				return;
	    			} else {
	    				$scope.updateList.push(row);
	    			}
	    		});
	    		
	    		if (!checkData) {
    				$scope.showErrorMsg("最低申購金額/最高申購金額有誤，請重新確認");
    				return;
	    		}
	    		
				$scope.sendRecv("PQC101", "updByType", "com.systex.jbranch.app.server.fps.pqc101.PQC101InputVO", {'updType': type, 'basicUpdList': $scope.updateList}, function(totas, isError) {
	    			if (!isError) {
	    				$scope.showSuccessMsg('ehl_01_common_025');
	    				$scope.queryByType(type);
	    			}
				});
				
	    		break;
	    	case 'Org':
	    		angular.forEach($scope.orgList, function(row, index, objs){
	    			if (row.DISABLED_BASE == 'N') {
	    				if (row.DISABLED_PQ == 'N' && row.DISABLED_AQ == 'N' && Number(row.PRD_LAVE) < 0) {
	    					// 商品額度總額與商品剩餘額度確認
    	    				$scope.showErrorMsg("第" + row.ROWNUM + "筆之商品剩餘額度有誤，請重新設定");
    	    				checkData = false;
    	    			} else if (row.DISABLED_PQ == 'N' && row.DISABLED_AQ == 'N' && Number(row.PRD_QUOTA) % 10000) {
	    	    			// 商品額度總額應為10000之倍數
    	    				$scope.showErrorMsg("第" + row.ROWNUM + "筆之商品額度總額應為10000之倍數，請重新設定");
    	    				checkData = false;
    	    			} else if (row.DISABLED_PQ == 'N' && row.DISABLED_AQ == 'N' && Number(row.PRD_QUOTA) != Number(row.ALL_QUOTA)) {
    	    				// 全行額度總額應等於商品額度總額
    	    				$scope.showErrorMsg("第" + row.ROWNUM + "筆之全行額度總額應等於商品額度總額，請重新設定");
    	    				checkData = false;
    	    			} else if (row.DISABLED_PQ == 'N' && row.DISABLED_AQ == 'N' && Number(row.ALL_QUOTA) % 10000) {
	    	    			// 全行額度總額應為10000之倍數
    	    				$scope.showErrorMsg("第" + row.ROWNUM + "筆之全行額度總額應為10000之倍數，請重新設定");
    	    				checkData = false;
    	    			} else if (row.DISABLED_PQ == 'N' && Number(row.ALL_QUOTA) == 0 && Number(row.PRD_QUOTA) != (Number(row.RC_QUOTA_1) + Number(row.RC_QUOTA_2) + Number(row.RC_QUOTA_3) + Number(row.RC_QUOTA_4) + Number(row.RC_QUOTA_5) + Number(row.RC_QUOTA_6) + Number(row.RC_QUOTA_UHRM))) {
    	    				// 各處&高端的額度總額應等於商品額度總額
    	    				$scope.showErrorMsg("#" + row.ROWNUM + "筆之各處&高端的額度總額應等於商品額度總額，請重新設定");
    						checkData = false;
	    				} else if (row.DISABLED_PQ == 'N' && Number(row.ALL_QUOTA) == 0 && Number(row.RC_QUOTA_1) % 10000) {
	    					$scope.showErrorMsg("第" + row.ROWNUM + "筆之分行業務一處應為10000之倍數，請重新設定");
    	    				checkData = false;
	    				} else if (row.DISABLED_PQ == 'N' && Number(row.ALL_QUOTA) == 0 && Number(row.RC_QUOTA_2) % 10000) {
	    					$scope.showErrorMsg("第" + row.ROWNUM + "筆之分行業務二處應為10000之倍數，請重新設定");
    	    				checkData = false;
	    				} else if (row.DISABLED_PQ == 'N' && Number(row.ALL_QUOTA) == 0 && Number(row.RC_QUOTA_3) % 10000) {
	    					$scope.showErrorMsg("第" + row.ROWNUM + "筆之分行業務三處應為10000之倍數，請重新設定");
    	    				checkData = false;
	    				} else if (row.DISABLED_PQ == 'N' && Number(row.ALL_QUOTA) == 0 && Number(row.RC_QUOTA_4) % 10000) {
	    					$scope.showErrorMsg("第" + row.ROWNUM + "筆之分行業務四處應為10000之倍數，請重新設定");
    	    				checkData = false;
	    				} else if (row.DISABLED_PQ == 'N' && Number(row.ALL_QUOTA) == 0 && Number(row.RC_QUOTA_5) % 10000) {
	    					$scope.showErrorMsg("第" + row.ROWNUM + "筆之分行業務五處應為10000之倍數，請重新設定");
    	    				checkData = false;
	    				} else if (row.DISABLED_PQ == 'N' && Number(row.ALL_QUOTA) == 0 && Number(row.RC_QUOTA_6) % 10000) {
	    					$scope.showErrorMsg("第" + row.ROWNUM + "筆之分行業務六處應為10000之倍數，請重新設定");
    	    				checkData = false;
	    				} else if (row.DISABLED_PQ == 'N' && Number(row.ALL_QUOTA) == 0 && Number(row.RC_QUOTA_UHRM) % 10000) {
	    					$scope.showErrorMsg("第" + row.ROWNUM + "筆之個人高端客群處應為10000之倍數，請重新設定");
    	    				checkData = false;
	    				} else {
		    				$scope.updateList.push(row);
		    			}
	    			}
	    		});
	    		
	    		if (!checkData) {
    				return;
	    		}
	    		
				$scope.sendRecv("PQC101", "updByType", "com.systex.jbranch.app.server.fps.pqc101.PQC101InputVO", {'updType': type, 'orgUpdList': $scope.updateList}, function(totas, isError) {
	    			if (!isError) {
	    				$scope.showSuccessMsg('ehl_01_common_004');
	    				$scope.queryByType(type);
	    			}
				});
				
	    		break;
    	}
    };
    
    $scope.chkDisabled = function(type, row) {
    	switch (type) {
	    	case 'PQ':
	    		if (Number(row.PRD_QUOTA) > 0) {
	    			row.DISABLED_AQ = Number(row.PRD_QUOTA) > 0 ? 'N' : 'Y';
	    			row.DISABLED_RCQ1 = Number(row.ALL_QUOTA) > 0 ? 'Y' : 'N';
	    			row.DISABLED_RCQ2 = Number(row.ALL_QUOTA) > 0 ? 'Y' : 'N';
	    			row.DISABLED_RCQ3 = Number(row.ALL_QUOTA) > 0 ? 'Y' : 'N';
	    			row.DISABLED_RCQ4 = Number(row.ALL_QUOTA) > 0 ? 'Y' : 'N';
	    			row.DISABLED_RCQ5 = Number(row.ALL_QUOTA) > 0 ? 'Y' : 'N';
	    			row.DISABLED_RCQ6 = Number(row.ALL_QUOTA) > 0 ? 'Y' : 'N';
	    			row.DISABLED_RCQUHRM = Number(row.ALL_QUOTA) > 0 ? 'Y' : 'N';
	    			
	    			row.PRD_APPLY = Number(row.PRD_APPLY_TEMP);
	    			row.PRD_LAVE = Number(row.PRD_QUOTA) - Number(row.PRD_APPLY_TEMP);
	    		} else {
	    			row.DISABLED_AQ = 'Y';
	    			row.DISABLED_RCQ1 = 'Y';
	    			row.DISABLED_RCQ2 = 'Y';
	    			row.DISABLED_RCQ3 = 'Y';
	    			row.DISABLED_RCQ4 = 'Y';
	    			row.DISABLED_RCQ5 = 'Y';
	    			row.DISABLED_RCQ6 = 'Y';
	    			row.DISABLED_RCQUHRM = 'Y';
	    		}
	    		
	    		break;
	    	case 'AQ':
	    		if (Number(row.ALL_QUOTA) > 0) {
	    			row.DISABLED_RCQ1 = Number(row.ALL_QUOTA) > 0 ? 'Y' : 'N';
	    			row.DISABLED_RCQ2 = Number(row.ALL_QUOTA) > 0 ? 'Y' : 'N';
	    			row.DISABLED_RCQ3 = Number(row.ALL_QUOTA) > 0 ? 'Y' : 'N';
	    			row.DISABLED_RCQ4 = Number(row.ALL_QUOTA) > 0 ? 'Y' : 'N';
	    			row.DISABLED_RCQ5 = Number(row.ALL_QUOTA) > 0 ? 'Y' : 'N';
	    			row.DISABLED_RCQ6 = Number(row.ALL_QUOTA) > 0 ? 'Y' : 'N';
	    			row.DISABLED_RCQUHRM = Number(row.ALL_QUOTA) > 0 ? 'Y' : 'N';
	    			
	    			row.RC_QUOTA_1 = 0;
	    			row.RC_APPLY_1 = 0;
	    			row.RC_LAVE_1 = 0;
	    			
	    			row.RC_QUOTA_2 = 0;
	    			row.RC_APPLY_2 = 0;
	    			row.RC_LAVE_2 = 0;
	    			
	    			row.RC_QUOTA_3 = 0;
	    			row.RC_APPLY_3 = 0;
	    			row.RC_LAVE_3 = 0;
	    			
	    			row.RC_QUOTA_4 = 0;
	    			row.RC_APPLY_4 = 0;
	    			row.RC_LAVE_4 = 0;
	    			
	    			row.RC_QUOTA_5 = 0;
	    			row.RC_APPLY_5 = 0;
	    			row.RC_LAVE_5 = 0;
	    			
	    			row.RC_QUOTA_6 = 0;
	    			row.RC_APPLY_6 = 0;
	    			row.RC_LAVE_6 = 0;
	    			
	    			row.RC_QUOTA_UHRM = 0;
	    			row.RC_APPLY_UHRM = 0;
	    			row.RC_LAVE_UHRM = 0;
	    			
	    			row.ALL_APPLY = Number(row.ALL_APPLY_TEMP);
	    			row.ALL_LAVE = Number(row.ALL_QUOTA) - Number(row.ALL_APPLY);	
	    		} else {
	    			row.DISABLED_AQ = 'N';
	    			row.DISABLED_RCQ1 = 'N';
	    			row.DISABLED_RCQ2 = 'N';
	    			row.DISABLED_RCQ3 = 'N';
	    			row.DISABLED_RCQ4 = 'N';
	    			row.DISABLED_RCQ5 = 'N';
	    			row.DISABLED_RCQ6 = 'N';
	    			row.DISABLED_RCQUHRM = 'N';
	    			
	    			row.ALL_APPLY = Number(row.ALL_APPLY_TEMP);
	    			row.ALL_LAVE = Number(row.ALL_QUOTA) - Number(row.ALL_APPLY);	
	    			
		    		row.RC_APPLY_1 = Number(row.RC_APPLY_TEMP_1);
	    			row.RC_LAVE_1 = Number(row.RC_QUOTA_1) - Number(row.RC_APPLY_1);
	    			
	    			row.RC_APPLY_2 = Number(row.RC_APPLY_TEMP_2);
	    			row.RC_LAVE_2 = Number(row.RC_QUOTA_2) - Number(row.RC_APPLY_2);
	    			
	    			row.RC_APPLY_3 = Number(row.RC_APPLY_TEMP_3);
	    			row.RC_LAVE_3 = Number(row.RC_QUOTA_3) - Number(row.RC_APPLY_3);
	    			
	    			row.RC_APPLY_4 = Number(row.RC_APPLY_TEMP_4);
	    			row.RC_LAVE_4 = Number(row.RC_QUOTA_4) - Number(row.RC_APPLY_4);
	    			
	    			row.RC_APPLY_5 = Number(row.RC_APPLY_TEMP_5);
	    			row.RC_LAVE_5 = Number(row.RC_QUOTA_5) - Number(row.RC_APPLY_5);
	    			
	    			row.RC_APPLY_6 = Number(row.RC_APPLY_TEMP_6);
	    			row.RC_LAVE_6 = Number(row.RC_QUOTA_6) - Number(row.RC_APPLY_6);
	    			
	    			row.RC_APPLY_UHRM = Number(row.RC_APPLY_TEMP_UHRM);
	    			row.RC_LAVE_UHRM = Number(row.RC_QUOTA_UHRM) - Number(row.RC_APPLY_UHRM);
	    		}
	    		
	    		break;
	    	case 'RCQ':
	    		if ((Number(row.RC_QUOTA_1) + Number(row.RC_QUOTA_2) + Number(row.RC_QUOTA_3) + Number(row.RC_QUOTA_4) + Number(row.RC_QUOTA_5) + Number(row.RC_QUOTA_6) + Number(row.RC_QUOTA_UHRM)) > 0) {
	    			row.DISABLED_AQ = 'Y';
	    			row.DISABLED_RCQ1 = 'N';
	    			row.DISABLED_RCQ2 = 'N';
	    			row.DISABLED_RCQ3 = 'N';
	    			row.DISABLED_RCQ4 = 'N';
	    			row.DISABLED_RCQ5 = 'N';
	    			row.DISABLED_RCQ6 = 'N';
	    			row.DISABLED_RCQUHRM = 'N';
	    			
	    			row.ALL_APPLY = 0;
	    			row.ALL_LAVE = 0;
	    		} else {
	    			row.DISABLED_AQ = 'N';
	    			row.DISABLED_RCQ1 = 'N';
	    			row.DISABLED_RCQ2 = 'N';
	    			row.DISABLED_RCQ3 = 'N';
	    			row.DISABLED_RCQ4 = 'N';
	    			row.DISABLED_RCQ5 = 'N';
	    			row.DISABLED_RCQ6 = 'N';
	    			row.DISABLED_RCQUHRM = 'N';
	    			
	    			row.ALL_APPLY = Number(row.ALL_APPLY_TEMP);
	    			row.ALL_LAVE = Number(row.ALL_QUOTA) - Number(row.ALL_APPLY);
	    		}
	    		
	    		row.RC_APPLY_1 = Number(row.RC_APPLY_TEMP_1);
    			row.RC_LAVE_1 = Number(row.RC_QUOTA_1) - Number(row.RC_APPLY_1);
    			
    			row.RC_APPLY_2 = Number(row.RC_APPLY_TEMP_2);
    			row.RC_LAVE_2 = Number(row.RC_QUOTA_2) - Number(row.RC_APPLY_2);
    			
    			row.RC_APPLY_3 = Number(row.RC_APPLY_TEMP_3);
    			row.RC_LAVE_3 = Number(row.RC_QUOTA_3) - Number(row.RC_APPLY_3);
    			
    			row.RC_APPLY_4 = Number(row.RC_APPLY_TEMP_4);
    			row.RC_LAVE_4 = Number(row.RC_QUOTA_4) - Number(row.RC_APPLY_4);
    			
    			row.RC_APPLY_5 = Number(row.RC_APPLY_TEMP_5);
    			row.RC_LAVE_5 = Number(row.RC_QUOTA_5) - Number(row.RC_APPLY_5);
    			
    			row.RC_APPLY_6 = Number(row.RC_APPLY_TEMP_6);
    			row.RC_LAVE_6 = Number(row.RC_QUOTA_6) - Number(row.RC_APPLY_6);
    			
    			row.RC_APPLY_UHRM = Number(row.RC_APPLY_TEMP_UHRM);
    			row.RC_LAVE_UHRM = Number(row.RC_QUOTA_UHRM) - Number(row.RC_APPLY_UHRM);
	    		
	    		break;
    	}
    };
});
