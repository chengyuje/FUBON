/*
 */
'use strict';
eSoafApp.controller('CAM211_3_Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CAM211_3_Controller";
		$scope.row = $scope.row || {};	
		$scope.init = function(){
			$scope.inputVO = {
					empId: $scope.row.EMP_ID,
					aoCode: $scope.row.AO_CODE,
					custId: $scope.row.CUST_ID,
					branch_Name: $scope.row.BRANCH_NAME,
					branchNbr: $scope.row.BRANCH_NBR,
					toDoList: '',
					aoList:'',
					channel:'',
					reason:'',
					otherReason:'',
					style : 'once'
        	};			
		};
        $scope.init();
        
        $scope.getList = function(){
        	
        	$scope.mappingSet['STYLE'] = [];
        	$scope.mappingSet['STYLE'].push({LABEL: '一次性改派', DATA: 'aa'}); 
        	    	
        	$scope.sendRecv("CAM211", "queryToDoListData", "com.systex.jbranch.app.server.fps.cam211.CAM211InputVO",  $scope.inputVO,
        			function(totas, isError) {
                 		if (!isError) {
                 			if(totas[0].body.resultList.length == 0) {
                 				$scope.showMsg("ehl_01_common_009");
                 	               return;
                 	         } else {
                 				$scope.mappingSet['toDoList'] = [];
            					angular.forEach(totas[0].body.resultList, function(row, index, objs){    		                	
        		                	$scope.mappingSet['toDoList'].push({LABEL: row.CAMPAIGN_NAME, DATA: row.LIST_DATA});
        		            	});
            					
            					$scope.mappingSet['toDoList2'] = [];
            					angular.forEach(totas[0].body.resultList2, function(row, index, objs){    		                	
        		                	$scope.mappingSet['toDoList2'].push({LABEL: row.CAMPAIGN_NAME, DATA: row.LIST_DATA});
        		            	});
                 			}                 			
                 			return;
                 		}
                 });
        }      	
        $scope.getList();
        
        //改派人員類別選項
		var vo = {'param_type': 'CAM.DISPATCH_CHANNEL', 'desc': false};
        if(!projInfoService.mappingSet['CAM.DISPATCH_CHANNEL']) {
        	$scope.requestComboBox(vo, function(totas) {
        		if (totas[totas.length - 1].body.result === 'success') {
        			projInfoService.mappingSet['CAM.DISPATCH_CHANNEL'] = totas[0].body.result;
        			$scope.mappingSet['CAM.DISPATCH_CHANNEL'] = projInfoService.mappingSet['CAM.DISPATCH_CHANNEL'];
        			
        		}
        	});
        } else {
        	$scope.mappingSet['CAM.DISPATCH_CHANNEL'] = projInfoService.mappingSet['CAM.DISPATCH_CHANNEL'];
        }
        
        $scope.action = function(row) {
        	if($scope.inputVO.channel != ''){
        		if ($scope.inputVO.channel != '002' && $scope.inputVO.channel != '003') {
        			$scope.inputVO.style = 'once';
        		}
        		
        		$scope.sendRecv("CAM211", "queryAoData_3", "com.systex.jbranch.app.server.fps.cam211.CAM211InputVO",  $scope.inputVO,
        				function(totas, isError) {
        					if (!isError) {
        						if(totas[0].body.resultList.length == 0) {
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
    	
    	//表單送出驗證
    	$scope.upDataList = function(row) {
    		if($scope.inputVO.channel == '') {
    			$scope.showErrorMsg("請選擇改派人員類別");
    		} else if ($scope.inputVO.aoList == '' ) {
    			$scope.showErrorMsg("請選擇改派人員");
    		} else {
    			$scope.sendRecv("CAM211", "updLead", "com.systex.jbranch.app.server.fps.cam211.CAM211InputVO",  $scope.inputVO,
    					function(totas, isError) {	
    				if (isError) {
    					$scope.showErrorMsg(tota[0].body.MsgData);
    				}
    				$scope.showSuccessMsg("ehl_01_common_006");  
    				$scope.closeThisDialog('successful');
    			});
    		}
    	}
    	//改派形式選項
	   	$scope.mappingSet['STYLE'] = [];
	   	$scope.mappingSet['STYLE'].push({LABEL: '一次性改派', DATA: 'once'}); 
//	   	$scope.mappingSet['STYLE'].push({LABEL: '換Code經營', DATA: 'changCode'}); 
    	
     	$scope.invalidList = function(row) {
    		$scope.sendRecv("CAM211", "invalidLead", "com.systex.jbranch.app.server.fps.cam211.CAM211InputVO",  $scope.inputVO,
    				function(totas, isError) {
	    		if (isError) {
						$scope.showErrorMsg(tota[0].body.MsgData);
				}
				$scope.showSuccessMsg("ehl_01_common_006");  
				$scope.closeThisDialog('successful');
    		});
    	}
     	//顯示改派表
		$scope.reassign = function(){
			$("#reassign_1").show();
			$("#invalid_1").hide();
			$("#reassign_2").show();
			$("#invalid_2").hide();
		}
		//顯示作廢表
		$scope.invalid = function(){
			$("#invalid_1").show();
			$("#reassign_1").hide();
			$("#invalid_2").show();
			$("#reassign_2").hide();
		}

        $scope.init();    
});
