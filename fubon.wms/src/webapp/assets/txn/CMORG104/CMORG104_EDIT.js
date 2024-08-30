/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('CMORG104_EditController',
    function($scope, $controller, socketService, alerts, projInfoService) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "CMORG104_EditController";
        
        $scope.mappingSet['time'] = [];
        for(var i=0;i<10;i++){
        	if(i<2){
        		$scope.mappingSet['time'].push({
					LABEL : '0'+(8+i)+':00',
					DATA : '0'+(8+i)+':00'
        		});
        	} else {
        		$scope.mappingSet['time'].push({
    				LABEL : (8+i)+':00',
    				DATA : (8+i)+':00'
            	});
        	}
        }
        $scope.mappingSet['timeE'] = [];
        for(var i=0;i<10;i++){
        	if(i<2){
        		$scope.mappingSet['timeE'].push({
					LABEL : '0'+(8+i)+':59',
					DATA : '0'+(8+i)+':59'
        		});
        	} else {
        		$scope.mappingSet['timeE'].push({
    				LABEL : (8+i)+':59',
    				DATA : (8+i)+':59'
            	});
        	}
        }
        
        $scope.init = function() {
        	$scope.eComboDisabled = true;
        	$scope.isUpdate = false;
        	$scope.empData2 = [];
        	if($scope.row){
        		$scope.isUpdate = true;
        	}
        	if(!$scope.isUpdate) {
        		$scope.sendRecv("CMORG104", "getEmpData", "com.systex.jbranch.app.server.fps.cmorg104.CMORG104EMPVO", {'tipEmpID': $scope.wkEmpID},
        				function(totas, isError) {
                        	if (isError) {
                        		$scope.showErrorMsg(totas[0].body.msgData);
                        	}
                        	if (totas.length > 0) {
                        		$scope.empData = totas[0].body.agent[0];
                        		$scope.TERRITORY_ID = $scope.empData.TERRITORY_ID;
                        		dogetDeputy();
                        	};
        				}
        		);
        	} else {
        		$scope.sendRecv("CMORG104", "getAgent", "com.systex.jbranch.app.server.fps.cmorg104.CMORG104EMPVO", {'seq': $scope.row.SEQ},
        				function(totas, isError) {
                        	if (isError) {
                        		$scope.showErrorMsg(totas[0].body.msgData);
                        	}
                        	if (totas.length > 0) {
                        		$scope.empData = totas[0].body.agent[0];
                        		$scope.empData2.EMP_NAME = $scope.empData.AgentName;
                        		$scope.TERRITORY_ID = $scope.empData.BRANCH_ID;
                        		dogetDeputy();
                        		$scope.inputVO = {
                        				tipEmpID: $scope.empData.AGENT_ID,
                        				agentTerritoryID: $scope.empData.AGENT_TERRITORY_ID,
                        				sTime: $scope.empData.STIME,
                        				eTime: $scope.empData.ETIME,
                        				tarDesc: $scope.empData.ASSIGN_DESC
                        		};
                        		if ($scope.empData.SDATE)
                            		$scope.inputVO.sDate = $scope.toJsDate($scope.empData.SDATE);
                            	if ($scope.empData.EDATE)
                            		$scope.inputVO.eDate = $scope.toJsDate($scope.empData.EDATE);
                        	};
        				}
        		);
        	}
        	$scope.startMaxDate = $scope.maxDate;
            $scope.endMinDate = $scope.minDate;
        };
        $scope.init();
        function dogetDeputy() {
        	$scope.sendRecv("CMORG104", "getDeputy", "com.systex.jbranch.app.server.fps.cmorg104.CMORG104DataVO", {'tipEmpID': $scope.empData.EMP_ID,'tipEmpRole': $scope.empData.SA_JOB_TITLE_ID,'cmbArea': $scope.empData.AREA_CODE,'cmbBranch': $scope.empData.BRANCH_ID},
    				function(totas, isError) {
                    	if (isError) {
                    		$scope.showErrorMsg(totas[0].body.msgData);
                    	}
                    	if (totas.length > 0) {
                    		$scope.Deputy = totas[0].body.agent;
                    		$scope.sa_type = totas[0].body.sa_type;
                    		if (totas[0].body.sa_type == "1") {
                    			$scope.rdgType = "2";
                    			$scope.rdgDisabled = false;
                    			gencmb();
                    		} else if (totas[0].body.sa_type == "2") {
                    			$scope.rdgType = "2";
                    			$scope.rdgDisabled = true;
                    		} else {
                    			$scope.rdgType = "2";
                    			$scope.rdgDisabled = true;
                    		}
                    	};
    				}
    		);
		}
        function gencmb() {
        	$scope.mappingSet['areasDesc'] = [];
			angular.forEach($scope.Deputy, function(row, index, objs){
				if ($scope.mappingSet['areasDesc'].map(function(e) { return e.DATA; }).indexOf(row.AREA_CODE) == -1) {
					$scope.mappingSet['areasDesc'].push({LABEL: row.AREA, DATA: row.AREA_CODE});
				}
			});
			if($scope.mappingSet['areasDesc'].length == 1)
				$scope.inputVO.cmbArea = $scope.empData.AREA_CODE;
			$scope.genBranch = function() {
				$scope.mappingSet['branchsDesc'] = [];
				if($scope.inputVO.cmbArea) {
					angular.forEach($scope.Deputy, function(row, index, objs){
						if(row.AREA_CODE == $scope.inputVO.cmbArea) {
							if ($scope.mappingSet['branchsDesc'].map(function(e) { return e.DATA; }).indexOf(row.BRANCH_ID) == -1) {
								$scope.mappingSet['branchsDesc'].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_ID});
							}
						}
					});
				} else {
					angular.forEach($scope.Deputy, function(row, index, objs){
						if ($scope.mappingSet['branchsDesc'].map(function(e) { return e.DATA; }).indexOf(row.BRANCH_ID) == -1) {
							$scope.mappingSet['branchsDesc'].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_ID});
						}
					});
				}
	        };
	        $scope.genBranch();
	        if($scope.mappingSet['branchsDesc'].length == 1)
				$scope.inputVO.cmbBranch = $scope.empData.BRANCH_ID;
	        $scope.genEmp = function() {
	        	$scope.mappingSet['empsDesc'] = [];
	        	if($scope.inputVO.cmbArea) {
	        		if($scope.inputVO.cmbBranch) {
	        			angular.forEach($scope.Deputy, function(row, index, objs){
	        				if(row.AREA_CODE == $scope.inputVO.cmbArea && row.BRANCH_ID == $scope.inputVO.cmbBranch) {
	        					if ($scope.mappingSet['empsDesc'].map(function(e) { return e.DATA; }).indexOf(row.EMP_ID) == -1) {
	    							$scope.mappingSet['empsDesc'].push({LABEL: row.EMP_NAME, DATA: row.EMP_ID});
	    						}
	        				}
	        			});
	        		} else {
	        			angular.forEach($scope.Deputy, function(row, index, objs){
	        				if(row.AREA_CODE == $scope.inputVO.cmbArea) {
	        					if ($scope.mappingSet['empsDesc'].map(function(e) { return e.DATA; }).indexOf(row.EMP_ID) == -1) {
	    							$scope.mappingSet['empsDesc'].push({LABEL: row.EMP_NAME, DATA: row.EMP_ID});
	    						}
	        				}
	        			});
	        		}
	        	} else {
	        		if($scope.inputVO.cmbBranch) {
	        			angular.forEach($scope.Deputy, function(row, index, objs){
	        				if(row.BRANCH_ID == $scope.inputVO.cmbBranch) {
	        					if ($scope.mappingSet['empsDesc'].map(function(e) { return e.DATA; }).indexOf(row.EMP_ID) == -1) {
	    							$scope.mappingSet['empsDesc'].push({LABEL: row.EMP_NAME, DATA: row.EMP_ID});
	    						}
	        				}
	        			});
	        		} else {
	        			angular.forEach($scope.Deputy, function(row, index, objs){
	        				if ($scope.mappingSet['empsDesc'].map(function(e) { return e.DATA; }).indexOf(row.EMP_ID) == -1) {
    							$scope.mappingSet['empsDesc'].push({LABEL: row.EMP_NAME, DATA: row.EMP_ID});
    						}
	        			});
	        		}
	        	}
	        	var index = $scope.mappingSet['empsDesc'].map(function(e) { return e.DATA; }).indexOf($scope.wkEmpID);
				if(index != -1)
					$scope.mappingSet['empsDesc'].splice(index, 1);
	        };
	        $scope.genEmp();
        }
        
        $scope.typeChange = function(type){
        	if (type == "1") {
        		$scope.eComboDisabled = false;
        		$scope.empIDDisabled = true;
        	} else if (type == "2") {
        		$scope.eComboDisabled = true;
        		$scope.empIDDisabled = false;
        	}
        };
        
        $scope.open = function($event, index) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope['opened'+index] = true;
        };
        $scope.limitDate = function() {
        	$scope.startMaxDate = $scope.inputVO.eDate || $scope.maxDate;
            $scope.endMinDate = $scope.inputVO.sDate || $scope.minDate;
        };
        
        $scope.getName = function(){
        	$scope.sendRecv("CMORG104", "getEmpData", "com.systex.jbranch.app.server.fps.cmorg104.CMORG104EMPVO", {'tipEmpID': $scope.inputVO.tipEmpID},
    				function(totas, isError) {
                    	if (isError) {
                    		$scope.showErrorMsg(totas[0].body.msgData);
                    	}
                    	if (totas.length > 0) {
                    		$scope.empData2 = totas[0].body.agent[0] || [];
                    		$scope.inputVO.agentTerritoryID = $scope.empData2.TERRITORY_ID;
                    	};
    				}
    		);
        };
        
        $scope.save = function(){
        	if($scope.inputVO.tipEmpID == $scope.wkEmpID) {
        		$scope.showErrorMsgInDialog('不可代理自己');
    			return;
    		}
        	if($scope.parameterTypeEditForm.$invalid){
        		console.log('form=' + JSON.stringify($scope.parameterTypeEditForm));
        		$scope.showErrorMsgInDialog('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
        	if ($scope.rdgType == "1") {
        		$scope.inputVO.agentID = $scope.inputVO.cmbEmp;
        		if ($scope.inputVO.cmbBranch)
        			$scope.inputVO.agentTerritoryID = $scope.inputVO.cmbBranch;
        		else if ($scope.inputVO.cmbArea)
        			$scope.inputVO.agentTerritoryID = $scope.inputVO.cmbArea;
        	}
	    	else
	    		$scope.inputVO.agentID = $scope.inputVO.tipEmpID;
        	if(!$scope.inputVO.agentID) {
        		$scope.showErrorMsgInDialog('請選擇或輸入被代理人資訊!!');
        		return;
        	}
        	if ($scope.sa_type != "3") {
        		if($scope.Deputy.map(function(e) { return e.EMP_ID; }).indexOf($scope.inputVO.agentID) == -1) {
        			$scope.showErrorMsgInDialog('該代理人非您可選擇人員，請重新輸入!!');
            		return;
        		}
        	}
        	if ($scope.inputVO.sDate.toISOString() == $scope.inputVO.eDate.toISOString()) {
        		if($scope.inputVO.sTime >= $scope.inputVO.eTime) {
        			$scope.showErrorMsgInDialog('代理起始時間不可大於等於代理結束時間');
        			return;
        		}
        	}
        	var inputvo = {'areaCode': $scope.empData.AREA_CODE,'empRole': $scope.empData.SA_JOB_TITLE_ID,'empID': $scope.wkEmpID,'territoryID': $scope.TERRITORY_ID,'agentSDate': $scope.inputVO.sDate,'agentSTime': $scope.inputVO.sTime,'agentEDate': $scope.inputVO.eDate,'agentETime': $scope.inputVO.eTime,'agentID': $scope.inputVO.agentID,'agentTerritoryID': $scope.inputVO.agentTerritoryID,'desc': $scope.inputVO.tarDesc};
        	if($scope.isUpdate) {
        		inputvo.seq = $scope.row.SEQ;
        	}
        	$scope.sendRecv("CMORG104", "saveAgent", "com.systex.jbranch.app.server.fps.cmorg104.CMORG104AUVO", inputvo,
    			function(totas, isError) {
	                if (isError) {
	                	$scope.showErrorMsgInDialog(totas.body.msgData);
	                    return;
	                }
	                 if (totas.length > 0) {
	                	 $scope.showSuccessMsg('儲存成功');
	                	 $scope.closeThisDialog('successful');
	                 };
	            }
        	);
        }
        
    }
);
