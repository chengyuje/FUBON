/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM251_EDITController',
	function($scope, $controller, socketService, ngDialog, projInfoService, sysInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM251_EDITController";
		
		$scope.priID = String(sysInfoService.getPriID());
		
		// combobox
		$scope.ao_code = projInfoService.getAoCode();
		$scope.sendRecv("CRM211", "getAOCode", "com.systex.jbranch.app.server.fps.crm251.CRM251InputVO", {},
				function(totas, isError) {
                	if (isError) {
                		$scope.showErrorMsg(totas[0].body.msgData);
                	}
                	if (totas.length > 0) {
						$scope.aolist = totas[0].body.resultList;
						$scope.AOCodes = [];
						if ($scope.ao_code != '' && $scope.ao_code != undefined) {		//有AO_CODE
	            			if($scope.ao_code.length > 1){		//有兩個以上AO_CODE的理專
	            				angular.forEach($scope.aolist, function(row, index, objs){
	            					angular.forEach($scope.ao_code, function(row2, index2, objs2){
	            						if(row.AO_CODE == row2){
	            							$scope.AOCodes.push({LABEL: row.EMP_NAME, DATA: row.AO_CODE});
	            						}
	            					});
	                			});
	            			}else if($scope.ao_code.length == 1){		//只有一個AO_CODE的理專
	                    		angular.forEach($scope.aolist, function(row, index, objs){
	                    			$scope.AOCodes.push({LABEL: row.EMP_NAME, DATA: row.AO_CODE});
	                			});
	            			}
	        			}else{		//無AO_CODE
	        				angular.forEach($scope.aolist, function(row, index, objs){
	        					$scope.AOCodes.push({LABEL: row.EMP_NAME, DATA: row.AO_CODE});
	        				});
	        			}
                	};
				}
		);
		
		$scope.init = function(){
        	if($scope.row){
        		$scope.isUpdate = true
        	}
            $scope.row = $scope.row || {};
            $scope.inputVO = {
            		group_id: $scope.row.GROUP_ID,
            		group_name: $scope.row.GROUP_NAME
            };
            
            $scope.pri = String(sysInfoService.getPriID());
            
            /*
			 * 取得UHRM人員清單(由員工檔+角色檔)
			 */
			$scope.sendRecv("ORG260", "getUHRMList", "com.systex.jbranch.app.server.fps.org260.ORG260InputVO", $scope.inputVO, 
					function(tota, isError) {
						if (isError) {
							return;
						}
						if (tota.length > 0) {
							$scope.mappingSet['UHRM_LIST'] = tota[0].body.uhrmList;
							$scope.inputVO.uEmpID = tota[0].body.uEmpID;
						}
			});
			
            if($scope.isUpdate)
            	$scope.inputVO.ao_code = $scope.row.AO_CODE;
            else {
            	if (projInfoService.getAoCode()[0] != "") {
                	$scope.inputVO.ao_code = projInfoService.getAoCode()[0];
        		}
            }	
        };
        $scope.init();
		
        $scope.save = function() {
        	if($scope.parameterTypeEditForm.$invalid){
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
        	if($scope.isUpdate) {
        		$scope.sendRecv("CRM251", "updateGroup", "com.systex.jbranch.app.server.fps.crm251.CRM251InputVO", $scope.inputVO,
    					function(totas, isError) {
    	                	if (isError) {
    	                		$scope.showErrorMsg(totas[0].body.msgData);
    	                	}
    	                	if (totas.length > 0) {
    	                		$scope.showSuccessMsg('ehl_01_common_004');
                        		$scope.closeThisDialog('successful');
    	                	};
    					}
    			);
        	} else {
        		$scope.sendRecv("CRM251", "addGroup", "com.systex.jbranch.app.server.fps.crm251.CRM251InputVO", $scope.inputVO,
    					function(totas, isError) {
    	                	if (isError) {
    	                		$scope.showErrorMsg(totas[0].body.msgData);
    	                	}
    	                	if (totas.length > 0) {
    	                		$scope.showSuccessMsg('ehl_01_common_004');
                        		$scope.closeThisDialog('successful');
    	                	};
    					}
    			);
        	}
        };
		
});
