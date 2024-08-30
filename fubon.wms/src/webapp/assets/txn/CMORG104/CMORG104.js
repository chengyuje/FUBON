/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('CMORG104Controller',
    function($scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "CMORG104Controller";
        
        $scope.rdgType = "2";
        $scope.eComboDisabled = true;
        if(projInfoService.getUser().isAO || projInfoService.getUser().isOP) {
        	$scope.rdgDisabled = true;
        	$scope.empIDDisabled = true;
        }
        $scope.mappingSet['areasDesc'] = [];
		angular.forEach(projInfoService.getAvailArea(), function(row, index, objs){
			$scope.mappingSet['areasDesc'].push({LABEL: row.AreaName, DATA: row.AreaCode});
		});
		$scope.genBranch = function() {
			$scope.mappingSet['branchsDesc'] = [];
			angular.forEach(projInfoService.getAvailBranch(), function(row, index, objs){
				if(row.AreaCode == $scope.inputVO.cmbArea)
					$scope.mappingSet['branchsDesc'].push({LABEL: row.BranchName, DATA: row.BranchNbr});
			});
        };
        $scope.genEmp = function() {
			$scope.sendRecv("CMORG104", "getBranchEmp", "com.systex.jbranch.app.server.fps.cmorg104.CMORG104EMPVO", {'cmbArea': $scope.inputVO.cmbArea,'cmbBranch': $scope.inputVO.cmbBranch},
    				function(totas, isError) {
                    	if (isError) {
                    		$scope.showErrorMsg(totas[0].body.msgData);
                    	}
                    	if (totas.length > 0) {
                    		$scope.mappingSet['empsDesc'] = [];
                    		angular.forEach(totas[0].body.agent, function(row, index, objs){
                    			$scope.mappingSet['empsDesc'].push({LABEL: row.EMP_NAME, DATA: row.EMP_ID});
                    		});
                    	};
    				}
    		);
        };
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
        $scope.getName = function(){
        	$scope.sendRecv("CMORG104", "getEmpData", "com.systex.jbranch.app.server.fps.cmorg104.CMORG104EMPVO", {'tipEmpID': $scope.inputVO.tipEmpID},
    				function(totas, isError) {
                    	if (isError) {
                    		$scope.showErrorMsg(totas[0].body.msgData);
                    	}
                    	if (totas.length > 0) {
                    		$scope.empData = totas[0].body.agent[0];
                    	};
    				}
    		);
        };
        $scope.init = function(){
        	$scope.inputVO = {
        			cmbArea: '',
        			cmbBranch: '',
        			cmbEmp: '',
        			tipEmpID: projInfoService.getUserID(),
        			tipEmpName: projInfoService.getUserName(),
        			sDate: undefined,
        			sTime: undefined,
        			eDate: undefined,
        			eTime: undefined
        	};
        	$scope.startMaxDate = $scope.maxDate;
            $scope.endMinDate = $scope.minDate;
            $scope.getName();
        }
        $scope.init();
        $scope.initText = function(){
        	$scope.inputVO.cmbArea = '';
        	$scope.inputVO.cmbBranch = '';
        	$scope.inputVO.cmbEmp = '';
        	$scope.inputVO.tipEmpID = '';
        	$scope.inputVO.tipEmpName = '';
        	$scope.empData = [];
        };
        
        $scope.typeChange = function(type){
        	if (type == "1") {
        		$scope.eComboDisabled = false;
        		$scope.empIDDisabled = true;
        		$scope.initText();
        	} else if (type == "2") {
        		$scope.eComboDisabled = true;
        		$scope.empIDDisabled = false;
        		$scope.initText();
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
        
        // 初始分頁資訊
        $scope.inquireInit = function(){
        	$scope.initLimit();
        	$scope.paramList = [];
        };
        $scope.inquireInit();
      
        $scope.inquire = function(){
        	if ($scope.rdgType == "1")
        		$scope.inputVO.agentID = $scope.inputVO.cmbEmp;
        	else
        		$scope.inputVO.agentID = $scope.inputVO.tipEmpID;
        	if(!$scope.inputVO.agentID) {
        		$scope.showErrorMsg('請選擇或輸入被代理人資訊!!');
        		return;
        	}
        	$scope.sendRecv("CMORG104", "getAgentData", "com.systex.jbranch.app.server.fps.cmorg104.CMORG104DataVO", $scope.inputVO,
    				function(totas, isError) {
                    	if (isError) {
                    		$scope.showErrorMsg(totas[0].body.msgData);
                    	}
                    	if (totas.length > 0) {
                    		if(totas[0].body.agent.length == 0) {
	                			$scope.showMsg("ehl_01_common_001");
	                			return;
	                		}
                    		$scope.pagingList($scope.paramList, totas[0].body.agent);
        					$scope.outputVO = totas[0].body;
        					angular.forEach($scope.paramList, function(row, index, objs){
        						row.set = [];
        						if(row.ASSIGN_STATUS == "1") {
        							row.actionIF = true;
        							row.set.push({LABEL: "修改代理", DATA: "1"},{LABEL: "取消代理", DATA: "2"});
        						} else if(row.ASSIGN_STATUS == "2") {
        							row.actionIF = true;
        							row.set.push({LABEL: "取消代理", DATA: "2"});
        						} else {
        							row.actionIF = false;
        						}
        					});
        					return;
                    	};
    				}
    		);
        };
        $scope.action = function(row) {
        	if(row.cmbAction) {
        		if (row.cmbAction == "2") {
        			$confirm({text: '是否取消代理!!'}, {size: 'sm'})
                    .then(function() {
                    	$scope.sendRecv("CMORG104", "cancelAgentData", "com.systex.jbranch.app.server.fps.cmorg104.CMORG104DataVO", {'seq': row.SEQ},
                				function(totas, isError) {
                                	if (isError) {
                                		$scope.showErrorMsg(totas[0].body.msgData);
                                	}
                                	if (totas.length > 0) {
                                		$scope.showSuccessMsg('取消代理成功');
                                		$scope.inquireInit();
                                		$scope.inquire();
                                	};
                				}
                		);
                    });
        		}
        		else
        			$scope.edit({'SEQ': row.SEQ});
        		row.cmbAction = "";
        	}
        };
        
        $scope.edit = function (row) {
	    	  var wkEmpID;
	    	  if ($scope.rdgType == "1")
	    		  wkEmpID = $scope.inputVO.cmbEmp;
	    	  else
	    		  wkEmpID = $scope.inputVO.tipEmpID;
	    	  if(!wkEmpID) {
	    		  $scope.showErrorMsg('請選擇或輸入被代理人資訊!!');
	    		  return;
	    	  }
	    	  console.log('edit data='+JSON.stringify(row));
              var dialog = ngDialog.open({
                  template: 'assets/txn/CMORG104/CMORG104_EDIT.html',
                  className: 'CMORG104',
                  showClose: false,
                  controller: ['$scope', function($scope) {
                	  $scope.row = row;
                	  $scope.wkEmpID = wkEmpID;
                  }]
              });
              dialog.closePromise.then(function (data) {
                  if(data.value === 'successful'){
                	  $scope.inquireInit();
                	  $scope.inquire();
                  }
              });
          };
    }
);
