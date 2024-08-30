/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD235_EDITUNITController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter, $filter, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD235_EDITUNITController";
        
		$scope.init = function() {
			$scope.inputVO = {};
			$scope.inputVO = $scope.row;
			$scope.inputVO.isPMRole = $scope.isPMRole;
			$scope.inputVO.isPMBossRole = $scope.isPMBossRole;
		}
		$scope.init();
		
		//取得該商品贖回明細資料
		$scope.getRedeemDetails = function() {
			$scope.sendRecv("PRD235", "getRedeemDetails", "com.systex.jbranch.app.server.fps.prd235.PRD235InputVO", {"SEQ_NO": $scope.inputVO.SEQ_NO},
                function(tota, isError) {
        		debugger
                    if (isError) {
                        $scope.showErrorMsg(tota[0].body.msgData);
                    } else {
                    	$scope.resultList = tota[0].body.resultList;
						$scope.outputVO = tota[0].body;
						$scope.inputVO.rdmTotalUnits = tota[0].body.rdmTotalUnits;
						$scope.inputVO.STATUS = tota[0].body.resultList[0].STATUS;
						$scope.inputVO.NEW_RDM_TOTAL_UNITS = tota[0].body.resultList[0].NEW_RDM_TOTAL_UNITS;
						//取得總調整後單位數
						$scope.calAdjTotalUnits();
                    }
			});
        };
        $scope.getRedeemDetails();
		
        $scope.adjUnitNumChanged = function(row) {
        	if(row.ADJ_UNIT_NUM && !$scope.inputVO.NEW_RDM_TOTAL_UNITS) {
        		$scope.showErrorMsg("請先填寫新單位數");
        		row.ADJ_UNIT_NUM = undefined;
        		return;
        	}
        	
        	$scope.calAdjTotalUnits();
        }
        
        //取得總調整後單位數
        $scope.calAdjTotalUnits = function() {
        	$scope.inputVO.adjTotalUnits = 0;
        	debugger
        	var totalUnits = 0;
        	angular.forEach($scope.resultList, function(row) {
        		debugger
        		if(row.ADJ_UNIT_NUM) {
        			totalUnits += Number(row.ADJ_UNIT_NUM);
        		}
        	});
        	$scope.inputVO.adjTotalUnits = totalUnits;
        	
        	//剩餘單位數
        	if($scope.inputVO.NEW_RDM_TOTAL_UNITS) {
        		$scope.inputVO.leftAdjTotalUnits = Number($scope.inputVO.NEW_RDM_TOTAL_UNITS) - totalUnits;
        	}
        }
        
        //依原單位數調整
        $scope.adjByOrgUnit = function() {
        	if(!$scope.inputVO.NEW_RDM_TOTAL_UNITS) {
        		$scope.showErrorMsg("請先填寫新單位數");
        		return;
        	}
        	
        	var newTotalUnits = Number($scope.inputVO.NEW_RDM_TOTAL_UNITS);
        	var rdmTotalUnits = Number($scope.inputVO.rdmTotalUnits);
        	if(newTotalUnits < rdmTotalUnits) {
        		$scope.showErrorMsg("新單位數小於總贖回單位數，無法依原單位數調整");
        		return;
        	}
        	
        	angular.forEach($scope.resultList, function(row) {
        		debugger
        		row.ADJ_UNIT_NUM = row.UNIT_NUM;
        	});
        	
        	//取得總調整後單位數
        	$scope.calAdjTotalUnits();
        }
        
        //依比例調整單位數
        $scope.adjByProportion = function() {
        	if(!$scope.inputVO.NEW_RDM_TOTAL_UNITS) {
        		$scope.showErrorMsg("請先填寫新單位數");
        		return;
        	}
        	
        	var newTotalUnits = Number($scope.inputVO.NEW_RDM_TOTAL_UNITS);
        	var rdmTotalUnits = Number($scope.inputVO.rdmTotalUnits);
        	
        	if($scope.resultList.length == 1) {
        		//贖回資料只有一筆全部給
        		row.ADJ_UNIT_NUM = newTotalUnits;
        	} else {
	        	var i = 1;
	        	var ttlunits = 0;
	        	angular.forEach($scope.resultList, function(row) {
	        		debugger
	        		if(i == $scope.resultList.length) {
	        			//最後一筆：新單位數-前面筆數的合，以免有小數點四捨五入的問題，沒有全部分配完
	        			row.ADJ_UNIT_NUM = Math.round((newTotalUnits - ttlunits) * 10000) / 10000;
	        		} else {
	        			//非最後一筆：(贖回單位數/總贖回單位數)x新單位數
	        			row.ADJ_UNIT_NUM = Math.round(((row.UNIT_NUM / rdmTotalUnits) * newTotalUnits) * 10000) / 10000; //小數第四位
	        			ttlunits += Number(row.ADJ_UNIT_NUM);
	        		}
	        		i++;
	        	});
        	}
        	
        	//取得總調整後單位數
        	$scope.calAdjTotalUnits();
        }
        
        $scope.chkNewTotalUnits = function() {
        	var newTotalUnits = Number($scope.inputVO.NEW_RDM_TOTAL_UNITS);
        	var rdmTotalUnits = Number($scope.inputVO.rdmTotalUnits);
        	
        	if(newTotalUnits > rdmTotalUnits) {
        		$scope.inputVO.NEW_RDM_TOTAL_UNITS = undefined;
        		$scope.showErrorMsg("新單位數不可大於總贖回單位數");
        		return;
        	}
        }
        
        $scope.save = function(type) {
        	if(type == "3") { //送主管覆核
        		//取得總調整後單位數
                $scope.calAdjTotalUnits();
                
                if($scope.inputVO.leftAdjTotalUnits && Number($scope.inputVO.leftAdjTotalUnits) < 0) {
                	$scope.showErrorMsg("調整後單位數不可大於總贖回單位數");
            		return;
                }
                
                $confirm({text: '確定要送審嗎？'}, {size: 'sm'}).then(function () {
    				var param = {};
    				param.saveType = type;
    				param.adjList = $scope.resultList;
    				param.NEW_RDM_TOTAL_UNITS = $scope.inputVO.NEW_RDM_TOTAL_UNITS;
                    $scope.sendRecv("PRD235", "save", "com.systex.jbranch.app.server.fps.prd235.PRD235InputVO", param,
                    	function (totas, isError) {
                            if (isError) {
                                $scope.showErrorMsg(totas[0].body.msgData);
                            }
                            if (totas.length > 0) {
                                $scope.showSuccessMsg("送審成功"); //送主管覆核成功
                                $scope.getRedeemDetails();
                            }
                    });
                });
        	} else if(type == "4") { //主管核可
        		var param = {};
				param.saveType = type;
				param.PRD_SEQ_NO = $scope.inputVO.SEQ_NO;
                $scope.sendRecv("PRD235", "save", "com.systex.jbranch.app.server.fps.prd235.PRD235InputVO", param,
                	function (totas, isError) {
                        if (isError) {
                            $scope.showErrorMsg(totas[0].body.msgData);
                        }
                        if (totas[0].body.errorCode != undefined && totas[0].body.errorCode != null && totas[0].body.errorCode != "") {
                        	$scope.showErrorMsg("傳送主機失敗：" + totas[0].body.errorCode + "-" + totas[0].body.errorMsg);
                        	$scope.getRedeemDetails();
                        } else {
                        	$scope.showSuccessMsg("傳送主機成功"); //覆核完成
                            $scope.getRedeemDetails();
                        }
                });
        	} else if(type == "5") { //主管退回
        		var param = {};
				param.saveType = type;
				param.PRD_SEQ_NO = $scope.inputVO.SEQ_NO;
                $scope.sendRecv("PRD235", "save", "com.systex.jbranch.app.server.fps.prd235.PRD235InputVO", param,
                	function (totas, isError) {
                        if (isError) {
                            $scope.showErrorMsg(totas[0].body.msgData);
                        }
                        if (totas.length > 0) {
                            $scope.showSuccessMsg("退回商品PM重新填寫單位數"); //主管退回
                            $scope.getRedeemDetails();
                        }
                });
        	}
			
        }
});